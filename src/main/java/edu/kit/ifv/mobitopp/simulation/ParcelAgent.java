package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;

public interface ParcelAgent {
	
	public void setPlannedProductionQuantity(int quantity);
	
	public int getPlannedProductionQuantity();
	
	public void addActualProductionQuantity(int quantity);
	
	public int getRemainingProductionQuantity();

	
	
	public void removeParcel(IParcel parcel);

	public void addParcel(IParcel parcel);

	public ParcelPolicyProvider getPolicyProvider();

	public void addDelivered(PrivateParcel parcel);
		

}
