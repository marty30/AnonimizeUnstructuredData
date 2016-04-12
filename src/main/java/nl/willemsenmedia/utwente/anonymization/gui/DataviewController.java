package nl.willemsenmedia.utwente.anonymization.gui;

import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.handling.AnonimizationController;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.net.URL;
import java.util.ResourceBundle;

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
	private Settings settings;

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

		anonymous_data_scrollpane.setOnScroll(event -> raw_data_scrollpane.setVvalue(anonymous_data_scrollpane.getVvalue()));
		raw_data_scrollpane.setOnScroll(event -> anonymous_data_scrollpane.setVvalue(raw_data_scrollpane.getVvalue()));
	}

	public void exportData(ActionEvent event) {
		AnonimizationController.exportData();
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public void addData(DataEntry raw_entry, DataEntry anonimous_entry) {
		//Add anonymous data
		HBox hbox = new HBox();
		String content = "";
		for (DataAttribute attribute : anonimous_entry.getDataAttributes()) {
			content += attribute.getData() + "\n" + AttributeSeparator + "\n";
		}
		hbox.getChildren().add(new Label(content.trim()));
		hbox.setAlignment(Pos.TOP_LEFT);
		anonymous_data_pane.addRow(anonymous_data_pane.getChildren().size(), hbox);
		//Add raw data
		hbox = new HBox();
		content = "";
		for (DataAttribute attribute : raw_entry.getDataAttributes()) {
			content += attribute.getData() + "\n" + AttributeSeparator + "\n";
		}
		hbox.getChildren().add(new Label(content.trim()));
		hbox.setAlignment(Pos.TOP_LEFT);
		raw_data_pane.addRow(anonymous_data_pane.getChildren().size(), hbox);
	}
}
