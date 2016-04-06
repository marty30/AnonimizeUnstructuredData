package nl.willemsenmedia.utwente.anonymization.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.handling.*;
import nl.willemsenmedia.utwente.anonymization.data.writing.FileWriter;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Martijn on 20-2-2016.
 * <p>
 * The controller for the dataview part of the GUI
 */
public class DataviewController implements Initializable {
	private static final String AttributeSeparator = "--------------------------";
	private List<DataEntry> raw_data;
	private List<DataEntry> anonimous_data;
	@FXML
	private TabPane tabPane;
	private Settings settings;
	private int tabnr;
	private int max_tabs;

	public void setData(DataEntry... data) {
		this.raw_data = Arrays.asList(data);
		this.anonimous_data = new ArrayList<>();
		tabPane.getTabs().clear();
		tabnr = 0;
		max_tabs = 25;
		for (DataEntry raw_entry : this.raw_data) {
			determineTechnique().doPreProcessing(raw_entry, settings);
			DataEntry anonimous_entry = determineTechnique().anonymize(raw_entry, settings);
			anonimous_data.add(anonimous_entry);
			if (System.getProperty("useGUI").equals("true")) {
				makeGUI(raw_entry, anonimous_entry);
			}
		}
	}

	public void makeGUI(DataEntry raw_entry, DataEntry anonimous_entry) {
		if (tabnr < max_tabs) {
			Tab tab = new Tab();
			//Create title
			String title = anonimous_entry.getDataAttributes().get(0).getData();
			if (title.length() > 15)
				title = title.substring(0, 15) + "...";
			tab.setText("Data: " + title);

			HBox hbox = new HBox();
			String content = "";
			for (DataAttribute attribute : anonimous_entry.getDataAttributes()) {
				content += attribute.getData() + "\n" + AttributeSeparator + "\n";
			}
			hbox.getChildren().add(new Label(content.trim()));
			hbox.setAlignment(Pos.TOP_LEFT);
			ScrollPane sp = new ScrollPane();
			sp.setContent(hbox);
			tab.setContent(sp);
			tabPane.getTabs().add(tab);
			tabnr++;
		}
	}

	private AnonymizationTechnique determineTechnique() {
		if (System.getProperty("technique") == null) {
			System.setProperty("technique", "");
		}
		switch (System.getProperty("technique")) {
			case "HashSentence":
				return new HashSentence();
			case "HashAll":
				return new HashAll();
			case "SmartHashing":
				return new SmartHashing();
			case "GeneralizeOrSuppress":
			case "k-anonymity":
				return new GeneralizeOrSuppress(Integer.parseInt(System.getProperty("k")));
			default:
				//Do nothing with the raw_data
				return new AnonymizationTechnique() {
					@Override
					public DataEntry anonymize(DataEntry dataEntry, Settings settings) {
						DataEntry newDataEntry = new DataEntry(dataEntry.getHeaders());
						dataEntry.getDataAttributes().stream().forEach((dataAttribute) -> newDataEntry.addDataAttribute(dataAttribute.clone()));
						return newDataEntry;
					}
				};
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void exportData(ActionEvent event) {
		FileWriter.exportDataToCSV(anonimous_data, FileWriter.createFile(".csv"));

	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
