package application;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import engine.network.snmp.SNMPReader;
import engine.network.snmp.SNMPv2Reader;
import engine.network.snmp.SNMPv3Reader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * 
 * @author Osman Oezsoy, Christian Janeczek, Wolfgang Mair
 * @version 2014-10-31
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

	/**
	 * Die main die einen SNMPv2Reader startet und die GUI ausführt
	 * 
	 * @param args tut nichts
	 */
	public static void main(String[] args) {
		logger.info("Starting the application!");
		SNMPReader read2 = new SNMPv2Reader("Firewall", new File("mib/NS-POLICY.mib"), "10.0.100.10", "5xHIT", "udp");
		logger.info("SNMPv2Reader generated");
		logger.info("Starting readMethod");
		
		for(List<String> list : read2.read()){
			for(String value : list){
				System.out.print(value+", ");
			}
			System.out.println();
		}
		
		launch(args);
		logger.info("The application was terminated/closed! Bye Bye");
	}
}
