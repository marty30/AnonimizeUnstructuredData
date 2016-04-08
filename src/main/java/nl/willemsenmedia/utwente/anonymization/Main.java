package nl.willemsenmedia.utwente.anonymization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.gui.DataviewController;
import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;
import nl.willemsenmedia.utwente.anonymization.gui.HomeController;
import nl.willemsenmedia.utwente.anonymization.nlp.ODWNReader;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Main extends Application {

	public static Stage mainStage = null;
	private static Logger log = Logger.getLogger(Main.class.getSimpleName());
	private static DataviewController controller;

	/**
	 * When the program is started, this method is used.
	 * This method will do some checks on how the program is started and will create the settings.
	 * Checks:
	 * -	Language (either English (en) or Dutch (nl)
	 * -	Is the GUI parameter specified? If not, make it false.
	 * -	Is a settingsfile specified? If not set the default settings
	 * -	Is a technique specified? If not return an error
	 * -	Is the specified settings file valid? Does it exist? Does it contain a valid XML? Does it contain the required settings? If not, return an error.
	 *
	 * @param args args for the program (not used, only passed to the GUI if used)
	 */
	public static void main(String[] args) {
		//Set language (for wordnet and stopwords etc.) if it is not yet set
		if (System.getProperty("lang") == null || System.getProperty("lang").equals("")) {
//			System.setProperty("lang", "en");
			System.setProperty("lang", "nl");
		}
		if (System.getProperty("useGUI") == null || System.getProperty("useGUI").equals("") || System.getProperty("useGUI").equals("true")) {
			System.setProperty("useGUI", "true");
		}
		if (System.getProperty("settings") == null || System.getProperty("settings").equals("") || System.getProperty("settings").equals("default")) {
			System.setProperty("settings", Main.class.getClassLoader().getResource("default_settings.xml").getFile());
		}
		if (System.getProperty("technique") == null) {
			System.err.println("Er moet een techniek bepaald zijn, anders werkt de applicatie niet!");
			System.err.println();
			System.err.println("Usage: -Dtechnique={HashSentence/HashAll/SmartHashing/GeneralizeOrSuppress/k-anonymity/???} -Dsettings=path_to_file -DuseGUI={true/false} -Dfile=path_fo_file_to_anonimize");
			System.exit(-1);
		} else if (System.getProperty("settings") != null && (!System.getProperty("settings").endsWith(".xml") || !new File(System.getProperty("settings")).exists())) {
			System.err.println("De settings moeten van een xml-bestand komen en het lijkt erop dat deze niet goed is gedefinieerd. Dit is gespecificeerd: " + System.getProperty("settings"));
			System.err.println();
			System.err.println("Usage: -Dtechnique={HashSentence/HashAll/SmartHashing/GeneralizeOrSuppress/k-anonymity/???} -Dsettings=path_to_file -DuseGUI={true/false} -Dfile=path_fo_file_to_anonimize");
			System.exit(-1);
		} else {
			log.info("Validate settings");
			Settings settings = validateSettings(new File(System.getProperty("settings")));
			log.info("Load wordnet");
			loadWordnet();
			log.info("Launch");
			if (System.getProperty("useGUI").equals("false")) {
				startProgramWithoutGUI(settings);
				log.info("Done! Now exit.");
			} else {
				startGUI(args);
			}
		}
	}

	private static void startProgramWithoutGUI(Settings settings) {
		if (System.getProperty("file") == null || System.getProperty("file").equals("")) {
			System.err.println("Bij gebruik van de tool zonder de GUI is het opgeven van een bestand vereist");
			System.err.println();
			System.err.println("Usage: -Dtechnique={HashSentence/HashAll/SmartHashing/GeneralizeOrSuppress/k-anonymity/???} -Dsettings=path_to_file -DuseGUI={true/false} -Dfile=path_fo_file_to_anonimize");
			System.exit(-1);
		}
		File chosenFile = new File(System.getProperty("file"));
		if (!chosenFile.exists()) {
			System.err.println("Bestand \"" + chosenFile.getAbsolutePath() + "\" kon niet gevonden worden! Probeer het opnieuw.");
			System.err.println();
			System.err.println("Usage: -Dtechnique={HashSentence/HashAll/SmartHashing/GeneralizeOrSuppress/k-anonymity/???} -Dsettings=path_to_file -DuseGUI={true/false} -Dfile=path_fo_file_to_anonimize");
			System.exit(-1);
		}
		HomeController controller = new HomeController(chosenFile);
		controller.setSettings(settings);
		controller.handleVerwerkBestand(null);
		Main.controller.exportData(null);
	}

	private static void startGUI(String[] args) {
		launch(args);
	}

	private static Settings validateSettings(File settings_file) {
		//Validate the xml-file
		Settings settings = null;
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File(Main.class.getClassLoader().getResource("settings_schema.xsd").getFile()));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(settings_file));
			settings = Settings.createSettingsFromFile(settings_file);
		} catch (IOException | JAXBException | SAXException e1) {
			System.err.println("Het bestand met instellingen is niet valide! Gebruikte bestand: " + settings_file.getAbsolutePath());
			e1.printStackTrace();
			System.exit(-1);
		}

		//The following settings are required:
		// - bevat_kopteksten
		// - beginrij
		// - eindrij
		// -

		//TODO
		return settings;
	}


	public static void OpenPageWithData(Settings settings, DataEntry... data) {
		if (System.getProperty("useGUI").equals("false")) {
			controller = new DataviewController();
		} else {
			FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("gui/dataview.fxml"));
			try {
				Parent root = fxmlLoader.load();
				mainStage.setScene(new Scene(root, 1024, 768));
			} catch (IOException e) {
				ErrorHandler.handleException(e);
			}
			controller = fxmlLoader.getController();
		}
		controller.setSettings(settings);
		controller.setData(data);
	}

	private static void loadWordnet() {
		new Thread() {
			@Override
			public void run() {
				ODWNReader.getInstance();
				log.info("Ready loading wordnet");
			}
		}.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("gui/home.fxml"));
		Parent root = fxmlLoader.load();
		primaryStage.setTitle("Anonimiseer ongestructureerde data");
		primaryStage.setScene(new Scene(root, 1024, 768));
		HomeController controller = fxmlLoader.getController();
		controller.setSettings(Settings.createSettingsFromFile(new File(System.getProperty("settings"))));
		primaryStage.show();
	}
}
