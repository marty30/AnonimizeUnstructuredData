package nl.willemsenmedia.utwente.anonymization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.gui.DataviewController;
import nl.willemsenmedia.utwente.anonymization.gui.HomeController;
import nl.willemsenmedia.utwente.anonymization.gui.PopupManager;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class Main extends Application {

	public static Stage mainStage = null;

	public static void main(String[] args) {
		if (System.getProperty("settings") == null || System.getProperty("settings").equals("") || System.getProperty("settings").equals("default")) {
			System.setProperty("settings", Main.class.getClassLoader().getResource("default_settings.xml").getFile());
		}
		if (System.getProperty("technique") == null) {
			System.err.println("Er moet een techniek bepaald zijn, anders werkt de applicatie niet!");
			System.err.println();
			System.err.println("Usage: -Dtechnique={HashSentence/HashAll/SmartHashing/GeneralizeOrSuppress/k-anonymity/???} -Dsettings=path_to_file");
			System.exit(-1);
		} else if (System.getProperty("settings") != null && (!System.getProperty("settings").endsWith(".xml") || !new File(System.getProperty("settings")).exists())) {
			System.err.println("De settings moeten van een xml-bestand komen en het lijkt erop dat deze niet goed is gedefinieerd. Dit is gespecificeerd: " + System.getProperty("settings"));
			System.err.println();
			System.err.println("Usage: -Dtechnique={HashSentence/HashAll/SmartHashing/GeneralizeOrSuppress/k-anonymity/???} -Dsettings=path_to_file");
			System.exit(-1);
		} else {
			validateSettings(new File(System.getProperty("settings")));
			launch(args);
		}
	}

	private static void validateSettings(File settings_file) {
		//Validate the xml-file
		Settings settings = null;
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File(Main.class.getClassLoader().getResource("settings_schema.xsd").getFile()));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(settings_file));
			settings = createSettingsFromFile(settings_file);
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

	}

	private static Settings createSettingsFromFile(File file) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (Settings) jaxbUnmarshaller.unmarshal(file);
	}

	public static void OpenPageWithData(Settings settings, DataEntry... data) {
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("gui/dataview.fxml"));
		try {
			Parent root = fxmlLoader.load();
			mainStage.setScene(new Scene(root, 1024, 768));
		} catch (IOException e) {
			PopupManager.error(null, null, null, e);
		}
		DataviewController controller = fxmlLoader.getController();
		controller.setSettings(settings);
		controller.setData(data);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("gui/home.fxml"));
		Parent root = fxmlLoader.load();
		primaryStage.setTitle("Anonimiseer ongestructureerde data");
		primaryStage.setScene(new Scene(root, 300, 275));
		HomeController controller = fxmlLoader.getController();
		controller.setSettings(createSettingsFromFile(new File(System.getProperty("settings"))));
		primaryStage.show();
	}
}
