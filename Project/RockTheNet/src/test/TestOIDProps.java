package test;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import engine.network.OIDProps;

/**
 * Tests the OIDProperties
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

	@Test
	public void testLoad() throws IOException{
		OIDProps oidp = OIDProps.get();

		String testoid = oidp.getProperty("test");
		
		assertEquals(".1.2.3.4.5.6.7.8.9", testoid);
		oidp.close();
	}
	
}
