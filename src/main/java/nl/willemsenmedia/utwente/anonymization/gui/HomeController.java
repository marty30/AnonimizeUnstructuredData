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
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

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
	private Settings settings;
	private Control inputElement;

	public HomeController() {
		tooltip = new Tooltip();
	}

	public void handleVerwerkBestand(ActionEvent event) {
		File file = new File(bestandsPad.getText());
		if (file.exists()) {
			List<DataEntry> data = FileReader.readFile(file, settings);
			if (data == null || data.size() == 0) {
				//Error
				PopupManager.error("Geen data gevonden", null, "Er is geen data gevonden in het opgegeven bestand.", null);
				bestandsPad.setText("Kies aan bestand...");
				setTooltipText("Klik op \"" + openBestand.getText() + "\" om een bestand te selecteren");
			} else {
				// Het bestand is ingelezen
				DataEntry[] data_array = new DataEntry[data.size()];
				data_array = data.toArray(data_array);
				Main.OpenPageWithData(settings, data_array);
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
			GridPane options = createGuiForSettings(fileType);
			additionalOptions.add(options, 1, 1, 2, 1);
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

	private GridPane createGuiForSettings(FileType fileType) {
		GridPane panel = new GridPane();
		int row = 0;
		for (Settings.Setting setting : settings.getSetting()) {
			if (setting.isOverwritable()) {
				if ("regexes".equals(setting.getName())) {
					//The regexes need a different markup then the other settings
					GridPane regex_panel = new GridPane();
					//TODO regexes netjes weergeven
					regex_panel.addRow(0, new Label("TODO"));
					panel.add(regex_panel, 0, row, 2, 1);
				} else {
					inputElement = getInputElement(setting);

					panel.addRow(row, new Label(setting.getScreenname()), inputElement);
				}
				row++;
			}
		}
		return panel;
	}

	private Control getInputElement(Settings.Setting setting) {
		switch (setting.getType()) {
			case JAVA_LANG_BOOLEAN:
				CheckBox checkBox = new CheckBox();
				checkBox.setId(setting.getName());
				checkBox.setSelected(Boolean.parseBoolean(setting.getValue()));
				setting.addViewUpdater(checkBox.selectedProperty());
				checkBox.selectedProperty().addListener((ev) -> {
					setting.setValue(checkBox.isSelected() + "");
				});
				switch (setting.getName()) {
					case "bevat_kopteksten":
						checkBox.setOnMouseClicked(event1 -> {
							if (checkBox.isSelected())
								if (settings.getSettingsMap().get("beginrij").getValue().equals("") || settings.getSettingsMap().get("beginrij").getValue().equals("0"))
									settings.getSettingsMap().get("beginrij").setValue("1");
								else
									settings.getSettingsMap().get("beginrij").setValue("" + (Integer.parseInt(settings.getSettingsMap().get("beginrij").getValue()) + 1));
							else if (settings.getSettingsMap().get("beginrij").getValue().equals("1"))
								settings.getSettingsMap().get("beginrij").setValue("0");
							else
								settings.getSettingsMap().get("beginrij").setValue("" + (Integer.parseInt(settings.getSettingsMap().get("beginrij").getValue()) - 1));
							settings.getSettingsMap().get("beginrij").updateView();
						});
						break;
				}
				return checkBox;
			case JAVA_LANG_INTEGER:
			case JAVA_LANG_STRING:
			default:
				TextField textField = new TextField();
				textField.setId(setting.getName());
				textField.textProperty().setValue(setting.getValue());
				setting.addViewUpdater(textField.textProperty());
				switch (setting.getName()) {
					case "beginrij":
						textField.textProperty().addListener((observable, oldValue, newValue) -> {
							if (!newValue.matches("\\d*")) {
								textField.setText(oldValue);
							} else
								setting.setValue(newValue);
						});
						break;
					case "eindrij":
						textField.textProperty().addListener((observable, oldValue, newValue) -> {
							if (!newValue.matches("\\d*")) {
								textField.setText(oldValue);
							} else
								setting.setValue(newValue);

							if (!newValue.equals("") && Integer.parseInt(newValue) < Integer.parseInt(settings.getSettingsMap().get("beginrij").getValue())) {
								textField.setStyle("-fx-control-inner-background: red");
							} else {
								textField.setStyle("");
							}
						});
						break;
					default:
						textField.textProperty().addListener((ev) -> {
							setting.setValue(textField.getText());
						});
				}
				return textField;
		}
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
