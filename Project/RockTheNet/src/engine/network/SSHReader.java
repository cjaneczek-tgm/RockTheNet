package engine.network;

import java.io.IOException;
import application.RTNMain;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This class is used to read with SSH
 * The firewall needs some sort of CRUD
 * @version 2014-10-31
 * @author Christian Janeczek
 */
public class SSHReader implements PolicyReader {
	
	private static final Logger logger = Logger.getLogger(RTNMain.class);
	private SSHProperties sshProperties;
	private String hostname, username, password;
	
	public SSHReader() throws IOException{
		
		//initializing the values, which are needed for reading the policies
		this.sshProperties = new SSHProperties();
		this.hostname = this.sshProperties.getHostname();
		this.username = this.sshProperties.getUsername();
		this.password = this.sshProperties.getPassword();
		
		//I tried the following libraries:
		//*Jsch
		//*sshj
		//etc.
		//They had no documentation at all
		
	}

	@Override
	public List<List<String>> read() {
		// TODO Auto-generated method stub
		return null;
	}
}


