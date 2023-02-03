package edu.kit.ifv.mobitopp.simulation.demand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ParcelDemandModelStep;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.demand.quantity.ParcelQuantityModel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.util.functions.QuadConsumer;

public class GenericParcelDemandModel<A extends ParcelAgent, P extends ParcelBuilder<A>> implements ParcelDemandModel<A, P> {

	private final ParcelQuantityModel<A> parcelQuantityModel;
	private final List<QuadConsumer<P, Collection<P>, Integer, Double>> steps;
	private final Function<A, DoubleSupplier> randomProvider;
	private final Function<A, P> parcelFactory;

	private final Map<Integer, Integer> sizeStatistics;
	private final Map<DayOfWeek, Integer> dayStatistics;
	private int agents = 0;

	public GenericParcelDemandModel(ParcelQuantityModel<A> numberOfParcelsSelector,
			Function<A, DoubleSupplier> randomProvider,
			Function<A, P> parcelFactory) {
		this.parcelQuantityModel = numberOfParcelsSelector;
		this.steps = new ArrayList<>();
		this.randomProvider = randomProvider;
		this.parcelFactory = parcelFactory;
		this.sizeStatistics = new HashMap<>();
		this.dayStatistics = new LinkedHashMap<>();
	}

	public <T> void add(ParcelDemandModelStep<A, P, T> step, BiConsumer<P, ValueProvider<T>> propertySetter) {
		this.steps.add((parcel, otherParcels, numOfParcels, randomNumber) -> step.set(parcel, otherParcels,
				numOfParcels, randomNumber, propertySetter));
	}

	@Override
	public Collection<P> createParcelDemand(A recipient) {
		DoubleSupplier randomNumbers = randomProvider.apply(recipient);
		Collection<P> parcels = new ArrayList<>();
		
		int quantity = this.parcelQuantityModel.select(recipient, randomNumbers.getAsDouble());
		logQuantity(quantity);

		for (int i = 0; i < quantity; i++) {
			P parcel = this.parcelFactory.apply(recipient);

			for (QuadConsumer<P, Collection<P>, Integer, Double> step : this.steps) {
				step.apply(parcel, parcels, quantity, randomNumbers.getAsDouble());
			}
			
			parcel.notifyAgents();
			parcels.add(parcel);
			
			logDay(parcel.getArrivalDate().weekDay());
		}

		return parcels;
	}
	
	private void logQuantity(int q) {
		this.agents++;
		this.sizeStatistics.merge(q, 1, Integer::sum);
	}
	
	private void logDay(DayOfWeek day) {
		this.dayStatistics.merge(day, 1, Integer::sum);
	}
	
	public void printStatistics(String label) {
		List<Integer> index = this.sizeStatistics.keySet().stream().sorted().collect(Collectors.toList());
		
		int sum = this.sizeStatistics.entrySet().stream().mapToInt(e -> e.getKey() * e.getValue()).sum();
		
		System.out.println("\nGenerated " + sum + " " + label + " parcels for " + this.agents + " agents." );
		System.out.println("Number of " + label + " parcel distribution:");
		for (int i : index) {
			System.out.println("Order size " + i + ": " + this.sizeStatistics.get(i));
		}
		
		System.out.println("\nWeek distribution of " + label + " parcels:");
		for(DayOfWeek day: DayOfWeek.values()) {
			System.out.println(day.name() + ": " + this.dayStatistics.get(day));
		}
		System.out.println();
	}

}
