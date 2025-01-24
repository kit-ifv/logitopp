package edu.kit.ifv.mobitopp.simulation.demand;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ActiveDeliveryPartnerSelector;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.LatentModelStepWarpper;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ParcelDemandModelStep;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.demand.bundling.NoBundlingModel;
import edu.kit.ifv.mobitopp.simulation.distribution.CEPServiceProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.MarketShareProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcelBuilder;

/**
 * The Class BusinessParcelDemandModelBuilder is a
 * {@link ParcelDemandModelBuilder} for {@link BusinessParcel}s and
 * {@link Business} {@link ParcelAgent}s.
 */
public class BusinessParcelDemandModelBuilder extends ParcelDemandModelBuilder<Business, BusinessParcelBuilder> {

	/**
	 * Adds the business {@link ParcelDemandModelStep demand model step}.
	 *
	 * @param <T>            the generic type of the property
	 * @param step           the model step
	 * @param propertySetter the property setter
	 * @return the business parcel demand model builder
	 */
	public <T> BusinessParcelDemandModelBuilder addBusinessStep(
			ParcelDemandModelStep<Business, BusinessParcelBuilder, T> step,
			BiConsumer<BusinessParcelBuilder, ValueProvider<T>> propertySetter,
			Function<BusinessParcelBuilder, ValueProvider<T>> copyGetter
	) {
		verifyAndInitialize();

		if (copyGetter!= null) {
			step.setBundleCopy(copyGetter);
		}

		ParcelDemandModelStep<Business, BusinessParcelBuilder, T> nextStep;
		if (nextIsLatent) {
			nextStep = new LatentModelStepWarpper<>(step);

		} else {
			nextStep = step;
		}
		nextIsLatent = false;

		this.parcelOrderModel.add(nextStep, propertySetter);

		return this;
	}

	private Function<BusinessParcelBuilder, ValueProvider<CEPServiceProvider>> createCepspCopyGetter(boolean copyInBundle) {
		if (copyInBundle) {
			return BusinessParcelBuilder::getServiceProvider;
		} else {
			return null;
		}
	}

	/**
	 * Add the {@link ActiveDeliveryPartnerSelector} step for shipping.
	 *
	 * @param expectedCapacity the expected capacity per vehicle type
	 * @return the private parcel demand model builder
	 */
	public BusinessParcelDemandModelBuilder selectActiveShippingPartner(Function<VehicleType, Integer> expectedCapacity, boolean copyInBundle) {
		return this.addBusinessStep(ActiveDeliveryPartnerSelector.forShipping(expectedCapacity), BusinessParcelBuilder::setServiceProvider, createCepspCopyGetter(copyInBundle));
	}
	
	/**
	 * Add the {@link ActiveDeliveryPartnerSelector} step for delivery.
	 *
	 * @param expectedCapacity the expected capacity per vehicle type
	 * @return the private parcel demand model builder
	 */
	public BusinessParcelDemandModelBuilder selectActiveDeliveryPartner(Function<VehicleType, Integer> expectedCapacity, boolean copyInBundle) {
		return this.addBusinessStep(ActiveDeliveryPartnerSelector.forDelivery(expectedCapacity), BusinessParcelBuilder::setServiceProvider, createCepspCopyGetter(copyInBundle));
	}
	
	
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
		builder.useBundlingModel(new NoBundlingModel<>());

		return builder;
	}

	/**
	 * Builds the default business parcel order model using a trivial zone filter
	 * (true).
	 *
	 * @param shares the cepsp market shares
	 * @param zoneRepo            the zone repo
	 * @param results             the results
	 * @return the parcel demand model
	 */
	public static ParcelDemandModel<Business, BusinessParcelBuilder> defaultBusinessParcelOrderModel(
			Map<CEPServiceProvider, Double> shares, ZoneRepository zoneRepo, DeliveryResults results) {
		return defaultBusinessParcelOrderModel(shares, z -> true, results);
	}

	/**
	 * Builds the default business parcel order model.
	 *
	 * @param shares the cepsp market shares
	 * @param zoneFilter          the zone filter
	 * @param results             the results
	 * @return the parcel demand model
	 */
	public static ParcelDemandModel<Business, BusinessParcelBuilder> defaultBusinessParcelOrderModel(
			Map<CEPServiceProvider, Double> shares, Predicate<Zone> zoneFilter, DeliveryResults results) {

		return forBusinessParcels(results).useRandomNumberSelector(0, 3, 0.01)
				.filterRecipients(o -> zoneFilter.test(o.location().zone()))
				.shareBasedCepspSelection(shares, false)
				.distributionCenterSelectionInCepspByFleetsize(false)
				.equalShipmentSizeSelection(false)
				.selectVolumeBasedOnShipmentSize(false)
				.randomArrivalDaySelectionExcludeSunday(false).build();
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
