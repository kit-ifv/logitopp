package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DefaultParcelOrderModel implements ParcelOrderModel {

	private final NumberOfParcelsSelector numberOfParcelsSelector;
	private final ParcelDestinationSelector parcelDestinationSelector;
	private final DistributionCenterSelector distributionCenterSelector;
	private final DeliveryServiceSelector deliveryServiceSelector;
	private final DeliveryDateSelector deliveryDateSelector;
	
	public DefaultParcelOrderModel(Collection<DistributionCenter> distributionCenters) {
		this(distributionCenters, z -> true);
	}
	
	public DefaultParcelOrderModel(Collection<DistributionCenter> distributionCenters, Predicate<Zone> workZoneFilter) {
		this.numberOfParcelsSelector = new NormalDistributedNumberOfParcelsSelector(0.65, 0.5, 10);
		this.parcelDestinationSelector = new ShareBasedParcelDestinationSelector(workZoneFilter);
		this.distributionCenterSelector = new ShareBasedDistributionCenterSelector(distributionCenters);
		this.deliveryServiceSelector = new ShareBasedDeliveryServiceSelector(Arrays.asList("Dummy Delivery Service"));
		this.deliveryDateSelector = new RandomDeliveryDateSelector();
	}
	

	public Collection<Parcel> createParcelOrders(PickUpParcelPerson person,
		DeliveryResults results) {
		int numOfParcels = numberOfParcelsSelector.select(person, person.getNextRandom());

		Collection<Parcel> parcels = new ArrayList<Parcel>();

		for (int i = 0; i < numOfParcels; i++) {
			
			ParcelDestinationType destination = parcelDestinationSelector.select(person, numOfParcels, parcels, person.getNextRandom());
			Time date = deliveryDateSelector.select(person, numOfParcels, destination, parcels, person.getNextRandom());
			String deliveryService = deliveryServiceSelector.select(person, numOfParcels, destination, date, parcels, person.getNextRandom());
			DistributionCenter distributionCenter = distributionCenterSelector.select(person, numOfParcels, destination, date, deliveryService, parcels, person.getNextRandom());
			
			parcels.add(new Parcel(person, destination, date, distributionCenter, deliveryService, results));

		}

		return parcels;
	}

}
