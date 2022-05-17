package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.util.stream.Collectors.toList;

import java.util.Collection;

import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.simulation.Household;

/**
 * The Class NeighborhoodRelationship provides a nearest neighbor query for {@link Household households}.
 */
public class NeighborhoodRelationship {
	
	/** The person loader. */
	private final PersonLoader personLoader;
	
	/** The max number of neighbors. */
	private final int maxNumberOfNeighbors;
	
	/** The max distance. */
	private final double maxDistance;
	
	/** The Constant NODE_CAPACITY. */
	private static final int NODE_CAPACITY = 10;
	
	/** The tree. */
	private KdTree<Household> tree;

	/**
	 * Instantiates a new neighborhood relationship.
	 *
	 * @param maxNumberOfNeighbors the maximum number of neighbors
	 * @param maxDistance the maximum distance between neighbors
	 * @param personLoader the person loader containing the households
	 */
	public NeighborhoodRelationship(int maxNumberOfNeighbors, double maxDistance, PersonLoader personLoader) {
		this.personLoader = personLoader;
		this.maxNumberOfNeighbors = maxNumberOfNeighbors;
		this.maxDistance = maxDistance;
	}
	
	/**
	 * Initiates the kD-tree holding the households.
	 */
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

	/**
	 * Gets the neighbors of the given household.
	 *
	 * @param household the household
	 * @param maxNumberOfNeighbors the maximum number of neighbors
	 * @param maxDistance the maximum distance to neighbors
	 * @return the neighbors of the given household
	 */
	public Collection<Household> getNeighborsOf(Household household, int maxNumberOfNeighbors, double maxDistance) {
		if (tree == null) {
			initTree();
		}
		return KNNSearch.findKNN(tree, household, maxNumberOfNeighbors, maxDistance);
	}
	
	/**
	 * Gets the neighbors of the given household using the default settings.
	 *
	 * @param household the household
	 * @return the neighbors of the given household
	 */
	public Collection<Household> getNeighborsOf(Household household) {
		return this.getNeighborsOf(household, maxNumberOfNeighbors, maxDistance);
	}
	
	/**
	 * Gets the neighbors of the given household.
	 *
	 * @param household the household
	 * @param maxNumberOfNeighbors the maximum number of neighbors
	 * @return the neighbors of the given household 
	 */
	public Collection<Household> getNeighborsOf(Household household, int maxNumberOfNeighbors) {
		return this.getNeighborsOf(household, maxNumberOfNeighbors, maxDistance);
	}
	
	/**
	 * Gets the neighbors of the given household.
	 *
	 * @param household the household
	 * @param maxDistance the maximum distance to neighbors
	 * @return the neighbors of the given household 
	 */
	public Collection<Household> getNeighborsOf(Household household, double maxDistance) {
		return this.getNeighborsOf(household, maxNumberOfNeighbors, maxDistance);
	}

}
