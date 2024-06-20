package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference;

import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.BIKE;
import static edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize.EXTRA_LARGE;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.CostFunction;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.parameter.LogitParameters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class LogitChainPreferenceModel implements PreferredChainModel {
	
	private final ImpedanceIfc impedance;
	private final CostFunction costFunction;
	
	private final LogitParameters parameters;
	private final DeliveryResults results;
	
	private static int choiceCnt = 0;
	
	public LogitChainPreferenceModel(ImpedanceIfc impedance, CostFunction costFunction, DeliveryResults results, LogitParameters parameters) {
		this.impedance = impedance;
		this.costFunction = costFunction;
		this.parameters = parameters;
		this.results = results;
	}

	@Override
	public TransportPreferences selectPreference(IParcel parcel, Collection<TimedTransportChain> choiceSet, double randomNumber, Time time) {
		int choiceId = choiceCnt++;
		
		Map<TimedTransportChain, UtilResults> utility = computeUtilities(parcel, choiceSet);
		
		Map<TimedTransportChain, Double> util = new LinkedHashMap<>();
		utility.forEach((c, ul) -> util.put(c, ul.getUtility()));
		
		Map<TimedTransportChain, Double> probabilities = new DefaultLogitModel<TimedTransportChain>().calculateProbabilities(util);

		long seed = Math.round(randomNumber * Long.MAX_VALUE);
		TransportPreferences transportPreferences = new TransportPreferences(choiceId, parcel, probabilities, seed);

		probabilities.forEach((c, p) ->
			logUtility(choiceId, c, parcel, utility.get(c), p, transportPreferences.getSelected().equals(c), time)
		);		

		return transportPreferences;
	}
	
	private Map<TimedTransportChain, UtilResults> computeUtilities(IParcel parcel, Collection<TimedTransportChain> choiceSet) {
		Map<TimedTransportChain, UtilResults> utility = new LinkedHashMap<>();
		
		for (TimedTransportChain chain : filterChoiceSet(parcel, choiceSet)) {
			utility.put(chain, computeUtility(chain, parcel));
		}
		
		return utility;
	}
	
	private UtilResults computeUtility(TimedTransportChain chain, IParcel parcel) {
		int capacity = getVehicleCapacity(chain.last());
		
		double lastMileCost = costFunction.estimateLastMileCost(chain, parcel, impedance);
		double lastMileTime = costFunction.estimateLastMileTime(chain, parcel, impedance);
		double lastMileDist = costFunction.estimateLastMileDistance(chain, parcel, impedance);
		
		double cost = (chain.getCost() + lastMileCost)/ capacity;
		double time = (chain.getTotalDuration() + lastMileTime) / capacity;
		double dist = (chain.getDistance() + lastMileDist) / capacity;
		
		String mode = chain.last().getVehicleType().asString().toLowerCase();

		double utility = parameters.get("asc_last_"+mode)
						+ parameters.get("b_cost_" + mode) * cost
						+ parameters.get("b_time_" + mode) * time
						+ parameters.get("b_dist_" + mode) * dist;

		return new UtilResults(utility, cost, time, dist, capacity);
	}
	
	public int getVehicleCapacity(DistributionCenter hub) { //TODO fix vehicle capacity or define mean capacity for estimation?
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
	private class UtilResults {
		double utility;
		double cost;
		double duration;
		double distance;
		int capacity;
	}
	

	@Override
	public Collection<TimedTransportChain> filterChoiceSet(IParcel parcel, Collection<TimedTransportChain> choiceSet) {
		return choiceSet.stream()
				.filter(chain -> canBeUsed(chain, parcel))
				.collect(toList());
	}
	
	private boolean canBeUsed(TransportChain chain, IParcel parcel) {
		return chain.canTransport(parcel)
			&& !(isXL(parcel)
			&& chain.uses(BIKE))
			&& (
					chain.last().getOrganization().equals("ALL")
				|| chain.last().getOrganization().equals(chain.first().getOrganization())
			);
	}

	private boolean isXL(IParcel parcel) {
		return parcel.getShipmentSize().equals(EXTRA_LARGE);
	}

	
	private void logUtility(int choiceId, TransportChain chain, IParcel parcel, UtilResults res, double probability, boolean selected, Time time) {
		results.logTransportChainPreference(choiceId, time, parcel, chain, probability, res.utility, res.cost, res.duration, res.distance, res.capacity, selected);
	}
}
