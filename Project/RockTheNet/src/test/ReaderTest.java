package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import net.percederberg.mibble.Mib;

import org.junit.Before;
import org.junit.Test;

import engine.network.snmp.SNMPv2Reader;
import engine.network.snmp.SNMPv3Reader;

public class ReaderTest {
	
	private File file = new File("mib/NETSCREEN-SMI.mib");
	private File fileX = new File("mib/GhostData.mib");
	SNMPv2Reader v2Reader = new SNMPv2Reader("Firewall", new File("mib/NS-POLICY.mib"), "10.0.100.10", "5xHIT", "udp");
	SNMPv3Reader v3Reader = new SNMPv3Reader("Firewall", new File("mib/NS-POLICY.mib"), "10.0.100.10", "5xHIT", "udp");
	
	@Before
	public void setup(){
		SNMPv2Reader v2Reader = new SNMPv2Reader("Firewall", new File("mib/NS-POLICY.mib"), "10.0.100.10", "5xHIT", "udp");
		SNMPv3Reader v3Reader = new SNMPv3Reader("Firewall", new File("mib/NS-POLICY.mib"), "10.0.100.10", "5xHIT", "udp");
	}
	
	
	
}
