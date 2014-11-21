package test;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import application.Koordinaten;
import engine.network.OIDProps;

/**
 * Tests the OIDProperties
 * 
 * @author Adrian Bergler
 * @version 2014-10-31
 */
public class TestOIDProps {

	/**
	 * Checks if any exceptions are thrown
	 */
	@Test
	public void testInit() throws IOException {
		OIDProps oidp = OIDProps.get();
		assertTrue(oidp != null);

		oidp.close();
	}

	/**
	 * Test the coordinates class
	 */
	@Test
	public void testCoordinates() throws IOException {
		Koordinaten k = new Koordinaten(5, 11.0);
		
		assertEquals(5, k.getX());
	}
}
