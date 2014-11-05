package engine.network;

import java.util.List;

/**
 * Reads policies from the firewall
 * 
 * @author Adrian Bergler
 * @version 2014-10-23
 */
public interface PolicyReader {

	/**
	 * Reads the complete policytable. The first column is defined to be the
	 * Throughput-OIDs, the top row is defined to be the headers.
	 * 
	 * @return the table
	 */
	public List<List<String>> read();

}
