package edu.kit.ifv.mobitopp.simulation.demand;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.LatentModelStepWarpper;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ParcelDemandModelStep;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ShareBasedParcelDestinationSelector;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

public class PrivateParcelDemandModelBuilder extends ParcelDemandModelBuilder<PickUpParcelPerson, PrivateParcelBuilder> {
	
	
	public static PrivateParcelDemandModelBuilder forPrivateParcels(DeliveryResults results) {
		PrivateParcelDemandModelBuilder builder = new PrivateParcelDemandModelBuilder();
		
		builder.useRandom(p -> p::getNextRandom);
		builder.useParcelFactory(a -> new PrivateParcelBuilder(a, results));
		
		return builder;
	}
	
	public <T> PrivateParcelDemandModelBuilder addPrivateStep(ParcelDemandModelStep<PickUpParcelPerson, PrivateParcelBuilder, T> step, BiConsumer<PrivateParcelBuilder, ValueProvider<T>> propertySetter) {
		verifyAndInitialize();
		
		ParcelDemandModelStep<PickUpParcelPerson, PrivateParcelBuilder, T> nextStep;
		if (nextIsLatent) {
			nextStep = new LatentModelStepWarpper<>(step);
			
		} else {
			nextStep = step;
		}
		nextIsLatent = false;
		
		this.parcelOrderModel.add(nextStep, propertySetter);
		
		return this;
	}
	
	
	
	public PrivateParcelDemandModelBuilder customParcelDestinationSelection(ParcelDemandModelStep<PickUpParcelPerson, PrivateParcelBuilder, ParcelDestinationType> step) {
		return this.addPrivateStep(step, PrivateParcelBuilder::setDestinationType);
	}
	
	public PrivateParcelDemandModelBuilder equalParcelDestinationSelection(Predicate<Zone> workZoneFilter) {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector(workZoneFilter));
	}
	
	public PrivateParcelDemandModelBuilder equalParcelDestinationSelection() {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector());
	}

	public PrivateParcelDemandModelBuilder shareBasedParcelDestinationSelection(Map<ParcelDestinationType, Double> shares) {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector(shares));
	}
	
	public PrivateParcelDemandModelBuilder shareBasedParcelDestinationSelection(Map<ParcelDestinationType, Double> shares, Predicate<Zone> workZoneFilter) {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector(shares, workZoneFilter));
	}
	
	
	
	
	
	public static ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> defaultPrivateParcelModel(Collection<DistributionCenter> distributionCenters, DeliveryResults results) {
		return defaultPrivateParcelModel(distributionCenters, z -> true, results);
	}
	
	public static ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> defaultPrivateParcelModel(Collection<DistributionCenter> distributionCenters, Predicate<Zone> workZoneFilter, DeliveryResults results) {
		PrivateParcelDemandModelBuilder builder = forPrivateParcels(results);
		
		builder.useNormalDistributionNumberSelector(0.65, 0.5, 1, 10);
		
		return builder.equalParcelDestinationSelection(workZoneFilter)
					  .shareBasedDistributionCenterSelection(distributionCenters)
					  .useDistributionCenterAsProducer()
					  .useAgentAsConsumer()
					//.equalServiceProviderSelection(List.of("Dummy Delivery Service"))
					  .randomArrivalDaySelectionExcludeSunday()
					  .build();
	}
	
	public static ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> nullPrivateParcelModel(DeliveryResults results) {
		return forPrivateParcels(results)
					.useNullNumberSelector()
					.build();
	}
}
