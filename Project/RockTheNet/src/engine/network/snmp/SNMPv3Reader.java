package engine.network.snmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import application.RTNMain;
import engine.network.PolicyEntry;
import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibValue;

/**
 * A class which provides methods to connect to the firewall and read the
 * firewall rules with snmpv3
 * 
 * @author Wolfgang Mair
 * @version 2014-10-31
 */
public class SNMPv3Reader implements SNMPReader {

	private static final Logger logger = Logger.getLogger(RTNMain.class);

	private String url;
	private String textOID;
	private String community;
	private int snmpVersion = SnmpConstants.version3;
	private final String port = "161";
	private String protocol = "udp";
	private Address targetAddress;
	private TransportMapping<? extends Address> transport;
	private Snmp snmp;
	private CommunityTarget target;
	private TreeUtils treeUtils;
	private List<String> policytypes = new LinkedList<String>();
	private File mibfile;

	/**
	 * Constructor with parameters
	 */
	public SNMPv3Reader(String firewallname, File mibfile, String url,
			String community, String connectWith) {
		this.community = community;
		this.protocol = connectWith;
		this.url = url;
		this.mibfile = mibfile;
		policytypes = new LinkedList<String>();
	}

	/**
	 * A method which prepares a snmp connection with snmpv3
	 */
	private void setup() {
		try {
			logger.info("Preparing the connection");

			targetAddress = GenericAddress.parse(protocol + ":" + url + "/"
					+ port);
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);

			// Building the address to read
			snmp.getUSM().addUser(
					new OctetString("5ahit"),
					new UsmUser(new OctetString("5ahit"), AuthMD5.ID,
							new OctetString("Waeng7ohch8o"), PrivDES.ID,
							new OctetString("Waeng7ohch8o")));
			// create the target
			UserTarget target = new UserTarget();
			target.setAddress(targetAddress);
			target.setRetries(2);
			target.setTimeout(2000);
			target.setVersion(snmpVersion);
			target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
			target.setSecurityName(new OctetString("5ahit"));

			treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		} catch (IOException e) {
			logger.error(e.getStackTrace().toString());
		} catch (NullPointerException e) {
			logger.error(e.getStackTrace().toString());
		}
	}

	/**
	 * A method wich starts the listen method in the SNMP object
	 */
	@Override
	public void open() {
		logger.info("Starting to listen");
		try {
			snmp.listen();
			logger.info("Is listening");
		} catch (IOException e) {
			logger.error(e.getStackTrace().toString());
		}
	}

	/**
	 * Method which closes the current snmpv2 Object
	 */
	@Override
	public void close() {
		logger.info("Closing the connection");
		try {
			snmp.close();
			logger.info("The connection is closed");
		} catch (IOException e) {
			logger.error(e.getStackTrace().toString());
		}
	}

	/**
	 * Return a List of TreeEvents which is read from the subtree of a OID with
	 * an max
	 * 
	 * @param tempoid
	 *            The OID to get the subtree from
	 * @param maxrep
	 *            The amount of VariableBindings per TreeEvent
	 * @return a List of TreeEvents
	 */
	private List<TreeEvent> subtree(String tempoid, int maxrep) {
		List<TreeEvent> subtree = null;
		if (treeUtils != null && target != null) {
			if (maxrep > 0)
				treeUtils.setMaxRepetitions(maxrep);
			subtree = treeUtils.getSubtree(target, new OID(tempoid));
			return subtree;
		} else {
			logger.error("TreeUtils or Target null");
			return null;
		}
	}

	/**
	 * Counting the Policies
	 * 
	 * @param oid
	 *            The Root-OID
	 * @return the count of Policies
	 */
	private int countPolicies(String oid) {
		logger.info("Starting to count");
		int count = 0;
		List<TreeEvent> events = subtree(oid, -1);
		if (events == null) {
			logger.error("Subtree null");
		} else {
			for (TreeEvent event : events) {
				if (event != null) {
					if (event.isError()) {
						count = -1;
					}
					VariableBinding[] values = event.getVariableBindings();
					if (values == null) {
						count = -1;
					}
					for (int i = 0; i < values.length; i++) {
						count++;
					}
				}
			}
		}
		logger.info("Finished counting");
		return count;
	}

	/**
	 * Reads all the Values we need for the policies
	 * 
	 * @return A list with policy entries
	 */
	private List<PolicyEntry> getPolicyEntries() {

		logger.info("Starting to read the Policies");

		List<PolicyEntry> list = null;
		setup();
		open();
		// Read from Settings PolicyId
		textOID = ".1.3.6.1.4.1.3224.10.1.1";
		int maxrep = countPolicies(textOID);
		int i = 0, k = 0;
		list = new LinkedList<PolicyEntry>();

		// Read from Settings Policy
		textOID = ".1.3.6.1.4.1.3224.10.1.1.24";
		for (TreeEvent event : subtree(textOID, maxrep)) {
			if (event != null) {
				if (event.isError()) {
					logger.info(event.getErrorMessage());
				}
				VariableBinding[] values = event.getVariableBindings();
				if (values == null) {
					logger.info("Values are null");
				} else if (values.length == 0) {
					logger.info("No Values found");
				}
				PolicyEntry entry = null;
				k = 0;
				for (VariableBinding value : values) {
					if (k == 0) {
						String tempoid = String.valueOf(value.getOid());
						policytypes.add(tempoid.subSequence(0,
								tempoid.length() - 4).toString());
					}
					if (i == 0) {
						entry = new PolicyEntry();
						entry.setName(value.getVariable().toString());
						entry.setCurrentOid(value.getOid().toString());
						list.add(k, entry);
					} else {
						entry = list.get(k);
						entry.setName(value.getVariable().toString());
						list.set(k, entry);
					}
					k++;
				}
				i++;
			}
		}

		textOID = ".1.3.6.1.4.1.3224.10.1.1.3";
		for (TreeEvent event : subtree(textOID, maxrep)) {
			if (event != null) {
				if (event.isError()) {
					logger.info(event.getErrorMessage());
				}
				VariableBinding[] values = event.getVariableBindings();
				if (values == null) {
					logger.info("Values are null");
				} else if (values.length == 0) {
					logger.info("No Values found");
				}
				PolicyEntry entry = null;
				k = 0;
				for (VariableBinding value : values) {
					if (k == 0) {
						String tempoid = String.valueOf(value.getOid());
						policytypes.add(tempoid.subSequence(0,
								tempoid.length() - 4).toString());
					}
					if (i == 0) {
						entry = new PolicyEntry();
						entry.setZone(value.getVariable().toString());
						entry.setCurrentOid(value.getOid().toString());
						list.add(k, entry);
					} else {
						entry = list.get(k);
						entry.setZone(value.getVariable().toString());
						list.set(k, entry);
					}
					k++;
				}
				i++;
			}
		}

		textOID = ".1.3.6.1.4.1.3224.10.1.1.25";
		for (TreeEvent event : subtree(textOID, maxrep)) {
			if (event != null) {
				if (event.isError()) {
					logger.info(event.getErrorMessage());
				}
				VariableBinding[] values = event.getVariableBindings();
				if (values == null) {
					logger.info("Values are null");
				} else if (values.length == 0) {
					logger.info("No Values found");
				}
				PolicyEntry entry = null;
				k = 0;
				for (VariableBinding value : values) {
					if (k == 0) {
						String tempoid = String.valueOf(value.getOid());
						policytypes.add(tempoid.subSequence(0,
								tempoid.length() - 4).toString());
					}
					if (i == 0) {
						entry = new PolicyEntry();
						entry.setService(value.getVariable().toString());
						entry.setCurrentOid(value.getOid().toString());
						list.add(k, entry);
					} else {
						entry = list.get(k);
						entry.setService(value.getVariable().toString());
						list.set(k, entry);
					}
					k++;
				}
				i++;
			}
		}
		close();
		logger.info("Finished reading the Policies");
		return list;
	}

	/**
	 * A Method which reads the bytes per second of an OID
	 * 
	 * @param index
	 *            The last specifing OID-branch number of the policy
	 * @return The bytes per second
	 */
	@Override
	public synchronized int getMonBytesSec(int index) {
		int value = 0;
		// Preparing connection
		setup();
		// Listening to connection
		open();

		// Read from Settings PolicyId
		textOID = ".1.3.6.1.4.1.3224.10.2.1.6." + index + ".0";
		PDU pdu = new PDU();
		pdu.addOID(new VariableBinding(new OID(textOID)));
		pdu.setType(PDU.GET);
		pdu.setMaxRepetitions(5);
		ResponseEvent response;

		try {
			response = snmp.send(pdu, target);
			PDU pduresponse = response.getResponse();
			for (VariableBinding vb : pduresponse.getVariableBindings()) {
				value = vb.getVariable().toInt();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		close();
		return value;
	}

	/**
	 * A Method which returns a list with all Policytypes of a certain
	 * 
	 * @return Eine Liste mit den OIDs der Policytypen
	 */
	private List<String> getPolicyTypes() {
		Mib mib = this.loadMib(mibfile);
		ArrayList<String> list = new ArrayList<String>();
		Iterator iter = mib.getAllSymbols().iterator();
		MibSymbol symbol;
		MibValue value;

		while (iter.hasNext()) {
			symbol = (MibSymbol) iter.next();
			list.add(symbol.getName());
		}

		return list;
	}

	/**
	 * A Method which loads a Mib File and returns a Mib Object which can be
	 * used to read the table and the values of the table
	 * 
	 * @param file
	 *            A File Object which contains the path of the mibFile
	 * @return A Mib Object which contains useful methods for snmp
	 */
	private Mib loadMib(File file) {

		MibLoader loader = new MibLoader();

		loader.addDir(file.getParentFile());
		try {
			return loader.load(file);
		} catch (MibLoaderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * A Method which returns a double List collection in order to generate a
	 * Table Each List is a Policy entry with the data: OID,name,zone,service in
	 * String form
	 * 
	 * @return A List with another list inside which contains Strings (Policy
	 *         info)
	 */
	@Override
	public List<List<String>> read() {
		List<List<String>> list = new ArrayList<List<String>>();

		for (PolicyEntry entry : this.getPolicyEntries()) {
			list.add(entry.getList());
		}
		return list;
	}

}
