package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.simulation.parcels.agents.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.distribution.DistributionServiceProvider;
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
		notifyAgent();
	}

	protected abstract void notifyAgent();
	
	public IParcel get() {
		if (this.parcel == null) {
			this.parcel = doBuild();
		}
		 return this.parcel;
	}

	protected abstract IParcel doBuild();
	
	public Time getArrivalDate() {
		return this.arrivalDate.getValue();
	}
	
}
