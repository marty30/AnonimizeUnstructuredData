package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.nlp_java.OpenNLPFactory;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.util.List;

import static nl.willemsenmedia.utwente.anonymization.data.DataModifier.hash;

/**
 * Created by Martijn on 19-2-2016.
 * <p>
 * This technique hashes each word.
 */
public class HashAll extends AnonymizationTechnique {

	@Override
	public DataEntry anonymize(DataEntry dataEntry, Settings settings) {
		DataEntry newDataEntry = new DataEntry(dataEntry.getHeaders());
		List<DataAttribute> attributes = dataEntry.getDataAttributes();
		for (DataAttribute attribute : attributes) {
			DataAttribute newDataAttribute = attribute.clone();
			if (attribute.doAnonimize()) {
				String[] words = OpenNLPFactory.getTokenizer().tokenize(attribute.getData());
				StringBuilder newData = new StringBuilder();
				for (String word : words) {
					newData.append(hash(word)).append(" ");
				}
				newDataAttribute.setData(newData.toString());
			}
			newDataEntry.addDataAttribute(newDataAttribute);
		}
		return newDataEntry;
	}
}
