package edu.kit.ifv.mobitopp.simulation.parcels.orders;

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
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

public class ShareBasedParcelDestinationSelector extends ShareBasedSelector<ParcelDestinationType> implements ParcelDestinationSelector {

	private final ShareBasedSelector<ParcelDestinationType> fallback;
	private final Predicate<Zone> workZoneFilter;

	public ShareBasedParcelDestinationSelector(Map<ParcelDestinationType, Double> shares) {
		this(shares, z -> true);
	}
	
	public ShareBasedParcelDestinationSelector(Map<ParcelDestinationType, Double> shares, Predicate<Zone> workZoneFilter) {
		super(shares);
		Map<ParcelDestinationType, Double> fallbakMap = new LinkedHashMap<ParcelDestinationType, Double>(shares);
		fallbakMap.remove(WORK);
		this.fallback = new ShareBasedSelector<ParcelDestinationType>(fallbakMap);
		this.workZoneFilter = workZoneFilter;
	}
	
	public ShareBasedParcelDestinationSelector() {
		this(z -> true);
	}
	
	public ShareBasedParcelDestinationSelector(Predicate<Zone> workZoneFilter) {
		super(Arrays.asList(ParcelDestinationType.values()));
		this.fallback = new ShareBasedSelector<ParcelDestinationType>(Arrays.asList(HOME, PACK_STATION));
		this.workZoneFilter = workZoneFilter;
	}


	@Override
	public ParcelDestinationType select(PickUpParcelPerson recipient, int numOfParcels, Collection<Parcel> otherParcels, double randomNumber) {
		
		if (recipient.hasFixedZoneFor(ActivityType.WORK) && workZoneFilter.test(recipient.fixedZoneFor(ActivityType.WORK))) {
			return this.select(randomNumber);
			
		} else {
			return fallback.select(randomNumber);
		}
	}

}
