package nl.willemsenmedia.utwente.anonymization;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataType;
import nl.willemsenmedia.utwente.anonymization.data.handling.AnonymizationTechnique;
import nl.willemsenmedia.utwente.anonymization.data.handling.HashSentence;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * Test class to bypass the gui
 */
public class Tester {
	private static String testdata = "nog een testje";

	public static void main(String[] args) {
		DataEntry dataEntry = new DataEntry(new DataAttribute(DataType.UNSTRUCTURED, testdata));

		AnonymizationTechnique hashAll = new HashSentence();
		System.out.println(hashAll.anonymize(dataEntry));
	}
}
