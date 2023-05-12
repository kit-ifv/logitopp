package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import static edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize.EXTRA_LARGE;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChainStatistics;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class LogitTransportChainPreferenceModel implements TransportChainPreferenceModel {
	
	private final LogitParameters parameters;
	private final TransportChainStatistics stats;
	
	private final DeliveryResults results;
		
	public LogitTransportChainPreferenceModel(LogitParameters parameters,  TransportChainStatistics stats, DeliveryResults results) {
		this.parameters = parameters;
		this.stats = stats;
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
	
	@Override
	public Collection<TransportChain> filterChoiceSet(IParcel parcel, Collection<TransportChain> choiceSet, Time time) {
		return choiceSet.stream()
						.filter(chain -> canBeUsed(chain, parcel, time))
						.collect(toList());
	}
	
	private boolean canBeUsed(TransportChain chain, IParcel parcel, Time time) {
		return chain.canTransport(parcel) && !(isXL(parcel) && stats.usesBike(chain)) && stats.tramAvailableIfRequired(chain, time);
	}

	private boolean isXL(IParcel parcel) {
		return parcel.getShipmentSize().equals(EXTRA_LARGE);
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
		
		double cost = stats.estimateCost(chain, parcel, currentTime) / capacity;
		double time = stats.estimateDuration(chain, parcel, currentTime) / capacity;
		double dist = stats.estimateDistance(chain, parcel) / capacity;
		
		String mode = chain.last().getVehicleType().asString().toLowerCase();
				
		double utility = parameters.get("asc_last_"+mode) + parameters.get("b_cost_" + mode) * cost 
												+ parameters.get("b_time_" + mode) * time 
												+ parameters.get("b_dist_" + mode) * dist;
		
		return new UtilLog(utility, cost, utility, dist);
	}

	
	public int getVehicleCapacity(DistributionCenter hub) { //TODO fix vehicle capacity
		switch (hub.getVehicleType()) {
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
