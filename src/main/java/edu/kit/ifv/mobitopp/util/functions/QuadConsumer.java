package edu.kit.ifv.mobitopp.util.functions;

@FunctionalInterface
public interface QuadConsumer<One, Two, Three, Four> {

	public void apply(One one, Two two, Three three, Four four);
	
}
