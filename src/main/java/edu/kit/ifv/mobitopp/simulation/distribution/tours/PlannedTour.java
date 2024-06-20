package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivity;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public interface PlannedTour {

	public int getId();

	public int journeyId();
	
	public boolean isReturning();

	public DistributionCenter depot();
	public Optional<DistributionCenter> nextHub();

	public Optional<Connection > usedConnection();

	public boolean usesTram();
	
	public boolean isReplanningAllowed();
	
	public Collection<IParcel> getAllParcels();
	
	public Collection<IParcel> getDeliveryParcels();
	
	public Collection<IParcel> getPickUpRequests();
	
	public RelativeTime getPlannedDuration();
	
	public Time prepare(Time currentTime, DeliveryVehicle vehicle, ImpedanceIfc impedance);
	
	public List<ParcelActivity> getPreparedStops();

	public List<ParcelActivityBuilder> getPlannedStops();

	public VehicleType getVehicleType();

}
