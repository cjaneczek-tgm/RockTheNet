package engine.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Stores the OID-Definitions
 * 
 * @author Adrian Bergler
 * @version 2014-10-31
 */
public class OIDProps extends Properties {

	private String path = "properties/oid.properties";
	private InputStream input;
	private OutputStream output;

	private static OIDProps oidprops;

	// Singleton
	private OIDProps() throws IOException {
		super();

		input = new FileInputStream(new File(path));
		output = new FileOutputStream(new File(path));
		this.load(input);
	}

	/**
	 * Returns the OIDProps-instance
	 * 
	 * @return the OIDProps-instance
	 */
	public static OIDProps get() {
		if (oidprops == null)
			try {
				oidprops = new OIDProps();
			} catch (IOException e) {
				return null;
			}
		return oidprops;
	}

	/**
	 * Stores the current properties
	 * 
	 * @param comments
	 *            comments (if there are any)
	 * @throws IOException
	 *             if the file doesnt exist
	 */
	public void store(String comments) throws IOException {
		super.store(output, comments);
	}

	/**
	 * Closes the Streams
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		input.close();
		output.close();
	}
}
