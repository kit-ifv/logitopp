package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
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
public class ZoneTspDeliveryTourStrategy extends TspBasedDeliveryTourStrategy {

	/**
	 * Instantiates a new {@link ZoneTspDeliveryTourStrategy}.
	 *
	 * @param impedance          the impedance
	 * @param clusteringStrategy a clustering strategy for clustering parcels to delivery stops
	 * @param durationModel      a model for estimating the duration of delivery activity
	 */
	public ZoneTspDeliveryTourStrategy(ImpedanceIfc impedance, DeliveryClusteringStrategy clusteringStrategy, DeliveryDurationModel durationModel) {
		super(impedance, clusteringStrategy, durationModel);
	}

	/**
	 * Plan giant tour through all delivery zones using a TSP 2 approximation.
	 *
	 * @param dc          the {@link DistributionCenter}
	 * @param deliveries  the planned delibvery/pickup activities
	 * @param currentTime the current {@link Time}
	 * @param mode        the mode
	 * @return tour of parcel activities through zones as ordered list
	 */
	@Override
	protected List<ParcelActivityBuilder> planGiantTour(DistributionCenter dc, Collection<ParcelActivityBuilder> deliveries, Time currentTime, Mode mode) {
		
		List<ParcelActivityBuilder> zoneTour = new ArrayList<>();

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
					double weight = travelTime(newZone, z, currentTime, mode)
							+ travelTime(z, newZone, currentTime, mode);
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

		zoneTour.addAll(prefix);
		zoneTour.addAll(suffix);
		zoneTour = zoneTour.stream().distinct().collect(toList());
		
		return zoneTour;
	}

}
