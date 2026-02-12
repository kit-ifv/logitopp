package edu.kit.ifv.mobitopp.simulation.demand.bundling;

import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.ParcelQuantityModel;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import lombok.val;

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
                val repaired = repairBundling(bundling, quantity);

				System.out.println("WARN: the bundling given for " + id + " does not match the given quantity (using repaired bundles below):\n" +
						" - Bundling: " + bundling.stream().map(Object::toString).collect(Collectors.joining(", ")) + "\n" +
						" - actual sum = " + sum + "\n" +
						" - expected quantity = " + quantity + "\n" +
                        " - repaired bundles = " + repaired.stream().map(Object::toString).collect(Collectors.joining(", ")) + "\n"
				);

                bundling = repaired;
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

    private List<Integer> repairBundling(List<Integer> bundling, int expectedQuantity) {
        if (expectedQuantity < 0) {
            throw new IllegalArgumentException("cannot repair bundling for expected quantity < 0: " + expectedQuantity);
        }

        List<Integer> repaired = new ArrayList<>();
        int sum = 0;
        for (Integer b: bundling) {
            sum += b;
            repaired.add(b);
        }

        if (sum == expectedQuantity) {
            return repaired;
        }

        if (sum < expectedQuantity) {
            return fillWithCopies(expectedQuantity, repaired, sum);
        }

        return dropBundlesAtEnd(expectedQuantity, sum, repaired);
    }

    private static List<Integer> dropBundlesAtEnd(int expectedQuantity, int sum, List<Integer> repaired) {
        int excess = sum - expectedQuantity;
        int i = repaired.size() - 1;

        while (i > 0 && excess > 0) {
            int value = repaired.get(i);

            if (value > excess) {
                repaired.set(i, value - excess);
                excess = 0;

            } else {
                excess -= value;
                repaired.remove(i);
                i--;
            }

        }

        return repaired;
    }

    private static List<Integer> fillWithCopies(int expectedQuantity, List<Integer> repaired, int sum) {
        if (repaired.isEmpty()) {
            if (expectedQuantity > 0) {
                repaired.add(0);
            }
            return repaired;
        }

        int copyIndex = 0;
        int missing = expectedQuantity - sum;
        while (missing > 0) {
            int value = repaired.get(copyIndex++);

            if (value <= missing) {
                repaired.add(value);
                missing -= value;
            } else {
                repaired.add(missing);
                missing = 0;
            }
        }

        return repaired;
    }
}
