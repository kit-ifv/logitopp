package edu.kit.ifv.mobitopp.simulation.demand;

import java.util.Collection;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcelBuilder;

public class BusinessParcelDemandModelBuilder extends ParcelDemandModelBuilder<Business, BusinessParcelBuilder> {
	
	
	
	public static BusinessParcelDemandModelBuilder forBusinessParcels(DeliveryResults results) {
		BusinessParcelDemandModelBuilder builder = new BusinessParcelDemandModelBuilder();

		builder.useRandom(b -> b::getNextRandom);
		builder.useParcelFactory(a -> new BusinessParcelBuilder(a, results));

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
