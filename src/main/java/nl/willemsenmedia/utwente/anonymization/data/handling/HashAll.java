package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataType;

import java.util.List;

import static nl.willemsenmedia.utwente.anonymization.data.DataModifier.filterStopwords;
import static nl.willemsenmedia.utwente.anonymization.data.DataModifier.hash;

/**
 * Created by Martijn on 19-2-2016.
 * <p>
 * This technique hashes each word. Before doing this it removes all stopwords (de, het, een).
 */
public class HashAll extends AnonymizationTechnique {

	@Override
	public DataEntry anonymize(DataEntry dataEntry) {
		List<DataAttribute> attributes = dataEntry.getDataAttributes();
		for (DataAttribute attribute : attributes) {
			if (attribute.getDataType().equals(DataType.UNSTRUCTURED)) {
				String oldData = attribute.getData();
				String[] words = filterStopwords(oldData).split("\\s+");
				StringBuilder newData = new StringBuilder();
				for (String word : words) {
					newData.append(hash(word)).append(" ");
				}
				attribute.setData(newData.toString());
			}
		}
		return dataEntry;
	}
}
