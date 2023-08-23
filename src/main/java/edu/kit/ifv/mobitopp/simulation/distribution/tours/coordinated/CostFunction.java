package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public class CostFunction { //TODO abstract class
	private final static double FUEL_COST_FACTOR = 1.42; // Verhältnis Kraftstoff- bzw. Energieverbrauch. HBEFA-Daten, Quelle angeben!
	private final static double LABOUR_COST = 28.99/60.0;  // Euro per hour normalized down to minutes
	
	
	private final ImpedanceIfc impedance;
	
	public CostFunction(ImpedanceIfc impedance) {
		this.impedance = impedance;
	}
	
	public double estimateCost(DistributionCenter origin, DistributionCenter destination,
			Time time, VehicleType vehicle, double travelTime, double transferTime, double distance) {
		
		return estimateCost(origin, destination.getZone(), time, vehicle, travelTime, transferTime, distance);
	}
	
	public double estimateLastMileCost(TimedTransportChain chain, IParcel parcel, ImpedanceIfc impedance) {
		Time lastMileDeparture = chain.getLastMileDeparture();
		ZoneId originZoneId = chain.last().getZone().getId();
		ZoneId parceZoneId = parcel.getZone().getId();
		
		return estimateCost(
				chain.last(), 
				parcel.getZone(), 
				lastMileDeparture, 
				chain.lastMileVehicle(), 
				impedance.getTravelTime(
						originZoneId, 
						parceZoneId, 
						chain.lastMileVehicle().getMode(), 
						lastMileDeparture
				), 
				0, //average deliver time??
				impedance.getDistance(
						originZoneId, 
						parceZoneId
				)
		);
	}
	
	private double estimateCost(DistributionCenter origin, Zone zone,
				Time time, VehicleType vehicle, double travelTime, double transferTime, double distance) {
		
		double cost = getCost(origin, zone, time) * vehicleCostFator(origin);
		cost += (transferTime + transferTime) * timeCostFactor(origin);
		
		return cost;
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
}
