package edu.kit.ifv.mobitopp.util.routing;

import java.io.File;
import java.util.Collection;
import java.util.function.Function;

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
	private final CachedTravelTime<E> dijkstra;
	
	public TspSolver(SimulationContext context, Function<E, Location> embedding) {
		
		File visumFile = new File(context.configuration().getVisumFile());
		String carCode = context.configuration().getVisumToMobitopp().getCarTransportSystemCode();
		VisumNetwork visumNet = new VisumNetworkReader().readNetwork(visumFile, carCode);
		
		DijkstraSolver<E> solver = new DijkstraSolver<E>(visumNet, embedding);
		this.dijkstra = new CachedTravelTime<>(solver);
	}

	public Tour<E> findTour(Collection<E> elements) {
		
		
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
				float weight = dijkstra.getTravelTime(element, other);
				
				if (weight > 0) { //TODO allow 0 weight?
					DefaultWeightedEdge edge = graph.addEdge(element, other);
					graph.setEdgeWeight(edge, weight);
				}
				
			}

		}

		// Use 2-approx algorithm for solving tsp
		TwoApproxMetricTSP<E, DefaultWeightedEdge> tspAlg = new TwoApproxMetricTSP<>();
		GraphPath<E, DefaultWeightedEdge> path = tspAlg.getTour(graph);
		
		return new Tour<E>(path.getVertexList(), (float) path.getWeight(), dijkstra);		
	}
	
}
