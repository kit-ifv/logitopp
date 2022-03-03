package edu.kit.ifv.mobitopp.simulation.parcels.demand;

import java.util.Collection;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.business.Business;
import edu.kit.ifv.mobitopp.simulation.parcels.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.model.BusinessParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.model.ParcelBuilder;

public class BusinessParcelDemandModelBuilder extends ParcelDemandModelBuilder<Business, BusinessParcelBuilder> {
	
	
	
	public static BusinessParcelDemandModelBuilder forBusinessParcels(DeliveryResults results) {
		BusinessParcelDemandModelBuilder builder = new BusinessParcelDemandModelBuilder();

		builder.setRandomProvider(b -> b::getNextRandom);
		builder.setParcelFactory(a -> new BusinessParcelBuilder(a, results));

		return builder;
	}
	
	
	
	public static ParcelDemandModel<Business, BusinessParcelBuilder> defaultBusinessParcelOrderModel(Collection<DistributionCenter> distributionCenters, ZoneRepository zoneRepo, DeliveryResults results) {
		return defaultBusinessParcelOrderModel(distributionCenters, z -> true, results);
	}
	
	public static ParcelDemandModel<Business, BusinessParcelBuilder> defaultBusinessParcelOrderModel(Collection<DistributionCenter> distributionCenters, Predicate<Zone> zoneFilter, DeliveryResults results) {
		return forBusinessParcels(results)
					.useRandomNumberSelector(0, 3, 0.01)
					.filterRecipients(o -> zoneFilter.test(o.location().zone()) )
					.shareBasedDistributionCenterSelection(distributionCenters)
					//.equalServiceProviderSelection(List.of("Dummy Delivery Service"))
					.randomArrivalDaySelectionExcludeSunday()
					.build();
	}
	
	public static ParcelDemandModel<Business,BusinessParcelBuilder> nullBusinessParcelOrderModel(DeliveryResults results) {
		return forBusinessParcels(results)
					.useNullNumberSelector()
					.build();
	}

}
