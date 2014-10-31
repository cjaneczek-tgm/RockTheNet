package engine.network;

import java.util.ArrayList;

public class PolicyEntry {
	
	private String name;
	private String currentOid;
	private String zone;
	private String service;
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getZone() {
		return zone;
	}



	public void setZone(String zone) {
		this.zone = zone;
	}



	public String getService() {
		return service;
	}



	public void setService(String service) {
		this.service = service;
	}



	public String getCurrentOid() {
		return currentOid;
	}



	public void setCurrentOid(String currentOid) {
		this.currentOid = currentOid;
	}



	public void addName(String name){
		this.name = name;
	}

	public String getValue() {
		return name;
	}

	public void setValue(String name) {
		this.name = name;
	}

	public ArrayList<String> getList(){
		ArrayList<String> list = new ArrayList<String>();
		
		list.add(this.currentOid);
		list.add(this.name);
		list.add(this.zone);
		list.add(this.service);
		
		return list;
	}
	
}
