package nl.willemsenmedia.utwente.anonymization.nlp_java;

import jlibs.xml.DefaultNamespaceContext;
import jlibs.xml.sax.dog.DataType;
import jlibs.xml.sax.dog.NodeItem;
import jlibs.xml.sax.dog.XMLDog;
import jlibs.xml.sax.dog.XPathResults;
import jlibs.xml.sax.dog.expr.Expression;
import nl.willemsenmedia.utwente.anonymization.nlp_java.xml_objects.Lemma;
import nl.willemsenmedia.utwente.anonymization.nlp_java.xml_objects.LexicalEntry;
import nl.willemsenmedia.utwente.anonymization.nlp_java.xml_objects.LexicalResource;
import org.jaxen.saxpath.SAXPathException;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.xpath.XPathException;
import java.io.File;
import java.util.List;

/**
 * Created by Martijn on 21-3-2016.
 */
public class ODWNReader {
	private static ODWNReader instance;
	private final LexicalResource lexicalResource;

	private ODWNReader() throws JAXBException {
		File file = new File(this.getClass().getClassLoader().getResource("odwn_orbn_gwg-LMF_1.3.xml").getFile());
		JAXBContext jaxbContext = JAXBContext.newInstance(LexicalResource.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		lexicalResource = (LexicalResource) jaxbUnmarshaller.unmarshal(file);
	}

	public static ODWNReader getInstance() throws JAXBException {
		if (instance == null)
			instance = new ODWNReader();
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

	public LexicalEntry getLexicalEntry(String word) {
		for (LexicalEntry le : lexicalResource.getLexicon().getLexicalEntry()) {
			if (le.getLemma().getWrittenForm().equals(word)) {
				return le;
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
}
