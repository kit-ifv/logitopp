package edu.kit.ifv.mobitopp.util.routing.tsp;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.util.routing.*;
import edu.kit.ifv.mobitopp.util.routing.tsp.TspSolver;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.reader.VisumNetworkReader;
import lombok.Getter;

public class TspSolver2Approx<E> implements TspSolver<E> {
	
	@Getter
	private final ModeTravelTimes<E> travelTimes;
	
	public TspSolver2Approx(TravelTimeProvider<E> travelTime) {

		this.travelTimes = new ModeTravelTimes<>(
				() -> new CachedTravelTime<>(travelTime)
		);
	}
	
	public static <E> TspSolver2Approx<E> createSolverUsingDijkstraTimes(SimulationContext context, Function<E, Location> embedding) {
		
		File visumFile = new File(context.configuration().getVisumFile());
		String carCode = context.configuration().getVisumToMobitopp().getCarTransportSystemCode();
		System.out.println(carCode);
		VisumNetwork visumNet = new VisumNetworkReader().readNetwork(visumFile, carCode);
		
		DijkstraSolver<E> solver = new DijkstraSolver<E>(visumNet, embedding);
		
		return new TspSolver2Approx<>(solver);
	}

	public Tour<E> findTour(Collection<E> elements, StandardMode mode) {
		if (elements.isEmpty()) {
			return new Tour<>(List.of(), 0.0f, travelTimes, mode);
		}
		
		
		SimpleWeightedGraph<E, DefaultWeightedEdge> graph 
			= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);


		// Create complete graph: for each parcel add the location as vertex, also add
		// edges to all existing vertices
		// Use 2d distance as edge weight
		for (E element : elements) {

			if (graph.vertexSet().contains(element)) {
				continue;
			}

			graph.addVertex(element);

			for (E other: graph.vertexSet()) {
				float weight = travelTimes.getTravelTime(mode, element, other);
				
				if (weight > 0 && !other.equals(element)) { //TODO allow 0 weight?
					DefaultWeightedEdge edge = graph.addEdge(element, other);
					graph.setEdgeWeight(edge, weight);
				}
				
			}

		}

		// Use 2-approx algorithm for solving tsp
		TwoApproxMetricTSP<E, DefaultWeightedEdge> tspAlg = new TwoApproxMetricTSP<>();
		GraphPath<E, DefaultWeightedEdge> path = tspAlg.getTour(graph);
			
		List<E> vertexList = path.getVertexList().stream().distinct().collect(Collectors.toList());
				
		return new Tour<>(vertexList, (float) path.getWeight(), travelTimes, mode);
	}
	
}
