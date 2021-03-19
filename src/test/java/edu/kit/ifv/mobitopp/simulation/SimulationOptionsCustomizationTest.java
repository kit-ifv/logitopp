package edu.kit.ifv.mobitopp.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.person.SimulationOptions;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.time.Time;

public class SimulationOptionsCustomizationTest {
	
	private SimulationOptions delegate;

	private DestinationChoiceModel oldDcm;
	private DestinationChoiceModel newDcm;
	
	private TourBasedModeChoiceModel oldMcm;
	private TourBasedModeChoiceModel newMcm;
	
	private ReschedulingStrategy oldRescheduling;
	private ReschedulingStrategy newRescheduling;
	
	private ZoneBasedRouteChoice oldRc;
	private ZoneBasedRouteChoice newRc;
	
	private ImpedanceIfc oldImpedance;
	private ImpedanceIfc newImpedance;
	
	private RideSharingOffers oldRso;
	private RideSharingOffers newRso;
	
	private ActivityPeriodFixer oldFixer;
	private ActivityPeriodFixer newFixer;
	
	private ActivityStartAndDurationRandomizer oldRandomizer;
	private ActivityStartAndDurationRandomizer newRandomizer;
		
	private int maxDifferenceMinutes;
	private Time simulationStart;
	private Time simulationEnd;
	
	@BeforeEach
	public void setUp() {
		this.delegate = mock(SimulationOptions.class);
		
		this.oldDcm = mock(DestinationChoiceModel.class);
		when(delegate.destinationChoiceModel()).thenReturn(oldDcm);
		this.newDcm = mock(DestinationChoiceModel.class);
		
		this.oldMcm = mock(TourBasedModeChoiceModel.class);
		when(delegate.modeChoiceModel()).thenReturn(oldMcm);
		this.newMcm = mock(TourBasedModeChoiceModel.class);
		
		this.oldRescheduling = mock(ReschedulingStrategy.class);
		when(delegate.rescheduling()).thenReturn(oldRescheduling);
		this.newRescheduling = mock(ReschedulingStrategy.class);
		
		this.oldRc = mock(ZoneBasedRouteChoice.class);
		when(delegate.routeChoice()).thenReturn(oldRc);
		this.newRc = mock(ZoneBasedRouteChoice.class);
		
		this.oldImpedance = mock(ImpedanceIfc.class);
		when(delegate.impedance()).thenReturn(oldImpedance);
		this.newImpedance = mock(ImpedanceIfc.class);
		
		this.oldRso = mock(RideSharingOffers.class);
		when(delegate.rideSharingOffers()).thenReturn(oldRso);
		this.newRso = mock(RideSharingOffers.class);
		
		this.oldFixer = mock(ActivityPeriodFixer.class);
		when(delegate.activityPeriodFixer()).thenReturn(oldFixer);
		this.newFixer = mock(ActivityPeriodFixer.class);
		
		this.oldRandomizer = mock(ActivityStartAndDurationRandomizer.class);
		when(delegate.activityDurationRandomizer()).thenReturn(oldRandomizer);
		this.newRandomizer = mock(ActivityStartAndDurationRandomizer.class);
		
		this.maxDifferenceMinutes = 42;
		when(delegate.maxDifferenceMinutes()).thenReturn(maxDifferenceMinutes);
		this.simulationStart = Time.start;
		when(delegate.simulationStart()).thenReturn(simulationStart);
		this.simulationEnd = Time.future;
		when(delegate.simulationEnd()).thenReturn(simulationEnd);
	}
	
	private void verifyUncustomitable(SimulationOptionsCustomization customization) {
		assertEquals(maxDifferenceMinutes, customization.maxDifferenceMinutes());
		assertEquals(simulationStart, customization.simulationStart());
		assertEquals(simulationEnd, customization.simulationEnd());
	}
	
	@Test
	public void allArgsConstructorReplaceAll() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newDcm, newMcm, newRescheduling, newRc, newImpedance, newRso, newFixer, newRandomizer);
	
		assertEquals(newDcm, custom.destinationChoiceModel());
		assertEquals(newMcm, custom.modeChoiceModel());
		assertEquals(newRescheduling, custom.rescheduling());
		assertEquals(newRc, custom.routeChoice());
		assertEquals(newImpedance, custom.impedance());
		assertEquals(newRso, custom.rideSharingOffers());
		assertEquals(newFixer, custom.activityPeriodFixer());
		assertEquals(newRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void allArgsConstructorReplaceDcm() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newDcm, null, null, null, null, null, null, null);
	
		assertEquals(newDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void allArgsConstructorReplaceMcm() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, null, newMcm, null, null, null, null, null, null);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(newMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void allArgsConstructorReplaceRescheduling() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, null, null, newRescheduling, null, null, null, null, null);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(newRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void allArgsConstructorReplaceRc() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, null, null, null, newRc, null, null, null, null);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(newRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void allArgsConstructorReplaceImpedance() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, null, null, null, null, newImpedance, null, null, null);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(newImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void allArgsConstructorReplaceRso() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, null, null, null, null, null, newRso, null, null);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(newRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void allArgsConstructorReplaceFixer() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, null, null, null, null, null, null, newFixer, null);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(newFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void allArgsConstructorReplaceRandomizer() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, null, null, null, null, null, null, null, newRandomizer);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(newRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void delegateConstructor() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void customizeAll() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate);
		
		custom.customize(newDcm, newMcm, newRescheduling, newRc, newImpedance, newRso, newFixer, newRandomizer);
				
		assertEquals(newDcm, custom.destinationChoiceModel());
		assertEquals(newMcm, custom.modeChoiceModel());
		assertEquals(newRescheduling, custom.rescheduling());
		assertEquals(newRc, custom.routeChoice());
		assertEquals(newImpedance, custom.impedance());
		assertEquals(newRso, custom.rideSharingOffers());
		assertEquals(newFixer, custom.activityPeriodFixer());
		assertEquals(newRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void setDelegate() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(mock(SimulationOptions.class));
		
		custom.setDelegate(delegate);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void reset() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newDcm, newMcm, newRescheduling, newRc, newImpedance, newRso, newFixer, newRandomizer);
	
		assertEquals(newDcm, custom.destinationChoiceModel());
		assertEquals(newMcm, custom.modeChoiceModel());
		assertEquals(newRescheduling, custom.rescheduling());
		assertEquals(newRc, custom.routeChoice());
		assertEquals(newImpedance, custom.impedance());
		assertEquals(newRso, custom.rideSharingOffers());
		assertEquals(newFixer, custom.activityPeriodFixer());
		assertEquals(newRandomizer, custom.activityDurationRandomizer());
		verifyUncustomitable(custom);
		
		custom.reset();
		
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void constructorReplaceDcm() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newDcm);
	
		assertEquals(newDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void constructorReplaceMcm() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newMcm);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(newMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void constructorReplaceRescheduling() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newRescheduling);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(newRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void constructorReplaceRc() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newRc);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(newRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void constructorReplaceImpedance() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newImpedance);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(newImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void constructorReplaceRso() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newRso);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(newRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void constructorReplaceFixer() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newFixer);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(newFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void constructorReplaceRandomizer() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate, newRandomizer);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(newRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void customizeDcm() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate);
		custom.customize(newDcm);
	
		assertEquals(newDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void customizeMcm() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate);
			custom.customize(newMcm);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(newMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void customizeRescheduling() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate);
			custom.customize(newRescheduling);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(newRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void customizeRc() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate);
			custom.customize(newRc);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(newRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void customizeImpedance() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate);
			custom.customize(newImpedance);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(newImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void customizeRso() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate);
			custom.customize(newRso);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(newRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void customizeFixer() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate);
			custom.customize(newFixer);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(newFixer, custom.activityPeriodFixer());
		assertEquals(oldRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
	
	@Test
	public void customizeRandomizer() {
		SimulationOptionsCustomization custom = 
			new SimulationOptionsCustomization(this.delegate);
			custom.customize(newRandomizer);
	
		assertEquals(oldDcm, custom.destinationChoiceModel());
		assertEquals(oldMcm, custom.modeChoiceModel());
		assertEquals(oldRescheduling, custom.rescheduling());
		assertEquals(oldRc, custom.routeChoice());
		assertEquals(oldImpedance, custom.impedance());
		assertEquals(oldRso, custom.rideSharingOffers());
		assertEquals(oldFixer, custom.activityPeriodFixer());
		assertEquals(newRandomizer, custom.activityDurationRandomizer());
		
		verifyUncustomitable(custom);
	}
}
