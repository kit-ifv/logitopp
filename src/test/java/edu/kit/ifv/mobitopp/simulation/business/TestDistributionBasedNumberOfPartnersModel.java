package edu.kit.ifv.mobitopp.simulation.business;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.business.partners.DistributionBasedNumberOfPartnersModel;
import edu.kit.ifv.mobitopp.util.random.BoxPlotDistribution;

public class TestDistributionBasedNumberOfPartnersModel {
	private static final int N = 1000;
	BoxPlotDistribution distr;
	DistributionBasedNumberOfPartnersModel model;
	Random random;
	
	@BeforeEach
	public void setUp() {
		distr = new BoxPlotDistribution(1, 2, 4, 6, 8);
		model = new DistributionBasedNumberOfPartnersModel(b -> distr);
		random = new Random(42);
	}
		
	@Test
	public void selectQ1() {
		for (int i = 0; i < N; i++) {
			int val = model.select(null, 0.24*random.nextDouble());
			assertTrue(val + "is not in [1,2]", 1 <= val && val <= 2);
		}
	}
	
	@Test
	public void selectQ2() {	
		for (int i = 0; i < N; i++) {
			int val = model.select(null, 0.25 + 0.24*random.nextDouble());
			assertTrue(val + "is not in [2,4]", 2 <= val && val <= 4);
		}
	}
	
	@Test
	public void selectQ3() {	
		for (int i = 0; i < N; i++) {
			int val = model.select(null, 0.5 + 0.24*random.nextDouble());
			assertTrue(val + "is not in [4,6]", 4 <= val && val <= 6);
		}
	}
	
	@Test
	public void selectQ4() {	
		for (int i = 0; i < N; i++) {
			int val = model.select(null, 0.75 + 0.24*random.nextDouble());
			assertTrue(val + "is not in [6,8]", 6 <= val && val <= 8);
		}
	}
	
	@Test
	public void select() {	
		List<Integer> res = new ArrayList<>();
		List<Double> rand = new ArrayList<>();
		
		int k = N*100;
		
		for (int i = 0; i < k; i++) {
			double r = random.nextDouble();
			rand.add(r);
			res.add(model.select(null, r));
		}
		
		assertEquals(1, res.stream().mapToInt(i->i).min().getAsInt());
		assertEquals(2, res.stream().sorted().skip((long) Math.ceil(k*0.25)).findFirst().get());
		assertEquals(4, res.stream().sorted().skip((long) Math.ceil(k*0.5)).findFirst().get());
		assertEquals(6, res.stream().sorted().skip((long) Math.ceil(k*0.75)).findFirst().get());
		assertEquals(8, res.stream().mapToInt(i->i).max().getAsInt());

	}

	@Test
	public void selectUnit() {
		this.distr = new BoxPlotDistribution(42, 42, 42, 42, 42);
		this.model = new DistributionBasedNumberOfPartnersModel(b -> distr);
		
		for (int i = 0; i < N; i++) {
			assertEquals(42, model.select(null, random.nextDouble()));
		}
	}
}
