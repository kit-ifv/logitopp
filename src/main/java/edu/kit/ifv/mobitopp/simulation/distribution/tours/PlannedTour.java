package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivity;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public interface PlannedTour {
	
	public boolean isReturning();
	
	public Optional<DistributionCenter> nextHub();
	
	public boolean isReplanningAllowed();
	
	public Collection<IParcel> getAllParcels();
	
	public Collection<IParcel> getDeliveryParcels();
	
	public Collection<IParcel> getPickUpRequests();
	
	public RelativeTime getPlannedDuration();
	
	public Time prepare(Time currentTime, DeliveryVehicle vehicle, ImpedanceIfc impedance);
	
	public List<ParcelActivity> getPreparedStops();
	

}
