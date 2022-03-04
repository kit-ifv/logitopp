package edu.kit.ifv.mobitopp.util.collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TripleTest {

	@Test
	public void getFirst() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertEquals("hello", triple.getFirst());
	}
	
	@Test
	public void getSecond() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertEquals("world", triple.getSecond());
	}
	
	@Test
	public void getThird() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertEquals("!", triple.getThird());
	}
	
	@Test
	public void setFirst() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		triple.setFirst("hola");
		
		assertEquals("hola", triple.getFirst());
		assertEquals("world", triple.getSecond());
		assertEquals("!", triple.getThird());
	}
	
	@Test
	public void setSecond() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		triple.setSecond("mundo");
		
		assertEquals("hello", triple.getFirst());
		assertEquals("mundo", triple.getSecond());
		assertEquals("!", triple.getThird());
	}
	
	@Test
	public void setThird() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		triple.setThird("?");
		
		assertEquals("hello", triple.getFirst());
		assertEquals("world", triple.getSecond());
		assertEquals("?", triple.getThird());
	}
	
	@Test
	public void objectEquality() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertTrue(triple.equals(triple));
	}
	
	@Test
	public void nullEquality() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertFalse(triple.equals(null));
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void otherClassEquality() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertFalse(triple.equals("hello;world;!"));
	}
	
	@Test
	public void valueEquality() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertTrue(triple.equals(new Triple<>("hello", "world", "!")));
	}
	
	@Test
	public void valueEqualityDiffFirst() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertFalse(triple.equals(new Triple<>("hola", "world", "!")));
	}
	
	@Test
	public void valueEqualityDiffSecond() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertFalse(triple.equals(new Triple<>("hello", "mundo", "!")));
	}
	
	@Test
	public void valueEqualityDiffThird() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertFalse(triple.equals(new Triple<>("hello", "world", "?")));
	}
	
	@Test
	public void valueEqualityDiffAll() {
		Triple<String,String,String> triple = new Triple<>("hello", "world", "!");
		
		assertFalse(triple.equals(new Triple<>("hola", "mundo", "?")));
	}
	
}
