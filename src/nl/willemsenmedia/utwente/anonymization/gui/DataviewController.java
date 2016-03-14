package nl.willemsenmedia.utwente.anonymization.gui;

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

import java.net.URL;
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
		for (DataEntry entry : this.data) {
			entry = determineTechnique().anonymize(entry);
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
}
