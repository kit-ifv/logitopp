package edu.kit.ifv.mobitopp.simulation.parcels;

/**
 * The Enum ParcelState describes the delivery life cycle of a parcel: Undefined
 * - on delivery - returning - undefined. Delivered - delivered.
 */
public enum ParcelState {

	/**
	 * The state delivered describes a {@link IParcel} already being delivered.
	 */
	DELIVERED {

		@Override
		public ParcelState nextState() {
			return DELIVERED;
		}

	},

	/**
	 * The state undefined describes a {@link IParcel} returning to the
	 * {@link DistributionCenter} as it could ot be delivered.
	 */
	RETURNING {

		@Override
		public ParcelState nextState() {
			return UNDEFINED;
		}

	},

	/**
	 * The state on delivery describes a {@link IParcel} currently being on a
	 * delivery tour but prior to its own delivery.
	 */
	ONDELIVERY(RETURNING),

	/**
	 * The state undefined describes a {@link IParcel} being stored at a
	 * {@link DistributionCenter} or prior to its arrival in that
	 * {@link DistributionCenter}.
	 */
	UNDEFINED(ONDELIVERY);

	protected ParcelState nextState;

	/**
	 * Instantiates a new parcel state with no successor state.
	 * {@link ParcelState#nextState} has to be overridden.
	 */
	private ParcelState() {
		this.nextState = null;
	}

	/**
	 * Instantiates a new parcel state with the given successor state.
	 *
	 * @param nextState the next state
	 */
	private ParcelState(ParcelState nextState) {
		this.nextState = nextState;
	}

	/**
	 * Returns the next state.
	 *
	 * @return the parcel state
	 */
	public ParcelState nextState() {
		return this.nextState;
	}

}
