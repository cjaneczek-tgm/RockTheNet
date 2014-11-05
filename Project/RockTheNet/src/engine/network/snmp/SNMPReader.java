package engine.network.snmp;

import java.util.List;

import engine.network.PolicyReader;

/**
 * Reads policies through SNMP
 * 
 * @author Adrian Bergler
 * @version 2014-10-23
 */
public interface SNMPReader extends PolicyReader {

	/**
	 * Opens the SNMP
	 */
	public void open();

	/**
	 * Closes the SNMP
	 */
	public void close();

	/**
	 * A Method which returns all policies OID's an its information
	 */
	public List<List<String>> read();

	/**
	 * A Method which returns the current Bytes per second of an policy with the
	 * OID ending index
	 * 
	 * @param index
	 *            the ending of the OID
	 * @return The current bytes per second
	 */
	public int getMonBytesSec(int index);
}
