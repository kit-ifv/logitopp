package edu.kit.ifv.mobitopp.simulation.parcels.box;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivity;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.TransferTimeModel;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelState;
import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

public abstract class ParcelBox implements IParcel, PlannedTour { //TODO console logging , later csv logging

	@Getter protected final int id = PlannedDeliveryTour.tourIdCnt--;
	
	@Getter protected final boolean isPickUp = false;
	@Getter protected final ShipmentSize shipmentSize = ShipmentSize.CONTAINER;
	protected final TransferTimeModel transferTime;
	@Getter protected RecipientType recipientType = RecipientType.DISTRIBUTION_CENTER;
		
	@Getter protected final DistributionCenter producer;
	@Getter protected final DistributionCenter consumer;
	
	@Getter	@Setter	protected Time plannedArrivalDate;
	@Getter	protected Time deliveryTime = Time.future;
	@Getter protected ParcelState state = ParcelState.UNDEFINED;
	
	protected final TimedTransportChain chain;
	protected final ImpedanceIfc impedance;
//	protected final TimeTable timeTable;
	
	public ParcelBox(TimedTransportChain chain, ImpedanceIfc impedance, TransferTimeModel transferTime) {
		if (chain.size() <= 1) { throw new IllegalArgumentException("Cannot construct a NestedParcel stack for an emty/singleton transport chain"); }
		
		this.producer = chain.first();
		this.consumer = chain.tail().get(0);
		this.plannedArrivalDate = chain.getArrival(consumer);

		this.chain = chain;
		this.impedance = impedance;
		this.transferTime = transferTime;
//		this.timeTable = timeTable;
	}

	@Override
	public List<ParcelActivityBuilder> getPlannedStops() {
		return List.of();
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
		VehicleType nextVeh = (isReturning() ? producer : consumer).getVehicleType();
		
		//TODO respect time table!
		int duration = chain.getDuration(producer);
		float distance = impedance.getDistance(producer.getZone().getId(), consumer.getZone().getId());
		Time arrival = departure.plusMinutes(duration);
		int transfer = transferTime.estimateTransferTimeMinutes(null, veh, nextVeh, currentTime);
		preparedStop = new ParcelActivity(1, getId(), consumer.getZoneAndLocation(), List.of(this), List.of(), vehicle, arrival, distance, duration, transfer); //TODO transfer time model
		
		vehicle.getOwner().getResults().logLoadEvent(vehicle, currentTime, getId(), 1, 1, 0, vehicle.getOwner().getZoneAndLocation(), distance, duration, transfer);

        return departure.plusMinutes(duration).plusMinutes(duration);
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
	public DistributionCenter depot() {
		return chain.first();
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
	public double getVolume() {
		return 0;
	}

	@Override
	public boolean isReplanningAllowed() {
		return false;
	}
	
	abstract public void addReturning(IParcel parcel);
	abstract public void addPickedUp(IParcel parcel);
	

	
	public static ParcelBox createDelivery(TimedTransportChain chain, PlannedDeliveryTour lastMileTour, ImpedanceIfc impedance, TransferTimeModel transferTime) {
		
		if (chain.size() == 2) {
			return new TransportChainBox(chain, impedance, transferTime, lastMileTour);
		} else {
			ParcelBox remainingTour = createDelivery(chain.getTimedTail(), lastMileTour, impedance, transferTime);
			return new TransportChainBox(chain, impedance, transferTime, remainingTour);
		}
		
	}

	public static BoxOnBike createBoxOnBike(TimedTransportChain chain, Collection<IParcel> returning, Collection<IParcel> pickedUp, ImpedanceIfc impedance, TransferTimeModel transferTime, int boxId) {

		ParcelBox box = createReturning(chain, returning, pickedUp, impedance, transferTime, boxId);
		return new BoxOnBike(box);
	}
	
	public static ParcelBox createReturning(TimedTransportChain chain, Collection<IParcel> returning, Collection<IParcel> pickedUp, ImpedanceIfc impedance, TransferTimeModel transferTime, int boxId) {
		
		if (chain.size() == 2) {
			return new ReturningParcelBox(chain, impedance, transferTime, returning, pickedUp, boxId);
		} else {
			ParcelBox remainingTour = createReturning(chain.getTimedTail(), returning, pickedUp, impedance, transferTime, boxId);
			return new ReturningBoxChain(chain, impedance, transferTime, remainingTour);
		}
		
	}
	
	@Override
	public String toString() {
		return "Box[" + getId() + "] " + depot().getName() + "->" + nextHub().get().getName();
	}

	@Override
	public Optional<Connection> usedConnection() {

		DistributionCenter owner = chain.isDeliveryDirection() ? chain.first() : chain.tail().get(0);

		if (owner.getVehicleType().equals(VehicleType.TRAM)) {

			return chain.getConnections().stream()
					.filter(c -> c.getFrom().equals(chain.first()))
					.filter(c -> c.getTo().equals(chain.tail().get(0)))
					.findFirst(); //there must be a connection in chain if tram is used

		}

		return Optional.empty();

	}

	@Override
	public boolean usesTram() {
		return chain.getVehicleTypes().contains(VehicleType.TRAM);
	}
}
