package nl.willemsenmedia.utwente.anonymization.nlp_java;

import jlibs.xml.DefaultNamespaceContext;
import jlibs.xml.sax.dog.DataType;
import jlibs.xml.sax.dog.NodeItem;
import jlibs.xml.sax.dog.XMLDog;
import jlibs.xml.sax.dog.XPathResults;
import jlibs.xml.sax.dog.expr.Expression;
import jlibs.xml.sax.dog.sniff.DOMBuilder;
import jlibs.xml.sax.dog.sniff.Event;
import org.jaxen.saxpath.SAXPathException;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathException;
import java.util.List;

/**
 * Created by Martijn on 21-3-2016.
 */
public class ODWNReader {

	public String getWord(String word) throws SAXPathException, XPathException {
		DefaultNamespaceContext nsContext = new DefaultNamespaceContext();

		XMLDog dog = new XMLDog(nsContext);
		Event event = dog.createEvent();
		XPathResults results = new XPathResults(event);
		event.setListener(results);
		event.setXMLBuilder(new DOMBuilder());
		Expression xpath1 = dog.addXPath("//LexicalEntry[@partOfSpeech=\"verb\"]/*");
		dog.sniff(event, new InputSource(this.getClass().getClassLoader().getResource("odwn_orbn_gwg-LMF_1.3.xml").getFile()));
		if (xpath1.resultType.equals(DataType.NODESET)) {
			List<NodeItem> list = (List<NodeItem>) results.getResult(xpath1);
			System.out.println(list.isEmpty() ? null : list.get(0).value);
		}
		return null;
	}
}
