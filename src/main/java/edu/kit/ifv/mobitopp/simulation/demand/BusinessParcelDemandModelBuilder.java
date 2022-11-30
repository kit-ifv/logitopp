package edu.kit.ifv.mobitopp.simulation.demand;

import java.util.Collection;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcelBuilder;

/**
 * The Class BusinessParcelDemandModelBuilder is a
 * {@link ParcelDemandModelBuilder} for {@link BusinessParcel}s and
 * {@link Business} {@link ParcelAgent}s.
 */
public class BusinessParcelDemandModelBuilder extends ParcelDemandModelBuilder<Business, BusinessParcelBuilder> {

	/**
	 * Creates a new {@link BusinessParcelDemandModelBuilder builder} for business
	 * parcels using the businesses' random number generator and a
	 * {@link BusinessParcelBuilder} as factory.
	 *
	 * @param results the results
	 * @return the business parcel demand model builder
	 */
	public static BusinessParcelDemandModelBuilder forBusinessParcels(DeliveryResults results) {
		BusinessParcelDemandModelBuilder builder = new BusinessParcelDemandModelBuilder();

		builder.useRandom(b -> b::getNextRandom);
		builder.useParcelFactory(a -> new BusinessParcelBuilder(a, results));

		return builder;
	}

	/**
	 * Builds the default business parcel order model using a trivial zone filter
	 * (true).
	 *
	 * @param distributionCenters the distribution centers
	 * @param zoneRepo            the zone repo
	 * @param results             the results
	 * @return the parcel demand model
	 */
	public static ParcelDemandModel<Business, BusinessParcelBuilder> defaultBusinessParcelOrderModel(
			Collection<DistributionCenter> distributionCenters, ZoneRepository zoneRepo, DeliveryResults results) {
		return defaultBusinessParcelOrderModel(distributionCenters, z -> true, results);
	}

	/**
	 * Builds the default business parcel order model.
	 *
	 * @param distributionCenters the distribution centers
	 * @param zoneFilter          the zone filter
	 * @param results             the results
	 * @return the parcel demand model
	 */
	public static ParcelDemandModel<Business, BusinessParcelBuilder> defaultBusinessParcelOrderModel(
			Collection<DistributionCenter> distributionCenters, Predicate<Zone> zoneFilter, DeliveryResults results) {
		return forBusinessParcels(results).useRandomNumberSelector(0, 3, 0.01)
				.filterRecipients(o -> zoneFilter.test(o.location().zone()))
				.businessShareBasedDistributionCenterSelection(distributionCenters)
				// .equalServiceProviderSelection(List.of("Dummy Delivery Service"))
				.randomArrivalDaySelectionExcludeSunday().build();
	}

	/**
	 * Builds the null business parcel order model which generates no parcel demand.
	 *
	 * @param results the results
	 * @return the parcel demand model
	 */
	public static ParcelDemandModel<Business, BusinessParcelBuilder> nullBusinessParcelOrderModel(
			DeliveryResults results) {
		return forBusinessParcels(results).useNullNumberSelector().build();
	}

}
