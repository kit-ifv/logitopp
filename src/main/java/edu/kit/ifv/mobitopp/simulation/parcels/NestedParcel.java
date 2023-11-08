package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

public class NestedParcel implements IParcel { //TODO console logging , later csv logging
	
	private static int OID_CNT = -1;
	
	@Getter	protected final int oId = OID_CNT--;
	@Getter protected final boolean isPickUp = false;
	@Getter protected final ShipmentSize shipmentSize = ShipmentSize.CONTAINER;
	@Getter protected RecipientType recipientType = RecipientType.DISTRIBUTION_CENTER;
	
	protected final PlannedDeliveryTour preplannedTour;
	
	@Getter protected final DistributionCenter producer;
	@Getter protected final DistributionCenter consumer;
	
	@Getter	@Setter	protected Time plannedArrivalDate;
	@Getter	protected Time deliveryTime = Time.future;
	@Getter protected ParcelState state = ParcelState.UNDEFINED;
	
	public NestedParcel(DistributionCenter producer, TimedTransportChain remainingChain, PlannedDeliveryTour lastMileTour, ImpedanceIfc impedance) {
		if (remainingChain.size() == 0) { throw new IllegalArgumentException("Cannot construct a NestedParcel stack for an emty transport chain"); }
		
		this.producer = producer;
		this.consumer = remainingChain.first();
		this.plannedArrivalDate = remainingChain.getArrival(consumer);
		
		List<DistributionCenter> tail = remainingChain.tail();
		if (tail.isEmpty()) {
			this.preplannedTour = lastMileTour;
			
		} else {
			NestedParcel restOfChain = new NestedParcel(consumer, remainingChain.getTimedTail(), lastMileTour, impedance);
			ParcelActivityBuilder dropOff = new ParcelActivityBuilder(List.of(restOfChain), getZoneAndLocation()).withDuration(0);
			
			this.preplannedTour = new PlannedDeliveryTour(
					consumer.getVehicleType(), 
					List.of(dropOff),
					RelativeTime.ofMinutes(remainingChain.getDuration(consumer)),
					plannedArrivalDate, 
					false, 
					impedance
			);
		}
		
		
	}
	
	@Override
	public boolean tryDelivery(Time currentTime, DeliveryVehicle vehicle) {
		if (!this.state.equals(ParcelState.ONDELIVERY)) {
			throw new IllegalStateException("When trying delivery with a NestedParcel, the previous state is expected to be ondelivery but was " + state.name());
		}
		
		this.state = ParcelState.DELIVERED;
		
		
		consumer.addDelivered(this);
		consumer.getStorage().addPlannedTour(preplannedTour);
		

		return true;
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


	@Override
	public void unload(Time time, DeliveryVehicle vehicle) {
		throw new UnsupportedOperationException("NestedParcel deliveries between distribution centers are expected to be always successful so no parcels should remain in the vehicle and need to be unloaded!");
	}
	
	

	@Override
	public int getDeliveryAttempts() {
		throw new UnsupportedOperationException("Preplanned tour parcels should be expected to be always successful!");
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



}
