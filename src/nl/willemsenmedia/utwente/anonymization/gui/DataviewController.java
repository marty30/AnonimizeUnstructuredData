package nl.willemsenmedia.utwente.anonymization.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;

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
	private List<DataEntry> data;

	@FXML
	private TabPane tabPane;


	public void setData(DataEntry... data) {
		this.data = Arrays.asList(data);
		tabPane.getTabs().clear();
		for (DataEntry entry : data) {
			Tab tab = new Tab();
			//Create title
			String title = entry.getDataAttributes().get(0).getData();
			if (title.length() > 15)
				title = title.substring(0, 15);
			tab.setText("Tab: " + title);

			HBox hbox = new HBox();
			String content = "";
			for (DataAttribute attribute : entry.getDataAttributes()) {
				content += attribute.getData() + "\n-----------------------\n";
			}
			hbox.getChildren().add(new Label(content.trim()));
			hbox.setAlignment(Pos.TOP_LEFT);
			tab.setContent(hbox);
			tabPane.getTabs().add(tab);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
