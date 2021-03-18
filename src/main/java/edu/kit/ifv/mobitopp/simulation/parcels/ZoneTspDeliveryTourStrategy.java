package edu.kit.ifv.mobitopp.simulation.parcels;

import static java.lang.Math.round;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class ZoneTspDeliveryTourStrategy is a DeliveryTourAssignmentStrategy.
 * When assigning parcels, a 'giant tour' containing all parcels is planned.
 * This is accomplished by solving a (2-approximation) TSP on all delivery locations.
 * Within the zone, parcels are grouped by destination type and household.
 * A delivery person is assigned parcels in the computed order, until their capacity is reached,
 * or the estimated duration of the tour reaches 8h.
 * The giant tour is recalculated every hour with the current impedance to consider the new trip durations.
 * The delivery persons capacity is drawn from a normal distribution.
 */
public class ZoneTspDeliveryTourStrategy implements DeliveryTourAssignmentStrategy {

	private List<Parcel> parcelTour = new ArrayList<Parcel>();
	private Time lastPlan = SimpleTime.start;
	
	private static final int MEAN_CAPACITY = 160;
	private static final double CAPACITY_STD_DEV = 16;
	private static final int MIN_CAPACITY = 100;
	private static final int MAX_CAPACITY = 200;
	
	private static final int MAX_HOURS = 8;
	private static final boolean SKIP_SUNDAY = true;
	
	/**
	 * Assign parcels to the given delivery person.
	 *
	 * @param dc the distribution center
	 * @param person the person
	 * @param work the work activity
	 * @return the list of assigned parcels
	 */
	@Override
	public List<Parcel> assignParcels(DistributionCenter dc, DeliveryPerson person,  ActivityIfc work) {
		DeliveryEfficiencyProfile efficiency = person.getEfficiency();
		
		if (SKIP_SUNDAY && startsOnSunday(work) || startsAfter1800(work)) {
			return Arrays.asList();
		}
		
		if (work.startDate().isAfter(lastPlan.plusHours(1))) {
			planZoneTour(dc, person, work.startDate());
		}

		Iterator<Parcel> it = parcelTour.stream().filter(p -> p.isUndefined()).iterator();
		
		ArrayList<Parcel> assigned = new ArrayList<Parcel>();
		if (!it.hasNext()) {return assigned;}
		
		
		Parcel prev = null;
		Parcel next = null;
		float tourDuration = 0;
		int parcels = 0;
		int capacity = selectCapacity(person);
		
		while (it.hasNext() && tourDuration < MAX_HOURS*60 && parcels < capacity) {
			next = it.next();
			assigned.add(next);
			tourDuration += duration(dc, person, work.startDate().plusMinutes((int)tourDuration), efficiency, next, prev);
			parcels++;
			prev = next;
		}
		

				
		return assigned;
		
	}


	/**
	 * Checks if the given work activity starts after 1800.
	 *
	 * @param work the work
	 * @return true, if successful
	 */
	private boolean startsAfter1800(ActivityIfc work) {
		return work.startDate().isAfter(work.startDate().startOfDay().plusHours(18));
	}


	/**
	 * Checks if the given work activity starts on sunday.
	 *
	 * @param work the work
	 * @return true, if successful
	 */
	private boolean startsOnSunday(ActivityIfc work) {
		return work.startDate().weekDay().equals(DayOfWeek.SUNDAY);
	}
	
	
	/**
	 * Estimates the duration of the delivery.
	 *
	 * @param dc the {@link DistributionCenter}
	 * @param person the {@link DeliveryPerson}
	 * @param time the current time
	 * @param efficiency the {@link DeliveryEfficiencyProfile}
	 * @param next the next {@link Parcel} to deliver
	 * @param prev the previous {@link Parcel} to deliver
	 * @return the estimate duration
	 */
	private float duration(DistributionCenter dc, DeliveryPerson person, Time time, DeliveryEfficiencyProfile efficiency, Parcel next, Parcel prev) {
		float dur = 0.0f;
		
		if (prev == null) {
			dur += travelTime(person, dc.getZone(), next.getZone(), time);
			dur += efficiency.getDeliveryDurBase();
			return dur;
		}
		
		if (!next.getLocation().equals(prev.getLocation())) {
			dur += travelTime(person, prev.getZone(), next.getZone(), time);
			dur += efficiency.getDeliveryDurBase();
			return dur;
		}
		
		if (next.getDestinationType().equals(ParcelDestinationType.PACK_STATION) && prev.getDestinationType().equals(prev.getDestinationType())) {
			dur += efficiency.getDeliveryDurPerParcel();
			
		} else if (next.getPerson().household().equals(prev.getPerson().household())) {
			dur += 0;
			
		} else {
			dur += efficiency.getDeliveryDurPerParcel();
		}
		
		
		return dur;
	}
	
	
	
	/**
	 * Returns the travel time form origin to destination.
	 *
	 * @param person the person
	 * @param origin the origin
	 * @param destination the destination
	 * @param time the time
	 * @return the float
	 */
	private float travelTime(DeliveryPerson person, Zone origin, Zone destination, Time time) {
		return person.options().impedance().getTravelTime(origin.getId(), destination.getId(), StandardMode.TRUCK, time);
	}
	
	/**
	 * Selects a capacity from a normal distribution.
	 *
	 * @param person the person
	 * @return the capacity
	 */
	private int selectCapacity(DeliveryPerson person) {
		double stdGauss = new Random((long) (person.getNextRandom() * Long.MAX_VALUE)).nextGaussian();
		double scaledGauss = CAPACITY_STD_DEV *stdGauss + MEAN_CAPACITY;

		return Math.min(Math.max(MIN_CAPACITY, (int) round(scaledGauss)), MAX_CAPACITY);
	}
	
	
	
	
	/**
	 * Plan giant tour through all delivery zones using a TSP 2 approximation.
	 *
	 * @param dc the {@link DistributionCenter}
	 * @param person the {@link DeliveryPerson}
	 * @param currentTime the current {@link Time}
	 */
	private void planZoneTour(DistributionCenter dc, DeliveryPerson person, Time currentTime) {

		List<Parcel> toBeDelivered = dc.getAvailableParcels(currentTime);
	
		SimpleWeightedGraph<Zone, DefaultWeightedEdge> graph = new SimpleWeightedGraph<Zone, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Zone start = dc.getZone();
		graph.addVertex(start);
		
		//Create complete graph: for each parcel add the zone as vertex, also add edges to all existing vertices (in both directions)
		//Use current travel time as edge weight
		for (Zone newZone : toBeDelivered.stream().map(Parcel::getZone).distinct().collect(toList())) {
			
			if (graph.vertexSet().contains(newZone)) {
				continue;
			}
			
			graph.addVertex(newZone);
			
			for (Zone z: graph.vertexSet()) {
				if (z != newZone) {
					DefaultWeightedEdge edgeTo = graph.addEdge(newZone, z);
					double weight = travelTime(person, newZone, z, currentTime) + travelTime(person, z, newZone, currentTime);
					weight /= 2.0;
					graph.setEdgeWeight(edgeTo, weight);
				}
			}
			
		}
		

		//Use 2-approx algorithm for solving tsp
		TwoApproxMetricTSP<Zone, DefaultWeightedEdge> tspAlg = new TwoApproxMetricTSP<>();
		GraphPath<Zone, DefaultWeightedEdge> path = tspAlg.getTour(graph);
		
		List<Parcel> prefix = new ArrayList<Parcel>();
		List<Parcel> suffix = new ArrayList<Parcel>();
		boolean foundStart = false;
		
		for (Zone z: path.getVertexList()) {
			if (z == start) {
				foundStart = true;
			} 			
				
			Map<Location, List<Parcel>> parcelLocations = toBeDelivered.stream().filter(p -> p.getZone().equals(z)).collect(Collectors.groupingBy(Parcel::getLocation));
			
			if (!foundStart) {
				for (List<Parcel> parcels : parcelLocations.values()) {
					suffix.addAll(sortParcelsByDestination(parcels));
				}
				
			} else {
				for (List<Parcel> parcels : parcelLocations.values()) {
					prefix.addAll(sortParcelsByDestination(parcels));
				}
			}
			
		}
		
		this.parcelTour.clear();
		this.parcelTour.addAll(prefix);
		this.parcelTour.addAll(suffix);
		this.parcelTour = this.parcelTour.stream().distinct().collect(toList());

	}
	
	/**
	 * Sort parcels by {@link ParcelDestinationType} and {@link Household}.
	 *
	 * @param parcels the parcels to be sorted
	 * @return the list of sorted parcels
	 */
	private List<Parcel> sortParcelsByDestination(List<Parcel> parcels) {
		return parcels.stream()
				 	  .sorted(Comparator.comparing(Parcel::getDestinationType)
				 			  			.thenComparing(p -> p.getPerson().household().getId().getOid())
				 			 )
				 	  .collect(toList());
	}
	
}
