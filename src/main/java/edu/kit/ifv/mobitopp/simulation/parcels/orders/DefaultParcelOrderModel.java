package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.tours.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.AllArgsConstructor;

/**
 * DefaultParcelOrderModel is a {@link ParcelOrderModel}
 * combining the default implementation of the various 
 * parcel order steps:
 * <br> {@link NormalDistributedNumberOfParcelsSelector} as {@link NumberOfParcelsSelector}
 * <br> {@link ShareBasedParcelDestinationSelector} as {@link ParcelDestinationSelector}
 * <br> {@link ShareBasedDistributionCenterSelector} as {@link DistributionCenterSelector}
 * <br> {@link ShareBasedDeliveryServiceSelector} as {@link DeliveryServiceSelector}
 * <br> {@link RandomDeliveryDateSelector} as {@link DeliveryDateSelector}
 */
@AllArgsConstructor
public class DefaultParcelOrderModel implements ParcelOrderModel {

	private final NumberOfParcelsSelector numberOfParcelsSelector;
	private final ParcelDestinationSelector parcelDestinationSelector;
	private final DistributionCenterSelector distributionCenterSelector;
	private final DeliveryServiceSelector deliveryServiceSelector;
	private final DeliveryDateSelector deliveryDateSelector;
	
	/**
	 * Instantiates a new {@link DefaultParcelOrderModel}
	 * with the given {@link DistributionCenter} and no work zone filter.
	 *
	 * @param distributionCenters the distribution centers
	 */
	public DefaultParcelOrderModel(Collection<DistributionCenter> distributionCenters) {
		this(distributionCenters, z -> true);
	}
	
	/**
	 * Instantiates a new {@link DefaultParcelOrderModel}
	 * with the given {@link DistributionCenter} and work zone selector.
	 *
	 * @param distributionCenters the distribution centers
	 * @param workZoneFilter the work zone filter
	 */
	public DefaultParcelOrderModel(Collection<DistributionCenter> distributionCenters, Predicate<Zone> workZoneFilter) {
		this.numberOfParcelsSelector = new NormalDistributedNumberOfParcelsSelector(0.65, 0.5, 10);
		this.parcelDestinationSelector = new ShareBasedParcelDestinationSelector(workZoneFilter);
		this.distributionCenterSelector = new ShareBasedDistributionCenterSelector(
			distributionCenters.stream().collect(toMap(Function.identity(), DistributionCenter::getRelativeShare))
		);
		this.deliveryServiceSelector = new ShareBasedDeliveryServiceSelector(Arrays.asList("Dummy Delivery Service"));
		this.deliveryDateSelector = new RandomDeliveryDateSelector();
	}
	

	/**
	 * Creates and returns the parcel orders for the given person.
	 * Logs the orders in the given {@link DeliveryResults}.
	 *
	 * @param person the person
	 * @param results the {@link DeliveryResults}
	 * @return the person's parcel orders
	 */
	public Collection<PrivateParcel> createParcelOrders(PickUpParcelPerson person,	DeliveryResults results) {
		
		int numOfParcels = numberOfParcelsSelector.select(person, person.getNextRandom());

		Collection<PrivateParcel> parcels = new ArrayList<PrivateParcel>();

		for (int i = 0; i < numOfParcels; i++) {
			
			ParcelDestinationType destination = parcelDestinationSelector.select(person, numOfParcels, parcels, person.getNextRandom());
			Time date = deliveryDateSelector.select(person, numOfParcels, destination, parcels, person.getNextRandom());
			String deliveryService = deliveryServiceSelector.select(person, numOfParcels, destination, date, parcels, person.getNextRandom());
			DistributionCenter distributionCenter = distributionCenterSelector.select(person, numOfParcels, destination, date, deliveryService, parcels, person.getNextRandom());
			
			parcels.add(new PrivateParcel(person, destination, destination.getZoneAndLocation(person), date, distributionCenter, deliveryService, results));

		}

		return parcels;
	}

}
