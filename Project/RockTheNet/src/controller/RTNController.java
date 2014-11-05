package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import engine.network.snmp.SNMPReader;
import engine.network.snmp.SNMPv2Reader;
import application.Koordinaten;
import application.LineChartIntervalRefresher;
import application.RTNMain;
import application.Table;
import javafx.application.Platform;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 
 * @author Osman Oezsoy, Christian Bobek
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
	private ComboBox zoneComboBox;

	@FXML
	private ComboBox actionComboBox;

	@FXML
	private RadioButton udpRadio;

	@FXML
	private RadioButton tcpRadio;

	@FXML
	private CheckBox saveConPropertiesCheckBox;

	@FXML
	private TableView<Table> policyTable = new TableView<>();

	@FXML
	private TableColumn<Table, String> policyNameCol = new TableColumn<>(
			"Policy Name");

	@FXML
	private TableColumn<Table, String> oIDCol = new TableColumn<>("OID");

	@FXML
	private TableColumn<Table, String> valueCol = new TableColumn<>("Value");

	@FXML
	private TableColumn<Table, String> zoneCol = new TableColumn<>("Zone");

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
	private TextField IDTf;

	@FXML
	private TextField sourceAddressTf;

	@FXML
	private TextField destinationAddressTf;

	@FXML
	private NumberAxis xAxis = new NumberAxis();

	@FXML
	private NumberAxis yAxis = new NumberAxis();

	@FXML
	private LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
			xAxis, yAxis);

	private ObservableList datalist;

	private List<String> list_policyNames = new ArrayList<String>();
	private List<String> list_oIDs = new ArrayList<String>();
	private List<String> list_values = new ArrayList<String>();
	private List<String> list_zones = new ArrayList<String>();

	private XYChart.Series series;

	private LineChartIntervalRefresher lcirf;

	private ArrayList<Koordinaten> lineChartData = new ArrayList<Koordinaten>();

	private int second;

	private String currentSelectedRB;

	private String previousSelectedRB;

	private SNMPReader read2;

	public ObservableList<Table> data;

	private static final Logger logger = Logger.getLogger(RTNMain.class);

	public RTNController() {
		// starting SNMPv2 read process to fill the table at program start
		startSNMPv2Read();

		this.currentSelectedRB = "byte";
		this.previousSelectedRB = "byte";

		// setting the title of the line chart
		this.lineChart.setTitle("Through-put");

		// initializing the refresh thread for the line chart
		this.lcirf = new LineChartIntervalRefresher(this);

		this.read2 = new SNMPv2Reader("Firewall",
				new File("mib/NS-POLICY.mib"), "10.0.100.10", "5xHIT", "udp");

		List<List<String>> list = read2.read();

		for (int i = 0; i < list.size(); i++) {
			list_policyNames.add(list.get(i).get(1));
			list_oIDs.add(list.get(i).get(0));
			list_values.add(list.get(i).get(3));
			list_zones.add(list.get(i).get(2));
		}

		this.data = FXCollections.observableArrayList();

		for (int t = 0; t < list.size(); t++) {
			String policyName = list_policyNames.get(t);
			String oid = list_oIDs.get(t);
			String value = list_values.get(t);
			String zone = list_zones.get(t);

			this.data.add(new Table(policyName, oid, value, zone));

		}

		Thread th = new Thread(this.lcirf);
		th.start();
	}

	/**
	 * 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.policyNameCol
				.setCellValueFactory(new PropertyValueFactory<Table, String>(
						"policyName"));
		this.oIDCol
				.setCellValueFactory(new PropertyValueFactory<Table, String>(
						"oID"));
		this.valueCol
				.setCellValueFactory(new PropertyValueFactory<Table, String>(
						"value"));
		this.zoneCol
				.setCellValueFactory(new PropertyValueFactory<Table, String>(
						"zone"));

		this.policyTable.setItems(this.data);
	}

	/**
	 * 
	 * @param sec
	 * @param data
	 */
	public void refreshLineChart(int sec, int data) {
		double d = data;

		if (this.kiloByteRadio.isSelected()) {
			for (int i = 0; i < this.lineChartData.size(); i++) {
				this.lineChartData.get(i).setY(
						this.lineChartData.get(i).getY() / 1024);
				System.out.println(this.lineChartData.get(i).getY());
			}

			d = d / 1024;
		} else {
			if (this.megaByteRadio.isSelected()) {
				for (int i = 0; i < this.lineChartData.size(); i++) {
					this.lineChartData.get(i).setY(
							(this.lineChartData.get(i).getY() / 1024) / 1024);
				}

				d = (d / 1024) / 1024;
			} else {
				d = d;
			}
		}

		this.lineChartData.add(new Koordinaten(sec, d));

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				refreshLC();
			}
		});
	}

	public void refreshLC() {
		this.series = new XYChart.Series();
		this.series.setName("Transaction rate");

		for (int i = 0; i < this.lineChartData.size(); i++) {
			this.series.getData().add(
					new XYChart.Data(this.lineChartData.get(i).getX(),
							this.lineChartData.get(i).getY()));
		}

		this.lineChart.getData().clear();
		this.lineChart.getData().add(this.series);

		if (this.kiloByteRadio.isSelected()) {
			for (int i = 0; i < this.lineChartData.size(); i++) {
				this.lineChartData.get(i).setY(
						this.lineChartData.get(i).getY() * 1024);
			}
		} else {
			if (this.megaByteRadio.isSelected()) {
				for (int i = 0; i < this.lineChartData.size(); i++) {
					this.lineChartData.get(i).setY(
							this.lineChartData.get(i).getY() * 1024 * 1024);
				}
			}
		}
	}

	public void startSNMPv2Read() {
		this.read2 = new SNMPv2Reader("Firewall",
				new File("mib/NS-POLICY.mib"), "10.0.100.10", "5xHIT", "udp");
		logger.info("SNMPv2Reader generated");
		logger.info("Starting readMethod");

		for (List<String> list : read2.read()) {
			for (String value : list) {
				System.out.print(value + ", ");
			}
			System.out.println();
		}
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

	}

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void refreshTable(ActionEvent event) throws IOException {
		if (!this.refreshTf.getText().equals("")) {
			this.lcirf
					.setSleepTime(Integer.parseInt(this.refreshTf.getText()) * 1000);
		}
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

	public SNMPReader getRead2() {
		return read2;
	}

	public void setRead2(SNMPReader read2) {
		this.read2 = read2;
	}

	public MenuItem getLogin() {
		return login;
	}

	public void setLogin(MenuItem login) {
		this.login = login;
	}

	public MenuItem getLogout() {
		return logout;
	}

	public void setLogout(MenuItem logout) {
		this.logout = logout;
	}

	public MenuItem getAccountInformation() {
		return accountInformation;
	}

	public void setAccountInformation(MenuItem accountInformation) {
		this.accountInformation = accountInformation;
	}

	public MenuItem getConnectToDevice() {
		return connectToDevice;
	}

	public void setConnectToDevice(MenuItem connectToDevice) {
		this.connectToDevice = connectToDevice;
	}

	public MenuItem getDisconnect() {
		return disconnect;
	}

	public void setDisconnect(MenuItem disconnect) {
		this.disconnect = disconnect;
	}

	public MenuItem getSupport() {
		return support;
	}

	public void setSupport(MenuItem support) {
		this.support = support;
	}

	public MenuItem getAbout() {
		return about;
	}

	public void setAbout(MenuItem about) {
		this.about = about;
	}

	public Button getbConnect() {
		return bConnect;
	}

	public void setbConnect(Button bConnect) {
		this.bConnect = bConnect;
	}

	public Button getbRefresh() {
		return bRefresh;
	}

	public void setbRefresh(Button bRefresh) {
		this.bRefresh = bRefresh;
	}

	public Button getbSignIn() {
		return bSignIn;
	}

	public void setbSignIn(Button bSignIn) {
		this.bSignIn = bSignIn;
	}

	public TextField getUsernameTf() {
		return usernameTf;
	}

	public void setUsernameTf(TextField usernameTf) {
		this.usernameTf = usernameTf;
	}

	public PasswordField getPasswordPf() {
		return passwordPf;
	}

	public void setPasswordPf(PasswordField passwordPf) {
		this.passwordPf = passwordPf;
	}

	public TextField getServerAddressTf() {
		return serverAddressTf;
	}

	public void setServerAddressTf(TextField serverAddressTf) {
		this.serverAddressTf = serverAddressTf;
	}

	public TextField getPortTf() {
		return portTf;
	}

	public void setPortTf(TextField portTf) {
		this.portTf = portTf;
	}

	public ComboBox getSnmpComboBox() {
		return snmpComboBox;
	}

	public void setSnmpComboBox(ComboBox snmpComboBox) {
		this.snmpComboBox = snmpComboBox;
	}

	public ComboBox getZoneComboBox() {
		return zoneComboBox;
	}

	public void setZoneComboBox(ComboBox zoneComboBox) {
		this.zoneComboBox = zoneComboBox;
	}

	public ComboBox getActionComboBox() {
		return actionComboBox;
	}

	public void setActionComboBox(ComboBox actionComboBox) {
		this.actionComboBox = actionComboBox;
	}

	public RadioButton getUdpRadio() {
		return udpRadio;
	}

	public void setUdpRadio(RadioButton udpRadio) {
		this.udpRadio = udpRadio;
	}

	public RadioButton getTcpRadio() {
		return tcpRadio;
	}

	public void setTcpRadio(RadioButton tcpRadio) {
		this.tcpRadio = tcpRadio;
	}

	public CheckBox getSaveConPropertiesCheckBox() {
		return saveConPropertiesCheckBox;
	}

	public void setSaveConPropertiesCheckBox(CheckBox saveConPropertiesCheckBox) {
		this.saveConPropertiesCheckBox = saveConPropertiesCheckBox;
	}

	public TableView<Table> getPolicyTable() {
		return policyTable;
	}

	public void setPolicyTable(TableView<Table> policyTable) {
		this.policyTable = policyTable;
	}

	public TableColumn<Table, String> getPolicyNameCol() {
		return policyNameCol;
	}

	public void setPolicyNameCol(TableColumn<Table, String> policyNameCol) {
		this.policyNameCol = policyNameCol;
	}

	public TableColumn<Table, String> getoIDCol() {
		return oIDCol;
	}

	public void setoIDCol(TableColumn<Table, String> oIDCol) {
		this.oIDCol = oIDCol;
	}

	public TableColumn<Table, String> getValueCol() {
		return valueCol;
	}

	public void setValueCol(TableColumn<Table, String> valueCol) {
		this.valueCol = valueCol;
	}

	public TableColumn<Table, String> getZoneCol() {
		return zoneCol;
	}

	public void setZoneCol(TableColumn<Table, String> zoneCol) {
		this.zoneCol = zoneCol;
	}

	public List<String> getList_policyNames() {
		return list_policyNames;
	}

	public void setList_policyNames(List<String> list_policyNames) {
		this.list_policyNames = list_policyNames;
	}

	public List<String> getList_oIDs() {
		return list_oIDs;
	}

	public void setList_oIDs(List<String> list_oIDs) {
		this.list_oIDs = list_oIDs;
	}

	public List<String> getList_values() {
		return list_values;
	}

	public void setList_values(List<String> list_values) {
		this.list_values = list_values;
	}

	public List<String> getList_zones() {
		return list_zones;
	}

	public void setList_zones(List<String> list_zones) {
		this.list_zones = list_zones;
	}

	public Tab getChartTab() {
		return chartTab;
	}

	public void setChartTab(Tab chartTab) {
		this.chartTab = chartTab;
	}

	public Tab getPropertiesTab() {
		return propertiesTab;
	}

	public void setPropertiesTab(Tab propertiesTab) {
		this.propertiesTab = propertiesTab;
	}

	public RadioButton getByteRadio() {
		return byteRadio;
	}

	public void setByteRadio(RadioButton byteRadio) {
		this.byteRadio = byteRadio;
	}

	public RadioButton getKiloByteRadio() {
		return kiloByteRadio;
	}

	public void setKiloByteRadio(RadioButton kiloByteRadio) {
		this.kiloByteRadio = kiloByteRadio;
	}

	public RadioButton getMegaByteRadio() {
		return megaByteRadio;
	}

	public void setMegaByteRadio(RadioButton megaByteRadio) {
		this.megaByteRadio = megaByteRadio;
	}

	public TextField getRefreshTf() {
		return refreshTf;
	}

	public void setRefreshTf(TextField refreshTf) {
		this.refreshTf = refreshTf;
	}

	public TextField getFirewallRuleTf() {
		return firewallRuleTf;
	}

	public void setFirewallRuleTf(TextField firewallRuleTf) {
		this.firewallRuleTf = firewallRuleTf;
	}

	public TextField getServiceTf() {
		return serviceTf;
	}

	public void setServiceTf(TextField serviceTf) {
		this.serviceTf = serviceTf;
	}

	public TextField getIDTf() {
		return IDTf;
	}

	public void setIDTf(TextField iDTf) {
		IDTf = iDTf;
	}

	public TextField getSourceAddressTf() {
		return sourceAddressTf;
	}

	public void setSourceAddressTf(TextField sourceAddressTf) {
		this.sourceAddressTf = sourceAddressTf;
	}

	public TextField getDestinationAddressTf() {
		return destinationAddressTf;
	}

	public void setDestinationAddressTf(TextField destinationAddressTf) {
		this.destinationAddressTf = destinationAddressTf;
	}

	public ObservableList getDatalist() {
		return datalist;
	}

	public void setDatalist(ObservableList datalist) {
		this.datalist = datalist;
	}

	public NumberAxis getxAxis() {
		return xAxis;
	}

	public void setxAxis(NumberAxis xAxis) {
		this.xAxis = xAxis;
	}

	public NumberAxis getyAxis() {
		return yAxis;
	}

	public void setyAxis(NumberAxis yAxis) {
		this.yAxis = yAxis;
	}

	public LineChart<Number, Number> getLineChart() {
		return lineChart;
	}

	public void setLineChart(LineChart<Number, Number> lineChart) {
		this.lineChart = lineChart;
	}

	public XYChart.Series getSeries() {
		return series;
	}

	public void setSeries(XYChart.Series series) {
		this.series = series;
	}

	public LineChartIntervalRefresher getLcirf() {
		return lcirf;
	}

	public void setLcirf(LineChartIntervalRefresher lcirf) {
		this.lcirf = lcirf;
	}

	public ArrayList<Koordinaten> getLineChartData() {
		return lineChartData;
	}

	public void setLineChartData(ArrayList<Koordinaten> lineChartData) {
		this.lineChartData = lineChartData;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public String getCurrentSelectedRB() {
		return currentSelectedRB;
	}

	public void setCurrentSelectedRB(String currentSelectedRB) {
		this.currentSelectedRB = currentSelectedRB;
	}

	public String getPreviousSelectedRB() {
		return previousSelectedRB;
	}

	public void setPreviousSelectedRB(String previousSelectedRB) {
		this.previousSelectedRB = previousSelectedRB;
	}

	public ObservableList<Table> getData() {
		return data;
	}

	public void setData(ObservableList<Table> data) {
		this.data = data;
	}

	public static Logger getLogger() {
		return logger;
	}
}
