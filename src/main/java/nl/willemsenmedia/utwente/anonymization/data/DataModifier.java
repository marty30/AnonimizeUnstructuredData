package nl.willemsenmedia.utwente.anonymization.data;

import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.dutchStemmer;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * This helper class modifies the data. All functions that really change the data are listed here to be sure
 * what might change in this application and what does not.
 */
public class DataModifier {
	//TODO Deze salt ergens verbergen voor privacy.
	private static final String SALT = "aasfbasf oaf if afbui aufi ba ufiabfoiasf ";
	public static String[] stopwords = new String[]{"een", "de", "het"};
	private static SnowballStemmer stemmer;

	public static String filterStopwords(String data) {
		StringBuilder filteredData = new StringBuilder();
		String[] words = data.split("\\s+");
		for (String word : words) {

			if (!Arrays.asList(stopwords).contains(word)) {
				filteredData.append(word).append(" ");
			}
		}
		return filteredData.toString().trim();
	}

	public static String hash(String unhashedString) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			return bin2hex(digest.digest((SALT + sanitize(unhashedString) + SALT).trim().getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e1) {
			ErrorHandler.handleException(e1);
			e1.printStackTrace();
		}
		return null;
	}

	public static String sanitize(String unsanitizedString) {
		return unsanitizedString.trim().toLowerCase().replaceAll("[\\.!?\"\']", "");
	}

	static String bin2hex(byte[] data) {
		return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
	}

	public static String getStem(String word) {
		if (stemmer == null) {
			stemmer = new dutchStemmer();
		}
		stemmer.setCurrent(word);
		boolean stemmingWorked = stemmer.stem();
		if (!stemmingWorked) {
			System.err.println("Stemming of " + word + " failed!");
		}
		return stemmer.getCurrent();
	}
}
