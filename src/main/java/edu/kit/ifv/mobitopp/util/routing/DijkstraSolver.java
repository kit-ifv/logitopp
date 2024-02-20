package edu.kit.ifv.mobitopp.util.routing;

import java.util.ArrayList;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.routing.DefaultPath;
import edu.kit.ifv.mobitopp.routing.Dijkstra;
import edu.kit.ifv.mobitopp.routing.GraphFromVisumNetwork;
import edu.kit.ifv.mobitopp.routing.Link;
import edu.kit.ifv.mobitopp.routing.Node;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.routing.util.PriorityQueueBasedPQ;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class DijkstraSolver<E> implements TravelTimeProvider<E> {
	
	private final GraphFromVisumNetwork graph;
	private final Dijkstra dijkstra = new Dijkstra(new PriorityQueueBasedPQ<>());
	private final Function<E, Location> embedding;


	public DijkstraSolver(VisumRoadNetwork network, Function<E, Location> embedding) {
		this.graph = new GraphFromVisumNetwork(network);
		this.embedding = embedding;
	}
	
	public Path selectRoute(E origin, E destination) {
		return selectRoute(embedding.apply(origin), embedding.apply(destination));
	}

	public Path selectRoute(Location origin, E destination) {
		return selectRoute(origin, embedding.apply(destination));
	}

	public Path selectRoute(E origin, Location destination) {
		return selectRoute(embedding.apply(origin), destination);
	}

	public Path selectRoute(Location origin, Location destination) {
	
		if (origin != destination) {
			
			Node source = getNode(origin);
			Node target = getNode(destination);
			
			return dijkstra.shortestPath(graph, source,target);
			
		} else {
			return DefaultPath.makePath(new ArrayList<Link>());
		}
	}


	private Node getNode(Location loc) {
		Link link = graph.links.get(""+loc.roadAccessEdgeId);
		return (loc.roadPosition > 0.5) ? link.to() : link.from();
	}
	
	

	@Override
	public float getTravelTime(E origin, E destination) {
		return selectRoute(origin, destination).travelTime();
	}

	@Override
	public float getTravelTime(ZoneAndLocation origin, E destination) {
		return selectRoute(origin.location(), destination).travelTime();
	}

	@Override
	public float getTravelTime(E origin, ZoneAndLocation destination) {
		return selectRoute(origin, destination.location()).travelTime();
	}

	@Override
	public float getTravelTime(ZoneAndLocation origin, ZoneAndLocation destination) {
		return selectRoute(origin.location(), destination.location()).travelTime();
	}

	@Override
	public boolean setMode(StandardMode mode) {
		return false;
	}

	@Override
	public boolean setTime(Time time) {
		return false;
	}

}
