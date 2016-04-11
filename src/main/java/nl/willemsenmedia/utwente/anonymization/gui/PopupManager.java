package nl.willemsenmedia.utwente.anonymization.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * Created by Martijn on 20-2-2016.
 * <p>
 * Zie ook: http://code.makery.ch/blog/javafx-dialogs-official/
 */
public class PopupManager {

	static Logger log = Logger.getLogger(PopupManager.class.getSimpleName());

	public static void error(String titel, String header, String msg, Throwable throwable) {
		try {
			if (!System.getProperty("useGUI").equals("false")) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle(titel == null ? "Oeps, er is een fout opgetreden" : titel);
				alert.setHeaderText(header);
				alert.setContentText(msg == null ? "De fout was: " + throwable.getMessage() : msg);


				if (throwable != null) {
					// Create expandable Exception.
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					throwable.printStackTrace(pw);
					throwable.printStackTrace();
					String exceptionText = sw.toString();

					Label label = new Label("De exception stacktrace was:");

					TextArea textArea = new TextArea(exceptionText);
					textArea.setEditable(false);
					textArea.setWrapText(true);

					textArea.setMaxWidth(Double.MAX_VALUE);
					textArea.setMaxHeight(Double.MAX_VALUE);
					GridPane.setVgrow(textArea, Priority.ALWAYS);
					GridPane.setHgrow(textArea, Priority.ALWAYS);

					GridPane expContent = new GridPane();
					expContent.setMaxWidth(Double.MAX_VALUE);
					expContent.add(label, 0, 0);
					expContent.add(textArea, 0, 1);

					// Set expandable Exception into the dialog pane.
					alert.getDialogPane().setExpandableContent(expContent);
				}

				alert.showAndWait();
			} else {
				String exceptionText = null;
				if (throwable != null) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					throwable.printStackTrace(pw);
					exceptionText = sw.toString();
				}
				log.severe("Popup bericht: " + titel + "\r\n" +
						"-------------------------------\r\n" +
						header + "\r\n" +
						"-------------------------------\r\n" +
						msg + "\r\n" +
						"-------------------------------\r\n" +
						exceptionText);
			}
		} catch (ExceptionInInitializerError e) {
			// There is no GUI, so just rethrowing the exception is fine
			throw new RuntimeException(throwable);
		}
	}

	public static void info(String titel, String header, String msg) {
		if (!System.getProperty("useGUI").equals("false")) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle(titel);
			alert.setHeaderText(header);
			alert.setContentText(msg);
			alert.showAndWait();
		} else {
			log.info("Popup bericht: " + titel + "\r\n" +
					"-------------------------------\r\n" +
					header + "\r\n" +
					"-------------------------------\r\n" +
					msg);
		}
	}
}
