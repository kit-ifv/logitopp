package edu.kit.ifv.mobitopp.simulation.demand;

/**
 * The Class DemandQuantity holds data about the estimated produced and consumed
 * parcel quantities of some entity.
 */
public class DemandQuantity {

	private int production;
	private int consumption;

	/**
	 * Gets the expected consumption quantity.
	 *
	 * @return the consumption quantity (number of parcels)
	 */
	public int getConsumption() {
		return consumption;
	}

	/**
	 * Sets the expected consumption quantity.
	 *
	 * @param consumption the new consumption quantity (number of parcels)
	 */
	public void setConsumption(int consumption) {
		this.consumption = consumption;
	}

	/**
	 * Adds the given amount to the current consumption quantity.
	 *
	 * @param increment the increment (number of parcels)
	 */
	public void addConsumption(int increment) {
		this.consumption += increment;
	}

	/**
	 * Gets the production quantity.
	 *
	 * @return the production quantity (number of parcels)
	 */
	public int getProduction() {
		return production;
	}

	/**
	 * Sets the production quantity.
	 *
	 * @param production the new production (number of parcels)
	 */
	public void setProduction(int production) {
		this.production = production;
	}

	/**
	 * Adds the given amount to the current production quantity.
	 *
	 * @param increment the increment (number of parcels)
	 */
	public void addProduction(int increment) {
		this.production += increment;
	}

}
