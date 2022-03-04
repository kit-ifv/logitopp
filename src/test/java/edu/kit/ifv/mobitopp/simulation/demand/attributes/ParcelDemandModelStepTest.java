package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

public class ParcelDemandModelStepTest {
	
	private boolean predetermine;
	private ParcelDemandModelStepImpl step;
	private ParcelDemandModelStepImpl spiedStep;
	private ValueProvider<String> result;
	private BiConsumer<ParcelBuilder<ParcelAgent>, ValueProvider<String>> propertySetter;
	
	static class ParcelDemandModelStepImpl implements ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String> {
		private Supplier<Boolean> getter;

		public ParcelDemandModelStepImpl(Supplier<Boolean> getter) {
			this.getter = getter;
		}

		@Override
		public String select(ParcelBuilder<ParcelAgent> parcel, Collection<ParcelBuilder<ParcelAgent>> otherParcels,
				int numOfParcels, double randomNumber) {
			return "hello";
		}
		
		@Override
		public boolean determinePreSimulation(ParcelBuilder<ParcelAgent> parcel,
				Collection<ParcelBuilder<ParcelAgent>> otherParcels, int numOfParcels, double randomNumber) {
			return this.getter.get();
		}
	}
	
	@BeforeEach
	public void setUp() {
			
		this.step = new ParcelDemandModelStepImpl(() -> this.predetermine);		
		this.spiedStep = spy(this.step);
		this.propertySetter = (p,v) -> this.result=v;
	}

	@Test
	public void predeterminedStep() {
		this.predetermine = true;
		
		this.spiedStep.set(null, null, 2, 1.7, propertySetter);
		
		assertTrue(this.result instanceof DeterminedValueProvider);
		assertTrue(this.result.isDetermined());
		assertEquals("hello", this.result.getValue());
		verify(this.spiedStep, times(1)).select(null, null, 2, 1.7);
		
	}
	
	
	@Test
	public void latentStep() {
		this.predetermine = false;
		
		this.spiedStep.set(null, null, 1, 4.2, propertySetter);
		
		verify(this.spiedStep, times(0)).select(any(), any(), anyInt(), anyDouble());
		assertTrue(this.result instanceof LatentValueProvider);
		assertFalse(this.result.isDetermined());
		assertEquals("hello", this.result.getValue());
		assertTrue(this.result.isDetermined());
		verify(this.spiedStep, times(1)).select(null, null, 1, 4.2);
		
	}
	
}
