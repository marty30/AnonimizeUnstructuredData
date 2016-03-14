package nl.willemsenmedia.utwente.anonymization.gui;

/**
 * Created by Martijn on 19-2-2016.
 * <p>
 * This class will be a helper class that helps with handling errors.
 */
public class ErrorHandler {
	public static void handleException(Exception e) {
		PopupManager.error(null, null, null, e);
	}
}
