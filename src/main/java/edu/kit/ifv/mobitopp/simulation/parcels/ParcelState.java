package edu.kit.ifv.mobitopp.simulation.parcels;

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
	
	private ParcelState(ParcelState nextState) {
		this.nextState = nextState;
	}
	
	public ParcelState nextState() {
		return this.nextState;
	}
	
}
