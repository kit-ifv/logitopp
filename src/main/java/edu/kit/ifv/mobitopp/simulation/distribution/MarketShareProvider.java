package edu.kit.ifv.mobitopp.simulation.distribution;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class MarketShareProvider {
	
	private final Map<CEPServiceProvider, Double> totalShare;
	
	private final Map<CEPServiceProvider, Double> privateShare;
	private final Map<CEPServiceProvider, Double> businessShare;
	private final Map<CEPServiceProvider, Double> productionShare;
	private final Map<CEPServiceProvider, Double> consumptionShare;
	
	private final Map<CEPServiceProvider, Double> privateProductionShare;
	private final Map<CEPServiceProvider, Double> privateConsumptionShare;
	private final Map<CEPServiceProvider, Double> businessProductionShare;
	private final Map<CEPServiceProvider, Double> businessConsumptionShare;
	
	public MarketShareProvider(Collection<CEPServiceProvider> serviceProviders) {
		this.totalShare = new LinkedHashMap<>();
		
		
		this.privateShare = new LinkedHashMap<>();
		this.businessShare = new LinkedHashMap<>();
		this.productionShare = new LinkedHashMap<>();
		this.consumptionShare = new LinkedHashMap<>();
		
		this.privateProductionShare = new LinkedHashMap<>();
		this.privateConsumptionShare = new LinkedHashMap<>();
		this.businessProductionShare = new LinkedHashMap<>();
		this.businessConsumptionShare = new LinkedHashMap<>();
		
		serviceProviders.forEach(s -> {
			totalShare.put(s, 0.0);
			privateShare.put(s, 0.0);
			businessShare.put(s, 0.0);
			productionShare.put(s, 0.0);
			consumptionShare.put(s, 0.0);
			privateProductionShare.put(s, 0.0);
			privateConsumptionShare.put(s, 0.0);
			businessProductionShare.put(s, 0.0);
			businessConsumptionShare.put(s, 0.0);
		});
	}
	
	void addShares(CEPServiceProvider cepsp, double total, double prvate, double business, double production, double consumption, 
			double privateProduction, double privateConsumption, double businessProduction, double businessConsumption) {
		this.totalShare.put(cepsp, total);
		this.privateShare.put(cepsp, prvate);
		this.businessShare.put(cepsp, business);
		this.productionShare.put(cepsp, production);
		this.consumptionShare.put(cepsp, consumption);
		this.privateProductionShare.put(cepsp, privateProduction);
		this.privateConsumptionShare.put(cepsp, privateConsumption);
		this.businessProductionShare.put(cepsp, businessProduction);
		this.businessConsumptionShare.put(cepsp, businessConsumption);
	}
	
}
