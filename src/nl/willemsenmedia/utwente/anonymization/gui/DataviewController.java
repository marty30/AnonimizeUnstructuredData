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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
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
	private List<DataEntry> data;
	@FXML
	private TabPane tabPane;


	public void setData(DataEntry... data) {
		this.data = Arrays.asList(data);
		tabPane.getTabs().clear();
		int tabnr = 0;
		int max_tabs = 25;
		for (DataEntry entry : this.data) {
			entry.update(determineTechnique().anonymize(entry));
			if (tabnr < max_tabs) {
				Tab tab = new Tab();
				//Create title
				String title = entry.getDataAttributes().get(0).getData();
				if (title.length() > 15)
					title = title.substring(0, 15) + "...";
				tab.setText("Data: " + title);

				HBox hbox = new HBox();
				String content = "";
				for (DataAttribute attribute : entry.getDataAttributes()) {
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
				//Do nothing with the data
				return new AnonymizationTechnique() {
					@Override
					public DataEntry anonymize(DataEntry dataEntry) {
						return dataEntry;
					}
				};
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void exportData(ActionEvent event) {
		String filename = "export_" + LocalDate.now().toString().trim().replace("\\s+", "_");
		String ext = ".txt";
		try {
			File exportFile = new File(filename + ext);
			if (!exportFile.exists())
				exportFile.createNewFile();
			else
				filename += "_" + LocalTime.now().toString().trim().replace("\\s+", "_").replace(":", "");
			exportFile = new File(filename + ext);
			if (!exportFile.exists())
				exportFile.createNewFile();
		} catch (IOException e) {
			ErrorHandler.handleException(e);
		}
		try (PrintWriter out = new PrintWriter(filename + ext)) {
			for (DataEntry dataEntry : data)
				out.println(dataEntry.toString());
			PopupManager.info("Data geëxporteerd", null, "De data die hier zichtbaar is, is geëxporteerd naar het bestand met de naam \"" + filename + ext + "\".");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleException(e);
		}
	}
}
