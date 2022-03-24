package edu.kit.ifv.mobitopp.simulation;

public interface ParcelAgent {
	
	public void setPlannedProductionQuantity(int quantity);
	
	public int getPlannedProductionQuantity();
	
	public void addActualProductionQuantity(int quantity);
	
	public int getRemainingProductionQuantity();

}
