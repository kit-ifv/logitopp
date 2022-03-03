package edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

/**
 * ShareBasedParcelDestinationSelector is a {@link ParcelDemandModelStep} extending {@link ShareBasedSelector}.
 * Additionally a fallback selector without work is used for persons working outside the survey area.
 */
public class ShareBasedParcelDestinationSelector
		extends ShareBasedSelector<PickUpParcelPerson, PrivateParcelBuilder, ParcelDestinationType> 
		implements ParcelDemandModelStep<PickUpParcelPerson, PrivateParcelBuilder, ParcelDestinationType> {

	private final ShareBasedSelector<PickUpParcelPerson, PrivateParcelBuilder, ParcelDestinationType> fallback;
	private final Predicate<Zone> workZoneFilter;
	
	/**
	 * Instantiates a new {@link ShareBasedParcelDestinationSelector}
	 * with the given shares and work zone filter.
	 *
	 * @param shares the shares
	 * @param workZoneFilter the work zone filter
	 */
	public ShareBasedParcelDestinationSelector(Map<ParcelDestinationType, Double> shares, Predicate<Zone> workZoneFilter) {
		super(shares);
		Map<ParcelDestinationType, Double> fallbakMap = new LinkedHashMap<ParcelDestinationType, Double>(shares);
		fallbakMap.remove(WORK);
		this.fallback = new ShareBasedSelector<>(fallbakMap);
		this.workZoneFilter = workZoneFilter;
	}
	
	/**
	 * Instantiates a new {@link ShareBasedParcelDestinationSelector}
	 * with the given shares and no work zone filter.
	 *
	 * @param shares the shares
	 */
	public ShareBasedParcelDestinationSelector(Map<ParcelDestinationType, Double> shares) {
		this(shares, z -> true);
	}
	
	/**
	 * Instantiates a default {@link ShareBasedParcelDestinationSelector}
	 * with no zone filter.
	 */
	public ShareBasedParcelDestinationSelector() {
		this(z -> true);
	}
	
	/**
	 * Instantiates a new {@link ShareBasedParcelDestinationSelector}
	 * with equal shares for each {@link ParcelDestinationType}
	 * and the given work zone filter.
	 *
	 * @param workZoneFilter the work zone filter
	 */
	public ShareBasedParcelDestinationSelector(Predicate<Zone> workZoneFilter) {
		super(Arrays.asList(ParcelDestinationType.values()));
		this.fallback = new ShareBasedSelector<>(Arrays.asList(HOME, PACK_STATION));
		this.workZoneFilter = workZoneFilter;
	}


	/**
	 * Selects the {@link ParcelDestinationType} for a parcel.
	 * Uses fallback selector without {@link ParcelDestinationType#WORK}
	 * if the recipient's work zone does not pass the work zone filter.
	 *
	 * @param parcel the parcel for which a {@link ParcelDestinationType} is selected
	 * @param otherParcels the other {@link PrivateParcel}s the recipient already ordered
	 * @param numOfParcels the number of parcels the recipient will order
	 * @param randomNumber a random number
	 * @return the selected {@link ParcelDestinationType}
	 */
	@Override
	public ParcelDestinationType select(PrivateParcelBuilder parcel, Collection<PrivateParcelBuilder> otherParcels, int numOfParcels, double randomNumber) {
		PickUpParcelPerson recipient = parcel.getAgent();
		
		if (recipient.hasFixedZoneFor(ActivityType.WORK) && workZoneFilter.test(recipient.fixedZoneFor(ActivityType.WORK))) {
			return this.select(randomNumber);
			
		} else {
			return fallback.select(randomNumber);
		}
	}
	

}
