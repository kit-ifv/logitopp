package edu.kit.ifv.mobitopp.simulation.distribution;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class MarketSharesParser {
	
	private final Map<String, CEPServiceProvider> serviceProviders;
	
	public MarketSharesParser(Collection<CEPServiceProvider> serviceProviders) {
		
		this.serviceProviders = new LinkedHashMap<>();
		serviceProviders.forEach(s -> this.serviceProviders.put(s.getName(), s));
	}
	
	
	public MarketShareProvider parse(String path) {
		return parse(CsvFile.createFrom(new File(path)));
	}
	
	public MarketShareProvider parse(File file) {
		return parse(CsvFile.createFrom(file));
	}
	
	public MarketShareProvider parse(CsvFile csv) {
		MarketShareProvider provider = new MarketShareProvider(serviceProviders.values());

		csv.stream().forEach(r -> parse(r, provider));
		
		return provider;
	}

	private void parse(Row r, MarketShareProvider provider) {
		String name = r.get("cepsp");
		CEPServiceProvider cepsp = serviceProviders.get(name);
		
		double total = r.valueAsDouble("total");
		double prvate = r.valueAsDouble("private");
		double business = r.valueAsDouble("business");
		double production = r.valueAsDouble("production");
		double consumption = r.valueAsDouble("consumption");
		
		double privateProduction = r.valueAsDouble("privateProduction");
		double privateConsumption = r.valueAsDouble("privateConsumption");
		double businessProduction = r.valueAsDouble("businessProduction");
		double businessConsumption = r.valueAsDouble("businessConsumption");
		
		provider.addShares(cepsp, total, prvate, business, production, consumption, privateProduction, privateConsumption, businessProduction, businessConsumption);		
	}

}
