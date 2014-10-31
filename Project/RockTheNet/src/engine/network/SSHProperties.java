package engine.network;

import java.util.Properties;

/**
 * Equivalent to OIDProperties, stores the information required for the SSH-Readers and -Writers
 * @version 2014-10-31
 * @author Christian Janeczek
 */
public class SSHProperties extends Properties{
	
	//The properties of SSH are currently stored statically.
	//TODO One of the GUI-elements should refer to an instance of SSHProperties and
	//TODO deliver the values dynamically (getter & setter)
	
	private static final long serialVersionUID = -2393053076040065937L;
	private String hostname = "10.0.100.10";
	private String username = "5ahit";
	private String password = "Waeng7ohch8o";
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
