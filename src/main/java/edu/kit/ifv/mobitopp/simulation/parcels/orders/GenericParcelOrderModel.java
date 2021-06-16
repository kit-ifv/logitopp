package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.util.functions.QuadConsumer;

public class GenericParcelOrderModel<R> implements ParcelOrderModel<R> {

	private final NumberOfParcelsSelector<R> numberOfParcelsSelector;
	private final List<QuadConsumer<ParcelBuilder, Collection<ParcelBuilder>, Integer, Double>> steps;
	private final Function<R, DoubleSupplier> randomProvider;
	private final BiConsumer<ParcelBuilder, R> parcelInitializer;
	private final Function<ParcelBuilder, IParcel> parcelBuildFcuntion;

	public GenericParcelOrderModel(NumberOfParcelsSelector<R> numberOfParcelsSelector,
			Function<R, DoubleSupplier> randomProvider, BiConsumer<ParcelBuilder, R> parcelInitializer,
			Function<ParcelBuilder, IParcel> parcelBuildFcuntion) {
		this.numberOfParcelsSelector = numberOfParcelsSelector;
		this.steps = new ArrayList<>();
		this.randomProvider = randomProvider;
		this.parcelInitializer = parcelInitializer;
		this.parcelBuildFcuntion = parcelBuildFcuntion;
	}

	public <T> void add(ParcelOrderStep<T> step, BiConsumer<ParcelBuilder, T> propertySetter) {
		this.steps.add((parcel, otherParcels, numOfParcels, randomNumber) -> step.set(parcel, otherParcels,
				numOfParcels, randomNumber, propertySetter));
	}

	@Override
	public Collection<IParcel> createParcelOrders(R recipient) {
		DoubleSupplier randomNumbers = randomProvider.apply(recipient);

		int numOfParcels = this.numberOfParcelsSelector.select(recipient, randomNumbers.getAsDouble());

		Collection<ParcelBuilder> parcels = new ArrayList<>();
		
		for (int i = 0; i < numOfParcels; i++) {
			ParcelBuilder parcel = new ParcelBuilder();
			parcelInitializer.accept(parcel, recipient);

			for (QuadConsumer<ParcelBuilder, Collection<ParcelBuilder>, Integer, Double> step : this.steps) {
				step.apply(parcel, parcels, numOfParcels, randomNumbers.getAsDouble());
			}
			
			parcels.add(parcel);
		}

		return parcels.stream().map(parcelBuildFcuntion).collect(toList());
	}

}
