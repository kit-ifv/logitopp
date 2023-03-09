package edu.kit.ifv.mobitopp.simulation.distribution;

import java.util.ArrayList;
import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

public class DepotStorage {
	
	private final Collection<IParcel> currentParcels;
	private final Collection<IParcel> collectedPickups;
	private final Collection<IParcel> pickupRequests;
	private final Collection<PlannedDeliveryTour> plannedTours;
	
	public DepotStorage() {
		this.currentParcels = new ArrayList<>();
		this.collectedPickups = new ArrayList<>();
		this.pickupRequests = new ArrayList<>();
		
		this.plannedTours = new ArrayList<>();
		
	}
	
	
	public void addParcel(IParcel parcel) {
		this.currentParcels.add(parcel);
	}
	
	public void addParcels(Collection<IParcel> parcels) {
		this.currentParcels.addAll(parcels);
	}
	
	public void removeParcel(IParcel parcel) {
		this.currentParcels.remove(parcel);
	}
	
	public void removeParcels(Collection<IParcel> parcels) {
		this.currentParcels.removeAll(parcels);
	}
	
	public Collection<IParcel> getParcels() {
		return this.currentParcels;
	}
	
	
	public void addRequest(IParcel request) {
		this.pickupRequests.add(request);
	}
	
	public void addRequests(Collection<IParcel> requests) {
		this.pickupRequests.addAll(requests);
	}
	
	public void removeRequest(IParcel parcel) {
		this.pickupRequests.remove(parcel);
	}
	
	public void removeRequests(Collection<IParcel> parcels) {
		this.pickupRequests.removeAll(parcels);
	}
	
	public Collection<IParcel> getRequests() {
		return this.pickupRequests; //TODO only return view?
	}
	
	
	
	public void receive(IParcel parcel)  {
		this.collectedPickups.add(parcel);
	}
	
	public void receive(Collection<IParcel> requests) {
		this.collectedPickups.addAll(requests);
	}
	
	
	
	public void addPlannedTour(PlannedDeliveryTour tour) {
		this.plannedTours.add(tour);
		
		removeParcels(tour.getDeliveryParcels());
		removeRequests(tour.getPickUpRequests());
	}
	
	public void addPlannedTours(Collection<PlannedDeliveryTour> tours) {
		tours.forEach(this::addPlannedTour);
	}
	
	public void pickPlannedTour(PlannedDeliveryTour tour) {
		this.plannedTours.remove(tour);
	}
	
	public void deletePlannedTour(PlannedDeliveryTour tour) {
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


	public Collection<PlannedDeliveryTour> getPlannedTours() {
		return this.plannedTours;
	}

}
