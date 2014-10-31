package engine.network;

public class Settings {

	private String oid;
	private String oid2;
	private String oid3;
	private String oid4;
	private String bytePerSecond;
	
	public Settings(){
		oid = ".1.3.6.1.4.1.3224.10.1.1";
		oid2 = ".1.3.6.1.4.1.3224.10.1.1.24";
		oid3 = ".1.3.6.1.4.1.3224.10.1.1.3";
		oid4 = ".1.3.6.1.4.1.3224.10.1.1.25";
		bytePerSecond = ".1.3.6.1.4.1.3224.10.2.1.6";
	}

	public String getOid() {
		return oid;
	}
	
	public String getOid2() {
		return oid2;
	}
	
	public String getbytePerSecond() {
		return bytePerSecond;
	}

	public String getOid3() {
		return oid3;
	}

	public String getOid4() {
		return oid4;
	}
	
	
}
