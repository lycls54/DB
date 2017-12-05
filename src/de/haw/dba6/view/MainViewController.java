package de.haw.dba6.view;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import de.haw.dba6.controller.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

/**
 * @author Ali Calis <ali.calis@haw-hamburg.de>
 */
public class MainViewController implements IViewController {
	@FXML
	private ListView<String> tabellen;

	@SuppressWarnings("rawtypes")
	@FXML
	private TableView tableViewSql;

	private Connection connection;

	@SuppressWarnings("rawtypes")
	private ObservableList<ObservableList> data;

	@FXML
	private MenuBar menuBar;

	@FXML
	private Menu menuCon;

	@FXML
	private MenuItem menuItemCon;

	@FXML
	private MenuItem dissconnect;

	@FXML
	private TextArea sqlCodeArea;

	@FXML
	private TextArea sqlCodeTextArea;

	@FXML
	private Text statusText;

	@FXML
	private Button resultButton;

	@FXML
	private Button execButton;

	@FXML
	private Button execTriggerButton;

	@FXML
	private Button addButton;

	private Controller controller;

	public MainViewController() {
		tabellen = new ListView<>();
		tableViewSql = new TableView<>();
		menuItemCon = new MenuItem("connect");
		dissconnect = new MenuItem("dissconnect");
		controller = Controller.getInstance(null);
	}

	@Override
	public Scene initScene() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/de/haw/dba6/resources/mainView.fxml"));
		return new Scene(root);
	}

	@Override
	public void initializeActions() {
		resultButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (connection != null && sqlCodeArea.getText() != "") {
					try {
						listRows(sqlCodeArea.getText());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		});

		execButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (connection != null && sqlCodeArea.getText() != "") {
					InputStream stream = (new ByteArrayInputStream(
							sqlCodeTextArea.getText().getBytes(StandardCharsets.UTF_8)));
					try {
						importSQL(stream);
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			}
		});

		execTriggerButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (connection != null && sqlCodeArea.getText() != "") {
					try {
						Statement stm = connection.createStatement();
						stm.execute(sqlCodeTextArea.getText());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		});

		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (connection != null && sqlCodeArea.getText() != "") {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Open Resource File");
					fileChooser.getExtensionFilters().addAll(new ExtensionFilter("SQL Files", "*.sql"));
					File file = fileChooser.showOpenDialog(controller.getStage());
					if (file != null) {
						appending(file.getAbsolutePath());
					}

				}
			}
		});
		menuItemCon.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					controller.conView();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		});
		dissconnect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				connection = null;
				statusText.setText("dissconnected");
				controller.setConnection(null);
				tabellen.getItems().clear();
				tableViewSql.getItems().clear();
			}
		});
		tabellen.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				try {
					if (connection != null) {
						listRows("SELECT * FROM " + tabellen.getSelectionModel().getSelectedItem().toString());
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void initialize() {
		connection = controller.getConnection();
		if (connection != null) {
			listTable();
			statusText.setText("connected");
		}
		initializeActions();
	}

	public void appending(String selFile) {
		sqlCodeTextArea.clear();
		BufferedReader buff = null;

		try {
			buff = new BufferedReader(new FileReader(selFile));

			String str;
			try {
				while ((str = buff.readLine()) != null) {
					sqlCodeTextArea.appendText("\n" + str);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void listTable() {
		ObservableList<String> tableNames = FXCollections.observableArrayList();
		try {
			ResultSet rs = connection.createStatement().executeQuery("SELECT table_name FROM user_tables");
			while (rs.next()) {
				tableNames.add(rs.getString("TABLE_NAME"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tabellen.setItems(tableNames);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void listRows(String sql) throws SQLException {
		data = FXCollections.observableArrayList();
		tableViewSql.getColumns().clear();

		// Execute SQL query
		tableViewSql.getItems().clear();

		// SQL FOR SELECTING ALL OF CUSTOMER
		String SQL = sql;

		// ResultSet
		ResultSet rs = connection.createStatement().executeQuery(SQL);

		/**********************************
		 * TABLE COLUMN ADDED DYNAMICALLY *
		 **********************************/

		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
			// We are using non property style for making dynamic table
			final int j = i;
			TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
			col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
					return new SimpleStringProperty(param.getValue().get(j).toString());
				}
			});

			tableViewSql.getColumns().addAll(col);
		}
		/********************************
		 * Data added to ObservableList *
		 ********************************/
		while (rs.next()) {
			// Iterate Row
			ObservableList<String> row = FXCollections.observableArrayList();
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				// Iterate Column
				row.add(rs.getString(i));
			}
			data.add(row);
		}
		// FINALLY ADDED TO TableView
		tableViewSql.setItems(data);
	}

	public void importSQL(InputStream in) throws SQLException {
		Scanner s = new Scanner(in);
		s.useDelimiter(";");
		Statement st = null;
		st = connection.createStatement();
		// Printing the tokenized Strings
		while (s.hasNext()) {
			String line = s.next();
			st.execute(line);
		}
		s.close();
	}

	public ListView<String> getTabellen() {
		return tabellen;
	}

	public void setTabellen(ListView<String> tabellen) {
		this.tabellen = tabellen;
	}

	@SuppressWarnings("rawtypes")
	public TableView getTableViewSql() {
		return tableViewSql;
	}

	@SuppressWarnings("rawtypes")
	public void setTableViewSql(TableView tableViewSql) {
		this.tableViewSql = tableViewSql;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	@SuppressWarnings("rawtypes")
	public ObservableList<ObservableList> getData() {
		return data;
	}

	@SuppressWarnings("rawtypes")
	public void setData(ObservableList<ObservableList> data) {
		this.data = data;
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}

	public void setMenuBar(MenuBar menuBar) {
		this.menuBar = menuBar;
	}

	public Menu getMenuCon() {
		return menuCon;
	}

	public void setMenuCon(Menu menuCon) {
		this.menuCon = menuCon;
	}

	public MenuItem getMenuItemCon() {
		return menuItemCon;
	}

	public void setMenuItemCon(MenuItem menuItemCon) {
		this.menuItemCon = menuItemCon;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

}