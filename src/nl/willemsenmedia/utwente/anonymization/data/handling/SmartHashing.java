package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;

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

	@Override
	public DataEntry anonymize(DataEntry dataEntry) {
		determineImportantWords(dataEntry);
		List<DataAttribute> attributes = dataEntry.getDataAttributes();
		for (DataAttribute attribute : attributes) {
			String[] sentences = attribute.getData().split("\\.");
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

				// 2. The only verb
				//
			}
		}
	}

	private boolean isImportantWord(DataEntry dataEntry, String sentence, String word) {
		return importantWords.indexOf(word) > -1;
	}
}
