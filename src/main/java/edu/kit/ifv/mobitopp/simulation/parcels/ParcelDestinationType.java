package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;

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
	
	
	
	public Location getLocation(Parcel parcel) {
		throw new UnsupportedOperationException("Get Location is mot implemented for detination type " + this.name());
	}
	
	public Zone getZone(Parcel parcel) {
		throw new UnsupportedOperationException("Get Zone is mot implemented for detination type " + this.name());
	}
	
	public ZoneAndLocation getZoneAndLocation(Parcel parcel) {
		return new ZoneAndLocation(this.getZone(parcel), this.getLocation(parcel));
	}
}
