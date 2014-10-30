package engine.network;

import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibValue;

public class PolicyEntry {
	
	private String value;
	private String currentOid;
	private String type;
	
	public PolicyEntry(){
		
	}

	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getCurrentOid() {
		return currentOid;
	}



	public void setCurrentOid(String currentOid) {
		this.currentOid = currentOid;
	}



	public void addValue(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
	
}
