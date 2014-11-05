package engine;

import engine.network.DefaultFirewallAccess;
import engine.network.FirewallAccess;

/**
 * This is the Object that is used to share the FirewallAccess and the
 * Properties Object (Basically a singleton Wrapper) This is used by the
 * controllers to call certain Firewall-methods
 * 
 * @author Adrian Bergler
 * @version 2014-10-23
 */
public class Executor {

	private static Executor executor;

	private FirewallAccess access;

	private RTNProperties properties;

	private Executor() {
		access = new DefaultFirewallAccess(); // So far this is hardcoded
	}

	public Executor get() {
		if (executor == null)
			executor = new Executor();
		return executor;
	}

	public FirewallAccess getAccess() {
		return access;
	}

	public RTNProperties getProperties() {
		return properties;
	}

	public void setAccess(FirewallAccess access) {
		this.access = access;
	}

	public void setProperties(RTNProperties properties) {
		this.properties = properties;
	}

}
