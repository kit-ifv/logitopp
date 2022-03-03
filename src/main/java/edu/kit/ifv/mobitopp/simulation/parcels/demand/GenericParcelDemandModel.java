package edu.kit.ifv.mobitopp.simulation.parcels.demand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.agents.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes.ParcelDemandModelStep;
import edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.demand.quantity.ParcelQuantityModel;
import edu.kit.ifv.mobitopp.util.functions.QuadConsumer;

public class GenericParcelDemandModel<A extends ParcelAgent, P extends ParcelBuilder<A>> implements ParcelDemandModel<A, P> {

	private final ParcelQuantityModel<A> parcelQuantityModel;
	private final List<QuadConsumer<P, Collection<P>, Integer, Double>> steps;
	private final Function<A, DoubleSupplier> randomProvider;
	
	private final Function<A, P> parcelFactory;


	public GenericParcelDemandModel(ParcelQuantityModel<A> numberOfParcelsSelector,
			Function<A, DoubleSupplier> randomProvider,
			Function<A, P> parcelFactory) {
		this.parcelQuantityModel = numberOfParcelsSelector;
		this.steps = new ArrayList<>();
		this.randomProvider = randomProvider;
		this.parcelFactory = parcelFactory;
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

		for (int i = 0; i < quantity; i++) {
			P parcel = this.parcelFactory.apply(recipient);

			for (QuadConsumer<P, Collection<P>, Integer, Double> step : this.steps) {
				step.apply(parcel, parcels, quantity, randomNumbers.getAsDouble());
			}
			
			parcels.add(parcel);
		}

		return parcels;
	}

}
