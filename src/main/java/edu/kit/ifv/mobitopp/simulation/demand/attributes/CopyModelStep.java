package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

import java.util.Optional;
import java.util.function.Function;

public abstract class CopyModelStep<A extends ParcelAgent, P extends ParcelBuilder<A>, T> implements ParcelDemandModelStep<A, P, T> {

    private Function<P, ValueProvider<T>> copyGetter = null;

    @Override
    public void setBundleCopy(Function<P, ValueProvider<T>> copyGetter) {
       this.copyGetter = copyGetter;
    }

    @Override
    public Optional<Function<P, ValueProvider<T>>> isBundleCopyActive() {
        return Optional.ofNullable(copyGetter);
    }
}
