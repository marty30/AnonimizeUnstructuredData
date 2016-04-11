package nl.willemsenmedia.utwente.anonymization.data;

import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.dutchStemmer;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * This helper class modifies the data. All functions that really change the data are listed here to be sure
 * what might change in this application and what does not.
 */
public class DataModifier {
	//TODO Deze salt ergens verbergen voor privacy.
	private static final String SALT = "aasfbasf oaf if afbui aufi ba ufiabfoiasf ";
	private static SnowballStemmer nl_stemmer;
	private static SnowballStemmer en_stemmer;

	public static String hash(String unhashedString) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			return bin2base64(digest.digest((SALT + sanitize(unhashedString) + SALT).trim().getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e1) {
			ErrorHandler.handleException(e1);
			e1.printStackTrace();
		}
		return null;
	}

	public static String sanitize(String unsanitizedString) {
		return unsanitizedString.trim().toLowerCase().replaceAll("[\\.!?\"\']", "");
	}

	public static String bin2hex(byte[] data) {
		return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
	}

	public static String bin2base64(byte[] data) {
		return new String(Base64.getEncoder().encode(data));
	}

	public static synchronized String getStem(String word) {
		SnowballStemmer stemmer;
		if ("en".equals(System.getProperty("lang"))) {
			if (en_stemmer == null) {
				en_stemmer = new dutchStemmer();
			}
			stemmer = en_stemmer;
		} else {
			if (nl_stemmer == null) {
				nl_stemmer = new dutchStemmer();
			}
			stemmer = nl_stemmer;
		}
		stemmer.setCurrent(word);
		boolean stemmingWorked = stemmer.stem();
		if (!stemmingWorked) {
			System.err.println("Stemming of " + word + " failed!");
		}
		return stemmer.getCurrent();
	}
}
