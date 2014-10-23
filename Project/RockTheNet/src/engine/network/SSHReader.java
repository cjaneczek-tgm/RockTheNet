package engine.network;

import java.util.List;

/**
 * This class is used to read with SSH, not required for now (because SNMP works as well)
 * @version 0.1
 */
public class SSHReader implements PolicyReader {
	
	private SSHProperties sSHProperties;


	/**
	 * @see engine.network.PolicyReader#read()
	 */
	public List<List<String>> read() {
		return null;
	}

}
