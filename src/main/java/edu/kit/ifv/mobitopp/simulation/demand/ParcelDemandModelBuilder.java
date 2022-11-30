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
import edu.kit.ifv.mobitopp.simulation.demand.attributes.CopyProviderModelStep;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.LatentModelStepWarpper;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ParcelDemandModelStep;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.RandomDateSelector;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ShareBasedMultipleModelOptionsStep;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ShareBasedSelector;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.FilteredNumberOfParcelsSelector;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.NormalDistributedNumberOfParcelsSelector;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.NullNumerOfParcelsSelector;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.ParcelQuantityModel;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.RandomNumberOfParcelsSelector;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
import edu.kit.ifv.mobitopp.time.Time;

public class ParcelDemandModelBuilder<A extends ParcelAgent, P extends ParcelBuilder<A>> {

	private ParcelQuantityModel<A> numberOfParcelsSelector;
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
	
	public ParcelDemandModelBuilder<A,P> filterRecipients(Predicate<A> filter) {
		if (this.numberOfParcelsSelector == null) {
			throw new IllegalStateException("A NumberOfParcelsSelector has to be selected, before filtering recipients.");
		}
		
		this.numberOfParcelsSelector = new FilteredNumberOfParcelsSelector<>(this.numberOfParcelsSelector, filter);
		return this;
	}
	
	
	
	protected void verifyAndInitialize() {
		if (parcelOrderModel != null) {
			return;
		}
		
		if (numberOfParcelsSelector==null) {
			throw new IllegalStateException("A NumberOfParcelsSelector should be selected before adding ParcelOrderSteps.");
		}
		if (randomProvider==null) {
			throw new IllegalStateException("A randomProvider (Function<R, DoubleSupplier>) should be selected before adding ParcelOrderSteps.");
		}
		if (parcelFactory==null) {
			throw new IllegalStateException("A parcelFactory (Function<ParcelBuilder, R>) should be selected before adding ParcelOrderSteps.");
		}
		
		this.parcelOrderModel = new GenericParcelDemandModel<>(numberOfParcelsSelector, randomProvider, parcelFactory);
	}
	
	
	
	public ParcelDemandModelBuilder<A,P> asLatent() {
		this.nextIsLatent  = true;
		return this;
	}
	
	public <T> ParcelDemandModelBuilder<A,P> addStep(ParcelDemandModelStep<A, P, T> step, BiConsumer<P, ValueProvider<T>> propertySetter) {
		verifyAndInitialize();
		
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
	
	
	public <T> ParcelDemandModelBuilder<A,P> selectShareBased(ShareBasedSelector<A, P, T> selector, BiConsumer<P, ValueProvider<T>> propertySetter) {
		return this.addStep(selector, propertySetter);
	}
	
	public <T> ParcelDemandModelBuilder<A,P> selectShareBased(Collection<T> values, BiConsumer<P, ValueProvider<T>> propertySetter) {
		return this.selectShareBased(new ShareBasedSelector<>(values), propertySetter);
	}
	
	public <T> ParcelDemandModelBuilder<A,P> selectShareBased(Map<T, Double> shares, BiConsumer<P, ValueProvider<T>> propertySetter) {
		return this.selectShareBased(new ShareBasedSelector<>(shares), propertySetter);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public  <T> ParcelDemandModelBuilder<A,P> equalDistributionStepOptions(BiConsumer<P, ValueProvider<T>> propertySetter, ParcelDemandModelStep<A, P, T> ... steps) {
		Map<ParcelDemandModelStep<A, P, T>, Double> shares = Arrays.asList(steps).stream().collect(toMap(identity(), e -> 1.0));
		
		return this.addStep(new ShareBasedMultipleModelOptionsStep<>(shares), propertySetter);
	}
	
	public  <T> ParcelDemandModelBuilder<A,P> shareBasedStepOptions(Map<ParcelDemandModelStep<A, P, T>, Double> shares, BiConsumer<P, ValueProvider<T>> propertySetter) {		
		return this.addStep(new ShareBasedMultipleModelOptionsStep<>(shares), propertySetter);
	}
	
	
	
	
	public ParcelDemandModelBuilder<A,P> customDistributionCenterSelection(ParcelDemandModelStep<A, P, DistributionCenter> step) {
		return this.addStep(step, ParcelBuilder::setDistributionCenter);
	}
	
	public ParcelDemandModelBuilder<A,P> equalDistributionCenterSelection(Collection<DistributionCenter> distributionCenters) {
		return this.selectShareBased(distributionCenters, ParcelBuilder::setDistributionCenter);
	}

	public ParcelDemandModelBuilder<A,P> privateShareBasedDistributionCenterSelection(Collection<DistributionCenter> distributionCenters) {
		Map<DistributionCenter, Double> shares = distributionCenters.stream().collect(toMap(Function.identity(), DistributionCenter::getSharePrivate));
		return this.selectShareBased(shares, ParcelBuilder::setDistributionCenter);
	}
	
	public ParcelDemandModelBuilder<A,P> businessShareBasedDistributionCenterSelection(Collection<DistributionCenter> distributionCenters) {
		Map<DistributionCenter, Double> shares = distributionCenters.stream().collect(toMap(Function.identity(), DistributionCenter::getShareBusiness));
		return this.selectShareBased(shares, ParcelBuilder::setDistributionCenter);
	}
	
	public ParcelDemandModelBuilder<A,P> customSharesDistributionCenterSelection(Map<DistributionCenter, Double> shares) {
		return this.selectShareBased(shares, ParcelBuilder::setDistributionCenter);
	}
	
	
	
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
	
	
	
	
	public ParcelDemandModelBuilder<A,P> customArrivalDateSelection(ParcelDemandModelStep<A, P, Time> step) {
		return this.addStep(step, ParcelBuilder::setArrivalDate);
	}
	
	public ParcelDemandModelBuilder<A,P> shareBasedArrivalDateSelection(Map<Time,Double> shares) {
		return this.selectShareBased(shares, ParcelBuilder::setArrivalDate);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalDateSelection(Time from, Time untilExclusive, Function<Time, Time> precision) {
		return this.customArrivalDateSelection(new RandomDateSelector<>(from, untilExclusive, precision));
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalDateSelectionExcludeSunday(Function<Time, Time> precision) {
		return this.customArrivalDateSelection(new RandomDateSelector<>(precision));
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalDaySelection(Time from, Time untilExclusive) {
		return this.randomArrivalDateSelection(from, untilExclusive, RandomDateSelector.DAY_PRECISION);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalDaySelectionExcludeSunday() {
		return this.randomArrivalDateSelectionExcludeSunday(RandomDateSelector.DAY_PRECISION);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalHourSelection(Time from, Time untilExclusive) {
		return this.randomArrivalDateSelection(from, untilExclusive, RandomDateSelector.HOUR_PRECISION);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalHourSelectionExcludeSunday() {
		return this.randomArrivalDateSelectionExcludeSunday(RandomDateSelector.HOUR_PRECISION);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalMinuteSelection(Time from, Time untilExclusive) {
		return this.randomArrivalDateSelection(from, untilExclusive, RandomDateSelector.MINUTE_PRECISION);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalMinuteSelectionExcludeSunday() {
		return this.randomArrivalDateSelectionExcludeSunday(RandomDateSelector.MINUTE_PRECISION);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalSecondSelection(Time from, Time untilExclusive) {
		return this.randomArrivalDateSelection(from, untilExclusive, RandomDateSelector.SECOND_PRECISION);
	}
	
	public ParcelDemandModelBuilder<A,P> randomArrivalSecondSelectionExcludeSunday() {
		return this.randomArrivalDateSelectionExcludeSunday(RandomDateSelector.SECOND_PRECISION);
	}
	
	
	
	
	public ParcelDemandModelBuilder<A,P> customShipmentSizeSelection(ParcelDemandModelStep<A, P, ShipmentSize> step) {
		return this.addStep(step, ParcelBuilder::setSize);
	}
	
	public ParcelDemandModelBuilder<A,P> equalShipmentSizeSelection() {
		return this.selectShareBased(Arrays.asList(ShipmentSize.values()), ParcelBuilder::setSize);
	}
	
	public ParcelDemandModelBuilder<A,P> shareBasedShipmentSizeSelection(Map<ShipmentSize, Double> shares) {
		return this.selectShareBased(shares, ParcelBuilder::setSize);
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

	public ParcelDemandModel<A, P> build() {
		verifyAndInitialize();
		
		return this.parcelOrderModel;
	}
	

}
