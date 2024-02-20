package edu.kit.ifv.mobitopp.simulation.parcels.box;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ReturningParcelBox extends ParcelBox {
	
	private final Collection<IParcel> returning;
	private final Collection<IParcel> pickedUp;

	public ReturningParcelBox(TimedTransportChain remainingChain, ImpedanceIfc impedance, Collection<IParcel> returning, Collection<IParcel> pickedUp) {
		super(remainingChain, impedance);
		this.returning = new ArrayList<>(returning);
		this.pickedUp = new ArrayList<>(pickedUp);
	}

	@Override
	public boolean tryDelivery(Time currentTime, DeliveryVehicle vehicle) {
		
		returning.forEach(consumer::addParcel);
		pickedUp.forEach(consumer::addDelivered);

		return true;
	}
	
	@Override
	public void addReturning(IParcel parcel) {
		this.returning.add(parcel);
	}
	
	@Override
	public void addPickedUp(IParcel parcel) {
		this.pickedUp.add(parcel);
	}

	@Override
	public boolean isReturning() {
		return true;
	}

	@Override
	public Collection<IParcel> getAllParcels() {
		List<IParcel> parcels = new ArrayList<>(returning);
		parcels.addAll(pickedUp);
		return parcels;
	}

	@Override
	public Collection<IParcel> getDeliveryParcels() {
		return returning;
	}

	@Override
	public Collection<IParcel> getPickUpRequests() {
		return pickedUp;
	}

	@Override
	public RelativeTime getPlannedDuration() {
		return RelativeTime.ofMinutes(chain.getDuration(producer));
	}

	@Override
	public Optional<DistributionCenter> nextHub() {
		return Optional.of(chain.last());
	}
	
}