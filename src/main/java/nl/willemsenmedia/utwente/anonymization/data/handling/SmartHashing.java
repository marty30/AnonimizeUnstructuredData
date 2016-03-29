package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.nlp_java.ODWNReader;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static nl.willemsenmedia.utwente.anonymization.data.DataModifier.filterStopwords;
import static nl.willemsenmedia.utwente.anonymization.data.DataModifier.hash;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * This technique tries to find important words and only hashes them.
 */
public class SmartHashing extends AnonymizationTechnique {
	private static ArrayList<String> importantWords = new ArrayList<>();
	private Settings settings;

	@Override
	public DataEntry anonymize(DataEntry dataEntry, Settings settings) {
		this.settings = settings;
		determineImportantWords(dataEntry);
		List<DataAttribute> attributes = dataEntry.getDataAttributes();
		for (DataAttribute attribute : attributes) {
			if (attribute.doAnonimize()) {
				String[] sentences = attribute.getData().split("\\.\\s");
				StringBuilder newData = new StringBuilder();
				for (String sentence : sentences) {
					String[] words = filterStopwords(sentence).split("\\s+");
					StringBuilder newSentence = new StringBuilder();
					for (String word : words) {
						if (isImportantWord(dataEntry, sentence, word))
							newSentence.append(hash(word)).append(" ");
						else
							newSentence.append(word).append(" ");
					}
					newData.append(newSentence.toString().trim()).append(".");
				}
				attribute.setData(newData.toString());
			}
		}
		return dataEntry;
	}

	private void determineImportantWords(DataEntry dataEntry) {
		//TODO determine important words
		for (DataAttribute dataAttribute : dataEntry.getDataAttributes()) {
			//Get the sentences
			String[] sentences = dataAttribute.getData().split("\\.\\s");
			for (String sentence : sentences) {
				String[] words = sentence.split("\\s+");
				// The following rules are applied to determine if the current word is of general importance:
				// The word is the main thing of the sentence. This can be so for several reasons:
				// 1. The only word (notice, skip abbreviations like i.e. and a.s.a.p.)

				// 2. The word is a noun
				// Located in SmartHasing#isImportantWord()
				// 3. The word starts with a captial letter
			}
		}
	}

	private boolean isImportantWord(DataEntry dataEntry, String sentence, String word) {
		boolean isImportant = importantWords.indexOf(word) > -1;
		if (!isImportant && "true".equals(settings.getSettingsMap().get("anonimiseer_zelfstandige_naamwoorden").getValue())) {
			isImportant = "noun".equals(ODWNReader.getInstance().getWordType(word));
			if (isImportant) importantWords.add(word);
		}
		if (!isImportant && "true".equals(settings.getSettingsMap().get("anonimiseer_werkwoorden").getValue())) {
			isImportant = "verb".equals(ODWNReader.getInstance().getWordType(word));
			if (isImportant) importantWords.add(word);
		}
		if (!isImportant && "true".equals(settings.getSettingsMap().get("anonimiseer_werkwoorden").getValue())) {
			isImportant = isDate(word);
			if (isImportant) importantWords.add(word);
		}
		return isImportant;
	}

	private boolean isDate(String word) {
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
