package edu.kit.ifv.mobitopp.simulation.distribution;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class DepotRelationsParser {
	
	private final Map<Integer, DistributionCenter> centerIds;
	
	public DepotRelationsParser(Collection<DistributionCenter> centers) {
		this.centerIds = new LinkedHashMap<>();
		centers.forEach(c -> this.centerIds.put(c.getId(), c));
	}
	
	public void parseRelations(String path) {
		this.parseRelations(new File(path));
	}
	
	public void parseRelations(File file) {
		this.parseRelations(CsvFile.createFrom(file));
	}
	
	public void parseRelations(CsvFile csv) {
		csv.stream().forEach(this::parse);
		
		System.out.println("\nDepot relations: (" + csv.getLength() + ")");
		this.centerIds.values().forEach(dc -> dc.getRegionalStructure().printRelations());
		System.out.println();
	}

	private void parse(Row row) {
		int from_id = row.valueAsInteger("from_depot");
		int to_id = row.valueAsInteger("to_depot");

		if (!centerIds.containsKey(from_id) || !centerIds.containsKey(to_id)) {
			throw new IllegalArgumentException("Tried to import depot relation " + from_id + " -> " + to_id + " but at leat one of the two depot ids is unknown!");
		}
		
		DistributionCenter from = centerIds.get(from_id);
		DistributionCenter to = centerIds.get(to_id);
		
		from.getRegionalStructure().addRelatedDeliveryHub(to);
	}
}
