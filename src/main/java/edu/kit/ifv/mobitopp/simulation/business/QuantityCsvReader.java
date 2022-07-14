package edu.kit.ifv.mobitopp.simulation.business;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.demand.quantity.ParcelQuantityModel;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class QuantityCsvReader implements ParcelQuantityModel<Business> {
	
	private final Map<Long, Integer> quantityById;
		
	public QuantityCsvReader(String filePath) {
		this(new File(filePath));
	}
	
	public QuantityCsvReader(File file) {
		this(CsvFile.createFrom(file));
	}
	
	public QuantityCsvReader(CsvFile csv) {
		this.quantityById = new HashMap<>();
		fillQuantityMap(csv);
	}
	
	private void fillQuantityMap(CsvFile csv) {
		csv.stream().forEach(r -> {
			long id = Long.parseLong(r.get("id"));
			int quantity = r.valueAsInteger("quantity");
			
			this.quantityById.put(id, quantity);
		});
	}

	@Override
	public int select(Business recipient, double randomNumber) {
		return this.quantityById.getOrDefault(recipient.getId(), 0);
	}

}
