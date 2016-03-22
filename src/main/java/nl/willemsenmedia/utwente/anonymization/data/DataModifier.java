package nl.willemsenmedia.utwente.anonymization.data;

import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;

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
			return bin2hex(digest.digest((SALT + unhashedString.trim() + SALT).trim().getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e1) {
			ErrorHandler.handleException(e1);
			e1.printStackTrace();
		}
		return null;
	}

	static String bin2hex(byte[] data) {
		return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
	}
}
