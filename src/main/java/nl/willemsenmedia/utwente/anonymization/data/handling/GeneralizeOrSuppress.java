package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.Vocabulary;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
		// Get data

		//// TODO: 23-4-2016 Dit moet op een andere plek want zo wijzigt je vocabulary tijdens het uitvoeren van het anonymiseren
		Map<Integer, Integer> wordmap = Vocabulary.createCleanWordcountMap(voc, dataEntry);
		// Sort all words

		// Check the occurrences (enough k?)
		List<String> words_to_generalize = new LinkedList<>();
		for (Map.Entry<Integer, Integer> entry : wordmap.entrySet()) {
			if (entry.getValue() < k) {
				words_to_generalize.add(voc.getWord(entry.getKey()));
			}
		}
		// Try generalization using Cornetto
		for (String word : words_to_generalize) {
			System.out.println(word);
		}
		// Check again (enough k?)
		List<String> words_to_suppress = new LinkedList<>();
		for (Map.Entry<Integer, Integer> entry : wordmap.entrySet()) {
			if (entry.getValue() < k) {
				words_to_generalize.add(voc.getWord(entry.getKey()));
			}
		}
		// Still not enough? Then suppress

		// Finally return the data
		return dataEntry;
	}
}
