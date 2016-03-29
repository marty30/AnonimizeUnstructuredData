package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.util.List;

import static nl.willemsenmedia.utwente.anonymization.data.DataModifier.filterStopwords;
import static nl.willemsenmedia.utwente.anonymization.data.DataModifier.hash;

/**
 * Created by Martijn on 19-2-2016.
 * <p>
 * This technique changes al sentences into a hashed sentence.
 */
public class HashSentence extends AnonymizationTechnique {

	@Override
	public DataEntry anonymize(DataEntry dataEntry, Settings settings) {
		List<DataAttribute> attributes = dataEntry.getDataAttributes();
		for (DataAttribute attribute : attributes) {
			if (attribute.doAnonimize()) {
				String[] sentences = filterStopwords(attribute.getData()).split("\\.\\s");
				StringBuilder newData = new StringBuilder();
				for (String sentence : sentences) {
					newData.append(hash(sentence)).append(" ");
				}
				attribute.setData(newData.toString());
			}
		}
		return dataEntry;
	}
}

