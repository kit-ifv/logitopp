package edu.kit.ifv.mobitopp.simulation.parcels.box;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelState;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class TransportChainBox extends ParcelBox {
	
	protected final PlannedTour preplannedTour;

	public TransportChainBox(TimedTransportChain remainingChain, ImpedanceIfc impedance, PlannedTour remainingTour) {
		super(remainingChain, impedance);
		this.preplannedTour = remainingTour;
	}
	
	@Override
	public boolean tryDelivery(Time currentTime, DeliveryVehicle vehicle) {
		if (!this.state.equals(ParcelState.ONDELIVERY)) {
			throw new IllegalStateException("When trying delivery with a NestedParcel, the previous state is expected to be ondelivery but was " + state.name());
		}
		
		this.state = ParcelState.DELIVERED;

		consumer.getStorage().addPlannedTour(preplannedTour);
		
		System.out.println("Box was delivered to " + consumer + ": " + this);

		return true;
	}

	@Override
	public boolean isReturning() {
		return false;
	}

	@Override
	public Collection<IParcel> getDeliveryParcels() {
		return preplannedTour.getDeliveryParcels().stream().flatMap(p -> p.getContainedParcels().stream()).collect(toList());
	}

	@Override
	public Collection<IParcel> getPickUpRequests() {
		return preplannedTour.getPickUpRequests().stream().flatMap(p -> p.getContainedParcels().stream()).collect(toList());
	}
	
	@Override
	public Collection<IParcel> getAllParcels() {
		return getContainedParcels();
	}
	
	@Override
	public Collection<IParcel> getContainedParcels() {
		return preplannedTour.getAllParcels().stream().flatMap(p -> p.getContainedParcels().stream()).collect(toList());
	}	
	
	@Override
	public RelativeTime getPlannedDuration() {
		return preplannedTour.getPlannedDuration().plusMinutes(chain.getDuration(producer));
	}

	@Override
	public VehicleType getVehicleType() {
		return chain.firstMileVehicle();
	}

	@Override
	public void addReturning(IParcel parcel) {}

	@Override
	public void addPickedUp(IParcel parcel) {}

	@Override
	public Optional<DistributionCenter> nextHub() {
		return Optional.of(chain.tail().get(0));
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + preplannedTour + "]";
	}

	@Override
	public int getOId() {
		return preplannedTour.journeyId();
	}

	@Override
	public int journeyId() {
		return preplannedTour.journeyId();
	}
}
