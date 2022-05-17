package edu.kit.ifv.mobitopp.simulation.distribution.tours.precalculated;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.activityschedule.DeliveryActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

public class LinkDeliveryActivityBuilder extends DeliveryActivityBuilder {
	
	@Override
	protected void verifyLocations() {
		if (this.parcels.stream().map(IParcel::getZone).distinct().count() > 1) {
			throw new IllegalStateException(
					"All parcels within a link delivery activity should have the same delivery zone.");
		}
	}
	
	@Override
	public Location getLocation() {
		if (this.parcels.isEmpty()) {
			throw new IllegalStateException("Cannot determine location of delivery without parcels");
		}
		
		List<IParcel> list = new ArrayList<>(this.parcels);
		list.sort(Comparator.comparingDouble(p -> p.getLocation().roadPosition()));
		
		return list.get(list.size()/2).getLocation();
	}
	
	public LinkDeliveryActivityBuilder merge(DeliveryActivityBuilder other) {
		LinkDeliveryActivityBuilder builder = new LinkDeliveryActivityBuilder();
		builder.addParcels(this.getParcels());
		builder.addParcels(other.getParcels());
		
		return builder;
	}

}
