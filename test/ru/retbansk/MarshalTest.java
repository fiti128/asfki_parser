package ru.retbansk;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class MarshalTest {
	static String ONE = "yes";
	static String TWO = "two";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void test() {
		assertTrue(ONE == TWO);
	}

}
