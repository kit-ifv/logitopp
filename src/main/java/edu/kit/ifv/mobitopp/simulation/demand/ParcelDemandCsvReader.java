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

public class ParcelDemandCsvReader implements ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> {
	
	private final DeliveryResults results;
	private final CsvFile file;
	private final Map<Integer, Collection<Row>> rowsPerPerson;
	private final Map<String, DistributionCenter> distributionCenters;
	
	private static final String PERSON_ID = "RecipientID";
	

	public ParcelDemandCsvReader(CsvFile file, Collection<DistributionCenter> distributionCenters, DeliveryResults results) {
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
			
			InstantValueProvider<Time> arrivalDate = new InstantValueProvider<>(Time.start.plusDays(row.valueAsInteger("ArrivalDay")));
			builder.setArrivalDate(arrivalDate);
			
			InstantValueProvider<ParcelDestinationType> destinationType = new InstantValueProvider<>(ParcelDestinationType.valueOf(row.get("DestinationType")));
			builder.setDestinationType(destinationType);
			
			DistributionCenter distributionCenter = this.distributionCenters.get(row.get("DistributionCenter").trim());
			builder.setDistributionCenter(new InstantValueProvider<DistributionCenter>(distributionCenter));
			
			
			builder.setConsumer(new InstantValueProvider<>(recipient));
			builder.setProducer(new InstantValueProvider<>(distributionCenter));
			builder.setSize(new InstantValueProvider<>(ShipmentSize.SMALL));
			
			parcels.add(builder);
		}
		
		return parcels;
	}

	@Override
	public void printStatistics(String label) {
		//TODO
	}

}
