package nl.willemsenmedia.utwente.anonymization;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataType;
import nl.willemsenmedia.utwente.anonymization.data.database.DBHandler;

import java.util.Arrays;
import java.util.List;

import static nl.willemsenmedia.utwente.anonymization.gui.DataviewController.determineTechnique;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * Test class to bypass the gui
 */
public class Tester {
	private static String testdata = "nog een testje";

	public static void main(String[] args) throws Exception {
		System.setProperty("technique", "HashAll");
		DBHandler dbHandler = new DBHandler();
		dbHandler.createEmptyTable();
		DataEntry entry = new DataEntry(
				Arrays.asList(new DataAttribute(DataType.NAME, "naam"), new DataAttribute(DataType.UNSTRUCTURED, "data")),
				new DataAttribute(DataType.UNSTRUCTURED, "naam", "Dit is de data", false));
		dbHandler.saveRawData(1, entry);
		List<DataEntry> raw = dbHandler.getRawData();
		DataEntry[] anonimous = new DataEntry[raw.size()];
		for (int i = 0; i < raw.size(); i++) {
			anonimous[i] = determineTechnique().anonymize(raw.get(i), null);
			dbHandler.saveAnonimousData(raw.get(i), anonimous[i]);
		}
		dbHandler.getData(true);
	}
}
