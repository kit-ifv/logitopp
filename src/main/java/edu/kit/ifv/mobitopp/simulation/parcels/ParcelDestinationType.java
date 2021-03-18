package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;

/**
 * The Enum ParcelDestinationType describes possible destinations for parcel deliveries.
 * and provides methods for mapping the delivery destination to a zone or location for a given parcel.
 */
public enum ParcelDestinationType {
	
	HOME {
		@Override
		public Location getLocation(Parcel parcel) {
			return parcel.getPerson().household().homeLocation();
		}
		
		@Override
		public Zone getZone(Parcel parcel) {
			return parcel.getPerson().household().homeZone();
		}
	},
	
	WORK {
		@Override
		public Location getLocation(Parcel parcel) {
			return parcel.getPerson().fixedDestinationFor(ActivityType.WORK);
		}
		
		@Override
		public Zone getZone(Parcel parcel) {
			return parcel.getPerson().fixedZoneFor(ActivityType.WORK);
		}
	}, 
	
	PACK_STATION {
		@Override
		public Location getLocation(Parcel parcel) {
			return parcel.getPerson().fixedDestinationFor(ActivityType.PICK_UP_PARCEL);
		}
		
		@Override
		public Zone getZone(Parcel parcel) {
			return parcel.getPerson().fixedZoneFor(ActivityType.PICK_UP_PARCEL);
		}
	};
	
	
	
	/**
	 * Gets the {@link Location} of the given parcel's delivery.
	 *
	 * @param parcel the parcel
	 * @return the location
	 */
	public Location getLocation(Parcel parcel) {
		throw new UnsupportedOperationException("Get Location is mot implemented for detination type " + this.name());
	}
	
	/**
	 * Gets the {@link Zone} of the given parcel's delivery.
	 *
	 * @param parcel the parcel
	 * @return the zone
	 */
	public Zone getZone(Parcel parcel) {
		throw new UnsupportedOperationException("Get Zone is mot implemented for detination type " + this.name());
	}
	
	/**
	 * Gets the {@link ZoneAndLocation} of the given parcel's delivery.
	 *
	 * @param parcel the parcel
	 * @return the zone and location
	 */
	public ZoneAndLocation getZoneAndLocation(Parcel parcel) {
		return new ZoneAndLocation(this.getZone(parcel), this.getLocation(parcel));
	}
}
