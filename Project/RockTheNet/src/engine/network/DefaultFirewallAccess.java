package engine.network;

import java.util.List;

/**
 * This is the default FirewallAccess (basically the normal implementation)
 * For more details look up the interface-documentation
 * @see engine.network.FirewallAccess
 * @version 2014-10-23
 */
public class DefaultFirewallAccess implements FirewallAccess {

	private PolicyReader reader;

	private PolicyWriter writer;

	private List<List<String>> lasttable;

	private OIDProps oIDProps;

	private PolicyReader policyReader;

	private PolicyWriter policyWriter;

	@Override
	public PolicyReader getNetReader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SSHWriter getNetWriter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<String>> getLastRead() {
		// TODO Auto-generated method stub
		return null;
	}

}
