package edu.kit.ifv.mobitopp.simulation.parcels.box;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

public class ReturningBoxChain extends TransportChainBox {
	
	private final ParcelBox remainingTour;

	public ReturningBoxChain(TimedTransportChain remainingChain, ImpedanceIfc impedance, ParcelBox remainingTour) {
		super(remainingChain, impedance, remainingTour);
		this.remainingTour = remainingTour;
	}
	
	@Override
	public boolean isReturning() {
		return true;
	}
	
	@Override
	public void addPickedUp(IParcel parcel) {
		this.remainingTour.addPickedUp(parcel);
	}
	
	@Override
	public void addReturning(IParcel parcel) {
		this.remainingTour.addReturning(parcel);
	}
	
	@Override
	public String toString() {
		return "ReturnTour" + super.toString();
	}

	@Override
	public int getOId() {
		return remainingTour.journeyId();
	}

	@Override
	public VehicleType getVehicleType() {
		return nextHub().get().getVehicleType();
	}
}
