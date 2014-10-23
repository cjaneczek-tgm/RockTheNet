package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RTNController implements Initializable {

	@FXML
	private MenuItem login;
	
	@FXML
	private MenuItem accountInformation;
	
	@FXML
	private MenuItem connectToDevice;
	
	@FXML
	private MenuItem support;
	
	@FXML
	private MenuItem about;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
			
	}
	
	@FXML
	public void showLoginWindow(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource(
				"/application/Login.fxml"));
		stage.setScene(new Scene(root));
		stage.setTitle("Login");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.show();
	}
	
	@FXML
	public void showAccountInfoWindow(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource(
				"/application/AccountInformation.fxml"));
		stage.setScene(new Scene(root));
		stage.setTitle("Account information");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.show();
	}
	
	@FXML
	public void showConnectionWindow(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource(
				"/application/ConnectToDevice.fxml"));
		stage.setScene(new Scene(root));
		stage.setTitle("Connect to device ...");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.show();
	}
	
	@FXML
	public void showSupportWindow(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource(
				"/application/Support.fxml"));
		stage.setScene(new Scene(root));
		stage.setTitle("Support");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.show();
	}
	
	@FXML
	public void showAboutWindow(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource(
				"/application/About.fxml"));
		stage.setScene(new Scene(root));
		stage.setTitle("About");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.show();
	}
}
