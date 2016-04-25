package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.Vocabulary;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Martijn on 8-3-2016.
 * <p>
 * This technique focusses on the techniques used in literature to achieve k-anonymity.
 */
public class GeneralizeOrSuppress extends AnonymizationTechnique {
	private static Vocabulary voc;
	private final int k;

	public GeneralizeOrSuppress(int k) {
		this.k = k;
		if (voc == null) voc = new Vocabulary();
	}

	@Override
	public DataEntry doPreProcessing(DataEntry dataEntry, Settings settings) {
		super.doPreProcessing(dataEntry, settings);
		voc.addData(dataEntry);
		return dataEntry;
	}

	@Override
	public DataEntry anonymize(DataEntry dataEntry, List<DataEntry> raw_data, Settings settings) {
		DataEntry anaonymous_entry = new DataEntry(dataEntry.getHeaders());
		// Get data
		Map<Integer, Integer> wordmap = Vocabulary.createCleanWordcountMap(voc, dataEntry);
		// Check the occurrences (enough k?)
		List<String> words_to_generalize = wordmap.entrySet().stream().filter(entry -> entry.getValue() < k).map(entry -> voc.getWord(entry.getKey())).collect(Collectors.toCollection(LinkedList::new));
		// Try generalization using Cornetto
		for (String word : words_to_generalize) {
			System.out.println(word);
		}
		// Check again (enough k?)
		List<String> words_to_suppress = wordmap.entrySet().stream().filter(entry -> entry.getValue() < k).map(entry -> voc.getWord(entry.getKey())).collect(Collectors.toCollection(LinkedList::new));
		// Still not enough? Then suppress
		for (DataAttribute raw_dataAttribute : dataEntry.getDataAttributes()) {
			DataAttribute anaonymous_dataAttribute = raw_dataAttribute.clone();
			for (String word : words_to_suppress) {
				anaonymous_dataAttribute.setData(raw_dataAttribute.getData().replace(word, ""));
			}
			anaonymous_entry.addDataAttribute(anaonymous_dataAttribute);
		}
		// Finally return the data
		return anaonymous_entry;
	}
}
