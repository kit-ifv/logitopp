package edu.kit.ifv.mobitopp.simulation.demand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.InstantValueProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class BusinessParcelDemandCsvReader implements ParcelDemandModel<Business, BusinessParcelBuilder> {
	
	private static final String ARRIVAL_TIME = "ArrivalTime";
	private static final String SIZE = "Size";
	private static final String DISTRIBUTION_CENTER = "DistributionCenter";
	private static final String ARRIVAL_DAY = "ArrivalDay";
	private final DeliveryResults results;
	private final CsvFile file;
	private final Map<Integer, Collection<Row>> rowsPerBusiness;
	private final Map<String, DistributionCenter> distributionCenters;
	
	private static final String BUSINESS_ID = "To";
	

	public BusinessParcelDemandCsvReader(CsvFile file, Collection<DistributionCenter> distributionCenters, DeliveryResults results) {
		this.results = results;
		this.file = file;
		this.rowsPerBusiness = new LinkedHashMap<>();
		fillMap();
		
		this.distributionCenters = new LinkedHashMap<>();
		distributionCenters.forEach(dc -> this.distributionCenters.put(dc.getName(), dc));
	}
	
	private void fillMap() {
		file.stream().forEach(r ->
			putRow(r.valueAsInteger(BUSINESS_ID), r)
		);
	}
	
	private void putRow(int id, Row row) {
		if (!this.rowsPerBusiness.containsKey(id)) {
			this.rowsPerBusiness.put(id, new ArrayList<>());
		}
		
		this.rowsPerBusiness.get(id).add(row);
	}

	@Override
	public Collection<BusinessParcelBuilder> createParcelDemand(Business recipient) {
		ArrayList<BusinessParcelBuilder> parcels = new ArrayList<>();
		
		for (Row row : this.rowsPerBusiness.getOrDefault(recipient.getId(), List.of())) {			
			BusinessParcelBuilder builder = new BusinessParcelBuilder(recipient, results);
			
			Time day = Time.start.plusDays(row.valueAsInteger(ARRIVAL_DAY));
			String[] parts = row.get(ARRIVAL_TIME).split(" ")[2].split(":");
			day = day.plusHours(Integer.parseInt(parts[0]));
			day = day.plusMinutes(Integer.parseInt(parts[1]));
			day = day.plusSeconds(Integer.parseInt(parts[2]));
			
			builder.setArrivalDate(new InstantValueProvider<>(day));
			
			DistributionCenter distributionCenter = this.distributionCenters.get(row.get(DISTRIBUTION_CENTER).trim());
			builder.setDistributionCenter(new InstantValueProvider<DistributionCenter>(distributionCenter));
			
			ShipmentSize size = ShipmentSize.valueOf(row.get(SIZE));
			builder.setSize(new InstantValueProvider<>(size));
			
			builder.setConsumer(new InstantValueProvider<>(recipient));
			builder.setProducer(new InstantValueProvider<>(distributionCenter));
			
			
			parcels.add(builder);
		}
		
		return parcels;
	}

	@Override
	public void printStatistics(String label) {
		System.out.println(file.getLength() + " parcels for " + rowsPerBusiness.size() + " businesses were loaded from csv");
	}

}
