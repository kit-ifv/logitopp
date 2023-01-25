package edu.kit.ifv.mobitopp.simulation.person;

import java.util.List;
import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.SimulationOptionsCustomization;
import edu.kit.ifv.mobitopp.simulation.SimulationPersonFactory;
import edu.kit.ifv.mobitopp.simulation.activityschedule.PickUpParcelReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;

public class ParcelPersonFactory implements SimulationPersonFactory {
	
	private final SimulationPersonFactory defaultFactory;
	
	public ParcelPersonFactory(SimulationPersonFactory defaultFactory) {
		this.defaultFactory = defaultFactory;
	}

	@Override
	public PickUpParcelPerson create(Person person, EventQueue queue, SimulationOptions options,
			List<Time> simulationDays, Set<Mode> modesInSimulation, TourFactory tourFactory, TripFactory tripFactory,
			PersonState initialState, PublicTransportBehaviour boarder, long seed, PersonListener listener) {
		
		SimulationOptionsCustomization customSimulationOptions = new SimulationOptionsCustomization(options);
		SimulationPerson simulationPerson = defaultFactory.create(person, queue, options, simulationDays, modesInSimulation, tourFactory, tripFactory, initialState, boarder, seed, listener);
				
		PickUpParcelPerson parcelPerson = new PickUpParcelPerson(simulationPerson, seed);
		
		PickUpParcelReschedulingStrategy pickUpReschedulingStrategy = new PickUpParcelReschedulingStrategy(parcelPerson, options.rescheduling());
		customSimulationOptions.customize(pickUpReschedulingStrategy);
		
		return parcelPerson;
	}

}
