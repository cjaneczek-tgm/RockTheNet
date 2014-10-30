package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.snmp4j.smi.OID;

import engine.network.PolicyEntry;
import engine.network.snmp.SNMPv2Reader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * 
 * @author Osman Oezsoy, Christian Janeczek, Wolfgang Mair
 * @version 2014-10-29
 */
public class RTNMain extends Application {

	private static final Logger logger = Logger.getLogger(RTNMain.class);

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource(
					"/application/RTNView.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(
					getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Rock the net");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		logger.info("Starting the application!");
		List<PolicyEntry> pList;
		SNMPv2Reader read2 = new SNMPv2Reader("Firewall", new File("mib/NS-POLICY.mib"), "10.0.100.10", "5xHIT", "udp");
		logger.info("SNMPv2Reader generated");
		pList = read2.getPolicyEntries();
		
		for (PolicyEntry pol : pList) {
			
			System.out.println("OID="+pol.getCurrentOid() +", Type="+pol.getType()+", Value="+ pol.getValue());
		}
		System.out.println(""+read2.getMonBytesSec(78));
		
//		logger.info("Connection started");
//		for (List<String> list : read2.read()) {
//			System.out.println(list.toString());
//		}
		
		launch(args);
		logger.info("The application was terminated/closed! Bye Bye");
	}
}
