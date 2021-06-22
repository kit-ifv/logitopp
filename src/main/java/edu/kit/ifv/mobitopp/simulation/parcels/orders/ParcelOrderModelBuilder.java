package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.tours.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Setter;

@Setter
public class ParcelOrderModelBuilder<R> {

	private NumberOfParcelsSelector<R> numberOfParcelsSelector;
	private Function<R, DoubleSupplier> randomProvider;
	private BiConsumer<ParcelBuilder, R> parcelInitializer;
	private Function<ParcelBuilder, IParcel> parcelBuildFcuntion;
	
	private GenericParcelOrderModel<R> parcelOrderModel;

	public static ParcelOrderModelBuilder<PickUpParcelPerson> forPrivateParcels(DeliveryResults results) {
		ParcelOrderModelBuilder<PickUpParcelPerson> builder = new ParcelOrderModelBuilder<>();
		
		builder.setRandomProvider(p -> p::getNextRandom);
		builder.setParcelInitializer(ParcelBuilder::setPerson);
		builder.setParcelBuildFcuntion(pb -> pb.buildPrivateParcel(results));
		
		return builder;
	}

	public static ParcelOrderModelBuilder<Opportunity> forBusinessParcels(ZoneRepository zoneRepo, DeliveryResults results) {
		ParcelOrderModelBuilder<Opportunity> builder = new ParcelOrderModelBuilder<>();
		
		Random random = new Random(42); 
		builder.setRandomProvider(o -> random::nextDouble);
		builder.setParcelInitializer(ParcelBuilder::setOpportunity);
		builder.setParcelBuildFcuntion(pb -> pb.buildBusinessParcel(zoneRepo, results));
		
		return builder;
	}
	
	public ParcelOrderModelBuilder<R> useRandom(Function<R, DoubleSupplier> randomProvider) {
		this.randomProvider = randomProvider;		
		return this;
	}
	
	public ParcelOrderModelBuilder<R> useInitializer(BiConsumer<ParcelBuilder, R> parcelInitializer) {
		this.parcelInitializer = parcelInitializer;		
		return this;
	}
	
	public ParcelOrderModelBuilder<R> useBuildFunction(Function<ParcelBuilder, IParcel> parcelBuildFcuntion) {
		this.parcelBuildFcuntion = parcelBuildFcuntion;		
		return this;
	}
	
	
	
	public ParcelOrderModelBuilder<R> useNumberSelector(NumberOfParcelsSelector<R> numberOfParcelsSelector) {
		this.numberOfParcelsSelector = numberOfParcelsSelector;		
		return this;
	}
	
	public ParcelOrderModelBuilder<R> useNullNumberSelector() {
		this.numberOfParcelsSelector = new NullNumerOfParcelsSelector<>();		
		return this;
	}
	
	public ParcelOrderModelBuilder<R> useRandomNumberSelector(int min, int max, double percent) {
		this.numberOfParcelsSelector = new RandomNumberOfParcelsSelector<>(min, max, percent);
		return this;
	}
	
	public ParcelOrderModelBuilder<R> useNormalDistributionNumberSelector(double mean, double stdDev, int capMin, int capMax) {
		this.numberOfParcelsSelector = new NormalDistributedNumberOfParcelsSelector<>(mean, stdDev, capMin, capMax);
		return this;
	}
	
	public ParcelOrderModelBuilder<R> useNormalDistributionNumberSelector(double mean, double stdDev, int capMax) {
		this.numberOfParcelsSelector = new NormalDistributedNumberOfParcelsSelector<>(mean, stdDev, capMax);
		return this;
	}
	
	public ParcelOrderModelBuilder<R> filterRecipients(Predicate<R> filter) {
		if (this.numberOfParcelsSelector == null) {
			throw new IllegalStateException("A NumberOfParcelsSelector has to be selected, before filtering recipients.");
		}
		
		this.numberOfParcelsSelector = new FilteredNumberOfParcelsSelector<>(this.numberOfParcelsSelector, filter);
		return this;
	}
	
	
	
	private void verifyAndInitialize() {
		if (parcelOrderModel != null) {
			return;
		}
		
		if (numberOfParcelsSelector==null) {
			throw new IllegalStateException("A NumberOfParcelsSelector should be selected before adding ParcelOrderSteps.");
		}
		if (randomProvider==null) {
			throw new IllegalStateException("A randomProvider (Function<R, DoubleSupplier>) should be selected before adding ParcelOrderSteps.");
		}
		if (parcelInitializer==null) {
			throw new IllegalStateException("A parcelInitializer (BiConsumer<ParcelBuilder, R>) should be selected before adding ParcelOrderSteps.");
		}
		if (parcelBuildFcuntion==null) {
			throw new IllegalStateException("A parcelBuildFcuntion (Function<ParcelBuilder, IParcel>) should be selected before adding ParcelOrderSteps.");
		}
		
		this.parcelOrderModel = new GenericParcelOrderModel<>(numberOfParcelsSelector, randomProvider, parcelInitializer, parcelBuildFcuntion);
	}
	
	public <T> ParcelOrderModelBuilder<R> addStep(ParcelOrderStep<T> step, BiConsumer<ParcelBuilder, T> propertySetter) {
		verifyAndInitialize();
		
		this.parcelOrderModel.add(step, propertySetter);
		
		return this;
	}
	
	
	public <T> ParcelOrderModelBuilder<R> selectShareBased(ShareBasedSelector<T> selector, BiConsumer<ParcelBuilder, T> propertySetter) {
		return this.addStep(selector, propertySetter);
	}
	
	public <T> ParcelOrderModelBuilder<R> selectShareBased(Collection<T> values, BiConsumer<ParcelBuilder, T> propertySetter) {
		return this.selectShareBased(new ShareBasedSelector<T>(values), propertySetter);
	}
	
	public <T> ParcelOrderModelBuilder<R> selectShareBased(Map<T, Double> shares, BiConsumer<ParcelBuilder, T> propertySetter) {
		return this.selectShareBased(new ShareBasedSelector<T>(shares), propertySetter);
	}
	
	
	
	
	
	
	public ParcelOrderModelBuilder<R> customDistributionCenterSelection(ParcelOrderStep<DistributionCenter> step) {
		return this.addStep(step, ParcelBuilder::setDistributionCenter);
	}
	
	public ParcelOrderModelBuilder<R> equalDistributionCenterSelection(Collection<DistributionCenter> distributionCenters) {
		return this.selectShareBased(distributionCenters, ParcelBuilder::setDistributionCenter);
	}

	public ParcelOrderModelBuilder<R> shareBasedDistributionCenterSelection(Collection<DistributionCenter> distributionCenters) {
		Map<DistributionCenter, Double> shares = distributionCenters.stream().collect(toMap(Function.identity(), DistributionCenter::getRelativeShare));
		return this.selectShareBased(shares, ParcelBuilder::setDistributionCenter);
	}
	
	public ParcelOrderModelBuilder<R> customSharesDistributionCenterSelection(Map<DistributionCenter, Double> shares) {
		return this.selectShareBased(shares, ParcelBuilder::setDistributionCenter);
	}
	
	
	
	
	
	public ParcelOrderModelBuilder<R> customDeliveryDateSelection(ParcelOrderStep<Time> step) {
		return this.addStep(step, ParcelBuilder::setPlannedArrivalDate);
	}
	
	public ParcelOrderModelBuilder<R> randomDeliveryDateSelection(Time from, Time untilExclusive, Function<Time, Time> precision) {
		return this.customDeliveryDateSelection(new RandomDeliveryDateSelector(from, untilExclusive, precision));
	}
	
	public ParcelOrderModelBuilder<R> randomDeliveryDateSelectionExcludeSunday(Function<Time, Time> precision) {
		return this.customDeliveryDateSelection(new RandomDeliveryDateSelector(precision));
	}
	
	public ParcelOrderModelBuilder<R> randomDeliveryDaySelection(Time from, Time untilExclusive) {
		return this.randomDeliveryDateSelection(from, untilExclusive, RandomDeliveryDateSelector.DAY_PRECISION);
	}
	
	public ParcelOrderModelBuilder<R> randomDeliveryDaySelectionExcludeSunday() {
		return this.randomDeliveryDateSelectionExcludeSunday(RandomDeliveryDateSelector.DAY_PRECISION);
	}
	
	public ParcelOrderModelBuilder<R> randomDeliveryHourSelection(Time from, Time untilExclusive) {
		return this.randomDeliveryDateSelection(from, untilExclusive, RandomDeliveryDateSelector.HOUR_PRECISION);
	}
	
	public ParcelOrderModelBuilder<R> randomDeliveryHourSelectionExcludeSunday() {
		return this.randomDeliveryDateSelectionExcludeSunday(RandomDeliveryDateSelector.HOUR_PRECISION);
	}
	
	public ParcelOrderModelBuilder<R> randomDeliveryMinuteSelection(Time from, Time untilExclusive) {
		return this.randomDeliveryDateSelection(from, untilExclusive, RandomDeliveryDateSelector.MINUTE_PRECISION);
	}
	
	public ParcelOrderModelBuilder<R> randomDeliveryMinuteSelectionExcludeSunday() {
		return this.randomDeliveryDateSelectionExcludeSunday(RandomDeliveryDateSelector.MINUTE_PRECISION);
	}
	
	public ParcelOrderModelBuilder<R> randomDeliverySecondSelection(Time from, Time untilExclusive) {
		return this.randomDeliveryDateSelection(from, untilExclusive, RandomDeliveryDateSelector.SECOND_PRECISION);
	}
	
	public ParcelOrderModelBuilder<R> randomDeliverySecondSelectionExcludeSunday() {
		return this.randomDeliveryDateSelectionExcludeSunday(RandomDeliveryDateSelector.SECOND_PRECISION);
	}
	
	
	
	
	public ParcelOrderModelBuilder<R> customDeliveryServiceSelection(ParcelOrderStep<String> step) {
		return this.addStep(step, ParcelBuilder::setDeliveryService);
	}
	
	public ParcelOrderModelBuilder<R> equalDeliveryServiceSelection(Collection<String> services) {
		return this.selectShareBased(services, ParcelBuilder::setDeliveryService);
	}

	public ParcelOrderModelBuilder<R> shareBasedDeliveryServiceSelection(Map<String, Double> shares) {
		return this.selectShareBased(shares, ParcelBuilder::setDeliveryService);
	}
	
	public ParcelOrderModelBuilder<R> distributionCenterAsDeliveryService() {
		return this.customDeliveryServiceSelection((parcel, otherParcels, numOfParcels, rand) -> parcel.getDistributionCenter().getName());
	}
	
	
	
	
	
	
	public ParcelOrderModelBuilder<R> customParcelDestinationSelection(ParcelOrderStep<ParcelDestinationType> step) {
		return this.addStep(step, ParcelBuilder::setDestinationType);
	}
	
	public ParcelOrderModelBuilder<R> equalParcelDestinationSelection(Predicate<Zone> workZoneFilter) {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector(workZoneFilter));
	}
	
	public ParcelOrderModelBuilder<R> equalParcelDestinationSelection() {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector());
	}

	public ParcelOrderModelBuilder<R> shareBasedParcelDestinationSelection(Map<ParcelDestinationType, Double> shares) {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector(shares));
	}
	
	public ParcelOrderModelBuilder<R> shareBasedParcelDestinationSelection(Map<ParcelDestinationType, Double> shares, Predicate<Zone> workZoneFilter) {
		return this.customParcelDestinationSelection(new ShareBasedParcelDestinationSelector(shares, workZoneFilter));
	}
	
	
	public ParcelOrderModel<R> build() {
		verifyAndInitialize();
		
		return this.parcelOrderModel;
	}
	
	
	public static ParcelOrderModel<PickUpParcelPerson> defaultPrivateParcelModel(Collection<DistributionCenter> distributionCenters, DeliveryResults results) {
		return defaultPrivateParcelModel(distributionCenters, z -> true, results);
	}
	
	public static ParcelOrderModel<PickUpParcelPerson> defaultPrivateParcelModel(Collection<DistributionCenter> distributionCenters, Predicate<Zone> workZoneFilter, DeliveryResults results) {
		return forPrivateParcels(results)
					.useNormalDistributionNumberSelector(0.65, 0.5, 10)
					.equalParcelDestinationSelection(workZoneFilter)
					.shareBasedDistributionCenterSelection(distributionCenters)
					.equalDeliveryServiceSelection(List.of("Dummy Delivery Service"))
					.randomDeliveryDaySelectionExcludeSunday()
					.build();
	}
	
	public static ParcelOrderModel<PickUpParcelPerson> nullPrivateParcelModel(DeliveryResults results) {
		return forPrivateParcels(results)
					.useNullNumberSelector()
					.build();
	}
	
	
	public static ParcelOrderModel<Opportunity> defaultBusinessParcelOrderModel(Collection<DistributionCenter> distributionCenters, ZoneRepository zoneRepo, DeliveryResults results) {
		return defaultBusinessParcelOrderModel(distributionCenters, zoneRepo, z -> true, results);
	}
	
	public static ParcelOrderModel<Opportunity> defaultBusinessParcelOrderModel(Collection<DistributionCenter> distributionCenters, ZoneRepository zoneRepo, Predicate<Zone> zoneFilter, DeliveryResults results) {
		return forBusinessParcels(zoneRepo, results)
					.useRandomNumberSelector(0, 3, 0.01)
					.filterRecipients(o -> zoneFilter.test(zoneRepo.getZoneById(o.zone())) )
					.filterRecipients(o -> o.activityType().equals(ActivityType.WORK))
					.shareBasedDistributionCenterSelection(distributionCenters)
					.equalDeliveryServiceSelection(List.of("Dummy Delivery Service"))
					.randomDeliveryDaySelectionExcludeSunday()
					.build();
	}
	
	public static ParcelOrderModel<Opportunity> nullBusinessParcelOrderModel(ZoneRepository zoneRepo, DeliveryResults results) {
		return forBusinessParcels(zoneRepo, results)
					.useNullNumberSelector()
					.build();
	}
	
}
