package engine.network.snmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
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
import engine.network.Settings;
import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.value.ObjectIdentifierValue;

/**
 * SNMPv2-Implementation
 * 
 * @version 2014-10-23
 */
public class SNMPv2Reader implements SNMPReader {

	private static final Logger logger = Logger.getLogger(RTNMain.class);

	private String url;
	private String strOID;
	private String community;	
	private int snmpVersion = SnmpConstants.version2c;
	private final String port = "161";
	private String readWith = "udp";
	private Address targetAddress;
	private TransportMapping<? extends Address> transport;
	private Snmp snmp;
	private CommunityTarget target;
	private TreeUtils treeUtils;
	private Settings settings;
	private List<String> policytypes = new LinkedList<String>();


	/**
	 * Constructor with parameters
	 */
	public SNMPv2Reader(String firewallname, File mibfile, String url, String community, String connectWith) {
		this.community = community;
		this.readWith = connectWith;
		this.url = url;
		policytypes = new LinkedList<String>();
	}

	/**
	 * Setup everthing general for SNMP-Communication
	 */
	private void setup() {
		try {
			logger.info("Preparing connection ...");
			//Genarte an Address to read
			targetAddress = GenericAddress.parse(readWith + ":" + url + "/" + port);
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			//setting up target
			target = new CommunityTarget();
			target.setCommunity(new OctetString(community));
			target.setAddress(targetAddress);
			target.setRetries(2);
			target.setTimeout(2000);
			target.setVersion(snmpVersion);
			treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		} catch (IOException e) {
			logger.error(e.getMessage() + "\n" + e.getStackTrace().toString());
		} catch (NullPointerException e) {
			logger.error(e.getMessage() + "\n" + e.getStackTrace().toString());
		}
	}

	/**
	 * Start SNMP-Communication
	 */
	private void start() {
		logger.info("Starting to listen ...");
		try {
			snmp.listen();
		} catch (IOException e) {
			logger.error(e.getMessage() + "\n" + e.getStackTrace().toString());
		}
	}

	/**
	 * Stop SNMP-Communication
	 */
	private void stop() {
		logger.info("Closing connection ...");
		try {
			snmp.close();
		} catch (IOException e) {
			logger.error(e.getMessage() + "\n" + e.getStackTrace().toString());
		}
	}

	/**
	 * Return a List of TreeEvents read with GETBULK internly,
	 * the VariableBindings per TreeEvent are random, but mostly 10
	 * @param tempoid The OID to Bulk
	 * @return a List of TreeEvents
	 */
	private List<TreeEvent> subtree(String tempoid) {
		//read Subtree from the given OID
		if(treeUtils != null && target != null) {
			List<TreeEvent> subtree = treeUtils.getSubtree(target, new OID(tempoid));
			return subtree;
		} else {
			logger.error("TreeUtils or Target null");
			return null;
		}
	}

	/**
	 * Return a List of TreeEvents read with GETBULK internly,
	 * the VariableBindings per TreeEvent are maxrep
	 * @param tempoid The OID to Bulk
	 * @param maxrep Count of VariableBindings per TreeEvent
	 * @return a List of TreeEvents
	 */
	private List<TreeEvent> subtree(String tempoid, int maxrep) {
		List<TreeEvent> subtree = null;
		if(treeUtils != null && target != null) {
			treeUtils.setMaxRepetitions(maxrep);
			subtree = treeUtils.getSubtree(target, new OID(tempoid));
			return subtree;
		} else {
			logger.error("TreeUtils or Target null");
			return null;
		}
	}

	/**
	 * Count the Policies, 
	 * over getting the IDs with subtree an count them
	 * @param oidSubtreePolicyIDs The Root-OID of the PolicyIDs
	 * @return the count of Policies
	 */
	public int countPolicies(String oidSubtreePolicyIDs) {
		logger.info("Counting started!");
		int count = 0;
		List<TreeEvent> events = subtree(oidSubtreePolicyIDs);
		if(events==null) {
			logger.error("Subtree null");
		} else {
			for(TreeEvent event : events) {
				if (event != null) {
					if (event.isError()) {count = -1;}
					VariableBinding[] values = event.getVariableBindings();
					if (values == null) {count = -1;}
					for (int i = 0; i < values.length; i++) {count++;}
				}
			}
		}
		return count;
	}

	/**
	 * @see mim.firewall.FirewallReader#getPolicyEntries()
	 */
	public List<PolicyEntry> getPolicyEntries() {
		
		logger.info("Starting to read Policies ...");
		
		List<PolicyEntry> list = null;
		setup();
		start();
		//Read from Settings PolicyId
		settings = new Settings();
		strOID = settings.getOid();
		int maxrep = countPolicies(strOID);
		int i = 0, k = 0;
		list = new LinkedList<PolicyEntry>();
		//Read from Settings Policy
		strOID = settings.getOid2();
		for(TreeEvent event : subtree(strOID, maxrep)) {
			if (event != null) {
				if (event.isError()) {
					logger.info(event.getErrorMessage());
				}
				VariableBinding[] values = event.getVariableBindings();
				if (values == null) {
					logger.info("No result returned. Values null");
				} else if (values.length == 0) {
					logger.info("No result returned. 0 Values");
				}
				PolicyEntry entry = null;
				k = 0;
				for (VariableBinding value : values) {
					if(k == 0) {
						String tempoid = String.valueOf(value.getOid());
						policytypes.add(tempoid.subSequence(0, tempoid.length()-4).toString());
					}
					if(i==0) {
						entry = new PolicyEntry();
						entry.setName(value.getVariable().toString());
						entry.setCurrentOid(value.getOid().toString());
						list.add(k, entry);
					} else {
						entry = list.get(k);
						entry.setName(value.getVariable().toString());
						list.set(k, entry);
					} k++; //System.out.println(value.getOid());
				} i++;
			}
		} 
		strOID = settings.getOid3();
		for(TreeEvent event : subtree(strOID, maxrep)) {
			if (event != null) {
				if (event.isError()) {
					logger.info(event.getErrorMessage());
				}
				VariableBinding[] values = event.getVariableBindings();
				if (values == null) {
					logger.info("No result returned. Values null");
				} else if (values.length == 0) {
					logger.info("No result returned. 0 Values");
				}
				PolicyEntry entry = null;
				k = 0;
				for (VariableBinding value : values) {
					if(k == 0) {
						String tempoid = String.valueOf(value.getOid());
						policytypes.add(tempoid.subSequence(0, tempoid.length()-4).toString());
					}
					if(i==0) {
						entry = new PolicyEntry();
						entry.setZone(value.getVariable().toString());
						entry.setCurrentOid(value.getOid().toString());
						list.add(k, entry);
					} else {
						entry = list.get(k);
						entry.setZone(value.getVariable().toString());
						list.set(k, entry);
					} k++; //System.out.println(value.getOid());
				} i++;
			}
		} 
		strOID = settings.getOid4();
		for(TreeEvent event : subtree(strOID, maxrep)) {
			if (event != null) {
				if (event.isError()) {
					logger.info(event.getErrorMessage());
				}
				VariableBinding[] values = event.getVariableBindings();
				if (values == null) {
					logger.info("No result returned. Values null");
				} else if (values.length == 0) {
					logger.info("No result returned. 0 Values");
				}
				PolicyEntry entry = null;
				k = 0;
				for (VariableBinding value : values) {
					if(k == 0) {
						String tempoid = String.valueOf(value.getOid());
						policytypes.add(tempoid.subSequence(0, tempoid.length()-4).toString());
					}
					if(i==0) {
						entry = new PolicyEntry();
						entry.setService(value.getVariable().toString());
						entry.setCurrentOid(value.getOid().toString());
						list.add(k, entry);
					} else {
						entry = list.get(k);
						entry.setService(value.getVariable().toString());
						list.set(k, entry);
					} k++; //System.out.println(value.getOid());
				} i++;
			}
		} 
		stop();
		return list;
	} 

	/**
	 * @see mim.firewall.FirewallReader#getMonBytesSec(int index)
	 * If the number can not been read the return is -1
	 */
	public synchronized int getMonBytesSec(int index) {
		int value = -1;
		setup();
		start();
		//Read from Settings PolicyId
		settings = new Settings();
		strOID = settings.getbytePerSecond() + "." + index + ".0";
		//SNMP stuff
		PDU pdu = new PDU();
		pdu.addOID(new VariableBinding(new OID(strOID)));
		pdu.setType(PDU.GET);
		pdu.setMaxRepetitions(10);
		ResponseEvent response;
		try {
			response = snmp.send(pdu, target);
			PDU pduresponse = response.getResponse();
			for(VariableBinding vb : pduresponse.getVariableBindings()) {value = vb.getVariable().toInt();}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		stop();
		return value;
	}



	/**
	 * A Method which returns a list with all Policytypes of a certain
	 * 
	 * @param OID
	 * @return
	 */
	public List<String> getPolicyTypes(){
		Mib mib = this.loadMib(new File("mib/NS-POLICY.mib"));
		ArrayList<String> list = new ArrayList<String>();
		Iterator   iter = mib.getAllSymbols().iterator();
		MibSymbol  symbol;
		MibValue   value;

		while (iter.hasNext()) {
			symbol = (MibSymbol) iter.next();
			list.add(symbol.getName());
		}

		return list;
	}


	/**
	 * A Method which loads a Mib File and returns a Mib Object
	 * which can be used to read the table and the values of the table
	 * 
	 * @param file A File Object which contains the path of the mibFile
	 * @return   A Mib Object which contains useful methods for snmp
	 */
	public Mib loadMib(File file){

		MibLoader  loader = new MibLoader();

		loader.addDir(file.getParentFile());
		try {
			return loader.load(file); 
		} catch ( MibLoaderException e) {
			e.printStackTrace();
		}
		catch ( FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * A Method which generates a HashMap with the symbolname as key and its value as value
	 * 
	 * @param mib The Mib Object which was generated through the mib file
	 * @return   A Hashmap with symbolname as key and its value as value
	 */
	public HashMap extractOids(Mib mib) {
		HashMap    map = new HashMap();
		Iterator   iter = mib.getAllSymbols().iterator();
		MibSymbol  symbol;
		MibValue   value;

		while (iter.hasNext()) {
			symbol = (MibSymbol) iter.next();
			value = extractOid(symbol);
			if (value != null) {
				map.put(symbol.getName(), value);
			}
		}
		return map;
	}

	/**
	 * A Method which takes a MibSymbol and extracts a ObjectidentifierValue
	 *from it if MibSymbol is in the instance of MibValueSymbol
	 * 
	 * @param symbol
	 * @return
	 */
	public ObjectIdentifierValue extractOid(MibSymbol symbol) {
		MibValue  value;

		if (symbol instanceof MibValueSymbol) {
			value = ((MibValueSymbol) symbol).getValue();
			if (value instanceof ObjectIdentifierValue) {
				return (ObjectIdentifierValue) value;
			}
		}
		return null;
	}


	/**
	 * A Method which opens a connection
	 */
	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

	/**
	 * Method which closes the current snmpv2 Object
	 */
	@Override
	public void close() {

	}

	/**
	 * A Method which returns a double List collection in order to generate a Table
	 * Each List is a Policy entry with the data: OID,name,zone,service in String form
	 * 
	 * @return   A List with another list inside which contains Strings (Policy info) 
	 */
	@Override
	public List<List<String>> read() {
		List<List<String>> list = new ArrayList<List<String>>();
		
		for(PolicyEntry entry : this.getPolicyEntries()){
			list.add(entry.getList());
		}
		return list;
	}

}
