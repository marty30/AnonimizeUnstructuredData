package nl.willemsenmedia.utwente.anonymization.nlp;

import com.sun.istack.internal.NotNull;
import jlibs.xml.DefaultNamespaceContext;
import jlibs.xml.sax.dog.DataType;
import jlibs.xml.sax.dog.NodeItem;
import jlibs.xml.sax.dog.XMLDog;
import jlibs.xml.sax.dog.XPathResults;
import jlibs.xml.sax.dog.expr.Expression;
import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;
import nl.willemsenmedia.utwente.anonymization.gui.PopupManager;
import nl.willemsenmedia.utwente.anonymization.nlp.xml_objects.Lemma;
import nl.willemsenmedia.utwente.anonymization.nlp.xml_objects.LexicalEntry;
import nl.willemsenmedia.utwente.anonymization.nlp.xml_objects.LexicalResource;
import nl.willemsenmedia.utwente.anonymization.nlp.xml_objects.WordForm;
import org.jaxen.saxpath.SAXPathException;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.xpath.XPathException;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Martijn on 21-3-2016.
 */
public class ODWNReader {
	private static ODWNReader instance;
	private final LexicalResource lexicalResource;
	private final HashMap<String, HashMap<String, Object>> discovered;

	private ODWNReader() throws JAXBException {
		File file = new File(this.getClass().getClassLoader().getResource("odwn_orbn_gwg-LMF_1.3.xml").getFile());
		JAXBContext jaxbContext = JAXBContext.newInstance(LexicalResource.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		lexicalResource = (LexicalResource) jaxbUnmarshaller.unmarshal(file);

		// Create a hashmap that uses the word as a key and has a list of hashmaps as value where some details are added
		discovered = new HashMap<>();
	}

	public static ODWNReader getInstance() {
		if (instance == null)
			try {
				instance = new ODWNReader();
			} catch (JAXBException e) {
				ErrorHandler.handleException(e);
			}
		return instance;
	}

	public String getWord_nonJAXB(String word) throws SAXPathException, XPathException {
		DefaultNamespaceContext nsContext = new DefaultNamespaceContext();

		XMLDog dog = new XMLDog(nsContext);
		Expression xpath1 = dog.addXPath("//LexicalEntry[@partOfSpeech=\"verb\"]/*");
		XPathResults results = dog.sniff(new InputSource(this.getClass().getClassLoader().getResource("odwn_orbn_gwg-LMF_1.3.xml").getFile()));
		if (xpath1.resultType.equals(DataType.NODESET)) {
			List<NodeItem> list = (List<NodeItem>) results.getResult(xpath1);
			list.get(0).printTo(System.err);
			return (list.isEmpty() ? null : list.get(0).toString());
		}
		return null;
	}

	private void addToHashmap(String word, LexicalEntry le, boolean limitToLemmas) {
		HashMap<String, Object> leAsMap = new HashMap<>();
		leAsMap.put("PartOfSpeech", le.getPartOfSpeech());
		leAsMap.put("Lemma", le.getLemma());
		leAsMap.put("LexicalEntry", le);
		discovered.put(word, leAsMap);
	}

	public LexicalEntry getLexicalEntry(@NotNull String word) {
		return getLexicalEntry(word, false);
	}

	public LexicalEntry getLexicalEntry(@NotNull String word, boolean limitToLemmas) {
		if (word == null) {
			return null;
		}
		//Search the word in the discovered map
		HashMap<String, Object> discoveredItem = discovered.get(word);
		if (discoveredItem != null) {
			// The word is found in the map, now we need to check if the search was limited to lemmas, and if it was, the map should have the lamma available
			if (!limitToLemmas || discoveredItem.get("Lemma") != null)
				return (LexicalEntry) discoveredItem.get("LexicalEntry");
		}
		//The word was not yet discovered, so search the wordnet
		for (LexicalEntry le : lexicalResource.getLexicon().getLexicalEntry()) {
			// If there are any null-objects, continue searching, but do mention it.
			try {
				if (le == null)
					// If the entry is null, there is nothing to find, so continue
					continue;
				else if (le.getLemma() == null) {
					// If the lemma is null, search in the wordforms
					// But only if it is not limited to lemmas
					if (limitToLemmas) {
						continue;
					}
					if (le.getWordForms() == null) {
						// There are no wordforms either, so search the multiword excpression
						if (le.getMultiwordExpression() != null) {
							if (le.getMultiwordExpression().getWrittenForm().contains(word)) {
								addToHashmap(word, le, limitToLemmas);
								return le;
							}
						}
					} else { // WordForms are available
						for (WordForm wordForm : le.getWordForms().getWordForm()) {
							if (word.equals(wordForm.getWrittenForm())) {
								addToHashmap(word, le, limitToLemmas);
								return le;
							}
						}
					}
				} else {
					if (word.equals(le.getLemma().getWrittenForm())) {
						addToHashmap(word, le, limitToLemmas);
						return le;
					}
				}
			} catch (NullPointerException e) {
				//What to do with NPE's...
				//Compile message
				String message = "Dit was de data:";
				message += "\nLexicalEntry: " + le;
				if (le != null) {
					message += "\nLemma: " + le.getLemma();
					if (le.getLemma() != null) {
						message += "\nWrittenForm" + le.getLemma().getWrittenForm();
					}
				}
				PopupManager.error(e.getMessage(), "NPE opgetreden tijdens het zoeken naar " + word + ".", message, e);
				e.printStackTrace();
			}
		}
		return null;
	}

	public Lemma getLemma(String word) {
		return getLexicalEntry(word).getLemma();
	}

	public String getWord(String word) {
		return getLexicalEntry(word).getLemma().getWrittenForm();
	}

	public String getWordType(String word) {
		LexicalEntry le = getLexicalEntry(word);
		if (le != null)
			return le.getPartOfSpeech();
		else
			return null;
	}
}
