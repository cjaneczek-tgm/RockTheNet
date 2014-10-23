package engine.network;

/**
 * Writes/edits policies on the firewall
 * @author Adrian Bergler
 * @version 2014-10-23
 */
public interface PolicyWriter {

	/**
	 * Writes to a target
	 * @param target the target
	 * @param input the input
	 * @return if it worked
	 */
	public boolean write(String target, String input);

}
