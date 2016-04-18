package nl.willemsenmedia.utwente.anonymization.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.handling.AnonimizationController;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by Martijn on 20-2-2016.
 * <p>
 * The controller for the dataview part of the GUI
 */
public class DataviewController implements Initializable {
	private static final String AttributeSeparator = "--------------------------";
	public HBox progressbox;
	public ProgressBar progressBar;
	public GridPane anonymous_data_pane;
	public GridPane raw_data_pane;
	public ScrollPane anonymous_data_scrollpane;
	public ScrollPane raw_data_scrollpane;
	public TableView<ObservableList> table_anonymous_pane;
	public TableView<ObservableList> table_raw_pane;
	private Settings settings;
	private ObservableList<ObservableList> table_anonymous_data;
	private ObservableList<ObservableList> table_raw_data;

	public void bind(DoubleProperty doubleProperty) {
		progressBar.progressProperty().bind(doubleProperty);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.doubleValue() > 0.99) {
				progressbox.setVisible(false);
			}
		});

//		anonymous_data_scrollpane.setOnScroll(event -> raw_data_scrollpane.setVvalue(anonymous_data_scrollpane.getVvalue()));
//		raw_data_scrollpane.setOnScroll(event -> anonymous_data_scrollpane.setVvalue(raw_data_scrollpane.getVvalue()));
	}

	public void exportData(ActionEvent event) {
		AnonimizationController.exportData();
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public void addData(DataEntry raw_entry, DataEntry anonymous_entry) {
		//Add anonymous data
//		HBox hbox = new HBox();
//		hbox.getChildren().add(new Label(StringUtils.join(anonimous_entry.getDataAttributes().stream().map(DataAttribute::getData).collect(Collectors.toList()), AttributeSeparator).trim()));
//		hbox.setAlignment(Pos.TOP_LEFT);
//		anonymous_data_pane.addRow(anonymous_data_pane.getChildren().size(), hbox);
//		anonymous_data_pane.addRow(anonymous_data_pane.getChildren().size(), new Separator());
//		//Add raw data
//		hbox = new HBox();
//		hbox.getChildren().add(new Label(StringUtils.join(raw_entry.getDataAttributes().stream().map(DataAttribute::getData).collect(Collectors.toList()), AttributeSeparator).trim()));
//		hbox.setAlignment(Pos.TOP_LEFT);
//		raw_data_pane.addRow(anonymous_data_pane.getChildren().size(), hbox);
//		raw_data_pane.addRow(anonymous_data_pane.getChildren().size(), new Separator());

		//This will create the table
		if (table_raw_data == null || table_raw_data.size() == 0) {
			table_raw_data = FXCollections.observableArrayList();
//			table_raw_pane = new TableView<>();
//			raw_data_pane.addRow(0, table_raw_pane);
			table_raw_pane.setItems(table_raw_data);
			for (DataAttribute dataAttribute : raw_entry.getHeaders()) {
				TableColumn<ObservableList, String> col = new TableColumn<>(dataAttribute.getData());
				col.setCellValueFactory(param -> new ReadOnlyStringWrapper((String) param.getValue().get(raw_entry.getHeaders().indexOf(dataAttribute))));
				table_raw_pane.getColumns().add(col);
			}
		}
		if (table_anonymous_data == null || table_anonymous_data.size() == 0) {
			table_anonymous_data = FXCollections.observableArrayList();
//			table_anonymous_pane = new TableView<>();
//			anonymous_data_pane.addRow(0, table_anonymous_pane);
			table_anonymous_pane.setItems(table_anonymous_data);
			for (DataAttribute dataAttribute : anonymous_entry.getHeaders()) {
				TableColumn<ObservableList, String> col = new TableColumn<>(dataAttribute.getData());
				col.setCellValueFactory(param -> new ReadOnlyStringWrapper((String) param.getValue().get(anonymous_entry.getHeaders().indexOf(dataAttribute))));
				table_anonymous_pane.getColumns().add(col);
			}
		}
		//Now add all the data
		table_raw_data.add(FXCollections.observableArrayList(raw_entry.getDataAttributes().stream().map(DataAttribute::getData).collect(Collectors.toList())));
		table_anonymous_data.add(FXCollections.observableArrayList(anonymous_entry.getDataAttributes().stream().map(DataAttribute::getData).collect(Collectors.toList())));
	}
}
