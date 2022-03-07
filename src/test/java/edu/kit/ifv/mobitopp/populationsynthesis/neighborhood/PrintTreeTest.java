package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrintTreeTest {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;

	@BeforeEach
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	}

	@AfterEach
	public void restoreStreams() {
	    System.setOut(originalOut);
	}
	
	@Test
	public void printTree() {
		Leaf<String> leaf1 = new Leaf<>("A", new Point2D.Double(1, 1));
		Leaf<String> leaf2 = new Leaf<>("B", new Point2D.Double(1, 2));
		Leaf<String> leaf3 = new Leaf<>("C", new Point2D.Double(10, 10));
		Leaf<String> leaf4 = new Leaf<>("D", new Point2D.Double(10, 11));
		KdTree<String> kdTree = new KdTree<String>(Arrays.asList(leaf1, leaf2, leaf3, leaf4), 1, new EuclidDistanceMetric());
		
		kdTree.print();
		
		String result = outContent.toString();
		
		String omega = "Box [x=1.0,y=1.0,w=9.0,h=10.0]:";
		String lambda = "Box [x=1.0,y=1.0,w=0.0,h=1.0]:";
		String alpha = "A [1.0,1.0]";
		String beta = "B [1.0,2.0]";
		String rho = "Box [x=10.0,y=10.0,w=0.0,h=1.0]:";
		String gamma = "C [10.0,10.0]";
		String delta = "D [10.0,11.0]";
		
		assertTrue(result.contains(omega));
		assertTrue(result.contains(lambda));
		assertTrue(result.contains(alpha));
		assertTrue(result.contains(beta));
		assertTrue(result.contains(rho));
		assertTrue(result.contains(gamma));
		assertTrue(result.contains(delta));
		
		assertTrue(result.indexOf(omega) < result.indexOf(lambda));
		assertTrue(result.indexOf(omega) < result.indexOf(rho));
		assertTrue(result.indexOf(lambda) < result.indexOf(alpha));
		assertTrue(result.indexOf(lambda) < result.indexOf(beta));
		assertTrue(result.indexOf(rho) < result.indexOf(gamma));
		assertTrue(result.indexOf(rho) < result.indexOf(delta));
	}
	
}
