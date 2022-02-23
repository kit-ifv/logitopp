package edu.kit.ifv.mobitopp.simulation.parcels.tours;

import static java.lang.Math.floor;
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
import edu.kit.ifv.mobitopp.simulation.activityschedule.DeliveryActivityBuilder;
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

	private List<DeliveryActivityBuilder> parcelTour;
	private Time lastPlan;

	private final int meanCapacity;
	private final double capacityStdDev;
	private final int minCapacity;
	private final int maxCapacity;
	private final boolean skipSunday;
	private final Mode mode;

	/**
	 * Instantiates a new {@link ZoneTspDeliveryTourStrategy} with the given
	 * oprtions.
	 *
	 * @param meanCapacity   the mean capacity
	 * @param capacityStdDev the capacity standard deviation
	 * @param minCapacity    the minimum capacity
	 * @param maxCapacity    the maximum capacity
	 * @param skipSunday     whether sunday should be skipped
	 * @param mode           the delivery mode
	 */
	public ZoneTspDeliveryTourStrategy(int meanCapacity, double capacityStdDev, int minCapacity, int maxCapacity,
			boolean skipSunday, Mode mode) {

		this.meanCapacity = meanCapacity;
		this.capacityStdDev = capacityStdDev;
		this.minCapacity = minCapacity;
		this.maxCapacity = maxCapacity;
		this.skipSunday = skipSunday;
		this.mode = mode;

		this.lastPlan = Time.start;
		this.parcelTour = new ArrayList<>();
	}

	/**
	 * Instantiates a default {@link ZoneTspDeliveryTourStrategy} with: <br>
	 * a mean capacity of 160 <br>
	 * a capacity standard deviation of 16 <br>
	 * a minimum capacity of 100 <br>
	 * a maximum capacity of 200 <br>
	 * no deliveries on sunday <br>
	 * and the mode truck.
	 */
	public ZoneTspDeliveryTourStrategy() {
		this(160, 16, 100, 200, true, StandardMode.TRUCK);
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
	public List<DeliveryActivityBuilder> assignParcels(Collection<DeliveryActivityBuilder> deliveries,
			DeliveryPerson person, Time currentTime, RelativeTime remainingWorkTime) {

		if (skipSunday && isSunday(currentTime) || startsAfter1800(currentTime)) {
			return Arrays.asList();
		}

		if (currentTime.isAfter(lastPlan.plusHours(1))) {
			planZoneTour(person.getDistributionCenter(), person, deliveries, currentTime);
		}

		ArrayList<DeliveryActivityBuilder> assigned = new ArrayList<>();

		int capacity = selectCapacity(person);
		Zone lastZone = person.getDistributionCenter().getZone();
		Time time = currentTime;
		Time endOfWork = currentTime.plus(remainingWorkTime);

		for (int i = 0; i < Math.min(capacity, parcelTour.size()); i++) {
			DeliveryActivityBuilder delivery = parcelTour.get(i);
			
			float tripDuration = travelTime(person, lastZone, delivery.getZone(), time);			
			delivery.withTripDuration(round(tripDuration));
			delivery.plannedAt(time.plusMinutes(round(tripDuration)));
			
			float deliveryDuration = delivery.estimateDuration(person.getEfficiency());
			time = time.plusMinutes(round(tripDuration + deliveryDuration));

			float withReturn = travelTime(person, delivery.getZone(), person.getDistributionCenter().getZone(), time);
			
			if (time.plusMinutes(round(withReturn)).isBeforeOrEqualTo(endOfWork)) {
				assigned.add(delivery);
			} else {
				break;
			}
			
			lastZone = delivery.getZone();

		}

		parcelTour.removeAll(assigned);

		return assigned;

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
	private float travelTime(DeliveryPerson person, Zone origin, Zone destination, Time time) {
		return person.options().impedance().getTravelTime(origin.getId(), destination.getId(), mode, time);
	}

	/**
	 * Selects a capacity from a normal distribution.
	 *
	 * @param person the person
	 * @return the capacity
	 */
	private int selectCapacity(DeliveryPerson person) {
		double stdGauss = new Random((long) (person.getNextRandom() * Long.MAX_VALUE)).nextGaussian();
		double scaledGauss = capacityStdDev * stdGauss + meanCapacity;

		return Math.min(Math.max(minCapacity, (int) round(scaledGauss)), maxCapacity);
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
			Collection<DeliveryActivityBuilder> deliveries, Time currentTime) {

		SimpleWeightedGraph<Zone, DefaultWeightedEdge> graph = new SimpleWeightedGraph<Zone, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		Zone start = dc.getZone();
		graph.addVertex(start);

		// Create complete graph: for each parcel add the zone as vertex, also add edges
		// to all existing vertices (in both directions)
		// Use current travel time as edge weight
		for (Zone newZone : deliveries.stream().map(DeliveryActivityBuilder::getZone).distinct().collect(toList())) {

			if (graph.vertexSet().contains(newZone)) {
				continue;
			}

			graph.addVertex(newZone);

			for (Zone z : graph.vertexSet()) {
				if (z != newZone) {
					DefaultWeightedEdge edgeTo = graph.addEdge(newZone, z);
					double weight = travelTime(person, newZone, z, currentTime)
							+ travelTime(person, z, newZone, currentTime);
					weight /= 2.0;
					graph.setEdgeWeight(edgeTo, weight);
				}
			}

		}

		// Use 2-approx algorithm for solving tsp
		TwoApproxMetricTSP<Zone, DefaultWeightedEdge> tspAlg = new TwoApproxMetricTSP<>();
		GraphPath<Zone, DefaultWeightedEdge> path = tspAlg.getTour(graph);

		List<DeliveryActivityBuilder> prefix = new ArrayList<>();
		List<DeliveryActivityBuilder> suffix = new ArrayList<>();
		boolean foundStart = false;

		for (Zone z : path.getVertexList()) {
			if (z == start) {
				foundStart = true;
			}

			List<DeliveryActivityBuilder> deliveriesInZone = deliveries.stream().filter(p -> p.getZone().equals(z))
					.collect(toList());

			if (!foundStart) {
				for (DeliveryActivityBuilder d : deliveriesInZone) {
					suffix.add(d);
				}

			} else {
				for (DeliveryActivityBuilder d : deliveriesInZone) {
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
