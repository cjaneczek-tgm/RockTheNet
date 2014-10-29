package engine.network.snmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
		File file = new File("NETSCREEN-SMI.mib");
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
