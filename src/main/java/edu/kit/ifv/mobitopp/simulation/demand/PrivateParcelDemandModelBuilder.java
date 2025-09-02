package edu.kit.ifv.mobitopp.simulation.demand;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.LatentModelStepWarpper;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ParcelDemandModelStep;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ShareBasedParcelDestinationSelector;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.demand.bundling.NoBundlingModel;
import edu.kit.ifv.mobitopp.simulation.distribution.CEPServiceProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

/**
 * The Class PrivateParcelDemandModelBuilder is a
 * {@link ParcelDemandModelBuilder} for {@link PrivateParcel}s and
 * {@link PickUpParcelPerson private} {@link ParcelAgent}s.
 */
public class PrivateParcelDemandModelBuilder
		extends ParcelDemandModelBuilder<PickUpParcelPerson, PrivateParcelBuilder> {

	/**
	 * Creates a new {@link PrivateParcelDemandModelBuilder builder} for private
	 * person parcels using the persons' random number generator and a
	 * {@link PrivateParcelBuilder} as factory.
	 *
	 * @param results the results
	 * @return the private parcel demand model builder
	 */
	public static PrivateParcelDemandModelBuilder forPrivateParcels(DeliveryResults results) {
		PrivateParcelDemandModelBuilder builder = new PrivateParcelDemandModelBuilder();

		builder.useRandom(p -> p::getNextRandom);
		builder.useParcelFactory(a -> new PrivateParcelBuilder(a, results));
		builder.useBundlingModel(new NoBundlingModel<>());

		return builder;
	}

	/**
	 * Adds the private {@link ParcelDemandModelStep demand model step}.
	 *
	 * @param <T>            the generic type of the property
	 * @param step           the model step
	 * @param propertySetter the property setter
	 * @return the private parcel demand model builder
	 */
	public <T> PrivateParcelDemandModelBuilder addPrivateStep(
			ParcelDemandModelStep<PickUpParcelPerson, PrivateParcelBuilder, T> step,
			BiConsumer<PrivateParcelBuilder, ValueProvider<T>> propertySetter,
			Function<PrivateParcelBuilder, ValueProvider<T>> copyGetter
	) {
		verifyAndInitialize();

		if (copyGetter!=null) {
			step.setBundleCopy(copyGetter);
		}

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

	private Function<PrivateParcelBuilder, ValueProvider<ParcelDestinationType>> createParcelDestinationTypeCopyGetter(boolean copyInBundle) {
		if (copyInBundle) {
			return PrivateParcelBuilder::getDestinationType;
		} else {
			return null;
		}
	}

	/**
	 * Add a custom {@link ParcelDestinationType parcel destination} selection model
	 * step.
	 *
	 * @param step the parcel destination model step
	 * @return the private parcel demand model builder
	 */
	public PrivateParcelDemandModelBuilder customParcelDestinationSelection(
			ParcelDemandModelStep<PickUpParcelPerson, PrivateParcelBuilder, ParcelDestinationType> step,
			boolean copyInBundle
	) {
		return this.addPrivateStep(step, PrivateParcelBuilder::setDestinationType, createParcelDestinationTypeCopyGetter(copyInBundle));
	}

	/**
	 * Add the equal {@link ParcelDestinationType parcel destination} selection
	 * model step with the given work-zone filter and equal shares of all
	 * {@link ParcelDestinationType destination types}.
	 *
	 * @param workZoneFilter the work zone filter, outside which WORK is not a valid
	 *                       option
	 * @return the private parcel demand model builder
	 */
	public PrivateParcelDemandModelBuilder equalParcelDestinationSelection(Predicate<Zone> workZoneFilter, boolean copyInBundle) {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector(workZoneFilter), copyInBundle);
	}

	/**
	 * Add the equal {@link ParcelDestinationType parcel destination} selection
	 * model step using equal shares of all {@link ParcelDestinationType destination
	 * types}.
	 *
	 * @return the private parcel demand model builder
	 */
	public PrivateParcelDemandModelBuilder equalParcelDestinationSelection(boolean copyInBundle) {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector(), copyInBundle);
	}

	/**
	 * Add the share based {@link ParcelDestinationType parcel destination}
	 * selection model step using the given shares.
	 *
	 * @param shares the shares of the {@link ParcelDestinationType destination
	 *               types}
	 * @return the private parcel demand model builder
	 */
	public PrivateParcelDemandModelBuilder shareBasedParcelDestinationSelection(
			Map<ParcelDestinationType, Double> shares,
			boolean copyInBundle
	) {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector(shares), copyInBundle);
	}

	/**
	 * Add the share based {@link ParcelDestinationType parcel destination}
	 * selection model step using the given shares and the given work-zone filter.
	 *
	 * @param shares         the shares of the {@link ParcelDestinationType destination
	 *               types}
	 * @param workZoneFilter the work zone filter, outside which WORK is not a valid
	 *                       option
	 * @return the private parcel demand model builder
	 */
	public PrivateParcelDemandModelBuilder shareBasedParcelDestinationSelection(
			Map<ParcelDestinationType, Double> shares,
			Predicate<Zone> workZoneFilter,
			boolean copyInBundle
	) {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector(shares, workZoneFilter), copyInBundle);
	}

	/**
	 * Builds the default private parcel model using a trivial zone filter
	 * (true).
	 * @param shares 			  the cep market shares 
	 * @param results             the results
	 *
	 * @return the parcel demand model
	 */
	public static ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> defaultPrivateParcelModel(
			Map<CEPServiceProvider, Double> shares, DeliveryResults results) {
		return defaultPrivateParcelModel(shares, z -> true, results);
	}

	/**
	 * Builds the default private parcel model.
	 * 
	 * @param shares              the cep market shares
	 * @param workZoneFilter      the work zone filter, outside which WORK-delivery
	 *                            is not a valid option
	 * @param results             the results
	 *
	 * @return the parcel demand model
	 */
	public static ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> defaultPrivateParcelModel(
			Map<CEPServiceProvider, Double> shares,
			Predicate<Zone> workZoneFilter, DeliveryResults results) {
		PrivateParcelDemandModelBuilder builder = forPrivateParcels(results);

		builder.useNormalDistributionNumberSelector(0.65, 0.5, 1, 10);

		return builder.equalParcelDestinationSelection(workZoneFilter, false)
				.shareBasedCepspSelection(shares, false)
				.distributionCenterSelectionInCepspByFleetsize(false).useDistributionCenterAsProducer()
//				.privateShareBasedDistributionCenterSelection(distributionCenters).useDistributionCenterAsProducer()
				.useAgentAsConsumer()
				.equalParcelSizeSelection(false)
				.selectVolumeBasedOnShipmentSize(false)
				// .equalServiceProviderSelection(List.of("Dummy Delivery Service"))
				.randomArrivalDaySelectionExcludeSunday(false).build();
	}

	/**
	 * Builds the null private parcel model which generates no parcel demand.
	 *
	 * @param results the results
	 * @return the parcel demand model
	 */
	public static ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> nullPrivateParcelModel(
			DeliveryResults results) {
		return forPrivateParcels(results).useNullNumberSelector().build();
	}
}
