package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import engine.network.snmp.SNMPv2Reader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 
 * @author Osman Oezsoy
 * @version 2014-10-29
 */
public class RTNController implements Initializable {

	@FXML
	private MenuItem login;

	@FXML
	private MenuItem logout;

	@FXML
	private MenuItem accountInformation;

	@FXML
	private MenuItem connectToDevice;

	@FXML
	private MenuItem disconnect;

	@FXML
	private MenuItem support;

	@FXML
	private MenuItem about;

	@FXML
	private Button bConnect;

	@FXML
	private Button bRefresh;

	@FXML
	private Button bSignIn;

	@FXML
	private TextField usernameTf;

	@FXML
	private PasswordField passwordPf;

	@FXML
	private TextField serverAddressTf;

	@FXML
	private TextField portTf;

	@FXML
	private ComboBox snmpComboBox;

	@FXML
	private RadioButton udpRadio;

	@FXML
	private RadioButton tcpRadio;

	@FXML
	private CheckBox saveConPropertiesCheckBox;

	@FXML
	private TableView policyTable;

	@FXML
	private Tab chartTab;

	@FXML
	private Tab propertiesTab;

	@FXML
	private RadioButton byteRadio;

	@FXML
	private RadioButton kiloByteRadio;

	@FXML
	private RadioButton megaByteRadio;

	@FXML
	private TextField refreshTf;

	@FXML
	private TextField firewallRuleTf;

	@FXML
	private TextField serviceTf;

	@FXML
	private TextField zoneTf;

	@FXML
	final NumberAxis xAxis = new NumberAxis();

	@FXML
	final NumberAxis yAxis = new NumberAxis();

	@FXML
	final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
			xAxis, yAxis);

	private XYChart.Series series;

	/**
	 * 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// setting the title of the line chart
		lineChart.setTitle("Through-put");
	}

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
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

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void logoutUser(ActionEvent event) throws IOException {

	}

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
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

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
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

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void disconnectFromDevice(ActionEvent event) throws IOException {

	}

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
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

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
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

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void refreshInterval(ActionEvent event) throws IOException {
		refreshLineChart();
	}

	/**
	 * 
	 */
	@FXML
	public void refreshLineChart() {
		
	}

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void addProperties(ActionEvent event) throws IOException {

	}

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void deleteProperties(ActionEvent event) throws IOException {

	}

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void saveProperties(ActionEvent event) throws IOException {

	}

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void createConnectionToDevice(ActionEvent event) throws IOException {

	}

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void signInUser(ActionEvent event) throws IOException {

	}
}
