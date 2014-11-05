package engine.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import application.RTNMain;

import java.util.List;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * This class is used to read with SSH The firewall needs some sort of CRUD
 * 
 * @version 2014-10-31
 * @author Christian Janeczek
 */
public class SSHReader implements PolicyReader {

	private static final Logger logger = Logger.getLogger(RTNMain.class);
	private SSHProperties sshProperties;
	private String hostname, username, password;

	public SSHReader() throws IOException {

		// initializing the values, which are needed for reading the policies
		this.sshProperties = new SSHProperties();
		this.hostname = this.sshProperties.getHostname();
		this.username = this.sshProperties.getUsername();
		this.password = this.sshProperties.getPassword();

		// I tried the following libraries:
		// *Jsch
		// *sshj
		// etc.
		// They had no documentation at all

		try {
			/* Create a connection instance */

			Connection conn = new Connection(hostname);

			/* Now connect */

			conn.connect();

			/*
			 * Authenticate. If you get an IOException saying something like
			 * "Authentication method password not supported by the server at this stage."
			 * then please check the FAQ.
			 */

			boolean isAuthenticated = conn.authenticateWithPassword(username,
					password);

			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");

			/* Create a session */

			Session sess = conn.openSession();

			sess.execCommand("get policy all");

			System.out
					.println("Here is some information about the remote host:");

			/*
			 * This basic example does not handle stderr, which is sometimes
			 * dangerous (please read the FAQ).
			 */

			InputStream stdout = new StreamGobbler(sess.getStdout());

			BufferedReader br = new BufferedReader(
					new InputStreamReader(stdout));

			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				System.out.println(line);
			}

			/* Show exit status, if available (otherwise "null") */

			System.out.println("ExitCode: " + sess.getExitStatus());

			/* Close this session */

			sess.close();

			/* Close the connection */

			conn.close();

		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(2);
		}
	}

	@Override
	public List<List<String>> read() {
		// TODO Auto-generated method stub
		return null;
	}
}
