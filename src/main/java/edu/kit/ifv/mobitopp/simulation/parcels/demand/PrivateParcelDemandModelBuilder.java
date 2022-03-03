package edu.kit.ifv.mobitopp.simulation.parcels.demand;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes.ParcelDemandModelStep;
import edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes.ShareBasedParcelDestinationSelector;
import edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

public class PrivateParcelDemandModelBuilder extends ParcelDemandModelBuilder<PickUpParcelPerson, PrivateParcelBuilder> {
	
	
	public static PrivateParcelDemandModelBuilder forPrivateParcels(DeliveryResults results) {
		PrivateParcelDemandModelBuilder builder = new PrivateParcelDemandModelBuilder();
		
		builder.setRandomProvider(p -> p::getNextRandom);
		builder.setParcelFactory(a -> new PrivateParcelBuilder(a, results));
		
		return builder;
	}
	
	public <T> PrivateParcelDemandModelBuilder addPrivateStep(ParcelDemandModelStep<PickUpParcelPerson, PrivateParcelBuilder, T> step, BiConsumer<PrivateParcelBuilder, ValueProvider<T>> propertySetter) {
		verifyAndInitialize();
		
		this.parcelOrderModel.add(step, propertySetter);
		
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
		
		builder.useNormalDistributionNumberSelector(0.65, 0.5, 10);
		builder.equalParcelDestinationSelection(workZoneFilter);
		
		return builder.shareBasedDistributionCenterSelection(distributionCenters)
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
