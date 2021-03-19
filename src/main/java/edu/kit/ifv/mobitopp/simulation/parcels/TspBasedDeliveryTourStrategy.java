package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class TspBasedDeliveryTourStrategy is an exemplary implementation of the DeliveryTourAssignmentStrategy interface.
 * When assigning parcels, a 'giant tour' containing all parcels is planned.
 * This is accomplished by solving a TSP on all delivery zones.
 * Within the zone, parcels are grouped by location and destination type.
 * A delivery person is assigned parcels in the computed order, until their capacity (160 parcels) is reached,
 * or the estimated duration of the tour reaches 8h.
 * The giant tour is recalculated every day.
 */
public class TspBasedDeliveryTourStrategy implements DeliveryTourAssignmentStrategy {

	private List<Parcel> parcelTour = new ArrayList<Parcel>();
	private Time nextPlan = Time.start;
	
	private static int MAX_CAPACITY = 160;
	private static int MAX_HOURS = 8;
	private static boolean SKIP_SUNDAY = true;
	
	/**
	 * Assign parcels to the given delivery person based on the duration of the working activity and the time required per parcel.
	 *
	 * @param dc the distribution center
	 * @param person the person
	 * @param work the work
	 * @return the collection
	 */
	@Override
	public List<Parcel> assignParcels(DistributionCenter dc, DeliveryPerson person,  ActivityIfc work) { //Maybe assign delivery activities instead of parcels?
		DeliveryEfficiencyProfile efficiency = person.getEfficiency();
		
		if (SKIP_SUNDAY && work.startDate().weekDay().equals(DayOfWeek.SUNDAY) || work.startDate().isAfter(work.startDate().startOfDay().plusHours(18))) {
			return Arrays.asList();
		}
		
		
		if (work.startDate().isAfter(nextPlan)) {
			planGiantTour(dc, work.startDate());
		}

		
		Iterator<Parcel> it = parcelTour.stream().filter(p -> p.isUndefined()).iterator();
		
		ArrayList<Parcel> assigned = new ArrayList<Parcel>();
		if (!it.hasNext()) {return assigned;}
		
		
		Parcel prev = it.next();
		Parcel next = null;
		float tripDuration = 2 * travelTime(person, dc.getZone(), prev.getZone(), work.startDate()) + efficiency.getDeliveryDurBase();
		int parcels = 1;
		assigned.add(prev);
		
		while (it.hasNext() && tripDuration < MAX_HOURS*60 && parcels < MAX_CAPACITY) {
			next = it.next();
			assigned.add(next);
			tripDuration += duration(person, work.startDate().plusMinutes((int)tripDuration), efficiency, next, prev);
			parcels++;
			prev = next;
		}

		return assigned;
		
	}
	
	/**
	 * Estimates the duration of the delivery.
	 *
	 * @param person the {@link DeliveryPerson}
	 * @param time the current {@link Time}
	 * @param efficiency the {@link DeliveryEfficiencyProfile}
	 * @param next the next {@link Parcel} to deliver
	 * @param prev the previous {@link Parcel} to deliver
	 * @return the estimate duration
	 */
	private float duration(DeliveryPerson person, Time time, DeliveryEfficiencyProfile efficiency, Parcel next, Parcel prev) {
		float dur = 0.0f;
		
		if (!next.getLocation().equals(prev.getLocation())) {
			dur += travelTime(person, prev.getZone(), next.getZone(), time);
			dur += efficiency.getDeliveryDurBase();
			return dur;
		}
		
		if (next.getDestinationType().equals(ParcelDestinationType.PACK_STATION) && prev.getDestinationType().equals(prev.getDestinationType())) {
			dur += efficiency.getDeliveryDurPerParcel();
			
		} else if (next.getPerson().household().equals(prev.getPerson().household())) {
			dur += efficiency.getDeliveryDurPerParcel();
			
		} else {
			dur += efficiency.getDeliveryDurBase();
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
		return person.options().impedance().getTravelTime(origin.getId(), destination.getId(), StandardMode.CAR, time);
	}
	
	
	
	/**
	 * Plan giant tour through all delivery locations using a TSP 2 approximation.
	 *
	 * @param dc the {@link DistributionCenter}
	 * @param person the {@link DeliveryPerson}
	 * @param currentTime the current {@link Time}
	 */
	private void planGiantTour(DistributionCenter dc, Time plannedTime) {
		
		List<Parcel> toBeDelivered = dc.getAvailableParcels(plannedTime);
	
		SimpleWeightedGraph<Location, DefaultWeightedEdge> graph = new SimpleWeightedGraph<Location, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Location start = dc.getLocation();
		graph.addVertex(start);
		
		//Create complete graph: for each parcel add the location as vertex, also add edges to all existing vertices
		//Use 2d distance as edge weight
		for (Parcel p : toBeDelivered) {
			Location newLocation = p.getLocation();
			
			if (graph.vertexSet().contains(newLocation)) {
				continue;
			}
			
			graph.addVertex(newLocation);
			
			for (Location l: graph.vertexSet()) {
				if (l != newLocation) {
					DefaultWeightedEdge edge = graph.addEdge(newLocation, l);
					graph.setEdgeWeight(edge, newLocation.coordinatesP().distance(l.coordinatesP()));
				}
			}
			
		}

		//Use 2-approx algorithm for solving tsp
		TwoApproxMetricTSP<Location, DefaultWeightedEdge> tspAlg = new TwoApproxMetricTSP<>();
		GraphPath<Location, DefaultWeightedEdge> path = tspAlg.getTour(graph);
		
		List<Parcel> prefix = new ArrayList<Parcel>();
		List<Parcel> suffix = new ArrayList<Parcel>();
		boolean foundStart = false;
		
		for (Location l: path.getVertexList()) {
			if (l == start) {
				foundStart = true;

			} 			
				
			if (!foundStart) {
				suffix.addAll(toBeDelivered.stream().filter(p -> p.getLocation().equals(l)).collect(Collectors.toList()));
			} else {
				prefix.addAll(toBeDelivered.stream().filter(p -> p.getLocation().equals(l)).collect(Collectors.toList()));
			}
			
		}

		
		this.parcelTour.clear();
		this.parcelTour.addAll(prefix);
		this.parcelTour.addAll(suffix);
		this.parcelTour = this.parcelTour.stream().distinct().collect(Collectors.toList());

		this.nextPlan = nextPlan.plusDays(1);
	}
	
}
