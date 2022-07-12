package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionServiceProvider;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

public abstract class ParcelBuilder<P extends ParcelAgent> {
	
	@Getter private final P agent;
	@Getter private final DeliveryResults results;
	
	@Getter @Setter private ValueProvider<DistributionServiceProvider> serviceProvider;
	@Getter @Setter private ValueProvider<DistributionCenter> distributionCenter;
	@Getter @Setter private ValueProvider<ParcelAgent> consumer;
	@Getter @Setter private ValueProvider<ParcelAgent> producer;
	@Setter private ValueProvider<Time> arrivalDate;
	@Getter @Setter private ValueProvider<ShipmentSize> size;
	
	private IParcel parcel;
	
	public ParcelBuilder(P agent, DeliveryResults results) {
		this.agent = agent;
		this.results = results;
	}

	public final void notifyProducer() {
		if (getProducer().isDetermined()) {
			getProducer().getValue().addActualProductionQuantity(1);
		}
	}
	
	public IParcel get() {
		if (parcel == null) {
			parcel = doBuild();
			parcel.getProducer().addParcel(parcel);
		}
		
		return parcel;
	}

	protected abstract IParcel doBuild();
	
	public Time getArrivalDate() {
		return arrivalDate.getValue();
	}
	
}
