import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
import lombok.AllArgsConstructor;

public class Test {
	
	public static void main(String[] args) {
		List<TestObj> objs = new ArrayList<>();
		
		Test t = new Test();
		
		for (int i = 0; i < 200; i++) {
			for (String s : "1234567890ßqwertzuiopü+asdfghjklöä#yxcvbnm,.-!§$%&/()=?QWERTZUIOPÜ*ASDFGHJKLÖÄ'YXCVBNM;:-><".split("")) {
				for (int size=0; size < 100; size++) {
					if (size < 3) {
						objs.add(t.new TestObj(s, i, true, ShipmentSize.EXTRA_LARGE));
						objs.add(t.new TestObj(s, i, false, ShipmentSize.EXTRA_LARGE));
					} else if (size < 16) {
						objs.add(t.new TestObj(s, i, true, ShipmentSize.LARGE));
						objs.add(t.new TestObj(s, i, false, ShipmentSize.LARGE));
					} else if (size < 59) {
						objs.add(t.new TestObj(s, i, true, ShipmentSize.MEDIUM));
						objs.add(t.new TestObj(s, i, false, ShipmentSize.MEDIUM));
					} else {
						objs.add(t.new TestObj(s, i, true, ShipmentSize.SMALL));
						objs.add(t.new TestObj(s, i, false, ShipmentSize.SMALL));
					}
				}
				
				
			}
		}

		Collections.shuffle(objs);
		System.out.println("Pcls: " + objs.size());
		
		int maxVol = 22 * 100 * 100 * 100;
//		maxVol = 120*80*94;
		int currVol = 0;
		int cnt = 0;
		List<Integer> capacities = new ArrayList<>();
		
		double total = 1.0*objs.size();
		System.out.println("S: "+objs.stream().filter(o -> o.size.equals(ShipmentSize.SMALL)).count() / total);
		System.out.println("M: "+objs.stream().filter(o -> o.size.equals(ShipmentSize.MEDIUM)).count() / total);
		System.out.println("L: "+objs.stream().filter(o -> o.size.equals(ShipmentSize.LARGE)).count() / total);
		System.out.println("XL: "+objs.stream().filter(o -> o.size.equals(ShipmentSize.EXTRA_LARGE)).count() / total);
		
		for (TestObj o : objs) {
			int v = o.size.getVolume(o);
			
			currVol += v;
			
			if (currVol > maxVol) {
				capacities.add(cnt);
				cnt = 0;
				currVol = v;
			}
			
			cnt += 1;
		}
		
		capacities.add(cnt);
		
		System.out.println("Avg cap: "+capacities.stream().mapToInt(i -> i).sum() / (capacities.size()*1.0));
		System.out.println("Vehicles: "+capacities.size());
		
	}
	
	@AllArgsConstructor
	public class TestObj {
		private String s;
		private int i;
		private boolean d;
		private ShipmentSize size;
		
	}
	
	private static double round(double value, int precision) {
	    int scale = (int) Math.pow(10, precision);
	    return (double) Math.floor(value * scale) / scale;
	}

}
