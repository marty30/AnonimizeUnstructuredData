package nl.willemsenmedia.utwente.anonymization.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import nl.willemsenmedia.utwente.anonymization.Main;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.FileType;
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
	public GridPane additionalOptions;
	private Tooltip tooltip;

	public HomeController() {
		tooltip = new Tooltip();
	}

	public void handleVerwerkBestand(ActionEvent event) {
		File file = new File(bestandsPad.getText());
		if (file.exists()) {
			List<DataEntry> data = FileReader.readFile(file, additionalOptions.getChildrenUnmodifiable());
			if (data == null || data.size() == 0) {
				//Error
				PopupManager.error("Geen data gevonden", null, "Er is geen data gevonden in het opgegeven bestand.", null);
				bestandsPad.setText("Kies aan bestand...");
				setTooltipText("Klik op \"" + openBestand.getText() + "\" om een bestand te selecteren");
			} else {
				// Het bestand is ingelezen
				DataEntry[] data_array = new DataEntry[data.size()];
				data_array = data.toArray(data_array);
				Main.OpenPageWithData(DataviewController.convertAdditionalOptionsToMap(additionalOptions.getChildrenUnmodifiable()), data_array);
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
			FileType fileType = FileReader.determineFileType(chosenFile);
			switch (fileType) {
				case CSV:
				case XLS:
				case XLSX:
					additionalOptions.getChildren().clear();
					additionalOptions.add(new Label("Bevat kopteksten"), 0, 0);
					CheckBox bevat_kopteksten = new CheckBox();
					bevat_kopteksten.setId("bevat_kopteksten");
					additionalOptions.add(bevat_kopteksten, 1, 0);
					additionalOptions.add(new Label("Beginrij"), 0, 1);
					TextField beginrij = new TextField();
					beginrij.setId("beginrij");
					additionalOptions.add(beginrij, 1, 1);
					additionalOptions.add(new Label("Eindrij"), 0, 2);
					TextField eindrij = new TextField();
					eindrij.setId("eindrij");
					additionalOptions.add(eindrij, 1, 2);

					bevat_kopteksten.setOnMouseClicked(event1 -> {
						if (bevat_kopteksten.isSelected())
							if (beginrij.getText().equals(""))
								beginrij.setText("1");
							else
								beginrij.setText("" + (Integer.parseInt(beginrij.getText()) + 1));
						else if (beginrij.getText().equals("1"))
							beginrij.setText("");
						else
							beginrij.setText("" + (Integer.parseInt(beginrij.getText()) - 1));
					});
					beginrij.textProperty().addListener((observable, oldValue, newValue) -> {
						if (!newValue.matches("\\d*")) {
							beginrij.setText(oldValue);
						}
					});
					eindrij.textProperty().addListener((observable, oldValue, newValue) -> {
						if (!newValue.matches("\\d*")) {
							eindrij.setText(oldValue);
						}

						if (!newValue.equals("") && Integer.parseInt(newValue) < Integer.parseInt(beginrij.getText())) {
							eindrij.setStyle("-fx-control-inner-background: red");
						} else {
							eindrij.setStyle("");
						}
					});

					break;
				default:
					additionalOptions.getChildren().clear();
			}
			additionalOptions.setVisible(additionalOptions.getChildren().size() > 0);
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
