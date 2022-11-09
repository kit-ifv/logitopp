package edu.kit.ifv.mobitopp.util.collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PairTest {

	@Test
	public void getFirst() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		assertEquals("hello", pair.getFirst());
	}
	
	@Test
	public void getSecond() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		assertEquals("world", pair.getSecond());
	}
	
	@Test
	public void setFirst() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		pair.setFirst("hola");
		
		assertEquals("hola", pair.getFirst());
		assertEquals("world", pair.getSecond());
	}
	
	@Test
	public void setSecond() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		pair.setSecond("mundo");
		
		assertEquals("hello", pair.getFirst());
		assertEquals("mundo", pair.getSecond());
	}
	
	@Test
	public void objectEquality() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		assertTrue(pair.equals(pair));
	}
	
	@Test
	public void nullEquality() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		assertFalse(pair.equals(null));
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void otherClassEquality() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		assertFalse(pair.equals("hello;world"));
	}
	
	@Test
	public void valueEquality() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		assertTrue(pair.equals(new Pair<>("hello", "world")));
	}
	
	@Test
	public void valueEqualityDiffFirst() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		assertFalse(pair.equals(new Pair<>("hola", "world")));
	}
	
	@Test
	public void valueEqualityDiffSecond() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		assertFalse(pair.equals(new Pair<>("hello", "mundo")));
	}
	
	@Test
	public void valueEqualityDiffBoth() {
		Pair<String,String> pair = new Pair<>("hello", "world");
		
		assertFalse(pair.equals(new Pair<>("hola", "mundo")));
	}
	
	@Test
	public void convertToString() {
		Pair<String,String> pair = new Pair<>("hello", "world");

		assertEquals("Pair(first=hello, second=world)", pair.toString());
	}
	
}
