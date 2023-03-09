package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
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
public class TspBasedDeliveryTourStrategy extends ClusterTourPlanningStrategy {

	private final ImpedanceIfc impedance;
	
	public TspBasedDeliveryTourStrategy(ImpedanceIfc impedance, DeliveryClusteringStrategy clusteringStrategy, DeliveryDurationModel durationModel) {
		super(clusteringStrategy, durationModel);
		this.impedance = impedance;
	}

	/**
	 * Returns the travel time form origin to destination.
	 *
	 * @param origin      the origin
	 * @param destination the destination
	 * @param time        the time
	 * @param mode        the mode
	 * @return the travel time
	 */
	protected float travelTime(Zone origin, Zone destination, Time time, Mode mode) {
		return impedance.getTravelTime(origin.getId(), destination.getId(), mode, time);
	}

	@Override
	public List<PlannedDeliveryTour> planTours(Collection<ParcelActivityBuilder> deliveries, DeliveryVehicle vehicle,
			Time currentTime, RelativeTime maxTourDuration) {
		
		Mode mode = vehicle.getType().getMode();
		int capacity = vehicle.getCapacity();
		
		List<PlannedDeliveryTour> plannedTours = new ArrayList<>();
		List<ParcelActivityBuilder> giantTour = planGiantTour(vehicle.getOwner(), deliveries, currentTime, mode);
		
		while (!giantTour.isEmpty()) {
			List<ParcelActivityBuilder> assigned = new ArrayList<>();
			Zone lastZone = vehicle.getOwner().getZone();
			Time time = currentTime;
			Time endOfTour = currentTime.plus(maxTourDuration);
		
			for (int i = 0; i < Math.min(capacity, giantTour.size()); i++) { //TODO check capacity computation/restriction
				ParcelActivityBuilder delivery = giantTour.get(i).by(vehicle);
		
				float tripDuration = travelTime(lastZone, delivery.getZone(), time, mode);		
				float deliveryDuration = delivery.withDuration(durationModel).getDeliveryMinutes();
				float returnTime = travelTime(delivery.getZone(), vehicle.getOwner().getZone(), time, mode);
				
				if (time.plusMinutes(round(tripDuration + deliveryDuration + returnTime)).isBeforeOrEqualTo(endOfTour)) {
					
					time = time.plusMinutes(round(tripDuration));
					assigned.add(delivery.plannedAt(time));
					time = time.plusMinutes(round(deliveryDuration));
					lastZone = delivery.getZone();
					
				} else {
					time = time.plusMinutes(round(returnTime));
					break;
				}
		
			}

			plannedTours.add(new PlannedDeliveryTour(vehicle.getType(), assigned, time.differenceTo(currentTime), currentTime, true));
			giantTour.removeAll(assigned);
		}

		return plannedTours;
	}

	/**
	 * Plan giant tour through all delivery locations using a TSP 2 approximation.
	 *
	 * @param distributionCenter the distribution center
	 * @param deliveries         the deliveries
	 * @param currentTime        the current {@link Time}
	 * @param mode               the mode
	 * @return an approximation of a giant tsp tour
	 */
	protected List<ParcelActivityBuilder> planGiantTour(DistributionCenter distributionCenter, Collection<ParcelActivityBuilder> deliveries,
			Time currentTime, Mode mode) {
		
		List<ParcelActivityBuilder> giantTour = new ArrayList<>();

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

		giantTour.clear();
		giantTour.addAll(prefix);
		giantTour.addAll(suffix);
		giantTour = giantTour.stream().distinct().collect(Collectors.toList());

		return giantTour;
	}

}
