package edu.kit.ifv.mobitopp.simulation.activityschedule;

import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.LinkedListElement;

public interface PersonParcelActivity extends ActivityIfc, LinkedListElement {
	
	public ParcelActivity asParcelActivity();

}
