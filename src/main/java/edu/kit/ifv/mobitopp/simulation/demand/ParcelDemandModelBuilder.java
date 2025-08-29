package edu.kit.ifv.mobitopp.simulation.demand;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.*;
import edu.kit.ifv.mobitopp.simulation.demand.bundling.ParcelBundlingModel;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.FilteredNumberOfParcelsSelector;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.NormalDistributedNumberOfParcelsSelector;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.NullNumerOfParcelsSelector;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.ParcelQuantityModel;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.RandomNumberOfParcelsSelector;
import edu.kit.ifv.mobitopp.simulation.distribution.CEPServiceProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.MarketShareProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelSize;
import edu.kit.ifv.mobitopp.time.Time;

public class ParcelDemandModelBuilder<A extends ParcelAgent, P extends ParcelBuilder<A>> {

	private ParcelQuantityModel<A> numberOfParcelsSelector;
	private ParcelBundlingModel<A> bundlingModel;
	private Function<A, DoubleSupplier> randomProvider;
	private Function<A, P> parcelFactory;
	
	protected GenericParcelDemandModel<A, P> parcelOrderModel;
	protected boolean nextIsLatent = false;

	public ParcelDemandModelBuilder<A,P> useRandom(Function<A, DoubleSupplier> randomProvider) {
		this.randomProvider = randomProvider;		
		return this;
	}
	
	public ParcelDemandModelBuilder<A,P> useParcelFactory(Function<A, P> parcelFactory) {
		this.parcelFactory = parcelFactory;		
		return this;
	}
	
	
	
	public ParcelDemandModelBuilder<A,P> useNumberSelector(ParcelQuantityModel<A> numberOfParcelsSelector) {
		this.numberOfParcelsSelector = numberOfParcelsSelector;		
		return this;
	}
	
	public ParcelDemandModelBuilder<A,P> useNullNumberSelector() {
		this.numberOfParcelsSelector = new NullNumerOfParcelsSelector<>();		
		return this;
	}
	
	public ParcelDemandModelBuilder<A,P> useRandomNumberSelector(int min, int max, double percent) {
		this.numberOfParcelsSelector = new RandomNumberOfParcelsSelector<>(min, max, percent);
		return this;
	}
	
	public ParcelDemandModelBuilder<A,P> useNormalDistributionNumberSelector(double mean, double stdDev, int capMin, int capMax) {
		this.numberOfParcelsSelector = new NormalDistributedNumberOfParcelsSelector<>(mean, stdDev, capMin, capMax);
		return this;
	}
	
	public ParcelDemandModelBuilder<A,P> useNormalDistributionNumberSelector(double mean, double stdDev, int capMax) {
		this.numberOfParcelsSelector = new NormalDistributedNumberOfParcelsSelector<>(mean, stdDev, capMax);
		return this;
	}

	public ParcelDemandModelBuilder<A,P> useBundlingModel(ParcelBundlingModel<A> bundlingModel) {
		this.bundlingModel = bundlingModel;
		return this;
	}
	
	public ParcelDemandModelBuilder<A,P> filterRecipients(Predicate<A> filter) {
		if (this.numberOfParcelsSelector == null) {
			throw new IllegalStateException("A ParcelQuantityModel has to be selected, before filtering recipients.");
		}
		
		this.numberOfParcelsSelector = new FilteredNumberOfParcelsSelector<>(this.numberOfParcelsSelector, filter);
		return this;
	}
	
	
	
	protected void verifyAndInitialize() {
		if (parcelOrderModel != null) {
			return;
		}
		
		if (numberOfParcelsSelector==null) {
			throw new IllegalStateException("A ParcelQuantityModel should be selected before adding ParcelDemandModelStep.");
		}
		if (bundlingModel==null) {
			throw new IllegalStateException("A ParcelBundlingModel should be selected before adding ParcelDemandModelStep.");
		}
		if (randomProvider==null) {
			throw new IllegalStateException("A randomProvider (Function<R, DoubleSupplier>) should be selected before adding ParcelDemandModelStep.");
		}
		if (parcelFactory==null) {
			throw new IllegalStateException("A parcelFactory (Function<ParcelBuilder, R>) should be selected before adding ParcelDemandModelStep.");
		}
		
		this.parcelOrderModel = new GenericParcelDemandModel<>(numberOfParcelsSelector, bundlingModel, randomProvider, parcelFactory);
	}
	
	
	
	public ParcelDemandModelBuilder<A,P> asLatent() {
		this.nextIsLatent = true;
		return this;
	}

	public <T> ParcelDemandModelBuilder<A,P> addStep(
			ParcelDemandModelStep<A, P, T> step,
			BiConsumer<P, ValueProvider<T>> propertySetter
	) {
		return this.addStep(step, propertySetter, null);
	}

	public <T> ParcelDemandModelBuilder<A,P> addStep(
			ParcelDemandModelStep<A, P, T> step,
			BiConsumer<P, ValueProvider<T>> propertySetter,
			Function<P, ValueProvider<T>> copyGetter
	) {
		verifyAndInitialize();

		if (copyGetter!=null) {
			step.setBundleCopy(copyGetter);
		}
		
		ParcelDemandModelStep<A, P, T> nextStep;
		if (nextIsLatent) {
			nextStep = new LatentModelStepWarpper<>(step);
			
		} else {
			nextStep = step;
		}
		nextIsLatent = false;

		this.parcelOrderModel.add(nextStep, propertySetter);
		
		return this;
	}


	public <T> ParcelDemandModelBuilder<A,P> selectShareBased(
			ShareBasedSelector<A, P, T> selector,
			BiConsumer<P, ValueProvider<T>> propertySetter
	) {
		return this.addStep(selector, propertySetter);
	}

	public <T> ParcelDemandModelBuilder<A,P> selectShareBased(
			ShareBasedSelector<A, P, T> selector,
			BiConsumer<P, ValueProvider<T>> propertySetter,
			Function<P, ValueProvider<T>> copyGetter
	) {
		return this.addStep(selector, propertySetter, copyGetter);
	}
	
	public <T> ParcelDemandModelBuilder<A,P> selectShareBased(
			Collection<T> values,
			BiConsumer<P, ValueProvider<T>> propertySetter,
			Function<P, ValueProvider<T>> copyGetter
	) {
		return this.selectShareBased(new ShareBasedSelector<>(values), propertySetter, copyGetter);
	}

	public <T> ParcelDemandModelBuilder<A,P> selectShareBased(
			Collection<T> values,
			BiConsumer<P, ValueProvider<T>> propertySetter
	) {
		return this.selectShareBased(new ShareBasedSelector<>(values), propertySetter);
	}
	
	public <T> ParcelDemandModelBuilder<A,P> selectShareBased(
			Map<T, Double> shares,
			BiConsumer<P, ValueProvider<T>> propertySetter
	) {
		return this.selectShareBased(new ShareBasedSelector<>(shares), propertySetter);
	}

	public <T> ParcelDemandModelBuilder<A,P> selectShareBased(
			Map<T, Double> shares,
			BiConsumer<P, ValueProvider<T>> propertySetter,
			Function<P, ValueProvider<T>> copyGetter
	) {
		return this.selectShareBased(new ShareBasedSelector<>(shares), propertySetter, copyGetter);
	}


	@SuppressWarnings("unchecked")
	public  <T> ParcelDemandModelBuilder<A,P> equalDistributionStepOptions(
			BiConsumer<P, ValueProvider<T>> propertySetter,
			Function<P, ValueProvider<T>> copyGetter,
			ParcelDemandModelStep<A, P, T> ... steps
	) {
		Map<ParcelDemandModelStep<A, P, T>, Double> shares = Arrays.asList(steps).stream().collect(toMap(identity(), e -> 1.0));
		return this.addStep(new ShareBasedMultipleModelOptionsStep<>(shares), propertySetter, copyGetter);
	}

	@SuppressWarnings("unchecked")
	public  <T> ParcelDemandModelBuilder<A,P> equalDistributionStepOptions(
			BiConsumer<P, ValueProvider<T>> propertySetter,
			ParcelDemandModelStep<A, P, T> ... steps
	) {
		return this.equalDistributionStepOptions(propertySetter, null, steps);
	}


	public  <T> ParcelDemandModelBuilder<A,P> shareBasedStepOptions(
			Map<ParcelDemandModelStep<A, P, T>, Double> shares,
			BiConsumer<P, ValueProvider<T>> propertySetter,
			Function<P, ValueProvider<T>> copyGetter
	) {
		return this.addStep(new ShareBasedMultipleModelOptionsStep<>(shares), propertySetter, copyGetter);
	}

	public  <T> ParcelDemandModelBuilder<A,P> shareBasedStepOptions(
			Map<ParcelDemandModelStep<A, P, T>, Double> shares,
			BiConsumer<P, ValueProvider<T>> propertySetter
	) {
		return this.addStep(new ShareBasedMultipleModelOptionsStep<>(shares), propertySetter);
	}


	private Function<P, ValueProvider<CEPServiceProvider>> createCepspCopyGetter(boolean copy) {
		if(copy) {
			return ParcelBuilder::getServiceProvider;
		} else {
			return null;
		}
	}

	public ParcelDemandModelBuilder<A,P> customCepspSelection(ParcelDemandModelStep<A, P, CEPServiceProvider> step, boolean copyInBundle) {
		return this.addStep(step, ParcelBuilder::setServiceProvider, createCepspCopyGetter(copyInBundle));
	}
	
	public ParcelDemandModelBuilder<A,P> equalDistributionCepspSelection(Collection<CEPServiceProvider> serviceProviders, boolean copyInBundle) {
		return this.selectShareBased(serviceProviders, ParcelBuilder::setServiceProvider, createCepspCopyGetter(copyInBundle));
	}
	
	public ParcelDemandModelBuilder<A,P> shareBasedCepspSelection(Map<CEPServiceProvider, Double> shares, boolean copyInBundle) {
		return this.selectShareBased(shares, ParcelBuilder::setServiceProvider, createCepspCopyGetter(copyInBundle));
	}
	
	public ParcelDemandModelBuilder<A,P> privateConsumptionShareBasedCepspSelection(MarketShareProvider shareProvider, boolean copyInBundle) {
		return this.shareBasedCepspSelection(shareProvider.getPrivateConsumptionShare(), copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> privateProductionShareBasedCepspSelection(MarketShareProvider shareProvider, boolean copyInBundle) {
		return this.shareBasedCepspSelection(shareProvider.getPrivateProductionShare(), copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> privateOverallShareBasedCepspSelection(MarketShareProvider shareProvider, boolean copyInBundle) {
		return this.shareBasedCepspSelection(shareProvider.getPrivateShare(), copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> businessConsumptionShareBasedCepspSelection(MarketShareProvider shareProvider, boolean copyInBundle) {
		return this.shareBasedCepspSelection(shareProvider.getBusinessConsumptionShare(), copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> businessProductionShareBasedCepspSelection(MarketShareProvider shareProvider, boolean copyInBundle) {
		return this.shareBasedCepspSelection(shareProvider.getBusinessProductionShare(), copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> businessOverallShareBasedCepspSelection(MarketShareProvider shareProvider, boolean copyInBundle) {
		return this.shareBasedCepspSelection(shareProvider.getBusinessShare(), copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> overallConsumptionShareBasedCepspSelection(MarketShareProvider shareProvider, boolean copyInBundle) {
		return this.shareBasedCepspSelection(shareProvider.getConsumptionShare(), copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> overallProductionShareBasedCepspSelection(MarketShareProvider shareProvider, boolean copyInBundle) {
		return this.shareBasedCepspSelection(shareProvider.getProductionShare(), copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> totalMarketShareBasedCepspSelection(MarketShareProvider shareProvider, boolean copyInBundle) {
		return this.shareBasedCepspSelection(shareProvider.getTotalShare(), copyInBundle);
	}
	
	
	

	private Function<P, ValueProvider<DistributionCenter>> createDcCopyGetter(boolean copyInBundle) {
		if(copyInBundle) {
			return ParcelBuilder::getDistributionCenter;
		} else {
			return null;
		}
	}

	
	public ParcelDemandModelBuilder<A,P> customDistributionCenterSelection(ParcelDemandModelStep<A, P, DistributionCenter> step, boolean copyInBundle) {
		return this.addStep(step, ParcelBuilder::setDistributionCenter, createDcCopyGetter(copyInBundle));
	}
	
	public ParcelDemandModelBuilder<A,P> distributionCenterSelectionInCepspByFleetsize(boolean copyInBundle) {
		return this.addStep(new DistributionCenterSelectorByFleetSize<>(), ParcelBuilder::setDistributionCenter, createDcCopyGetter(copyInBundle));
	}
	
	
//	public ParcelDemandModelBuilder<A,P> equalDistributionCenterSelection(Collection<DistributionCenter> distributionCenters) {
//		return this.selectShareBased(distributionCenters, ParcelBuilder::setDistributionCenter);
//	}
//
//	public ParcelDemandModelBuilder<A,P> privateShareBasedDistributionCenterSelection(Collection<DistributionCenter> distributionCenters) {
//		Map<DistributionCenter, Double> shares = distributionCenters.stream().collect(toMap(Function.identity(), DistributionCenter::getSharePrivate));
//		return this.selectShareBased(shares, ParcelBuilder::setDistributionCenter);
//	}
//	
//	public ParcelDemandModelBuilder<A,P> businessShareBasedDistributionCenterSelection(Collection<DistributionCenter> distributionCenters) {
//		Map<DistributionCenter, Double> shares = distributionCenters.stream().collect(toMap(Function.identity(), DistributionCenter::getShareBusiness));
//		return this.selectShareBased(shares, ParcelBuilder::setDistributionCenter);
//	}
//	
//	public ParcelDemandModelBuilder<A,P> customSharesDistributionCenterSelection(Map<DistributionCenter, Double> shares) {
//		return this.selectShareBased(shares, ParcelBuilder::setDistributionCenter);
//	}
	
	
	
	public ParcelDemandModelBuilder<A,P> useDistributionCenterAsConsumer() {
		return this.addStep(new CopyProviderModelStep<A, P, ParcelAgent>(ParcelBuilder::getDistributionCenter), ParcelBuilder::setConsumer);
	}
	
	public ParcelDemandModelBuilder<A,P> useDistributionCenterAsProducer() {
		return this.addStep(new CopyProviderModelStep<A, P, ParcelAgent>(ParcelBuilder::getDistributionCenter), ParcelBuilder::setProducer);
	}
	
	public ParcelDemandModelBuilder<A, P> useAgentAsProducer() {
		return this.addStep((parcel, otherParcels, numOfParcels, randomNumber) -> (ParcelAgent) parcel.getAgent(), ParcelBuilder::setProducer);
	}
	
	public ParcelDemandModelBuilder<A, P> useAgentAsConsumer() {
		return this.addStep((parcel, otherParcels, numOfParcels, randomNumber) -> (ParcelAgent) parcel.getAgent(), ParcelBuilder::setConsumer);
	}
	
	
	private Function<P, ValueProvider<Time>> createArrivalDateCopyGetter(boolean copyInBundle) {
		if(copyInBundle) {
			return p -> new InstantValueProvider<>(p.getArrivalDate()); //TODO check if first parcel time is initialized when second parcel calls get
		} else {
			return null;
		}
	}
	
	public ParcelDemandModelBuilder<A,P> customArrivalDateSelection(ParcelDemandModelStep<A, P, Time> step, boolean copyInBundle) {
		return this.addStep(step, ParcelBuilder::setArrivalDate, createArrivalDateCopyGetter(copyInBundle));
	}
	
	public ParcelDemandModelBuilder<A,P> shareBasedArrivalDateSelection(Map<Time,Double> shares, boolean copyInBundle) {
		return this.selectShareBased(shares, ParcelBuilder::setArrivalDate, createArrivalDateCopyGetter(copyInBundle));
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalDateSelection(Time from, Time untilExclusive, Function<Time, Time> precision, boolean copyInBundle) {
		return this.customArrivalDateSelection(new RandomDateSelector<>(from, untilExclusive, precision), copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalDateSelectionExcludeSunday(Function<Time, Time> precision, boolean copyInBundle) {
		return this.customArrivalDateSelection(new RandomDateSelector<>(precision), copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalDaySelection(Time from, Time untilExclusive, boolean copyInBundle) {
		return this.randomArrivalDateSelection(from, untilExclusive, RandomDateSelector.DAY_PRECISION, copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalDaySelectionExcludeSunday(boolean copyInBundle) {
		return this.randomArrivalDateSelectionExcludeSunday(RandomDateSelector.DAY_PRECISION, copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalHourSelection(Time from, Time untilExclusive, boolean copyInBundle) {
		return this.randomArrivalDateSelection(from, untilExclusive, RandomDateSelector.HOUR_PRECISION, copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalHourSelectionExcludeSunday(boolean copyInBundle) {
		return this.randomArrivalDateSelectionExcludeSunday(RandomDateSelector.HOUR_PRECISION, copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalMinuteSelection(Time from, Time untilExclusive, boolean copyInBundle) {
		return this.randomArrivalDateSelection(from, untilExclusive, RandomDateSelector.MINUTE_PRECISION, copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalMinuteSelectionExcludeSunday(boolean copyInBundle) {
		return this.randomArrivalDateSelectionExcludeSunday(RandomDateSelector.MINUTE_PRECISION, copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalSecondSelection(Time from, Time untilExclusive, boolean copyInBundle) {
		return this.randomArrivalDateSelection(from, untilExclusive, RandomDateSelector.SECOND_PRECISION, copyInBundle);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalSecondSelectionExcludeSunday(boolean copyInBundle) {
		return this.randomArrivalDateSelectionExcludeSunday(RandomDateSelector.SECOND_PRECISION, copyInBundle);
	}
	

	private Function<P, ValueProvider<ParcelSize>> createParcelSizeCopyGetter(boolean copyInBundle) {
		if(copyInBundle) {
			return ParcelBuilder::getSize;
		} else {
			return null;
		}
	}
	
	public ParcelDemandModelBuilder<A,P> customParcelSizeSelection(ParcelDemandModelStep<A, P, ParcelSize> step, boolean copyInBundle) {
		return this.addStep(step, ParcelBuilder::setSize, createParcelSizeCopyGetter(copyInBundle));
	}
	
	public ParcelDemandModelBuilder<A,P> equalParcelSizeSelection(boolean copyInBundle) {
		return this.selectShareBased(Arrays.asList(ParcelSize.values()), ParcelBuilder::setSize, createParcelSizeCopyGetter(copyInBundle));
	}
	
	public ParcelDemandModelBuilder<A,P> shareBasedParcelSizeSelection(Map<ParcelSize, Double> shares, boolean copyInBundle) {
		return this.selectShareBased(shares, ParcelBuilder::setSize, createParcelSizeCopyGetter(copyInBundle));
	}



	private Function<P, ValueProvider<Double>> createVolumeCopyGetter(boolean copyInBundle) {
		if(copyInBundle) {
			return ParcelBuilder::getVolume;
		} else {
			return null;
		}
	}

	public ParcelDemandModelBuilder<A,P> selectVolumeBasedOnShipmentSize(boolean copyInBundle) {
		return this.addStep(new VolumeSelector<>(), ParcelBuilder::setVolume, createVolumeCopyGetter(copyInBundle));
	}
	
	
//	public ParcelDemandModelBuilder<A,P> customServiceProviderSelection(ParcelDemandModelStep<A, P, DistributionServiceProvider> step) {
//		return this.addStep(step, ParcelBuilder::setServiceProvider);
//	}
//	
//	public ParcelDemandModelBuilder<A,P> equalServiceProviderSelection(Collection<DistributionServiceProvider> services) {
//		return this.selectShareBased(services, ParcelBuilder::setServiceProvider);
//	}
//
//	public ParcelDemandModelBuilder<A,P> shareBasedServiceProviderSelection(Map<DistributionServiceProvider, Double> shares) {
//		return this.selectShareBased(shares, ParcelBuilder::setServiceProvider);
//	}
//	
//	public ParcelDemandModelBuilder<A,P> distributionCenterAsServiceProvider() {
//		return this.customServiceProviderSelection((parcel, otherParcels, numOfParcels, rand) -> parcel.getDistributionCenter().getServiceProvider());
//	}


	private Function<P, ValueProvider<Boolean>> createIsPickupCopyGetter(boolean copyInBundle) {
		if(copyInBundle) {
			return ParcelBuilder::getIsPickUp;
		} else {
			return null;
		}
	}

	public ParcelDemandModelBuilder<A,P> customPickupOrDeliverySelection(ParcelDemandModelStep<A, P, Boolean> step, boolean copyInBundle) {
		return this.addStep(step, ParcelBuilder::setIsPickUp, createIsPickupCopyGetter(copyInBundle));
	}
	
	public ParcelDemandModelBuilder<A, P> allAsPickup() {
		return this.addStep((p, op, n, r) -> true, ParcelBuilder::setIsPickUp);
	}
	
	public ParcelDemandModelBuilder<A, P> allAsDelivery() {
		return this.addStep((p, op, n, r) -> false, ParcelBuilder::setIsPickUp);
	}
	
	
	
	
	public ParcelDemandModel<A, P> build() {
		verifyAndInitialize();
		
		return this.parcelOrderModel;
	}
	

}
