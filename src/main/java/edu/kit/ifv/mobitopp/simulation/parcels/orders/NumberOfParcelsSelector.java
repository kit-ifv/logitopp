package edu.kit.ifv.mobitopp.simulation.parcels.orders;

/**
 * The Interface for NumberOfParcelsSelectors.
 * A model for selecting the number of parcels, a generic recipient will order during the simulation.
 */
public interface NumberOfParcelsSelector<R> {

	/**
	 * Selects the number of parcels, the given recipient orders.
	 *
	 * @param recipient the recipient
	 * @param randomNumber a random number
	 * @return the number of parcels
	 */
	public int select(R recipient, double randomNumber);
	
}
