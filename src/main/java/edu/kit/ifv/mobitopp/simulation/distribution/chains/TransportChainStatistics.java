package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.BIKE;
import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.TRAM;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public class TransportChainStatistics {
	public final static int TRANSFER_TIME_MIN = 5;
	private final static double FUEL_COST_FACTOR = 1.42; // Verhältnis Kraftstoff- bzw. Energieverbrauch. HBEFA-Daten, Quelle angeben!
	private final static double LABOUR_COST = 28.99/60.0;  // Euro per hour normalized down to minutes
	
	private final ImpedanceIfc impedance;
	private final TimeTable timeTable;
	
	public TransportChainStatistics(ImpedanceIfc impedance, TimeTable timeTable) {
		this.impedance = impedance;
		this.timeTable = timeTable;
	}
	
	public double estimateDuration(TransportChain chain, IParcel parcel, Time currentTime) {
		double duration = 0;
		
		DistributionCenter origin = chain.first();
		for (DistributionCenter destination : chain.tail()) {
			
			duration += getTravelTime(origin, destination, currentTime);
			duration += TRANSFER_TIME_MIN;
			
			origin = destination;
		}
		
		
		duration += getTravelTime(origin, parcel.getZone(), currentTime); // TODO within tour factor
		
		return duration;
	}
	
	public double estimateDuration(TransportChain chain, Time currentTime) {
		double duration = 0;
		
		DistributionCenter origin = chain.first();
		for (DistributionCenter destination : chain.tail()) {
			
			duration += getTravelTime(origin, destination, currentTime);
			duration += TRANSFER_TIME_MIN;
			
			origin = destination;
		}
				
		return duration;
	}
	
	public double estimateDistance(TransportChain chain, IParcel parcel) {
		double distance = 0;
		
		DistributionCenter origin = chain.first();
		for (DistributionCenter destination : chain.tail()) {
			
			distance += getDistance(origin, destination);
			origin = destination;
		}
		
		
		distance += getDistance(origin, parcel.getZone()); // TODO within tour factor
		
		return distance;
	}
	
	public double estimateCost(TransportChain chain, IParcel parcel, Time currentTime) {
		double cost = 0;
		
		DistributionCenter origin = chain.first();
		for (DistributionCenter destination : chain.tail()) {
			
			cost += getCost(origin, destination, currentTime) * vehicleCostFator(origin);
			cost += (getTravelTime(origin, destination, currentTime) + TRANSFER_TIME_MIN) * timeCostFactor(origin);
			
			origin = destination;
		}
		
		
		cost += getCost(origin, parcel.getZone(), currentTime) * vehicleCostFator(origin); // TODO within tour factor
		cost += (getTravelTime(origin, parcel.getZone(), currentTime) + TRANSFER_TIME_MIN) * timeCostFactor(origin);
		
		return cost;
	}
	

	public float getTravelTime(DistributionCenter origin, DistributionCenter destination, Time currentTime) {
		VehicleType vehicleType = origin.getVehicleType();
		
		if (vehicleType.equals(TRAM)) {
			return timeTable.getNextDuration(origin, destination, currentTime);
		}
		
		return getTravelTime(origin, destination.getZone(), currentTime);
	}
	
	private float getTravelTime(DistributionCenter origin, Zone destination, Time currentTime) {		
		return impedance.getTravelTime(origin.getZone().getId(), destination.getId(),  origin.getVehicleType().getMode(), currentTime);
	}
	
	private float getDistance(DistributionCenter origin, DistributionCenter destination) {
		return getDistance(origin, destination.getZone());
	}
	
	private float getDistance(DistributionCenter origin, Zone destination) {
		return impedance.getDistance(origin.getZone().getId(), destination.getId());
	}
	
	private float getCost(DistributionCenter origin, DistributionCenter destination, Time currentTime) {
		return getCost(origin, destination.getZone(), currentTime);
	}
	
	private float getCost(DistributionCenter origin, Zone destination, Time currentTime) {
		return impedance.getTravelCost(origin.getZone().getId(), destination.getId(), origin.getVehicleType().getMode(), currentTime);
	}
	
	private double vehicleCostFator(DistributionCenter hub) {
		switch (hub.getVehicleType()) {
			case TRUCK:
				return FUEL_COST_FACTOR;
	
			case BIKE:
			case OTHER:
			case TRAM:
			default:
				return 1.0;

		}
	}
	
	private double timeCostFactor(DistributionCenter hub) {
		switch (hub.getVehicleType()) {
			case TRUCK:
			case BIKE:	
				return LABOUR_COST;

			case OTHER:
			case TRAM:
			default:
				return 0.0;

		}
	}
	
	public boolean usesBike(TransportChain chain) {
		return chain.getVehicleTypes().contains(BIKE);
	}
	
	public boolean tramAvailableIfRequired(TransportChain chain, Time time) {
		return chain.legsOfType(TRAM).stream().allMatch(p -> timeTable.hasNextConnection(p.getFirst(), p.getSecond(), time));
	}
	
	public Optional<Connection> nextReachableConnection(DistributionCenter from, DistributionCenter to, Time currentTime) {
		return timeTable.getNextConnection(from, to, currentTime);
	}

	
	public int getTransferTime(DistributionCenter origin, DistributionCenter destination) {
		return TRANSFER_TIME_MIN; //TODO transfer time depending on vehicle type??
	}
}
