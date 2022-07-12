package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class TspBasedDeliveryTourStrategy is an exemplary implementation of the
 * DeliveryTourAssignmentStrategy interface. When assigning parcels, a 'giant
 * tour' containing all parcels is planned. This is accomplished by solving a
 * TSP on all delivery zones. Within the zone, parcels are grouped by location
 * and destination type. A delivery person is assigned parcels in the computed
 * order, until their capacity (160 parcels) is reached, or the estimated
 * duration of the tour reaches 8h. The giant tour is recalculated every day.
 */
public class TspBasedDeliveryTourStrategy implements DeliveryTourAssignmentStrategy {

	private List<ParcelActivityBuilder> deliveryTour = new ArrayList<>();
	private Time nextPlan = Time.start;

	private static int MAX_CAPACITY = 160;
//	private static int MAX_HOURS = 8;
	private static boolean SKIP_SUNDAY = true;

	/**
	 * Assign parcels to the given delivery person based on the duration of the
	 * working activity and the time required per parcel.
	 *
	 * @param deliveries        the deliveries
	 * @param person            the person
	 * @param currentTime       the current time
	 * @param remainingWorkTime the remaining work time
	 * @return the collection of assigned deliveries
	 */
	@Override
	public List<ParcelActivityBuilder> assignParcels(Collection<ParcelActivityBuilder> deliveries,
			DeliveryPerson person, Time currentTime, RelativeTime remainingWorkTime) {

		if (SKIP_SUNDAY && currentTime.weekDay().equals(DayOfWeek.SUNDAY)
				|| currentTime.isAfter(currentTime.startOfDay().plusHours(18))) {
			return Arrays.asList();
		}

		if (currentTime.isAfter(nextPlan)) {
			planGiantTour(person.getDistributionCenter(), deliveries, currentTime);
		}

		ArrayList<ParcelActivityBuilder> assigned = new ArrayList<>();

		int capacity = MAX_CAPACITY;
		Zone lastZone = person.getDistributionCenter().getZone();
		Time time = currentTime;
		Time endOfWork = currentTime.plus(remainingWorkTime);

		for (int i = 0; i < Math.min(capacity, deliveryTour.size()); i++) {
			ParcelActivityBuilder delivery = deliveryTour.get(i);

			float tripDuration = travelTime(person, lastZone, delivery.getZone(), time);			
			delivery.withTripDuration(round(tripDuration));
			delivery.plannedAt(time.plusMinutes(round(tripDuration)));
			
			float deliveryDuration = delivery.estimateDuration();
			time = time.plusMinutes(round(tripDuration + deliveryDuration));

			float withReturn = travelTime(person, delivery.getZone(), person.getDistributionCenter().getZone(), time);
			if (time.plusMinutes(round(withReturn)).isBeforeOrEqualTo(endOfWork)) {
				assigned.add(delivery);
			} else {
				break;
			}

		}

		deliveryTour.removeAll(assigned);

		return assigned;

	}

	/**
	 * Returns the travel time form origin to destination.
	 *
	 * @param person      the person
	 * @param origin      the origin
	 * @param destination the destination
	 * @param time        the time
	 * @return the float
	 */
	private float travelTime(DeliveryPerson person, Zone origin, Zone destination, Time time) {
		return person.options().impedance().getTravelTime(origin.getId(), destination.getId(), StandardMode.CAR, time);
	}

	/**
	 * Plan giant tour through all delivery locations using a TSP 2 approximation.
	 * 
	 * @param distributionCenter
	 * @param currentTime
	 * @param deliveries
	 *
	 * @param dc                 the {@link DistributionCenter}
	 * @param person             the {@link DeliveryPerson}
	 * @param currentTime        the current {@link Time}
	 */
	private void planGiantTour(DistributionCenter distributionCenter, Collection<ParcelActivityBuilder> deliveries,
			Time currentTime) {

		SimpleWeightedGraph<Location, DefaultWeightedEdge> graph = new SimpleWeightedGraph<Location, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);

		Location start = distributionCenter.getLocation();
		graph.addVertex(start);

		// Create complete graph: for each parcel add the location as vertex, also add
		// edges to all existing vertices
		// Use 2d distance as edge weight
		for (ParcelActivityBuilder d : deliveries) {
			Location newLocation = d.getLocation();

			if (graph.vertexSet().contains(newLocation)) {
				continue;
			}

			graph.addVertex(newLocation);

			for (Location l : graph.vertexSet()) {
				if (l != newLocation) {
					DefaultWeightedEdge edge = graph.addEdge(newLocation, l);
					graph.setEdgeWeight(edge, newLocation.coordinatesP().distance(l.coordinatesP()));
				}
			}

		}

		// Use 2-approx algorithm for solving tsp
		TwoApproxMetricTSP<Location, DefaultWeightedEdge> tspAlg = new TwoApproxMetricTSP<>();
		GraphPath<Location, DefaultWeightedEdge> path = tspAlg.getTour(graph);

		List<ParcelActivityBuilder> prefix = new ArrayList<>();
		List<ParcelActivityBuilder> suffix = new ArrayList<>();
		boolean foundStart = false;

		for (Location l : path.getVertexList()) {
			if (l == start) {
				foundStart = true;

			}

			if (!foundStart) {
				suffix.addAll(deliveries.stream().filter(p -> p.getLocation().equals(l)).collect(Collectors.toList()));
			} else {
				prefix.addAll(deliveries.stream().filter(p -> p.getLocation().equals(l)).collect(Collectors.toList()));
			}

		}

		this.deliveryTour.clear();
		this.deliveryTour.addAll(prefix);
		this.deliveryTour.addAll(suffix);
		this.deliveryTour = this.deliveryTour.stream().distinct().collect(Collectors.toList());

		this.nextPlan = nextPlan.plusDays(1);
	}

}
