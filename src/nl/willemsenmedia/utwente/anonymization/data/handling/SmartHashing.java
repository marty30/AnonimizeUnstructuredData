package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;

import java.util.List;

import static nl.willemsenmedia.utwente.anonymization.data.DataModifier.filterStopwords;
import static nl.willemsenmedia.utwente.anonymization.data.DataModifier.hash;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * This technique tries to find important words and only hashes them.
 */
public class SmartHashing extends AnonymizationTechnique {
	@Override
	public DataEntry anonymize(DataEntry dataEntry) {
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

	private boolean isImportantWord(DataEntry dataEntry, String sentence, String word) {
		//TODO determine important words
		return "nog".equals(word);
	}
}
