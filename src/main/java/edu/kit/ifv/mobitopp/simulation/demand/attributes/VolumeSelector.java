package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.Collection;
import java.util.Random;

public class VolumeSelector<A extends ParcelAgent, P extends ParcelBuilder<A>> implements ParcelDemandModelStep<A, P, Double> {

    @Override
    public Double select(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
        ShipmentSize size = parcel.getSize().getValue();

        Random random = new Random((long) (randomNumber * (Long.MAX_VALUE -1)));

        return size.getVolume(random);
    }
}
