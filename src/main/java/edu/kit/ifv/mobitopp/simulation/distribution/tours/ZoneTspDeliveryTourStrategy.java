package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import static java.lang.Math.round;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class ZoneTspDeliveryTourStrategy is a DeliveryTourAssignmentStrategy.
 * When assigning parcels, a 'giant tour' containing all parcels is planned.
 * This is accomplished by solving a (2-approximation) TSP on all delivery
 * locations. Within the zone, parcels are grouped by destination type and
 * household. A delivery person is assigned parcels in the computed order, until
 * their capacity is reached, or the estimated duration of the tour reaches 8h.
 * The giant tour is recalculated every hour with the current impedance to
 * consider the new trip durations. The delivery persons capacity is drawn from
 * a normal distribution.
 */
public class ZoneTspDeliveryTourStrategy implements DeliveryTourAssignmentStrategy {

	private List<ParcelActivityBuilder> parcelTour;
	private Time lastPlan;

	private final boolean skipSunday;

	/**
	 * Instantiates a new {@link ZoneTspDeliveryTourStrategy} with the given
	 * oprtions.
	 *
	 * @param skipSunday     whether sunday should be skipped
	 */
	public ZoneTspDeliveryTourStrategy(boolean skipSunday) {

		this.skipSunday = skipSunday;

		this.lastPlan = Time.start;
		this.parcelTour = new ArrayList<>();
	}

	/**
	 * Instantiates a default {@link ZoneTspDeliveryTourStrategy} with no deliveries
	 * on sunday and the mode truck.
	 */
	public ZoneTspDeliveryTourStrategy() {
		this(true);
	}

	/**
	 * Assign parcels to the given delivery person.
	 *
	 * @param deliveries        the deliveries
	 * @param person            the person
	 * @param currentTime       the current time
	 * @param remainingWorkTime the remaining work time
	 * @return the list of assigned parcels
	 */
	@Override
	public List<ParcelActivityBuilder> assignParcels(Collection<ParcelActivityBuilder> deliveries,
			DeliveryPerson person, Time currentTime, RelativeTime remainingWorkTime, VehicleType vehicle) {

		if (skipSunday && isSunday(currentTime) || startsAfter1800(currentTime)) {
			return Arrays.asList();
		}

		if (currentTime.isAfter(lastPlan.plusHours(1))) {
			lastPlan = currentTime.startOfDay().plusHours(currentTime.getHour());
			planZoneTour(person.getDistributionCenter(), person, deliveries, currentTime, vehicle.getMode());
		}

		ArrayList<ParcelActivityBuilder> assigned = new ArrayList<>();

		int capacity = vehicle.getVolume();
		int volume = 0;
		Zone lastZone = person.getDistributionCenter().getZone();
		Time time = currentTime;
		Time endOfWork = currentTime.plus(remainingWorkTime);

		for (int i = 0; i < parcelTour.size() && volume <= capacity; i++) {
			ParcelActivityBuilder delivery = parcelTour.get(i);

			float tripDuration = travelTime(person, lastZone, delivery.getZone(), time, vehicle.getMode());
			delivery.withTripDuration(round(tripDuration));
			delivery.plannedAt(time.plusMinutes(round(tripDuration)));

			float deliveryDuration = delivery.estimateDuration();
			time = time.plusMinutes(round(tripDuration + deliveryDuration));

			float withReturnDur = travelTime(person, delivery.getZone(), person.getDistributionCenter().getZone(), time,
					vehicle.getMode());
			
			int deliveryVolume = getVolume(delivery);
			volume += deliveryVolume;
			
			if (withinWorkhours(time, endOfWork, withReturnDur) && volume <= capacity) {
				assigned.add(delivery);

			} else {
				break;
			}

			lastZone = delivery.getZone();
		}
		
		System.out.println("Tour size:" + assigned.size());
		System.out.println("Num pcls:" + assigned.stream().mapToInt(d -> d.getParcels().size()).sum());
		System.out.println("Volume: " + assigned.stream().mapToInt(d -> d.volume()).sum());
		System.out.println("Time:" + time.differenceTo(currentTime));

		parcelTour.removeAll(assigned);

		return assigned;

	}

	private boolean withinWorkhours(Time time, Time endOfWork, float withReturn) {
		return time.plusMinutes(round(withReturn)).isBeforeOrEqualTo(endOfWork);
	}
	
	private int getVolume(ParcelActivityBuilder delivery) {
		return delivery.getParcels().stream().mapToInt(p -> p.getShipmentSize().getVolume(p)).sum();
	}

	/**
	 * Checks if the given work activity starts after 1800.
	 *
	 * @param currentTime the work
	 * @return true, if successful
	 */
	private boolean startsAfter1800(Time currentTime) {
		return currentTime.isAfter(currentTime.startOfDay().plusHours(18));
	}

	/**
	 * Checks if the given work activity starts on sunday.
	 *
	 * @param work the work
	 * @return true, if successful
	 */
	private boolean isSunday(Time currentTime) {
		return currentTime.weekDay().equals(DayOfWeek.SUNDAY);
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
	private float travelTime(DeliveryPerson person, Zone origin, Zone destination, Time time, Mode mode) {
		return person.options().impedance().getTravelTime(origin.getId(), destination.getId(), mode, time);
	}

	/**
	 * Plan giant tour through all delivery zones using a TSP 2 approximation.
	 *
	 * @param dc          the {@link DistributionCenter}
	 * @param person
	 * @param deliveries  the {@link DeliveryPerson}
	 * @param currentTime the current {@link Time}
	 */
	private void planZoneTour(DistributionCenter dc, DeliveryPerson person,
			Collection<ParcelActivityBuilder> deliveries, Time currentTime, Mode mode) {

		SimpleWeightedGraph<Zone, DefaultWeightedEdge> graph = new SimpleWeightedGraph<Zone, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		Zone start = dc.getZone();
		graph.addVertex(start);

		// Create complete graph: for each parcel add the zone as vertex, also add edges
		// to all existing vertices (in both directions)
		// Use current travel time as edge weight
		for (Zone newZone : deliveries.stream().map(ParcelActivityBuilder::getZone).distinct().collect(toList())) {

			if (graph.vertexSet().contains(newZone)) {
				continue;
			}

			graph.addVertex(newZone);

			for (Zone z : graph.vertexSet()) {
				if (z != newZone) {
					DefaultWeightedEdge edgeTo = graph.addEdge(newZone, z);
					double weight = travelTime(person, newZone, z, currentTime, mode)
							+ travelTime(person, z, newZone, currentTime, mode);
					weight /= 2.0;
					graph.setEdgeWeight(edgeTo, weight);
				}
			}

		}

		// Use 2-approx algorithm for solving tsp
		TwoApproxMetricTSP<Zone, DefaultWeightedEdge> tspAlg = new TwoApproxMetricTSP<>();
		GraphPath<Zone, DefaultWeightedEdge> path = tspAlg.getTour(graph);

		List<ParcelActivityBuilder> prefix = new ArrayList<>();
		List<ParcelActivityBuilder> suffix = new ArrayList<>();
		boolean foundStart = false;

		for (Zone z : path.getVertexList()) {
			if (z == start) {
				foundStart = true;
			}

			List<ParcelActivityBuilder> deliveriesInZone = deliveries.stream().filter(p -> p.getZone().equals(z))
					.collect(toList());

			if (!foundStart) {
				for (ParcelActivityBuilder d : deliveriesInZone) {
					suffix.add(d);
				}

			} else {
				for (ParcelActivityBuilder d : deliveriesInZone) {
					prefix.add(d);
				}
			}

		}

		this.parcelTour.clear();
		this.parcelTour.addAll(prefix);
		this.parcelTour.addAll(suffix);
		this.parcelTour = this.parcelTour.stream().distinct().collect(toList());

	}

}
