package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.demand.attributes.InstantValueProvider;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.LatentValueProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class ParcelSchedulerHook stores pending {@link ParcelBuilder parcels} in a virtual vault and
 * releases them into the simulation when their arrival time is reached.
 * Therefore, the arrival date of all {@link ParcelBuilder parcels} must be determined {@link InstantValueProvider before} the start of the simulation and may not be {@link LatentValueProvider latent} decisions.
 */
public class ParcelSchedulerHook implements Hook {

	private final Map<Time, Collection<ParcelBuilder<?>>> parcels;
	private final boolean keepSchedule;

	/**
	 * Instantiates a new empty parcel scheduler hook.
	 *
	 * @param keepSchedule whether to the keep the schedule or delete the processed parcels
	 */
	public ParcelSchedulerHook(boolean keepSchedule) {
		this.parcels = new HashMap<>();
		this.keepSchedule = keepSchedule;
	}

	/**
	 * Registers this hook at the given {@link DemandSimulatorPassenger simulator}.
	 * 
	 * The hook is registered before each time slice.
	 *
	 * @param simulator the simulator
	 */
	public void register(DemandSimulatorPassenger simulator) {
		simulator.addBeforeTimeSliceHook(this);
	}

	/**
	 * Adds the given parcels to the schedule.
	 * 
	 * For each parcel the arrival time is evaluated.
	 * Parcels with the same arrival time are {@link ParcelSchedulerHook#addParcel(ParcelBuilder) collected} in one group in the schedule.
	 *
	 * @param parcels the parcels to be added
	 */
	public void addParcels(ParcelBuilder<?>... parcels) {
		Arrays.asList(parcels).forEach(this::addParcel);
	}

	/**
	 * Adds the given parcel to the schedule.
	 * 
	 * The parcels arrival time is evaluated.
	 * If its arrival time already exists in the schedule, it is added to the according group.
	 * Otherwise a new time entry is added to the schedule containing the given parcel.	 * 
	 *
	 * @param parcel the parcel to be added
	 */
	public void addParcel(ParcelBuilder<?> parcel) {
		Time time = parcel.getArrivalDate();

		if (this.parcels.containsKey(time)) {
			this.parcels.get(time).add(parcel);
		} else {
			ArrayList<ParcelBuilder<?>> list = new ArrayList<>();
			list.add(parcel);
			this.parcels.put(time, list);
		}
	}

	/**
	 * Processes the given date.
	 * 
	 * Checks if the given time exists in the schedule.
	 * If parcels are scheduled for that time, they are each inserted into the simulation.
	 * To do so the {@link ParcelBuilder parcels} are {@link ParcelBuilder#get() built} and moved to the according producer agent.
	 *
	 * @param date the date to be processed
	 */
	@Override
	public void process(Time date) {

		if (this.parcels.containsKey(date)) {
			this.parcels.get(date).forEach(ParcelBuilder::get);

			if (!keepSchedule) {
				this.parcels.remove(date);
			}

		}
	}

	/**
	 * Flush all parcels into the simulation.
	 * 
	 * Ignore the schedule and insert all parcels into the simulation.
	 *
	 * @return the collection of flushed parcels
	 */
	public Collection<IParcel> flushAllParcels() {
		Collection<IParcel> parcels = new ArrayList<>();

		for (Time time : this.parcels.keySet()) {
			this.parcels.get(time).forEach(p -> {
				parcels.add(p.get());
			});
		}

		return parcels;
	}

}
