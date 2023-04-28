package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.BIKE;
import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.TRAM;
import static edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize.EXTRA_LARGE;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.region.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class LogitTransportChainPreferenceModel implements TransportChainPreferenceModel {

	private final static int TRANSFER_TIME_MIN = 5;
	private final static double FUEL_COST_FACTOR = 1.42; // Verhältnis Kraftstoff- bzw. Energieverbrauch. HBEFA-Daten, Quelle angeben!
	private final static double LABOUR_COST = 28.99/60.0;  // Euro per hour normalized down to minutes
	
	private final LogitParameters parameters;
	private final ImpedanceIfc impedance;
	private final TimeTable timeTable;
	
	private final DeliveryResults results;
		
	public LogitTransportChainPreferenceModel(LogitParameters parameters, ImpedanceIfc impedance, TimeTable timeTable, DeliveryResults results) {
		this.parameters = parameters;
		this.impedance = impedance;
		this.timeTable = timeTable;
		this.results = results;
	}
	
	
	
	@Override
	public TransportChain selectPreference(IParcel parcel, Collection<TransportChain> choiceSet, Time currentTime, double randomNumber) {
		Map<TransportChain, UtilLog> utility = computeUtilities(parcel, choiceSet, currentTime);
		
		Map<TransportChain, Double> util = new LinkedHashMap<>();
		utility.forEach((c, ul) -> util.put(c, ul.getUtility()));
		Map<TransportChain, Double> probabilities = new DefaultLogitModel<TransportChain>().calculateProbabilities(util);

		DiscreteRandomVariable<TransportChain> distribution = new DiscreteRandomVariable<>(probabilities);
		TransportChain selected = distribution.realization(randomNumber);
		
		probabilities.forEach((c, p) -> 
			logUtility(c, parcel, currentTime, utility.get(c), p, String.valueOf(c.equals(selected)))
		);
		
		return selected;
	}

	@Override
	public Map<TransportChain, Double> computePreferences(IParcel parcel, Collection<TransportChain> choiceSet, Time currentTime) {//TODO C-Logit?
		Map<TransportChain, UtilLog> utility = computeUtilities(parcel, choiceSet, currentTime);
		
		Map<TransportChain, Double> util = new LinkedHashMap<>();
		utility.forEach((c, ul) -> util.put(c, ul.getUtility()));
		
		Map<TransportChain, Double> probabilities = new DefaultLogitModel<TransportChain>().calculateProbabilities(util);
		
		probabilities.forEach((c, p) -> 
			logUtility(c, parcel, currentTime, utility.get(c), p, "-")
		);
		
		return probabilities;
	}


	private Map<TransportChain, UtilLog> computeUtilities(IParcel parcel, Collection<TransportChain> choiceSet,
			Time currentTime) {
		Map<TransportChain, UtilLog> utility = new LinkedHashMap<>();
		
		for (TransportChain chain : filterChoiceSet(parcel, choiceSet, currentTime)) {
			utility.put(chain, computeUtility(chain, parcel, currentTime));
		}
		return utility;
	}
	
	private Collection<TransportChain> filterChoiceSet(IParcel parcel, Collection<TransportChain> choiceSet, Time time) {
		return choiceSet.stream()
						.filter(chain -> canBeUsed(chain, parcel, time))
						.collect(toList());
	}
	
	private boolean canBeUsed(TransportChain chain, IParcel parcel, Time time) {
		return chain.canTransport(parcel) && !(isXL(parcel) && usesBike(chain)) && tramAvailableIfRequired(chain, time);
	}

	private boolean isXL(IParcel parcel) {
		return parcel.getShipmentSize().equals(EXTRA_LARGE);
	}

	private boolean usesBike(TransportChain chain) {
		return chain.getVehicleTypes().contains(BIKE);
	}
	
	private boolean tramAvailableIfRequired(TransportChain chain, Time time) {
		return chain.legsOfType(TRAM).stream().allMatch(p -> timeTable.hasNextConnection(p.getFirst(), p.getSecond(), time));
	}
	
	
	
	private UtilLog computeUtility(TransportChain chain, IParcel parcel, Time currentTime) {		
//		x_costs_chain1 = 1 / capacity_van * (alpha_van_cos * costs_zones_van + cost_labour * time_zones_van)
//      x_time_chain1 = 1 / capacity_van * time_zones_van
//      x_distanz_chain1 = 1 / capacity_cargotram * dist_zones_van
		
		//TODO check bug: capacity_cargotram in x_distanz_chain1
		
//		asc_van + b_van_cost * x_costs_chain1 + b_van_time * x_time_chain1 + b_van_distance * x_distanz_chain1		
	

		
//		x_costs_chain2 = 1 / capacity_cargotram * (costs_zones_tram + cost_labour * (time_zones_bike_CH + loading_time))
//		x_time_chain2 = 1 / capacity_cargotram * (time_zones_tram + loading_time + time_zones_bike_CH)
//		x_distanz_chain2 = 1 / capacity_cargotram * (dist_zones_tram + dist_zones_bike_CH)
//
//		asc_cargotram + b_tram_cost * x_costs_chain2 + b_tram_time * x_time_chain2 + b_tram_distance * x_distanz_chain2

		
		
		
//		x_costs_chain3 = 1 / capacity_cargotram * (costs_zones_van_toTram + costs_zones_tram + cost_labour * (time_zones_van_toTram + time_zones_bike_CH + 2 * loading_time))
//		x_time_chain3 = 1 / capacity_cargotram * (time_zones_van_toTram + time_zones_tram + 2 * loading_time + time_zones_bike_CH)
//		x_distanz_chain3 = 1 / capacity_cargotram * (dist_zones_van_toTram + dist_zones_tram + time_zones_bike_CH) 
		
		//TODO check bug: time_zones_bike_CH in x_distanz_chain3

//		asc_cargotram + b_tram_cost * x_costs_chain3 + b_tram_time * x_time_chain3 + b_tram_distance * x_distanz_chain3
		
		int capacity = getVehicleCapacity(chain.last());
		
		double cost = estimateCost(chain, parcel, currentTime) / capacity;
		double time = estimateDuration(chain, parcel, currentTime) / capacity;
		double dist = estimateDistance(chain, parcel) / capacity;
		
		String mode = chain.last().getFleet().getVehicleType().asString().toLowerCase();
		
		System.out.println("asc_last_" +  mode);
		System.out.println("b_cost_" +  mode);
		System.out.println("b_time_" +  mode);
		System.out.println("b_dist_" +  mode);
		
		double utility = parameters.get("asc_last_"+mode) + parameters.get("b_cost_" + mode) * cost 
												+ parameters.get("b_time_" + mode) * time 
												+ parameters.get("b_dist_" + mode) * dist;
		
		return new UtilLog(utility, cost, utility, dist);
	}

	private double estimateDuration(TransportChain chain, IParcel parcel, Time currentTime) {
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
	
	private double estimateDistance(TransportChain chain, IParcel parcel) {
		double distance = 0;
		
		DistributionCenter origin = chain.first();
		for (DistributionCenter destination : chain.tail()) {
			
			distance += getDistance(origin, destination);
			origin = destination;
		}
		
		
		distance += getDistance(origin, parcel.getZone()); // TODO within tour factor
		
		return distance;
	}
	
	private double estimateCost(TransportChain chain, IParcel parcel, Time currentTime) {
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
	

	private float getTravelTime(DistributionCenter origin, DistributionCenter destination, Time currentTime) {
		VehicleType vehicleType = origin.getFleet().getVehicleType();
		
		if (vehicleType.equals(TRAM)) {
			return timeTable.getNextDuration(origin, destination, currentTime);
		}
		
		return getTravelTime(origin, destination.getZone(), currentTime);
	}
	
	private float getTravelTime(DistributionCenter origin, Zone destination, Time currentTime) {		
		return impedance.getTravelTime(origin.getZone().getId(), destination.getId(),  origin.getFleet().getVehicleType().getMode(), currentTime);
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
		return impedance.getTravelCost(origin.getZone().getId(), destination.getId(), origin.getFleet().getVehicleType().getMode(), currentTime);
	}
	
	private double vehicleCostFator(DistributionCenter hub) {
		switch (hub.getFleet().getVehicleType()) {
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
		switch (hub.getFleet().getVehicleType()) {
			case TRUCK:
			case BIKE:	
				return LABOUR_COST;

			case OTHER:
			case TRAM:
			default:
				return 0.0;

		}
	}
	
	private int getVehicleCapacity(DistributionCenter hub) { //TODO fix vehicle capacity
		switch (hub.getFleet().getVehicleType()) {
			case TRUCK:
				return 157;
	
			case BIKE:
				return 57;
				
			case OTHER:
			case TRAM:
			default:
				return 0;
	
		}
	}
	
	@Getter
	@Setter
	@AllArgsConstructor
	private class UtilLog {
		double utility;
		double cost;
		double duration;
		double distance;
	}
	
	private void logUtility(TransportChain chain, IParcel parcel, Time time, UtilLog util, double probability, String selected) {
		results.logTransportChainPreference(parcel, chain, selected, probability, util.utility, util.cost, util.duration, util.distance, time);
	}
	
}
