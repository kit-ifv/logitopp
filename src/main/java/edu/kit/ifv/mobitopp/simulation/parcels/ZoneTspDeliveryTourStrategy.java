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
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class DummyDeliveryTourStrategy is an exemplary implementation of the DeliveryTourAssignmentStrategy interface.
 * To be replaced!
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
		
		if (SKIP_SUNDAY && startsOnSunday(work) || startsAfter1800(work)) {
			return Arrays.asList();
		}
		
		if (work.startDate().isAfter(lastPlan.plusHours(1))) {
			planZoneTour(dc, person, work.startDate());
		}

		long start = System.currentTimeMillis();
		System.out.println("Start assigning parcels");
		
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
		
		long end = System.currentTimeMillis();
		System.out.println("Finished assigning parcels (" + parcels +"/" + capacity + " parcels;" + tourDuration + " min): took " + (end-start) + " ms");
				
		return assigned;
		
	}


	private boolean startsAfter1800(ActivityIfc work) {
		return work.startDate().isAfter(work.startDate().startOfDay().plusHours(18));
	}


	private boolean startsOnSunday(ActivityIfc work) {
		return work.startDate().weekDay().equals(DayOfWeek.SUNDAY);
	}
	
	
	private float duration(DistributionCenter dc, DeliveryPerson person, Time time, DeliveryEfficiencyProfile efficiency, Parcel next, Parcel prev) {
		float dur = 0.0f;
		
		if (prev == null) {
			dur += travelTime(person, dc.getZone(), next.getZone(), time);
			dur += efficiency.getDeliveryDurAdd();
			return dur;
		}
		
		if (!next.getLocation().equals(prev.getLocation())) {
			dur += travelTime(person, prev.getZone(), next.getZone(), time);
			dur += efficiency.getDeliveryDurAdd();
			return dur;
		}
		
		if (next.getDestinationType().equals(ParcelDestinationType.PACK_STATION) && prev.getDestinationType().equals(prev.getDestinationType())) {
			dur += efficiency.getDeliveryDurMul();
			
		} else if (next.getPerson().household().equals(prev.getPerson().household())) {
			dur += 0;
			
		} else {
			dur += efficiency.getDeliveryDurAdd();
		}
		
		
		return dur;
	}
	
	
	
	private float travelTime(DeliveryPerson person, Zone origin, Zone destination, Time time) {
		return person.options().impedance().getTravelTime(origin.getId(), destination.getId(), StandardMode.TRUCK, time);
	}
	
	private int selectCapacity(DeliveryPerson person) {
		double stdGauss = new Random((long) (person.getNextRandom() * Long.MAX_VALUE)).nextGaussian();
		double scaledGauss = CAPACITY_STD_DEV *stdGauss + MAX_CAPACITY;

		return Math.min(Math.max(MIN_CAPACITY, (int) round(scaledGauss)), MAX_CAPACITY);
	}
	
	
	
	
	private void planZoneTour(DistributionCenter dc, DeliveryPerson person, Time currentTime) {
		System.out.println("Plan giant tour for " + currentTime.toString());
		long time = System.currentTimeMillis();
		
		
		List<Parcel> toBeDelivered = dc.getAvailableParcels(currentTime);
		System.out.println("Plan for " + toBeDelivered.size() + " parcels. (" + toBeDelivered.size() + "current, " + toBeDelivered.stream().filter(p -> p.getPlannedArrivalDate().startOfDay().isBefore(currentTime.startOfDay())).count() + " old, "+ dc.getDelivered().size() + " delivered)");
			
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
		
		long end = System.currentTimeMillis();
		System.out.println("Creating the graph took " + (end-time) + " ms");

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
		
		long end2 = System.currentTimeMillis();
		System.out.println("Solving tsp took " + (end2-end) + " ms");
		System.out.println("Finished planning giant tour: took " + (end2-time) + " ms");
		
		this.parcelTour.clear();
		this.parcelTour.addAll(prefix);
		this.parcelTour.addAll(suffix);
		this.parcelTour = this.parcelTour.stream().distinct().collect(toList());
		System.out.println("Tour contains " + parcelTour.size() + " parcels.");

	}
	
	private List<Parcel> sortParcelsByDestination(List<Parcel> parcels) {
		return parcels.stream()
				 	  .sorted(Comparator.comparing(Parcel::getDestinationType)
				 			  			.thenComparing(p -> p.getPerson().household().getId().getOid())
				 			 )
				 	  .collect(toList());
	}
	
}
