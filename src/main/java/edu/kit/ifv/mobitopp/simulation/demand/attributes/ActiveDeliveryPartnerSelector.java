package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcelBuilder;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class ActiveDeliveryPartnerSelector implements ParcelDemandModelStep<Business, BusinessParcelBuilder, DistributionCenter> {

	private final Function<Business, Collection<DistributionCenter>> partnerProvider;
	private final int meanCapacity;
	
	private ActiveDeliveryPartnerSelector(Function<Business, Collection<DistributionCenter>> partnerProvider, int meanCapacity) {
		this.partnerProvider = partnerProvider;
		this.meanCapacity = meanCapacity;
	}
	
	public static ActiveDeliveryPartnerSelector forShipping(int meanCapacity) {
		return new ActiveDeliveryPartnerSelector(b -> b.getShippingPartners(), meanCapacity);
	}
	
	public static ActiveDeliveryPartnerSelector forDelivery(int meanCapacity) {
		return new ActiveDeliveryPartnerSelector(b -> b.getDeliveryPartners(), meanCapacity);
	}
	
	@Override
	public DistributionCenter select(BusinessParcelBuilder parcel, Collection<BusinessParcelBuilder> otherParcels,
			int numOfParcels, double randomNumber) {
		
		Collection<DistributionCenter> choiceSet = partnerProvider.apply(parcel.getAgent());
		
		Map<DistributionCenter, Integer> capacities = 
			choiceSet.stream()
				 	 .collect(toMap(Function.identity(), this::estimateRemainingCapacity));

		
		boolean allMaxed = capacities.values().stream().allMatch(v -> v <= 0);
		for (DistributionCenter dc : choiceSet) {
			capacities.computeIfPresent(dc, (w, prev) -> (prev > 0 || allMaxed) ? abs(prev) : 0);
		}

		DiscreteRandomVariable<DistributionCenter> randVar = new DiscreteRandomVariable<>(capacities); //TODO logit model, other parcels at same day
		DistributionCenter selectedPartner = randVar.realization(randomNumber);
		
		//TODO log
		
		return selectedPartner;
	}

	private int estimateRemainingCapacity(DistributionCenter dc) {
		return dc.getNumEmployees() * meanCapacity - dc.currentShippingDemand() - dc.currentDeliveryDemand();
	}
	
	@Override
	public boolean determinePreSimulation(BusinessParcelBuilder parcel, Collection<BusinessParcelBuilder> otherParcels,
			int numOfParcels, double randomNumber) {
		return false;
	}
	
	

}
