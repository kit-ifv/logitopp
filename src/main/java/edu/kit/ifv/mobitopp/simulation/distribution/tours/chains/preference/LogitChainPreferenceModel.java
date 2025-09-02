package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference;

import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.BIKE;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelSize.EXTRA_LARGE;
import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.*;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.CostFunction;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;
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

	private final double cost_norm_min;
	private final double cost_norm_max;
	private final double dist_norm_min;
	private final double dist_norm_max;
	private final double time_norm_min;
	private final double time_norm_max;

	private final double b_cost;
	private final double b_time;
	private final double b_dist;
	private final double b_parcel_size;
	private final double b_transfers_more_than_one;
	private final double b_environmental_impact;
	private final double b_business_recipient_tram;

	public LogitChainPreferenceModel(ImpedanceIfc impedance, CostFunction costFunction, DeliveryResults results, LogitParameters parameters) {
		this.impedance = impedance;
		this.costFunction = costFunction;
		this.parameters = parameters;
		this.results = results;

		cost_norm_min = parameters.get("cost_norm_min");
		cost_norm_max = parameters.get("cost_norm_max");
		dist_norm_min = parameters.get("dist_norm_min");
		dist_norm_max = parameters.get("dist_norm_max");
		time_norm_min = parameters.get("time_norm_min");
		time_norm_max = parameters.get("time_norm_max");

		b_cost = parameters.get("b_cost");
		b_time = parameters.get("b_time");
		b_dist = parameters.get("b_dist");
		b_parcel_size = parameters.get("b_parcel_size");
		b_transfers_more_than_one = parameters.get("b_transfers_more_than_one");
		b_environmental_impact = parameters.get("b_environmental_impact");
		b_business_recipient_tram = parameters.get("b_business_recipient_tram");

		System.out.println("Log model parameters of LogitChainPreferenceModel:");
		System.out.println(parameters);
		System.out.println("cost_norm_min:" + cost_norm_min);
		System.out.println("cost_norm_max:" + cost_norm_max);
		System.out.println("dist_norm_min:" + dist_norm_min);
		System.out.println("dist_norm_max:" + dist_norm_max);
		System.out.println("time_norm_min:" + time_norm_min);
		System.out.println("time_norm_max:" + time_norm_max);
		System.out.println("asc_truck:" + parameters.get("asc_truck"));
		System.out.println("asc_truck_tram_bike:" + parameters.get("asc_truck_tram_bike"));
		System.out.println("asc_truck_bike:" + parameters.get("asc_truck_bike"));
		System.out.println("b_cost:" + b_cost);
		System.out.println("b_time:" + b_time);
		System.out.println("b_dist:" + b_dist);
		System.out.println("b_parcel_size:" + b_parcel_size);
		System.out.println("b_transfers_more_than_one:" + b_transfers_more_than_one);
		System.out.println("b_environmental_impact:" + b_environmental_impact);
		System.out.println("b_business_recipient_tram:" + b_business_recipient_tram);
	}

	@Override
	public TransportPreferences selectPreference(IParcel parcel, Collection<TimedTransportChain> choiceSet, double randomNumber, Time time) {
		int choiceId = choiceCnt++;
		
		Map<TimedTransportChain, UtilResults> utility = computeUtilities(parcel, choiceSet);
		
		Map<TimedTransportChain, Double> util = new LinkedHashMap<>();
		utility.forEach((c, ul) -> util.put(c, ul.getUtility()));
		
		Map<TimedTransportChain, Double> probabilities = new DefaultLogitModel<TimedTransportChain>().calculateProbabilities(util);

		long seed = Math.round(randomNumber * Long.MAX_VALUE);
		TransportPreferences transportPreferences = new TransportPreferenceProbabilities(choiceId, parcel, probabilities, seed);

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
		
		String modes = getModeCombinationString(chain);

		double cost_normalized = (cost-cost_norm_min)/(cost_norm_max-cost_norm_min);
		double time_normalized = (time-time_norm_min)/(time_norm_max-time_norm_min);
		double dist_normalized = (dist-dist_norm_min)/(dist_norm_max-dist_norm_min);

		double uses_tram = chain.uses(TRAM) ? 1.0 : 0.0;
		double parcel_size_factor = getParcelFactor(parcel);
		double transfers_more_than_one = (chain.size() > 2) ? 1.0 : 0.0;
		double environmental_impact_truck = (chain.lastMileVehicle() == TRUCK) ? 1.0 : 0.0;
		double is_business_agent = (parcel instanceof BusinessParcel) ? 1.0 : 0.0;


		double utility =  parameters.get("asc_" + modes)
						+ b_cost * cost_normalized
						+ b_time * time_normalized
						+ b_dist * dist_normalized
						+ b_parcel_size * uses_tram * parcel_size_factor
						+ b_transfers_more_than_one * transfers_more_than_one
						+ b_environmental_impact * environmental_impact_truck
						+ b_business_recipient_tram * is_business_agent * uses_tram;

		return new UtilResults(utility, cost_normalized, time_normalized, dist_normalized, capacity);
	}

	private String getModeCombinationString(TimedTransportChain chain) {
		return chain.getVehicleTypes().stream().map(t -> t.name().toLowerCase()).collect(Collectors.joining("_"));
	}

	private double getParcelFactor(IParcel parcel) {
		switch (parcel.getShipmentSize()) {
            case SMALL:
                return 0.0;
            case MEDIUM:
                return 0.5;
            case LARGE:
                return 1.0;
            case EXTRA_LARGE:
            case PALLET:
            case CONTAINER:
			default:
                return 999.0;
        }
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
			&& !(isXL(parcel) && chain.uses(BIKE))
			&& (
				chain.last().getOrganization().equals("ALL")
				|| chain.last().getOrganization().equals(chain.first().getOrganization())
			);
	}

	private boolean isXL(IParcel parcel) {
		return parcel.getParcelSize().equals(EXTRA_LARGE);
	}

	
	private void logUtility(int choiceId, TransportChain chain, IParcel parcel, UtilResults res, double probability, boolean selected, Time time) {
		results.logTransportChainPreference(choiceId, time, parcel, chain, probability, res.utility, res.cost, res.duration, res.distance, res.capacity, selected);
	}
}
