package nl.willemsenmedia.utwente.anonymization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.gui.DataviewController;
import nl.willemsenmedia.utwente.anonymization.gui.PopupManager;

import java.io.IOException;
import java.util.HashMap;

public class Main extends Application {

	public static Stage mainStage = null;

	public static void main(String[] args) {
		if (args.length > 0) {
			System.setProperty("technique", args[0]);
			if (args.length == 2) {
				System.setProperty("k", args[1]);
			}
		}
		if (System.getProperty("technique") == null) {
			System.err.println("Er moet een techniek bepaald zijn, anders werkt de applicatie niet!");
			System.err.println();
			System.err.println("Usage: -Dtechnique={HashSentence/HashAll/SmartHashing/GeneralizeOrSuppress/k-anonymity/???}");
			System.exit(-1);
		} else
			launch(args);
	}

	public static void OpenPageWithData(HashMap<String, Node> additionalOptions, DataEntry... data) {
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("gui/dataview.fxml"));
		try {
			Parent root = fxmlLoader.load();
			mainStage.setScene(new Scene(root, 1024, 768));
		} catch (IOException e) {
			PopupManager.error(null, null, null, e);
		}
		DataviewController controller = fxmlLoader.getController();
		controller.setAdditionalOptions(additionalOptions);
		controller.setData(data);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui/home.fxml"));
		Parent root = fxmlLoader.load();
		primaryStage.setTitle("Anonimiseer ongestructureerde data");
		primaryStage.setScene(new Scene(root, 300, 275));
		primaryStage.show();
	}
}
