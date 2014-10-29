package application;

import java.io.File;
import java.util.List;

import net.percederberg.mibble.Mib;

import org.apache.log4j.Logger;

import engine.network.snmp.SNMPv2Reader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

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
		SNMPv2Reader read2 = new SNMPv2Reader();
		logger.info("SNMPv2Reader generated");
		read2.open();
		logger.info("Connection started");
		for(List<String> list : read2.read()){
			System.out.println(list.toString());
		}
		logger.info("Generated List");
		launch(args);
	}
}
