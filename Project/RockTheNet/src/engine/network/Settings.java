package engine.network;

public class Settings {

	private String oid;
	private String oid2;
	private String bytePerSecond;
	
	public Settings(String firewall){
		oid = ".1.3.6.1.4.1.3224.10.1";
		oid2 = ".1.3.6.1.4.1.3224.10.1.1.24";
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
}
