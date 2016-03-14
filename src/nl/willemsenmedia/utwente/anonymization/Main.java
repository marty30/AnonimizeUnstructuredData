package nl.willemsenmedia.utwente.anonymization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.gui.DataviewController;
import nl.willemsenmedia.utwente.anonymization.gui.PopupManager;

import java.io.IOException;
import java.util.List;

public class Main extends Application {

	public static Stage mainStage = null;

	public static void main(String[] args) {
		launch(args);
	}

	public static void OpenPageWithData(List<DataEntry> data) {
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("gui/dataview.fxml"));
		try {
			Parent root = fxmlLoader.load();
			mainStage.setScene(new Scene(root, 1024, 768));
		} catch (IOException e) {
			PopupManager.error(null, null, null, e);
		}
		DataviewController controller = fxmlLoader.getController();
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
