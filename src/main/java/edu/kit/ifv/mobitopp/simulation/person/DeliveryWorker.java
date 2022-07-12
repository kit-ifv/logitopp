package edu.kit.ifv.mobitopp.simulation.person;

import static java.util.stream.Collectors.toList;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ParcelActivity;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.time.Time;

public class DeliveryWorker extends DeliveryAgent implements Hook {

	private static int idCnt = -1;
	private final int id;
	
	public DeliveryWorker(DistributionCenter distributionCenter, long seed) {
		super(distributionCenter, seed);
		this.id = idCnt--;
	}

	@Override
	public int getOid() {
		return id;
	}

	@Override
	public void process(Time date) {
		List<ParcelActivity> reachedDeliveries = 
				this.getCurrentTour().stream().filter(d -> d.startDate().isBeforeOrEqualTo(date)).collect(toList());
		
		reachedDeliveries.forEach(d -> d.executeActivity(date));
	}

}
