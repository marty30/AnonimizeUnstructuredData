package nl.willemsenmedia.utwente.anonymization;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.Vocabulary;
import nl.willemsenmedia.utwente.anonymization.data.reading.FileReader;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.io.File;
import java.util.List;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * Test class to bypass the gui
 */
public class Tester {
	private static String testdata = "nog een testje";
	final Button button = new Button("Send");
	final Label notification = new Label();
	final TextField subject = new TextField("");
	final TextArea text = new TextArea("");
	String address = " ";

	public static void main(String[] args) throws Exception {
//		System.setProperty("technique", "HashAll");
//		DBHandler dbHandler = new DBHandler();
//		dbHandler.createEmptyTable();
//		DataEntry entry = new DataEntry(
//				Arrays.asList(new DataAttribute(DataType.NAME, "naam"), new DataAttribute(DataType.UNSTRUCTURED, "data")),
//				new DataAttribute(DataType.UNSTRUCTURED, "naam", "Dit is de data", false));
//		dbHandler.saveRawData(1, entry);
//		List<DataEntry> raw = dbHandler.getRawData();
//		DataEntry[] anonimous = new DataEntry[raw.size()];
//		for (int i = 0; i < raw.size(); i++) {
//			anonimous[i] = determineTechnique().anonymize(raw.get(i), null);
//			dbHandler.saveAnonimousData(raw.get(i), anonimous[i]);
//		}
//		dbHandler.getData(true);
		List<DataEntry> dat = FileReader.readFile(new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus.csv"), Settings.getDefault(), null);
		Vocabulary voc = new Vocabulary();
		dat.forEach(dataEntry -> voc.addData(dataEntry));
		dat.forEach(dataEntry -> voc.removeData(dataEntry));
		System.out.println(voc);
	}
}
