package nl.willemsenmedia.utwente.anonymization.nlp;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Martijn on 5-4-2016.
 */
public class NLPHelper {

	public static boolean isDate(String word) {
		return word.matches("[0-9]{2}[\\-/\\.]*[0-9]{2}[\\-/\\.]*[0-9]{4}");// || word.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}") || word.matches("[0-9]{8}");
	}

	@Test
	public void testIsDate() {
		String date1 = "01-02-1993";
		String date2 = "01/02/1993";
		String date3 = "01021993";
		String nodate1 = "de 13e dag";
		String nodate2 = "06-03930254";

		Assert.assertTrue(isDate(date1));
		Assert.assertTrue(isDate(date2));
		Assert.assertTrue(isDate(date3));
		Assert.assertFalse(isDate(nodate1));
		Assert.assertFalse(isDate(nodate2));
	}
}
