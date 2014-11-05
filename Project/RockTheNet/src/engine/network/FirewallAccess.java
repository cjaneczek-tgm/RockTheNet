package engine.network;

import java.util.List;

/**
 * This is the FirewallAccess. It is used to access the readers and writers of
 * the Firewall
 * 
 * @author Adrian Bergler
 * @version 2014-10-23
 */
public interface FirewallAccess {

	public abstract PolicyReader getNetReader();

	public abstract SSHWriter getNetWriter();

	public abstract List<List<String>> getLastRead();

}
