package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.util.stream.Collectors.toList;

import java.util.Collection;

import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.simulation.Household;

public class NeighborhoodRelationship {
	
	private final PersonLoader personLoader;
	private final int maxNumberOfNeighbors;
	private final double maxDistance;
	private static final int NODE_CAPACITY = 10;
	
	private KdTree<Household> tree;

	public NeighborhoodRelationship(int maxNumberOfNeighbors, double maxDistance, PersonLoader personLoader) {
		this.personLoader = personLoader;
		this.maxNumberOfNeighbors = maxNumberOfNeighbors;
		this.maxDistance = maxDistance;
	}
	
	private void initTree() {
		long start = System.nanoTime();
		this.tree = new KdTree<>(
							personLoader.households().collect(toList()),
							h -> h.homeLocation().coordinate,
							NODE_CAPACITY,
							new GeoDistanceMetric()
					);
		long stop = System.nanoTime();
		
		System.out.println("Building tree took " + (stop - start) + " ns");
	}

	public Collection<Household> getNeighborsOf(Household household, int maxNumberOfNeighbors, double maxDistance) {
		if (tree == null) {
			initTree();
		}
		return KNNSearch.findKNN(tree, household, maxNumberOfNeighbors, maxDistance);
	}
	
	public Collection<Household> getNeighborsOf(Household household) {
		return this.getNeighborsOf(household, maxNumberOfNeighbors, maxDistance);
	}
	
	public Collection<Household> getNeighborsOf(Household household, int maxNumberOfNeighbors) {
		return this.getNeighborsOf(household, maxNumberOfNeighbors, maxDistance);
	}
	
	public Collection<Household> getNeighborsOf(Household household, double maxDistance) {
		return this.getNeighborsOf(household, maxNumberOfNeighbors, maxDistance);
	}

}
