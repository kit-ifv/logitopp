package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;

public class ParcelProductionQuantityModel<P extends ParcelAgent> implements ParcelQuantityModel<P> {
	
	final private ParcelQuantityModel<P> quantityModel;
	
	public ParcelProductionQuantityModel(ParcelQuantityModel<P> quantityModel) {
		this.quantityModel = quantityModel;
	}
	
	@Override
	public int select(P producer, double randomNumber) {
		
		int totalQuantity = this.quantityModel.select(producer, randomNumber);
		producer.setPlannedProductionQuantity(totalQuantity);
				
		return producer.getRemainingProductionQuantity();
	}

}
