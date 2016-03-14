package nl.willemsenmedia.utwente.anonymization.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import nl.willemsenmedia.utwente.anonymization.Main;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.reading.FileReader;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

	@FXML
	public Button openBestand;
	@FXML
	public Label bestandsPad;
	@FXML
	public Button verwerkBestand;
	private Tooltip tooltip;

	public HomeController() {
		tooltip = new Tooltip();
	}

	public void handleVerwerkBestand(ActionEvent event) {
		File file = new File(bestandsPad.getText());
		if (file.exists()) {
			//TODO van een file een lijst van data-entries maken
			PopupManager.info("tada", null, "We gaan wat doen met het bestand: " + file.getAbsolutePath());
			List<DataEntry> data = FileReader.readFile(file);
			if (data == null || data.size() == 0) {
				//Error
				PopupManager.error("Geen data gevonden", null, "Er is geen data gevonden in het opgegeven bestand.", null);
				bestandsPad.setText("Kies aan bestand...");
				setTooltipText("Klik op \"" + openBestand.getText() + "\" om een bestand te selecteren");
			} else {
				// Het bestand is ingelezen
				DataEntry[] data_array = new DataEntry[data.size()];
				data_array = data.toArray(data_array);
				Main.OpenPageWithData(data_array);
			}
		} else {
			PopupManager.error("Bestand niet gevonden", null, "Kan het bestand " + bestandsPad.getText() + " niet vinden. Probeer het opnieuw.", null);
		}
	}

	public void handleOpenBestand(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		File chosenFile = fileChooser.showOpenDialog(Main.mainStage);
		if (chosenFile != null) {
			bestandsPad.setText(chosenFile.getAbsolutePath());
			setTooltipText(chosenFile.getAbsolutePath());

			// Check if there should be any additional parameters

		}
	}

	public void setTooltipText(String tooltipText) {
		tooltip.setText(tooltipText);
	}

	public String getTootipText() {
		return tooltip.getText();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tooltip.setText("Klik op \"" + openBestand.getText() + "\" om een bestand te selecteren");
		bestandsPad.setTooltip(tooltip);
	}
}
