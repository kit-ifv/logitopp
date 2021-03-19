package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.parcels.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * ShareBasedDistributionCenterSelector is a {@link DistributionCenterSelector} extending {@link ShareBasedDistributionCenterSelector}.
 */
public class ShareBasedDistributionCenterSelector extends ShareBasedSelector<DistributionCenter> implements DistributionCenterSelector {

	/**
	 * Instantiates a new {@link ShareBasedDistributionCenterSelector}
	 * with the given shares.
	 *
	 * @param distributionCenters the distribution centers with shares
	 */
	public ShareBasedDistributionCenterSelector(Map<DistributionCenter, Double> distributionCenters) {
		super(distributionCenters);
	}
	
	/**
	 * Instantiates a new {@link ShareBasedDistributionCenterSelector}
	 * with the given list of {@link DistributionCenter}s.
	 * All {@link DistributionCenter}s receive the same share/probability.
	 *
	 * @param distributionCenters the {@link DistributionCenter}s
	 */
	public ShareBasedDistributionCenterSelector(Collection<DistributionCenter> distributionCenters) {
		super(distributionCenters);
	}

	/**
	 * Selects a distribution center from where a parcel will be delivered.
	 *
	 * @param recipient the recipient
	 * @param numOfParcels the number of parcels the recipient will order
	 * @param destination the {@link ParcelDestinationType}
	 * @param arrivalDate the planned arrival date
	 * @param deliveryService a delivery service tag
	 * @param otherParcels the other {@link Parcel}s the recipient already ordered
	 * @param randomNumber a random number
	 * @return the selected distribution center
	 */
	@Override
	public DistributionCenter select(PickUpParcelPerson recipient, int numOfParcels,
		ParcelDestinationType destination, Time arrivalDate, String deliveryService,
		Collection<Parcel> otherParcels, double randomNumber) {

		return this.select(randomNumber);
	}

}
