package edu.kit.ifv.mobitopp.simulation.distribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

public class DepotStorage {
	
	private final Map<Integer, IParcel> currentParcels;
	private final Collection<IParcel> collectedPickups;
	private final Map<Integer, IParcel> pickupRequests;
	private final Collection<PlannedTour> plannedTours;
	
	public DepotStorage() {
		this.currentParcels = new LinkedHashMap<>();
		this.collectedPickups = new ArrayList<>();
		this.pickupRequests = new LinkedHashMap<>();
		
		this.plannedTours = new ArrayList<>();
		
	}
	
	
	public void addParcel(IParcel parcel) {
		this.currentParcels.put(parcel.getOId(), parcel);
	}
	
	public void addParcels(Collection<IParcel> parcels) {
		parcels.forEach(this::addParcel);
	}
	
	public void removeParcel(IParcel parcel) {
		this.currentParcels.remove(parcel.getOId());
	}
	
	public void removeParcels(Collection<IParcel> parcels) {
		parcels.forEach(this::removeParcel);
	}
	
	public Collection<IParcel> getParcels() {
		return this.currentParcels.values();
	}
	
	
	public void addRequest(IParcel request) {
		this.pickupRequests.put(request.getOId(), request);
	}
	
	public void addRequests(Collection<IParcel> requests) {
		requests.forEach(this::addRequest);
	}
	
	public void removeRequest(IParcel parcel) {
		this.pickupRequests.remove(parcel.getOId());
	}
	
	public void removeRequests(Collection<IParcel> parcels) {
		parcels.forEach(this::removeRequest);
	}
	
	public Collection<IParcel> getRequests() {
		return this.pickupRequests.values(); //TODO only return view?
	}
	
	
	
	public void receive(IParcel parcel)  {
		this.collectedPickups.add(parcel);
	}
	
	public void receive(Collection<IParcel> requests) {
		this.collectedPickups.addAll(requests);
	}
	
	
	
	public void addPlannedTour(PlannedTour tour) {
		this.plannedTours.add(tour);
		
		removeParcels(tour.getDeliveryParcels());
		removeRequests(tour.getPickUpRequests());
	}
	
	public void addPlannedTours(Collection<PlannedTour> tours) {
		tours.forEach(this::addPlannedTour);
	}
	
	public void pickPlannedTour(PlannedTour tour) {
		this.plannedTours.remove(tour);
	}
	
	public void deletePlannedTour(PlannedTour tour) {
		this.plannedTours.remove(tour);
		
		addParcels(tour.getDeliveryParcels());
		addRequests(tour.getPickUpRequests());
	}
	
	
	
	public int currentDeliveryDemand() {
		return this.currentParcels.size();
	}

	public int currentShippingDemand() {
		return this.pickupRequests.size();
	}


	public Collection<PlannedTour> getPlannedTours() {
		return this.plannedTours;
	}

}
