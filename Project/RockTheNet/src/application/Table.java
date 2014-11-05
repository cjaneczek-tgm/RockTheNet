package application;

import javafx.beans.property.SimpleStringProperty;

/**
 * 
 * @author Christian Bobek
 * @version 2014-10-30
 */
public class Table {
	private final SimpleStringProperty policyName;
	private final SimpleStringProperty oID;
	private final SimpleStringProperty value;
	private final SimpleStringProperty zone;

	/**
	 * 
	 * @param spolicyName
	 * @param soID
	 * @param svalue
	 * @param szone
	 */
	public Table(String spolicyName, String soID, String svalue, String szone) {
		this.policyName = new SimpleStringProperty(spolicyName);
		this.oID = new SimpleStringProperty(soID);
		this.value = new SimpleStringProperty(svalue);
		this.zone = new SimpleStringProperty(szone);

	}

	public String getPolicyName() {
		return policyName.get();
	}

	public void setPolicyName(String fName) {
		policyName.set(fName);
	}

	public String getOID() {
		return oID.get();
	}

	public void setOID(String fName) {
		oID.set(fName);
	}

	public String getValue() {
		return value.get();
	}

	public void setValue(String fName) {
		value.set(fName);
	}

	public String getZone() {
		return zone.get();
	}

	public void setZone(String fName) {
		zone.set(fName);
	}
}