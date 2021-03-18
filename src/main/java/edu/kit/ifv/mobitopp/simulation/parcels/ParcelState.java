package edu.kit.ifv.mobitopp.simulation.parcels;


/**
 * The Enum ParcelState describes the delivery life cycle of a parcel.
 * Undefined - on delivery - returning - undefined.
 * Delivered - delivered.
 */
public enum ParcelState {
	
	DELIVERED {
		@Override
		public ParcelState nextState() {
			return DELIVERED;
		}
	},
	
	RETURNING {
		@Override
		public ParcelState nextState() {
			return UNDEFINED;
		}
	}, 
	
	ONDELIVERY(RETURNING),
	
	UNDEFINED(ONDELIVERY);
	
	protected ParcelState nextState;
	
	private ParcelState() {
		this.nextState = null;
	}
	
	/**
	 * Instantiates a new parcel state
	 * with the given successor state.
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
