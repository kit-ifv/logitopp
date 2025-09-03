package edu.kit.ifv.mobitopp.simulation.demand.bundling;

import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.ParcelQuantityModel;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuantityAndBundlingCsvReader implements ParcelQuantityModel<Business>, ParcelBundlingModel<Business> {

	private final Map<Long, Integer> quantityById;
	private final Map<Long, List<Integer>> bundlesById;

	public QuantityAndBundlingCsvReader(String filePath) {
		this(new File(filePath));
	}

	public QuantityAndBundlingCsvReader(File file) {
		this(CsvFile.createFrom(file));
	}

	public QuantityAndBundlingCsvReader(CsvFile csv) {
		this.quantityById = new HashMap<>();
		this.bundlesById = new HashMap<>();
		fillQuantityMap(csv);
	}
	
	private void fillQuantityMap(CsvFile csv) {
		csv.stream().forEach(r -> {
			long id = Long.parseLong(r.get("id"));
			int quantity = r.valueAsInteger("quantity");

			List<Integer> bundling = Arrays.stream(r.get("shipments")
					.replace(" ", "")
					.split(";"))
					.map(Integer::parseInt)
					.filter(i -> i > 0)
					.collect(Collectors.toList());

			int sum = bundling.stream().mapToInt(i -> i).sum();
			if (sum != quantity) {
				System.out.println("WARN: the bundling given for " + id + " does not match the given quantity (using only bundles of size 1 instead):\n" +
						" - Bundling: " + bundling.stream().map(Object::toString).collect(Collectors.joining(", ")) + "\n" +
						" - actual sum = " + sum + "\n" +
						" - expected quantity = " + quantity + "\n"
				);

				bundling = Collections.nCopies(quantity, 1);
			}
			
			this.quantityById.put(id, quantity);
			this.bundlesById.put(id, bundling);
		});
	}

	@Override
	public int select(Business recipient, double randomNumber) {
		if (!quantityById.containsKey(recipient.getId())) {
			System.out.println("Warning: quantity/bundling csv does not contain key: " + recipient.getId());
		}

		return this.quantityById.getOrDefault(recipient.getId(), 0);
	}

	@Override
	public List<Integer> selectBundling(Business agent, int quantity, double randomNumber) {
		if (!quantityById.containsKey(agent.getId())) {
			System.out.println("Warning: quantity/bundling csv does not contain key: " + agent.getId());
		}

        return this.bundlesById.getOrDefault(agent.getId(), Collections.emptyList());
	}
}
