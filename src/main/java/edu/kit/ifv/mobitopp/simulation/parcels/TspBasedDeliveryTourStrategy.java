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
 * The Class DummyDeliveryTourStrategy is an exemplary implementation of the DeliveryTourAssignmentStrategy interface.
 * To be replaced!
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
	 * @param efficiency the efficiency
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
		
		long start = System.currentTimeMillis();
		System.out.println("Start assigning parcels");
		
		Iterator<Parcel> it = parcelTour.stream().filter(p -> p.isUndefined()).iterator();
		
		ArrayList<Parcel> assigned = new ArrayList<Parcel>();
		if (!it.hasNext()) {return assigned;}
		
		
		Parcel prev = it.next();
		Parcel next = null;
		float tripDuration = 2 * travelTime(person, dc.getZone(), prev.getZone(), work.startDate()) + efficiency.getDeliveryDurAdd();
		int parcels = 1;
		assigned.add(prev);
		
		while (it.hasNext() && tripDuration < MAX_HOURS*60 && parcels < MAX_CAPACITY) {
			next = it.next();
			assigned.add(next);
			tripDuration += duration(person, work.startDate().plusMinutes((int)tripDuration), efficiency, next, prev);
			parcels++;
			prev = next;
		}
		
		long end = System.currentTimeMillis();
		System.out.println("Finished assigning parcels (" + parcels +"/" + MAX_CAPACITY + " parcels;" + tripDuration + " min): took " + (end-start) + " ms");
				
		return assigned;
		
	}
	
	
	private float duration(DeliveryPerson person, Time time, DeliveryEfficiencyProfile efficiency, Parcel next, Parcel prev) {
		float dur = 0.0f;
		
		if (!next.getLocation().equals(prev.getLocation())) {
			dur += travelTime(person, prev.getZone(), next.getZone(), time);
			dur += efficiency.getDeliveryDurAdd();
			return dur;
		}
		
		if (next.getDestinationType().equals(ParcelDestinationType.PACK_STATION) && prev.getDestinationType().equals(prev.getDestinationType())) {
			dur += efficiency.getDeliveryDurMul();
			
		} else if (next.getPerson().household().equals(prev.getPerson().household())) {
			dur += efficiency.getDeliveryDurMul();
			
		} else {
			dur += efficiency.getDeliveryDurAdd();
		}
		
		
		return dur;
	}
		
	private float travelTime(DeliveryPerson person, Zone origin, Zone destination, Time time) {
		return person.options().impedance().getTravelTime(origin.getId(), destination.getId(), StandardMode.CAR, time);
	}
	
	
	
	
	private void planGiantTour(DistributionCenter dc, Time plannedTime) {
		System.out.println("Plan giant tour for " + plannedTime.toString());
		long time = System.currentTimeMillis();
		
		
		List<Parcel> toBeDelivered = dc.getAvailableParcels(plannedTime);
		System.out.println("Plan for " + toBeDelivered.size() + " parcels. (" + toBeDelivered.size() + "current, " + toBeDelivered.stream().filter(p -> p.getPlannedArrivalDate().startOfDay().isBefore(plannedTime.startOfDay())).count() + " old, "+ dc.getDelivered().size() + " delivered)");
			
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
		
		long end = System.currentTimeMillis();
		System.out.println("Creating the graph took " + (end-time) + " ms");

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
		
		long end2 = System.currentTimeMillis();
		System.out.println("Solving tsp took " + (end2-end) + " ms");
		System.out.println("Finished planning giant tour: took " + (end2-time) + " ms");
		
		this.parcelTour.clear();
		this.parcelTour.addAll(prefix);
		this.parcelTour.addAll(suffix);
		this.parcelTour = this.parcelTour.stream().distinct().collect(Collectors.toList());
		System.out.println("Tour contains " + parcelTour.size() + " parcels.");
		
		
		
		this.nextPlan = nextPlan.plusDays(1);
	}
	
}
