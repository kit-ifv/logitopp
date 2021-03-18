package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.person.SimulationOptions;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModel;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class SimulationOptionsCustomization allows to 'customize' some
 * SimulationOptions attributes while delegating the rest to the
 * wrapped/decorated SimulationOptions.
 */
public class SimulationOptionsCustomization implements SimulationOptions {

	/** The delegate simulation options. */
	private SimulationOptions delegateSimulationOptions;

	private DestinationChoiceModel destinationChoiceModel;
	private TourBasedModeChoiceModel modeChoiceModel;
	private ReschedulingStrategy rescheduling;
	private ZoneBasedRouteChoice routeChoice;
	private ImpedanceIfc impedance;
	private RideSharingOffers rideSharingOffers;
	private ActivityPeriodFixer activityPeriodFixer;
	private ActivityStartAndDurationRandomizer activityDurationRandomizer;

	/**
	 * Instantiates a new simulation options customization.
	 *
	 * @param delegate                   the delegate
	 * @param destinationChoiceModel     the destination choice model
	 * @param modeChoiceModel            the mode choice model
	 * @param rescheduling               the rescheduling
	 * @param routeChoice                the route choice
	 * @param impedance                  the impedance
	 * @param rideSharingOffers          the ride sharing offers
	 * @param activityPeriodFixer the activity period fixer
	 * @param activityDurationRandomizer the activity duration randomizer
	 */
	public SimulationOptionsCustomization(SimulationOptions delegate, DestinationChoiceModel destinationChoiceModel,
			TourBasedModeChoiceModel modeChoiceModel, ReschedulingStrategy rescheduling,
			ZoneBasedRouteChoice routeChoice, ImpedanceIfc impedance, RideSharingOffers rideSharingOffers,
			 ActivityPeriodFixer activityPeriodFixer, ActivityStartAndDurationRandomizer activityDurationRandomizer) {

		this.delegateSimulationOptions = delegate;
		this.destinationChoiceModel = destinationChoiceModel;
		this.modeChoiceModel = modeChoiceModel;
		this.rescheduling = rescheduling;
		this.routeChoice = routeChoice;
		this.impedance = impedance;
		this.rideSharingOffers = rideSharingOffers;
		this.activityPeriodFixer = activityPeriodFixer;
		this.activityDurationRandomizer = activityDurationRandomizer;
	}

	/**
	 * Customize this {@link SimulationOptionsCustomization} with the given options.
	 *
	 * @param destinationChoiceModel the destination choice model
	 * @param modeChoiceModel the mode choice model
	 * @param rescheduling the rescheduling
	 * @param routeChoice the route choice
	 * @param impedance the impedance
	 * @param rideSharingOffers the ride sharing offers
	 * @param activityPeriodFixer the activity period fixer
	 * @param activityDurationRandomizer the activity duration randomizer
	 */
	public void customize(DestinationChoiceModel destinationChoiceModel, TourBasedModeChoiceModel modeChoiceModel,
			ReschedulingStrategy rescheduling, ZoneBasedRouteChoice routeChoice, ImpedanceIfc impedance,
			RideSharingOffers rideSharingOffers, ActivityPeriodFixer activityPeriodFixer, 
			ActivityStartAndDurationRandomizer activityDurationRandomizer) {
		this.destinationChoiceModel = destinationChoiceModel;
		this.modeChoiceModel = modeChoiceModel;
		this.rescheduling = rescheduling;
		this.routeChoice = routeChoice;
		this.impedance = impedance;
		this.rideSharingOffers = rideSharingOffers;
		this.activityPeriodFixer = activityPeriodFixer;
		this.activityDurationRandomizer = activityDurationRandomizer;
	}

	/**
	 * Sets the delegate {@link SimulationOptions}.
	 *
	 * @param options the new delegate
	 */
	public void setDelegate(SimulationOptions options) {
		this.delegateSimulationOptions = options;
	}
	
	/**
	 * Instantiates a new simulation options customization with no customization.
	 *
	 * @param delegate the delegate
	 */
	public SimulationOptionsCustomization(SimulationOptions delegate) {
		this(delegate, null, null, null, null, null, null, null, null);
	}
	
	/**
	 * Resets the customization.
	 * Now, all simulation options are provided by the delegate.
	 */
	public void reset() {
		this.destinationChoiceModel = null;
		this.modeChoiceModel = null;
		this.rescheduling = null;
		this.routeChoice = null;
		this.impedance = null;
		this.rideSharingOffers = null;
		this.activityPeriodFixer = null;
		this.activityDurationRandomizer = null;
	}

	/**
	 * Instantiates a new simulation options customization with a custom
	 * destinationChoiceModel.
	 *
	 * @param delegate               the delegate
	 * @param destinationChoiceModel the destination choice model
	 */
	public SimulationOptionsCustomization(SimulationOptions delegate, DestinationChoiceModel destinationChoiceModel) {
		this(delegate, destinationChoiceModel, null, null, null, null, null, null, null);
	}
	
	/**
	 * Customize this {@link SimulationOptionsCustomization} with the given {@link DestinationChoiceModel}.
	 *
	 * @param destinationChoiceModel the destination choice model
	 */
	public void customize(DestinationChoiceModel destinationChoiceModel) {
		this.destinationChoiceModel = destinationChoiceModel;
	}
	

	/**
	 * Instantiates a new simulation options customization with a custom mode choice
	 * model.
	 *
	 * @param delegate        the delegate
	 * @param modeChoiceModel the mode choice model
	 */
	public SimulationOptionsCustomization(SimulationOptions delegate, TourBasedModeChoiceModel modeChoiceModel) {
		this(delegate, null, modeChoiceModel, null, null, null, null, null, null);
	}
	
	/**
	 * Customize this {@link SimulationOptionsCustomization} with the given {@link TourBasedModeChoiceModel}.
	 *
	 * @param modeChoiceModel the mode choice model
	 */
	public void customize(TourBasedModeChoiceModel modeChoiceModel) {
		this.modeChoiceModel = modeChoiceModel;
	}

	/**
	 * Instantiates a new simulation options customization with a custom
	 * rescheduling strategy.
	 *
	 * @param delegate     the delegate
	 * @param rescheduling the rescheduling strategy
	 */
	public SimulationOptionsCustomization(SimulationOptions delegate, ReschedulingStrategy rescheduling) {
		this(delegate, null, null, rescheduling, null, null, null, null, null);
	}
	
	/**
	 * Customize this {@link SimulationOptionsCustomization} with the given {@link ReschedulingStrategy}.
	 *
	 * @param rescheduling the rescheduling
	 */
	public void customize(ReschedulingStrategy rescheduling) {
		this.rescheduling = rescheduling;
	}

	/**
	 * Instantiates a new simulation options customization with a custom route
	 * choice model.
	 *
	 * @param delegate    the delegate
	 * @param routeChoice the route choice model
	 */
	public SimulationOptionsCustomization(SimulationOptions delegate, ZoneBasedRouteChoice routeChoice) {
		this(delegate, null, null, null, routeChoice, null, null, null, null);
	}
	
	/**
	 * Customize this {@link SimulationOptionsCustomization} with the given {@link ZoneBasedRouteChoice}.
	 *
	 * @param routeChoice the route choice
	 */
	public void customize(ZoneBasedRouteChoice routeChoice) {
		this.routeChoice = routeChoice;
	}

	/**
	 * Instantiates a new simulation options customization with a custom impedance.
	 *
	 * @param delegate  the delegate
	 * @param impedance the impedance
	 */
	public SimulationOptionsCustomization(SimulationOptions delegate, ImpedanceIfc impedance) {
		this(delegate, null, null, null, null, impedance, null, null, null);
	}
	
	/**
	 * Customize this {@link SimulationOptionsCustomization} with the given {@link ImpedanceIfc}.
	 *
	 * @param impedance the impedance
	 */
	public void customize(ImpedanceIfc impedance) {
		this.impedance = impedance;
	}

	/**
	 * Instantiates a new simulation options customization with a custom ride
	 * sharing offer.
	 *
	 * @param delegate          the delegate
	 * @param rideSharingOffers the ride sharing offers
	 */
	public SimulationOptionsCustomization(SimulationOptions delegate, RideSharingOffers rideSharingOffers) {
		this(delegate, null, null, null, null, null, rideSharingOffers, null, null);
	}
	
	/**
	 * Customize this {@link SimulationOptionsCustomization} with the given {@link RideSharingOffers}.
	 *
	 * @param rideSharingOffers the ride sharing offers
	 */
	public void customize(RideSharingOffers rideSharingOffers) {
		this.rideSharingOffers = rideSharingOffers;
	}

	/**
	 * Instantiates a new simulation options customization with a custom activity
	 * period fixer.
	 *
	 * @param delegate              the delegate
	 * @param activityPeriodFixer   the activity duration randomizer
	 */
	public SimulationOptionsCustomization(SimulationOptions delegate,
			ActivityPeriodFixer activityPeriodFixer) {
		this(delegate, null, null, null, null, null, null, activityPeriodFixer, null);
	}
	
	/**
	 * Customize this {@link SimulationOptionsCustomization} with the given {@link ActivityPeriodFixer}.
	 *
	 * @param activityPeriodFixer the activity period fixer
	 */
	public void customize(ActivityPeriodFixer activityPeriodFixer) {
		this.activityPeriodFixer = activityPeriodFixer;
	}
	
	/**
	 * Instantiates a new simulation options customization with a custom activity
	 * duration randomizer.
	 *
	 * @param delegate                   the delegate
	 * @param activityDurationRandomizer the activity duration randomizer
	 */
	public SimulationOptionsCustomization(SimulationOptions delegate,
			ActivityStartAndDurationRandomizer activityDurationRandomizer) {
		this(delegate, null, null, null, null, null, null, null, activityDurationRandomizer);
	}
	
	/**
	 * Customize this {@link SimulationOptionsCustomization} with the given {@link ActivityStartAndDurationRandomizer}.
	 *
	 * @param activityDurationRandomizer the activity duration randomizer
	 */
	public void customize(ActivityStartAndDurationRandomizer activityDurationRandomizer) {
		this.activityDurationRandomizer = activityDurationRandomizer;
	}
	


	
	/**
	 * Gets the destination choice model. If it is null, returns the delegate's
	 * destination choice model.
	 *
	 * @return the destination choice model
	 */
	@Override
	public DestinationChoiceModel destinationChoiceModel() {
		if (destinationChoiceModel != null) {
			return destinationChoiceModel;
		} else {
			return delegateSimulationOptions.destinationChoiceModel();
		}
	}

	/**
	 * Gets the mode choice model. If it is null, returns the delegate's mode choice
	 * model.
	 *
	 * @return the tour based mode choice model
	 */
	@Override
	public TourBasedModeChoiceModel modeChoiceModel() {
		if (modeChoiceModel != null) {
			return modeChoiceModel;
		} else {
			return delegateSimulationOptions.modeChoiceModel();
		}
	}

	/**
	 * Gets the rescheduling strategy. If it is null, returns the delegate's
	 * rescheduling strategy.
	 *
	 * @return the rescheduling strategy
	 */
	@Override
	public ReschedulingStrategy rescheduling() {
		if (rescheduling != null) {
			return rescheduling;
		} else {
			return delegateSimulationOptions.rescheduling();
		}
	}

	/**
	 * Gets the route choice model. If it is null, returns the delegate's route
	 * choice model.
	 *
	 * @return the zone based route choice
	 */
	@Override
	public ZoneBasedRouteChoice routeChoice() {
		if (routeChoice != null) {
			return routeChoice;
		} else {
			return delegateSimulationOptions.routeChoice();
		}
	}

	/**
	 * Gets the impedance. If it is null, returns the delegate's impedance.
	 * 
	 * @return the impedance
	 */
	@Override
	public ImpedanceIfc impedance() {
		if (impedance != null) {
			return impedance;
		} else {
			return delegateSimulationOptions.impedance();
		}
	}

	/**
	 * Gets the delegates max difference minutes.
	 *
	 * @return the int
	 */
	@Override
	public int maxDifferenceMinutes() {
		return delegateSimulationOptions.maxDifferenceMinutes();
	}

	/**
	 * Gets the ride sharing offers. If it is null, returns the delegate's ride
	 * sharing offers.
	 *
	 * @return the ride sharing offers
	 */
	@Override
	public RideSharingOffers rideSharingOffers() {
		if (rideSharingOffers != null) {
			return rideSharingOffers;
		} else {
			return delegateSimulationOptions.rideSharingOffers();
		}
	}

	/**
	 * Gets the delegate's simulation start.
	 *
	 * @return the simulation start
	 */
	@Override
	public Time simulationStart() {
		return delegateSimulationOptions.simulationStart();
	}

	/**
	 * Gets the delegates simulation end.
	 *
	 * @return the simulation end
	 */
	@Override
	public Time simulationEnd() {
		return delegateSimulationOptions.simulationEnd();
	}

	/**
	 * Gets the activity duration randomizer. If it is null, returns the delegate's
	 * activity duration randomizer.
	 *
	 * @return the activity start and duration randomizer
	 */
	@Override
	public ActivityStartAndDurationRandomizer activityDurationRandomizer() {
		if (activityDurationRandomizer != null) {
			return activityDurationRandomizer;
		} else {
			return delegateSimulationOptions.activityDurationRandomizer();
		}

	}

	@Override
	public ActivityPeriodFixer activityPeriodFixer() {
		if (activityDurationRandomizer != null) {
			return activityPeriodFixer;
		} else {
			return delegateSimulationOptions.activityPeriodFixer();
		}
	}

}
