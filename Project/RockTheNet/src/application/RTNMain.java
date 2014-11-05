package application;

import org.apache.log4j.Logger;

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
	 * Main-method, which starts the GUI
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("Starting the application!");
		launch(args);
		logger.info("The application was terminated/closed! Bye Bye");
	}
}
