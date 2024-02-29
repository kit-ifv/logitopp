package edu.kit.ifv.mobitopp.util.routing;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.reader.VisumNetworkReader;
import lombok.Getter;

public class TspSolver<E> {
	
	@Getter
	private final CachedTravelTime<E> travelTimes;
	
	public TspSolver(TravelTimeProvider<E> travelTime) {

		this.travelTimes = new CachedTravelTime<>(travelTime);
	}
	
	public static <E> TspSolver<E> createSolverUsingDijkstraTimes(SimulationContext context, Function<E, Location> embedding) {
		
		File visumFile = new File(context.configuration().getVisumFile());
		String carCode = context.configuration().getVisumToMobitopp().getCarTransportSystemCode();
		System.out.println(carCode);
		VisumNetwork visumNet = new VisumNetworkReader().readNetwork(visumFile, carCode);
		
		DijkstraSolver<E> solver = new DijkstraSolver<E>(visumNet, embedding);
		
		return new TspSolver<>(solver);
	}

	public Tour<E> findTour(Collection<E> elements) {
		if (elements.isEmpty()) {
			return new Tour<E>(List.of(), 0.0f, travelTimes);
		}
		
		
		SimpleWeightedGraph<E, DefaultWeightedEdge> graph 
			= new SimpleWeightedGraph<E, DefaultWeightedEdge>(DefaultWeightedEdge.class);


		// Create complete graph: for each parcel add the location as vertex, also add
		// edges to all existing vertices
		// Use 2d distance as edge weight
		for (E element : elements) {

			if (graph.vertexSet().contains(element)) {
				continue;
			}

			graph.addVertex(element);

			for (E other: graph.vertexSet()) {
				float weight = travelTimes.getTravelTime(element, other);
				
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
				
		return new Tour<E>(vertexList, (float) path.getWeight(), travelTimes);		
	}
	
}
