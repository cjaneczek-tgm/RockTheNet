package engine.network.snmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import application.RTNMain;
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
 * @author Christian Janeczek, Wolfgang Mair
 * @version 2014-10-29
 */
public class SNMPv2Reader implements SNMPReader {
	private static final Logger logger = Logger.getLogger(RTNMain.class);

	private String address;

	private Snmp snmp;


	public SNMPv2Reader(String address) {
		super();
		this.address = address;
		this.open();
	}

	public String getAsString(OID oid) throws IOException {
		ResponseEvent event = get(new OID[]{oid});
		return event.getResponse().get(0).getVariable().toString();
	}


	public void getAsString(OID oids,ResponseListener listener) {
		try {
			snmp.send(getPDU(new OID[]{oids}), getTarget(),null, listener);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	private PDU getPDU(OID oids[]) {
		PDU pdu = new PDU();
		for (OID oid : oids) {
			pdu.add(new VariableBinding(oid));
		}

		pdu.setType(PDU.GET);
		return pdu;
	}

	public ResponseEvent get(OID oids[]) throws IOException {
		ResponseEvent event = snmp.send(getPDU(oids), getTarget(), null);
		if(event != null) {
			return event;
		}
		throw new RuntimeException("GET timed out");	  
	}

	private Target getTarget() {
		Address targetAddress = GenericAddress.parse(address);
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));
		target.setAddress(targetAddress);
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);
		return target;
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
			logger.info("Loading the MibFile...");
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
		logger.error("MibFile loading error!");
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
		try{
			TransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			// Do not forget this line!
			transport.listen();
		}
		catch(IOException ioe){
			logger.error("SNMP starting transport error!");
			ioe.printStackTrace();
		}

	}

	/**
	 * Method which closes the current snmpv2 Object
	 */
	@Override
	public void close() {
		try {
			snmp.close();
		} catch (IOException e) {
			logger.error("SNMP closing error");
			e.printStackTrace();
		}
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
