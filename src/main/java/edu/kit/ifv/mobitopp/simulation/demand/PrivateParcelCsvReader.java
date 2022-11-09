package edu.kit.ifv.mobitopp.simulation.demand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.InstantValueProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class PrivateParcelCsvReader implements ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> {
	
	private static final String ARRIVAL_TIME = "ArrivalTime";
	private static final String SIZE = "Size";
	private static final String DISTRIBUTION_CENTER = "DistributionCenter";
	private static final String DESTINATION_TYPE = "DestinationType";
	private static final String ARRIVAL_DAY = "ArrivalDay";
	private final DeliveryResults results;
	private final CsvFile file;
	private final Map<Integer, Collection<Row>> rowsPerPerson;
	private final Map<String, DistributionCenter> distributionCenters;
	
	private static final String PERSON_ID = "RecipientID";
	

	public PrivateParcelCsvReader(CsvFile file, Collection<DistributionCenter> distributionCenters, DeliveryResults results) {
		this.results = results;
		this.file = file;
		this.rowsPerPerson = new LinkedHashMap<>();
		fillMap();
		
		this.distributionCenters = new LinkedHashMap<>();
		distributionCenters.forEach(dc -> this.distributionCenters.put(dc.getName(), dc));
	}
	
	private void fillMap() {
		file.stream().forEach(r ->
			putRow(r.valueAsInteger(PERSON_ID), r)
		);
	}
	
	private void putRow(int id, Row row) {
		if (!this.rowsPerPerson.containsKey(id)) {
			this.rowsPerPerson.put(id, new ArrayList<>());
		}
		
		this.rowsPerPerson.get(id).add(row);
	}

	@Override
	public Collection<PrivateParcelBuilder> createParcelDemand(PickUpParcelPerson recipient) {
		ArrayList<PrivateParcelBuilder> parcels = new ArrayList<>();
		
		for (Row row : this.rowsPerPerson.getOrDefault(recipient.getOid(), List.of())) {			
			PrivateParcelBuilder builder = new PrivateParcelBuilder(recipient, results);
			
			Time day = Time.start.plusDays(row.valueAsInteger(ARRIVAL_DAY));
			String[] parts = row.get(ARRIVAL_TIME).split(" ")[2].split(":");
			day = day.plusHours(Integer.parseInt(parts[0]));
			day = day.plusMinutes(Integer.parseInt(parts[1]));
			day = day.plusSeconds(Integer.parseInt(parts[2]));
			
			builder.setArrivalDate(new InstantValueProvider<>(day));			
			
			ParcelDestinationType destination = ParcelDestinationType.valueOf(row.get(DESTINATION_TYPE));
			builder.setDestinationType(new InstantValueProvider<>(destination));
			
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
		System.out.println(file.getLength() + " parcels for " + rowsPerPerson.size() + " private persons were loaded from csv");
	}

}
