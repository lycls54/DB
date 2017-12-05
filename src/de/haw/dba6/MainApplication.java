package de.haw.dba6;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import de.haw.dba6.controller.Controller;

public class MainApplication extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			Controller controller = Controller.getInstance(primaryStage);
			controller.mainView();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}