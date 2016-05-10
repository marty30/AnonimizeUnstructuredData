package nl.willemsenmedia.utwente.anonymization.data.handling;

import edu.smu.tspell.wordnet.*;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.Vocabulary;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.util.Arrays;
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
	private static WordNetDatabase wordNetDatabase = WordNetDatabase.getFileInstance();
	private static String could_not_generalize_placeholder = System.getProperty("could_not_generalize_placeholder") == null ? "<sprds>" : System.getProperty("could_not_generalize_placeholder");
	private static Vocabulary voc_clone;
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
		if (voc_clone == null)
			voc_clone = voc.clone();
		DataEntry anaonymous_entry = dataEntry.clone();
		try {
			// Get data
			Map<Integer, Integer> wordmap = Vocabulary.createCleanWordcountMap(voc, dataEntry);
			// Check the occurrences (enough k?)
			List<String> words_to_generalize = wordmap.entrySet().stream().filter(entry -> entry.getValue() < k).map(entry -> voc.getWord(entry.getKey())).collect(Collectors.toCollection(LinkedList::new));
			// Try generalization using the correct wordnet
			for (String word : words_to_generalize) {
				if (!word.equals(could_not_generalize_placeholder))
					replace_word(anaonymous_entry, word, getHypernym(word));
			}
			//Todo dit zou eigenlijk voor de hele lijst moeten, en niet per entry... Ik kan het wel hacken maar dan werkt de progress niet.
			// Check again (enough k?)
			List<String> words_to_suppress = wordmap.entrySet().stream().filter(entry -> entry.getValue() < k).map(entry -> voc.getWord(entry.getKey())).collect(Collectors.toCollection(LinkedList::new));
			// Still not enough? Then suppress
			for (String word : words_to_suppress) {
				replace_word(dataEntry, word, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Remove chars
		anaonymous_entry.deleteChars("\'");
		// Finally return the data
		return anaonymous_entry;
	}

	private synchronized void replace_word(DataEntry dataEntry, String search, String replace) {
		if (search != null) {
			dataEntry.replace(" " + search.trim() + " ", replace == null ? " " + could_not_generalize_placeholder.trim() + " " : " " + replace.trim() + " ");
			voc.changeWord(search, replace, dataEntry, could_not_generalize_placeholder);
		}
	}

	private synchronized String getHypernym(String word) {
		if (System.getProperty("lang").equals("en")) {
			Synset[] synsets = wordNetDatabase.getSynsets(word);
			if (synsets.length == 0) {
				return could_not_generalize_placeholder;
			}
			List<Synset> possible_hypernyms = new LinkedList<>();
			for (Synset synset : synsets) {
				if (synset.getType().equals(SynsetType.NOUN)) {
					possible_hypernyms.addAll(Arrays.asList(((NounSynset) synset).getHypernyms()).stream().filter(hypernym -> hypernym.getType().equals(SynsetType.NOUN)).collect(Collectors.toList()));
				} else if (synset.getType().equals(SynsetType.VERB)) {
					possible_hypernyms.addAll(Arrays.asList(((VerbSynset) synset).getHypernyms()).stream().filter(hypernym -> hypernym.getType().equals(SynsetType.VERB)).collect(Collectors.toList()));
				}
			}
			if (possible_hypernyms == null || possible_hypernyms.size() == 0) {
				System.err.println("Could not find a hypernym for " + word);
				return could_not_generalize_placeholder;
			}
			for (Synset possible_hypernym : possible_hypernyms) {
				if (possible_hypernym.getWordForms().length > 0) {
					return possible_hypernym.getWordForms()[0];
				}
			}
			return could_not_generalize_placeholder;
		} else if (System.getProperty("lang").equals("nl")) {
			// Still to be determined what I will do with this.
			throw new RuntimeException("Kan geen generalisatie vinden voor nederlandse woorden");
		}
		return could_not_generalize_placeholder;
	}
}
