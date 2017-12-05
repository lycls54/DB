package de.haw.dba6.controller;

import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import de.haw.dba6.view.ConViewController;
import de.haw.dba6.view.IViewController;
import de.haw.dba6.view.MainViewController;

public class Controller {
	private static Controller controller;
	private IViewController currentView;
	private Stage stage;
	private Connection connection;

	public static Controller getInstance(Stage stage) {
		if (controller == null) {
			controller = new Controller(stage);
		}
		return controller;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private Controller(Stage stage) {
		this.stage = stage;

	}

	public void conView() throws IOException {
		this.currentView = new ConViewController();
		this.stage.setScene(currentView.initScene());
		this.stage.setTitle("Connection");
		if (!this.stage.isShowing()) {
			this.stage.show();
		}
	}

	public void mainView() throws IOException {
		this.currentView = new MainViewController();
		this.stage.setScene(currentView.initScene());
		this.stage.setTitle("Oracle");
		if (!this.stage.isShowing()) {
			this.stage.show();
		}
		((MainViewController) this.currentView).setConnection(getConnection());
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
