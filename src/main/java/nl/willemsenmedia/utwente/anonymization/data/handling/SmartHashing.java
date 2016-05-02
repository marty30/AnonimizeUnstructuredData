package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataType;
import nl.willemsenmedia.utwente.anonymization.data.Vocabulary;
import nl.willemsenmedia.utwente.anonymization.nlp.NLPHelper;
import nl.willemsenmedia.utwente.anonymization.nlp.OpenNLPFactory;
import nl.willemsenmedia.utwente.anonymization.nlp.POS;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static nl.willemsenmedia.utwente.anonymization.data.DataModifier.hash;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * This technique tries to find important words and only hashes them.
 */
public class SmartHashing extends AnonymizationTechnique {
	private static Set<String> importantWords = new ConcurrentSkipListSet<>();
	private Settings settings;

	public SmartHashing() {
		super();
		doStem = false;
	}

	@Override
	public DataEntry anonymize(DataEntry dataEntry, List<DataEntry> raw_data, Settings settings) {
		this.settings = settings;
		DataEntry newDataEntry = new DataEntry(dataEntry.getHeaders());
		determineImportantWords(dataEntry, raw_data);
		List<DataAttribute> attributes = dataEntry.getDataAttributes();
		for (DataAttribute attribute : attributes) {
			DataAttribute newDataAttribute = attribute.clone();
			if (attribute.doAnonimize()) {
				String[] sentences = attribute.getData().split("\\.\\s");
				StringBuilder newData = new StringBuilder();
				for (String sentence : sentences) {
					String[] words = OpenNLPFactory.getTokenizer().tokenize(sentence);
					StringBuilder newSentence = new StringBuilder();
					for (String word : words) {
						if (isImportantWord(dataEntry, sentence, word))
							newSentence.append(hash(word)).append(" ");
						else
							newSentence.append(word).append(" ");
					}
					newData.append(newSentence.toString().trim()).append(".");
				}
				newDataAttribute.setData(newData.toString());
			}
			newDataEntry.addDataAttribute(newDataAttribute);

		}
		return newDataEntry;
	}

	private void determineImportantWords(DataEntry dataEntry, List<DataEntry> raw_data) {
		//Td -idf
		//td = term frequncy = number of ocurrences for all words
		Vocabulary voc = new Vocabulary();
		for (DataAttribute dataAttribute : dataEntry.getDataAttributes()) {
			voc.addData(dataAttribute);
			//Get the sentences
			String[] sentences = dataAttribute.getData().split("\\.\\s");
			for (String sentence : sentences) {
				String[] words = OpenNLPFactory.getTokenizer().tokenize(sentence);
				// The following rules are applied to determine if the current word is of general importance:
				// The word is the main thing of the sentence. This can be so for several reasons:
				// 1. The only word (notice, skip abbreviations like i.e. and a.s.a.p.)
				if (words.length == 1 && !dataAttribute.getDataType().equals(DataType.CLASS) && words[0].length() > 1) {
					importantWords.add(words[0]);
				}
				// 2. The word is a noun
				// Located in SmartHasing#isImportantWord()
				// 3. The word starts with a captial letter
				for (String word : words) {
					if (Character.isUpperCase(word.toCharArray()[0])) {
						importantWords.add(word);
					}
				}
			}
		}
		if ("true".equals(settings.getSettingsMap().get("gebruik_tfidf").getValue())) {
			int nr_of_terms = voc.getWordcountMap().size();
			for (Map.Entry<String, Integer> entry : voc.getWordcountMap().entrySet()) {
				String word = entry.getKey();
				//calculate td's
				double tf = (double) entry.getValue() / nr_of_terms;
				//calulate idf's
				double idf = Math.log((double) raw_data.size() / (1 + getNrOfDocumentsThatContains(word, raw_data))); /*nr of documents where the term occurs*/
				double tfidf = tf * idf;
				System.out.println("Tf-idf for " + word + " is: " + tfidf);
				if (tfidf < Double.parseDouble(settings.getSettingsMap().get("max_tfidf").getValue())) {
					importantWords.add(word);
					System.out.println("The word " + word + " is important enough because of frequency: " + tf + " * " + idf + " = " + tfidf);
				}
			}
		}
	}

	private int getNrOfDocumentsThatContains(String word, List<DataEntry> dataEntries) {
		int nr = 0;
		for (DataEntry dataEntry : dataEntries) {
			for (DataAttribute dataAttribute : dataEntry.getDataAttributes()) {
				if (dataAttribute.getData().contains(word)) {
					nr++;
					break;
				}
			}
		}
		return nr;
	}

	private boolean isImportantWord(DataEntry dataEntry, String sentence, String word) {
		boolean isImportant = importantWords.contains(word);
		String[] tagged = OpenNLPFactory.getPOSTagger().tag(new String[]{word});
		if (!isImportant && "true".equals(settings.getSettingsMap().get("anonimiseer_zelfstandige_naamwoorden").getValue())) {
//			boolean isImportant = "noun".equals(ODWNReader.getInstance().getWordType(word));
			isImportant = POS.NOUN.is(tagged[0]);
			if (isImportant) importantWords.add(word);
		}
		if (!isImportant && "true".equals(settings.getSettingsMap().get("anonimiseer_werkwoorden").getValue())) {
//			isImportant = "verb".equals(ODWNReader.getInstance().getWordType(word));
			isImportant = POS.VERB.is(tagged[0]);
			if (isImportant) importantWords.add(word);
		}
		if (!isImportant && "true".equals(settings.getSettingsMap().get("anonimiseer_werkwoorden").getValue())) {
			isImportant = NLPHelper.isDate(word);
			if (isImportant) importantWords.add(word);
		}
		return isImportant;
	}
}
