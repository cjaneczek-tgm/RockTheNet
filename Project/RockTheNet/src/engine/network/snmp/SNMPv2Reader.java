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
	
	/**
	 * The IP-Address of the Device
	 */
	private String url;
	
	/**
	 * The current to read OID
	 */
	private String strOID;
	
	/**
	 * Community String
	 */
	private String community;
	
	/**
	 * The SNMP version, ist SNMPv2c
	 */
	private final int snmpVersion = SnmpConstants.version2c;
	
	/**
	 * The Portnummer of SNMP
	 */
	private final String port = "161";
	
	/**
	 * The Connection-Type to the Firewall (UDP/TCP)
	 */
	private String readWith = "udp";
	
	/**
	 * Address to communicate with
	 */
	private Address targetAddress;
	
	/**
	 * TransportMapping
	 */
	private TransportMapping<? extends Address> transport;
	
	/**
	 * Session
	 */
	private Snmp snmp;
	
	/**
	 * Target of the Community
	 */
	private CommunityTarget target;
	
	/**
	 * Treeutils
	 */
	private TreeUtils treeUtils;
	
	/**
	 * Settingsclass
	 */
	private Settings settings;
	
	/**
	 * Firewallname
	 */
	private String firewallname;
	
	/**
	 * Mibtranslator
	 */
//	private MIBTranslator mibtranslator;
	
	/**
	 * PolicyTypes
	 */
	private List<String> policytypes = new LinkedList<String>();
	
	
	/**
	 * Constructor with parameters
	 */
	public SNMPv2Reader(String firewallname, File mibfile, String url, String community, String connectWith) {
		this.community = community;
		this.readWith = connectWith;
		this.url = url;
		policytypes = new LinkedList<String>();
		this.firewallname = firewallname.toLowerCase();
	}
	
	/**
	 * Setup everthing general for SNMP-Communication
	 */
	private void setup() {
		try {
			//Genarte an Address to read
			targetAddress = GenericAddress.parse(readWith + ":" + url + "/" + port);
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			//setting up target
			target = new CommunityTarget();
			target.setCommunity(new OctetString(community));
			target.setAddress(targetAddress);
			target.setRetries(3);
			target.setTimeout(3000);
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
		List<PolicyEntry> list = null;
		setup();
		start();
		//Read from Settings PolicyId
		settings = new Settings("firewall");
		strOID = settings.getOid();
		int maxrep = countPolicies(strOID);
		int i = 0, k = 0;
		list = new LinkedList<PolicyEntry>();
		//Read from Settings Policy
		strOID = settings.getOid2();
		if(subtree(strOID, maxrep) == null) {
			logger.error("Subtree null");
		} else {
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
							entry.addValue(value.getVariable().toString());
							entry.setCurrentOid(value.getOid().toString());
							entry.setType(value.getVariable().getSyntaxString());
							list.add(k, entry);
						} else {
							entry = list.get(k);
							entry.addValue(value.getVariable().toString());
							list.set(k, entry);
						} k++; //System.out.println(value.getOid());
					} i++;
				}
			} stop();
		} return list;
	}

	/**
	 * It is only useful to call this methode after public List<PolicyEntry> getPolicyEntries()
	 * because in the other Methode Data is colleting to do this Methode.
	 * @see mim.firewall.FirewallReader#getPolicyEntries()
	 */
	public List<String> getPolicyTypes() {
		
		return this.getPolicyTypes();
	}

	/**
	 * @see mim.firewall.FirewallReader#getMonBytesSec(int index)
	 * If the number can not been read the return is -1
	 */
	public synchronized int getMonBytesSec(int index) {
		int returnvalue = -1;
		setup();
		start();
		//Read from Settings PolicyId
		settings = new Settings("firewall");
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
			for(VariableBinding vb : pduresponse.getVariableBindings()) {returnvalue = vb.getVariable().toInt();}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		stop();
		return returnvalue;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public List<String> getPolicyTypes(String OID){
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
	 * Method we might not need
	 */
	@Override
	public List<List<String>> read() {
		File file = new File("mib/NS-POLICY.mib");
		Mib mib = loadMib(file);
		
		List<List<String>> list = new ArrayList<List<String>>();

		Iterator   iter = mib.getAllSymbols().iterator();
		MibSymbol  symbol;
		MibValue   value;

		while (iter.hasNext()) {
			List<String> listTemp = new ArrayList<String>();
			symbol = (MibSymbol) iter.next();
			value = extractOid(symbol);
			if (value != null) {
				listTemp.add(value.toString());
				listTemp.add(symbol.getName());
				list.add(listTemp);
			}
		}
		return list;
	}

}
