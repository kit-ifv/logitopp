package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.distribution.CEPServiceProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.MarketShareProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcelBuilder;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class ActiveDeliveryPartnerSelector implements ParcelDemandModelStep<Business, BusinessParcelBuilder, CEPServiceProvider> {

	private final Function<Business, Collection<CEPServiceProvider>> partnerProvider;
	private final int meanCapacity;
	
	private ActiveDeliveryPartnerSelector(Function<Business, Collection<CEPServiceProvider>> partnerProvider,
										  int meanCapacity) {
		
		this.partnerProvider = partnerProvider;
		this.meanCapacity = meanCapacity;
	}
	
	public static ActiveDeliveryPartnerSelector forShipping(int meanCapacity, MarketShareProvider shareProvider) {
		return new ActiveDeliveryPartnerSelector(b -> b.getShippingPartners(), meanCapacity);
	}
	
	public static ActiveDeliveryPartnerSelector forDelivery(int meanCapacity, MarketShareProvider shareProvider) {
		return new ActiveDeliveryPartnerSelector(b -> b.getDeliveryPartners(), meanCapacity);
	}
	
	@Override
	public CEPServiceProvider select(BusinessParcelBuilder parcel, Collection<BusinessParcelBuilder> otherParcels,
			int numOfParcels, double randomNumber) {
		
		Collection<CEPServiceProvider> choiceSet = partnerProvider.apply(parcel.getAgent());
		
		Map<CEPServiceProvider, Integer> capacities = 
			choiceSet.stream()
				 	 .collect(toMap(Function.identity(), this::estimateRemainingCapacity));

		
		boolean allMaxed = capacities.values().stream().allMatch(v -> v <= 0);
		for (CEPServiceProvider dc : choiceSet) {
			capacities.computeIfPresent(dc, (w, prev) -> (prev > 0 || allMaxed) ? abs(prev) : 0);
		}

		DiscreteRandomVariable<CEPServiceProvider> randVar = new DiscreteRandomVariable<>(capacities); //TODO logit model, other parcels at same day
		CEPServiceProvider selectedPartner = randVar.realization(randomNumber);
		
		//TODO log
		
		return selectedPartner;
	}

	private int estimateRemainingCapacity(CEPServiceProvider cepsp) {
		return cepsp.getNumVehicles() * meanCapacity - cepsp.currentShippingDemand() - cepsp.currentDeliveryDemand();
	}
	
	@Override
	public boolean determinePreSimulation(BusinessParcelBuilder parcel, Collection<BusinessParcelBuilder> otherParcels,
			int numOfParcels, double randomNumber) {
		return false;
	}
	
	

}
