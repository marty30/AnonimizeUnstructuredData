package nl.willemsenmedia.utwente.anonymization;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * Test class to bypass the gui
 */
public class Tester extends Application {
	private static String testdata = "nog een testje";
	final Button button = new Button("Send");
	final Label notification = new Label();
	final TextField subject = new TextField("");
	final TextArea text = new TextArea("");
	String address = " ";

	public static void main(String[] args) throws Exception {
//		System.setProperty("technique", "HashAll");
//		DBHandler dbHandler = new DBHandler();
//		dbHandler.createEmptyTable();
//		DataEntry entry = new DataEntry(
//				Arrays.asList(new DataAttribute(DataType.NAME, "naam"), new DataAttribute(DataType.UNSTRUCTURED, "data")),
//				new DataAttribute(DataType.UNSTRUCTURED, "naam", "Dit is de data", false));
//		dbHandler.saveRawData(1, entry);
//		List<DataEntry> raw = dbHandler.getRawData();
//		DataEntry[] anonimous = new DataEntry[raw.size()];
//		for (int i = 0; i < raw.size(); i++) {
//			anonimous[i] = determineTechnique().anonymize(raw.get(i), null);
//			dbHandler.saveAnonimousData(raw.get(i), anonimous[i]);
//		}
//		dbHandler.getData(true);
		System.setProperty("glass.accessible.force", "false");
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("ComboBoxSample");
		Scene scene = new Scene(new Group(), 450, 250);

		final ComboBox emailComboBox = new ComboBox();
		emailComboBox.getItems().addAll(
				"jacob.smith@example.com",
				"isabella.johnson@example.com",
				"ethan.williams@example.com",
				"emma.jones@example.com",
				"michael.brown@example.com"
		);
		emailComboBox.setPromptText("Email address");
		emailComboBox.setEditable(true);
		emailComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				address = t1;
			}
		});

		final ComboBox priorityComboBox = new ComboBox();
		priorityComboBox.getItems().addAll(
				"Highest",
				"High",
				"Normal",
				"Low",
				"Lowest"
		);

		priorityComboBox.setValue("Normal");


		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (emailComboBox.getValue() != null &&
						!emailComboBox.getValue().toString().isEmpty()) {
					notification.setText("Your message was successfully sent"
							+ " to " + address);
					emailComboBox.setValue(null);
					if (priorityComboBox.getValue() != null &&
							!priorityComboBox.getValue().toString().isEmpty()) {
						priorityComboBox.setValue(null);
					}
					subject.clear();
					text.clear();
				} else {
					notification.setText("You have not selected a recipient!");
				}
			}
		});

		GridPane grid = new GridPane();
		grid.setVgap(4);
		grid.setHgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.add(new Label("To: "), 0, 0);
		grid.add(emailComboBox, 1, 0);
		grid.add(new Label("Priority: "), 2, 0);
		grid.add(priorityComboBox, 3, 0);
		grid.add(new Label("Subject: "), 0, 1);
		grid.add(subject, 1, 1, 3, 1);
		grid.add(text, 0, 2, 4, 1);
		grid.add(button, 0, 3);
		grid.add(notification, 1, 3, 3, 1);

		Group root = (Group) scene.getRoot();
		root.getChildren().add(grid);
		stage.setScene(scene);
		stage.show();

	}
}
