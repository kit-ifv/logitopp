package edu.kit.ifv.mobitopp.simulation.parcels;

public class BundleIdProvider {
    private static int BUNDLE_ID_CNT = 0;

    public static int nextId() {
        return BUNDLE_ID_CNT++;
    }

}
