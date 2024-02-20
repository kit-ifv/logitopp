package edu.kit.ifv.mobitopp.simulation.parcels.box;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivity;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelState;
import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

public abstract class ParcelBox implements IParcel, PlannedTour { //TODO console logging , later csv logging
	
	private static int OID_CNT = -1;
	
	@Getter	protected final int oId = OID_CNT--;
	@Getter protected final boolean isPickUp = false;
	@Getter protected final ShipmentSize shipmentSize = ShipmentSize.CONTAINER;
	@Getter protected RecipientType recipientType = RecipientType.DISTRIBUTION_CENTER;
		
	@Getter protected final DistributionCenter producer;
	@Getter protected final DistributionCenter consumer;
	
	@Getter	@Setter	protected Time plannedArrivalDate;
	@Getter	protected Time deliveryTime = Time.future;
	@Getter protected ParcelState state = ParcelState.UNDEFINED;
	
	protected final TimedTransportChain chain;
	protected final ImpedanceIfc impedance;
//	protected final TimeTable timeTable;
	
	public ParcelBox(TimedTransportChain chain, ImpedanceIfc impedance) {
		if (chain.size() <= 1) { throw new IllegalArgumentException("Cannot construct a NestedParcel stack for an emty/singleton transport chain"); }
		
		this.producer = chain.first();
		this.consumer = chain.tail().get(0);
		this.plannedArrivalDate = chain.getArrival(consumer);

		this.chain = chain;
		this.impedance = impedance;
//		this.timeTable = timeTable;
	}
	

	
	

	@Override
	public boolean tryPickup(Time currentTime, DeliveryVehicle vehicle) {
		throw new UnsupportedOperationException("NestedParcel deliveries between distribution centers are expected to be deliveries not pickups!");
	}


	@Override
	public void load(Time time, DeliveryVehicle vehicle) {
		if (!this.state.equals(ParcelState.UNDEFINED)) {
			throw new IllegalStateException("When loading a NestedParcel, the previous state is expected to be undefined but was " + state.name());
		}
		
		this.state = ParcelState.ONDELIVERY;
	}
	

	private ParcelActivity preparedStop;
	
	@Override
	public Time prepare(Time currentTime, DeliveryVehicle vehicle, ImpedanceIfc impedance) {
		if (preparedStop != null) {
			throw new IllegalArgumentException("Cannot prepare a planned tour that has already been prepared!");
		}
		
		Time departure = chain.getDeparture(producer);
		if (departure.isBefore(currentTime)) {
			departure = currentTime;
		}
		
		VehicleType veh = (isReturning() ? consumer : producer).getVehicleType();
		
		//TODO respect time table!
		preparedStop = new ParcelActivity(-1, consumer.getZoneAndLocation(), List.of(this), List.of(), vehicle, departure, -1, chain.getDuration(producer), 0);
		
		vehicle.getOwner().getResults().logLoadEvent(vehicle, currentTime, 1, 1, 0, vehicle.getOwner().getZoneAndLocation(), -1, -1, -1);
		
		Time returnTime = departure.plusMinutes(chain.getDuration(producer)).plusMinutes(chain.getDuration(producer));
		return returnTime;
	}

	@Override
	public List<ParcelActivity> getPreparedStops() {
		if (preparedStop == null) {
			throw new IllegalArgumentException("Planned tour has not yet been prepared for dispatch!");
		}
		
		return List.of(preparedStop);
	}


	@Override
	public void unload(Time time, DeliveryVehicle vehicle) {
		throw new UnsupportedOperationException("NestedParcel deliveries between distribution centers are expected to be always successful so no parcels should remain in the vehicle and need to be unloaded!");
	}
	
	@Override
	public int getDeliveryAttempts() {
		throw new UnsupportedOperationException("(Nested) Preplanned tour parcels should be expected to be always successful!");
	}


	@Override
	public void setProducer(ParcelAgent producer) {
		throw new UnsupportedOperationException("Producer cannot be changed in preplanned tour pacels");
	}

	@Override
	public void setConsumer(ParcelAgent producer) {
		throw new UnsupportedOperationException("Croducer cannot be changed in preplanned tour parcels");
	}

	@Override
	public Location getLocation() {
		return getZoneAndLocation().location();
	}

	@Override
	public Zone getZone() {
		return getZoneAndLocation().zone();
	}

	@Override
	public ZoneAndLocation getZoneAndLocation() {
		return consumer.getZoneAndLocation();
	}
	
	@Override
	public boolean isReplanningAllowed() {
		return false;
	}
	
	abstract public void addReturning(IParcel parcel);
	abstract public void addPickedUp(IParcel parcel);
	

	
	public static ParcelBox createDelivery(TimedTransportChain chain, PlannedDeliveryTour lastMileTour, ImpedanceIfc impedance) {
		
		if (chain.size() == 2) {
			return new TransportChainBox(chain, impedance, lastMileTour);
		} else {
			ParcelBox remainingTour = createDelivery(chain.getTimedTail(), lastMileTour, impedance);
			return new TransportChainBox(chain, impedance, remainingTour);
		}
		
	}
	
	public static ParcelBox createReturning(TimedTransportChain chain, Collection<IParcel> returning, Collection<IParcel> pickedUp, ImpedanceIfc impedance) {
		
		if (chain.size() == 2) {
			return new ReturningParcelBox(chain, impedance, returning, pickedUp);
		} else {
			ParcelBox remainingTour = createReturning(chain.getTimedTail(), returning, pickedUp, impedance);
			return new ReturningBoxChain(chain, impedance, remainingTour);
		}
		
	}

}
