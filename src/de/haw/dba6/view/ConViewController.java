package de.haw.dba6.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.haw.dba6.controller.Controller;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * @author Ali Calis <ali.calis@haw-hamburg.de>
 */
public class ConViewController implements IViewController {

	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private TextField port;

	@FXML
	private TextField SID;

	@FXML
	private TextField hostname;

	@FXML
	private Button connectButton;

	@FXML
	private Button switchButton;

	@FXML
	private Text connectingInfo;

	@FXML
	private GridPane gP;

	private Connection connection;

	private Controller controller;

	public ConViewController() {
		controller = Controller.getInstance(null);
	}

	@Override
	public Scene initScene() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/de/haw/dba6/resources/conView.fxml"));
		return new Scene(root);
	}

	@Override
	public void initializeActions() {

		switchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					controller.mainView();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		username.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {

				if ((event.getCode() == KeyCode.ENTER)) {
					connect();
					byConnectingAutoSwitch();
				}

			};
		});

		password.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {

				if ((event.getCode() == KeyCode.ENTER)) {
					connect();
					byConnectingAutoSwitch();
				}

			};
		});

		port.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {

				if ((event.getCode() == KeyCode.ENTER)) {
					connect();
					byConnectingAutoSwitch();
				}

			};
		});

		SID.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {

				if ((event.getCode() == KeyCode.ENTER)) {
					connect();
					byConnectingAutoSwitch();
				}

			};
		});

		hostname.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {

				if ((event.getCode() == KeyCode.ENTER)) {
					connect();
					byConnectingAutoSwitch();
				}

			};
		});
		connectButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				connect();
				byConnectingAutoSwitch();

			}
		});
	}

	@Override
	public void initialize() {
		initializeActions();

	}

	public void connect() {
		connectingInfo.setText("-------- Oracle JDBC Connection Testing ------");

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			connectingInfo.setText("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return;
		}

		connectingInfo.setText("Oracle JDBC Driver Registered!");

		connection = null;

		try {
			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@" + hostname.getText() + ":" + port.getText() + ":" + SID.getText(),
					username.getText(), password.getText());

		} catch (SQLException e) {
			connectingInfo.setText("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (connection != null) {

			controller.setConnection(connection);

			connectingInfo.setText("Connection succeeded!");

		} else {
			connectingInfo.setText("Failed to make connection!");
		}

	}

	public void byConnectingAutoSwitch() {

		Task<Void> sleeper = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
				}
				return null;
			}
		};
		sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				if (connection != null) {
					try {
						controller.mainView();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		new Thread(sleeper).start();

	}

	public TextField getUsername() {
		return username;
	}

	public void setUsername(TextField username) {
		this.username = username;
	}

	public PasswordField getPassword() {
		return password;
	}

	public void setPassword(PasswordField password) {
		this.password = password;
	}

	public TextField getPort() {
		return port;
	}

	public void setPort(TextField port) {
		this.port = port;
	}

	public TextField getSID() {
		return SID;
	}

	public void setSID(TextField sID) {
		SID = sID;
	}

	public TextField getHostname() {
		return hostname;
	}

	public void setHostname(TextField hostname) {
		this.hostname = hostname;
	}

	public Button getConnectButton() {
		return connectButton;
	}

	public void setConnectButton(Button connectButton) {
		this.connectButton = connectButton;
	}

	public Button getSwitchButton() {
		return switchButton;
	}

	public void setSwitchButton(Button switchButton) {
		this.switchButton = switchButton;
	}

	public Text getConnectingInfo() {
		return connectingInfo;
	}

	public void setConnectingInfo(Text connectingInfo) {
		this.connectingInfo = connectingInfo;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
}