package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.DeliveryActivity;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.Event;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * A {@link PersonState} state machine for {@link DeliveryPerson}s.
 * In the {@link PersonStateDelivery#EXECUTE_ACTIVITY} state, delivery activities are executed.
 */
public enum PersonStateDelivery implements PersonState {

	FINISHED(false) {

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.empty();
		}
	},


	MAKE_TRIP(false){
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.selectRoute( person.options().routeChoice(), person.currentTrip(), currentTime);
			person.startTrip(person.options().impedance(), person.currentTrip(), currentTime);
		}
		
		@Override
		public void doActionAtEnd(SimulationPerson person, Time currentTime) {
			person.endTrip(person.options().impedance(), person.options().rescheduling(), currentTime);
		}
		
		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			if  (person.hasNextActivity()) {
				return EXECUTE_ACTIVITY;
			} else {
				return FINISHED;
			}
		}
		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.of(Event.tripEnding(person, person.currentTrip()));
		}
	},

	EXECUTE_ACTIVITY(MAKE_TRIP) {
		
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.startActivity(person.currentTrip(), person.options().rescheduling(), currentTime);
		}
		
		@Override
		public void doActionAtEnd(SimulationPerson person, Time currentTime) {
				
			if (person.currentActivity().activityType().equals(ActivityType.DELIVER_PARCEL)) {
				System.out.println("Try delivery by " + person.getOid());
				
				((DeliveryActivity) person.currentActivity()).tryDelivery(currentTime);
			}
			
			
			person.endActivity();
			person.selectDestinationAndMode(person.options().destinationChoiceModel(),
					person.options().modeChoiceModel(), person.options().impedance(), true);
			person.prepareTrip(person.options().impedance(), person.currentTrip(),
					person.currentTrip().startDate());
		}
		
		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.of(Event.activityEnding(person, person.currentActivity()));
		}
	},			

	UNINITIALIZED(false,EXECUTE_ACTIVITY) {

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.empty();
		}
	}; 		

	private final boolean instantaneous;
	private final PersonStateDelivery nextState;

	private PersonStateDelivery(boolean instantaneous, PersonStateDelivery nextState) {
		this.instantaneous = instantaneous;
		this.nextState = nextState;
	}

	private PersonStateDelivery(boolean instantaneous) {
		this(instantaneous, null);
	}

	private PersonStateDelivery(PersonStateDelivery nextState) {
		this(false, nextState);
	}

	private PersonStateDelivery() {
		this(false,null);
	}

	public boolean instantaneous() {
		return this.instantaneous;
	}

	public PersonState nextState(SimulationPerson person, Time currentTime) {
		if (this.nextState != null) {
			return this.nextState;
		}
		throw new AssertionError();
	}

	@Override
	public void doActionAtStart(SimulationPerson person, Time currentTime) {
	}
	
	@Override
	public void doActionAtEnd(SimulationPerson person, Time currentTime) {
	}

	@Override
	public Optional<DemandSimulationEventIfc> nextEvent(
			SimulationPerson person, Time currentDate) {
		throw new AssertionError("No next event for this state configured: " + person.currentState());
	}

}
