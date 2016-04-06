package nl.willemsenmedia.utwente.anonymization.gui;

import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import nl.willemsenmedia.utwente.anonymization.Main;
import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.FileType;
import nl.willemsenmedia.utwente.anonymization.data.reading.FileReader;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

	@FXML
	public Button openBestand;
	@FXML
	public Label bestandsPad;
	@FXML
	public Button verwerkBestand;
	@FXML
	public GridPane additionalOptions;
	private Tooltip tooltip;
	private Settings settings;
	private String errors_settings;
	private ArrayList<DataAttribute> headerList;

	public HomeController() {
		tooltip = new Tooltip();
	}

	public void handleVerwerkBestand(ActionEvent event) {
		File file = new File(bestandsPad.getText());
		if (file.exists()) {
			List<DataEntry> data = FileReader.readFile(file, settings, headerList);
			if (data == null || data.size() == 0) {
				//Error
				PopupManager.error("Geen data gevonden", null, "Er is geen data gevonden in het opgegeven bestand.", null);
				resetBestand();
			} else if (!settingsAreValid()) {
				//Error
				PopupManager.error("Ongeldige settings", null, "Er is een probleem opgetreden tijdens het valideren van de instellingen: " + (errors_settings.equals("") ? "Onbekende fout." : errors_settings), null);
			} else {
				// Het bestand is ingelezen
				DataEntry[] data_array = new DataEntry[data.size()];
				data_array = data.toArray(data_array);
				Main.OpenPageWithData(settings, data_array);
			}
		} else {
			PopupManager.error("Bestand niet gevonden", null, "Kan het bestand \"" + bestandsPad.getText() + "\" niet vinden. Probeer het opnieuw.", null);
			resetBestand();
		}
	}

	private boolean settingsAreValid() {
		this.errors_settings = "";
		//Requirements for the settings are as follows:
		// - It is impossible to lowercase all words, but not all starting words
		if (settings.getSettingsMap().get("maak_alle_woorden_lowercase").getValue().equals("true") && settings.getSettingsMap().get("maak_beginwoorden_lowercase").getValue().equals("false")) {
			errors_settings += "\nJe kunt niet alle woorden lowercase maken, maar niet alle beginwoorden normaal laten.";
		}
		if (settings.getSettingsMap().get("verwijder_datums").getValue().equals("true") && settings.getSettingsMap().get("anonimiseer_datums").getValue().equals("true")) {
			errors_settings += "\nJe kunt niet alle datums verwijderen én alle datums anonimiseren.";
		}
		return this.errors_settings.equals("");
	}

	private void resetBestand() {
		bestandsPad.setText("Kies aan bestand...");
		setTooltipText("Klik op \"" + openBestand.getText() + "\" om een bestand te selecteren");
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
			headerList = new ArrayList<>();
//			GridPane headers = getHeaders(fileType, chosenFile, headerList);
//			additionalOptions.add(headers, 1, 1, 2, 1);
			additionalOptions.add(options, 1, 2, 2, 1);
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
					regex_panel.add(new Label(setting.getScreenname()), 0, 0, 3, 1);
					regex_panel.addRow(1, new Label("Search"), new Label("Replace"), new Label("Opmerkingen"));
					int regex_row = 2;
					for (int i = 0; i < setting.getContent().size(); i++) {
						if (setting.getContent().get(i) instanceof JAXBElement) {
							Settings.Setting.Entry entry = (Settings.Setting.Entry) ((JAXBElement) setting.getContent().get(i)).getValue();
							TextField textField_search = new TextField();
							textField_search.textProperty().setValue(entry.getRegexSearch());
							textField_search.textProperty().addListener((ev) -> {
								entry.setRegexSearch(textField_search.getText());
							});
							TextField textField_replace = new TextField();
							textField_replace.textProperty().setValue(entry.getRegexReplace());
							textField_replace.textProperty().addListener((ev) -> {
								entry.setRegexReplace(textField_replace.getText());
							});
							regex_panel.add(textField_search, 0, regex_row);
							regex_panel.add(textField_replace, 1, regex_row);
							regex_panel.add(new Label(entry.getComment()), 2, regex_row);
							regex_row++;
						}
					}
					panel.add(regex_panel, 0, row, 2, 1);
				} else {
					if (!System.getProperty("technique").equals("SmartHashing")) {
						switch (setting.getName()) {
							case "anonimiseer_zelfstandige_naamwoorden":
							case "anonimiseer_werkwoorden":
							case "anonimiseer_datums":
								break;
							default:
								panel.addRow(row, new Label(setting.getScreenname()), getInputElement(setting));
								break;
						}
					} else
						panel.addRow(row, new Label(setting.getScreenname()), getInputElement(setting));
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
					case "maak_alle_woorden_lowercase":
						checkBox.setOnMouseClicked(event1 -> {
							if (checkBox.isSelected()) {
								settings.getSettingsMap().get("maak_beginwoorden_lowercase").setOverwritable(Boolean.FALSE);
								settings.getSettingsMap().get("maak_beginwoorden_lowercase").setValue("true");
							} else
								settings.getSettingsMap().get("maak_beginwoorden_lowercase").setOverwritable(Boolean.TRUE);
							settings.getSettingsMap().get("maak_beginwoorden_lowercase").updateView();
						});
						break;
					case "maak_beginwoorden_lowercase":
						checkBox.setOnMouseClicked(event1 -> {
							if (checkBox.isSelected())
								settings.getSettingsMap().get("maak_alle_woorden_lowercase").setOverwritable(Boolean.FALSE);

							else {
								settings.getSettingsMap().get("maak_alle_woorden_lowercase").setOverwritable(Boolean.TRUE);
								settings.getSettingsMap().get("maak_alle_woorden_lowercase").setValue("false");
							}
							settings.getSettingsMap().get("maak_alle_woorden_lowercase").updateView();
						});
						break;
					case "verwijder_datums":
						checkBox.setOnMouseClicked(event1 -> {
							if (checkBox.isSelected()) {
								settings.getSettingsMap().get("anonimiseer_datums").setOverwritable(Boolean.FALSE);
								settings.getSettingsMap().get("anonimiseer_datums").setValue("false");
							} else
								settings.getSettingsMap().get("anonimiseer_datums").setOverwritable(Boolean.TRUE);
							settings.getSettingsMap().get("anonimiseer_datums").updateView();
						});
						break;
					case "anonimiseer_datums":
						checkBox.setOnMouseClicked(event1 -> {
							if (checkBox.isSelected()) {
								settings.getSettingsMap().get("verwijder_datums").setOverwritable(Boolean.FALSE);
								settings.getSettingsMap().get("verwijder_datums").setValue("false");
							} else {
								settings.getSettingsMap().get("verwijder_datums").setOverwritable(Boolean.TRUE);
							}
							settings.getSettingsMap().get("verwijder_datums").updateView();
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
							if (!newValue.matches("\\d*") && !newValue.equals("-") && !newValue.equals("-1")) {
								textField.setText(oldValue);
							} else
								setting.setValue(newValue);

							if (!newValue.equals("") && (!newValue.equals("-") || !newValue.equals("-1")) && Integer.parseInt(newValue) < Integer.parseInt(settings.getSettingsMap().get("beginrij").getValue())) {
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

	private GridPane getHeaders(FileType fileType, File file, List<DataAttribute> attributeList) {
		GridPane pane = new GridPane();
		pane.add(new Label("Kolom"), 0, 0);
		pane.add(new Label("Annonimiseer deze kolom?"), 1, 0);
		if (fileType.equals(FileType.CSV)) {
			attributeList.clear();
			attributeList.addAll(FileReader.readCSVHeaders(file));
		} else if (fileType.equals(FileType.XLS) || fileType.equals(FileType.XLSX)) {
			attributeList.clear();
			attributeList.addAll(FileReader.readExcelHeaders(file));
		}

		//
		for (int i = 0; attributeList != null && i < attributeList.size(); i++) {
			DataAttribute attribute = attributeList.get(i);
			Label label = new Label(attribute.getData());
			pane.add(label, 0, i + 1);
			ComboBox<String> choicebox = new ComboBox<>();
			choicebox.getItems().addAll("Ja", "Nee");
			choicebox.getProperties().addListener(new MapChangeListener<Object, Object>() {
				@Override
				public void onChanged(Change<?, ?> change) {
					attribute.setDoAnonimize(!"Nee".equals(choicebox.getValue()));
				}
			});
			pane.add(choicebox, 1, i + 1);
		}
		return pane;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
