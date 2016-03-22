package nl.willemsenmedia.utwente.anonymization;

//import nl.willemsenmedia.utwente.anonymization.nlp.OpenDutchWordNet;
//import org.python.core.PyObject;

import nl.willemsenmedia.utwente.anonymization.nlp_java.ODWNReader;
import org.jaxen.saxpath.SAXPathException;

import javax.xml.xpath.XPathException;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * Test class to bypass the gui
 */
public class Tester {
	private static String testdata = "nog een testje";

	public static void main(String[] args) {
//		DataEntry dataEntry = new DataEntry(new DataAttribute(DataType.UNSTRUCTURED, testdata));
//
//		AnonymizationTechnique hashAll = new SmartHashing();
//		System.out.println(hashAll.anonymize(dataEntry));

//		// Create an instance of the PythonInterpreter
//		PythonInterpreter interp = new PythonInterpreter();
//
//		// The exec() method executes strings of code
//		interp.exec("import sys");
//		interp.exec("print sys");
//
//		// Set variable values within the PythonInterpreter instance
//		interp.set("a", new PyInteger(42));
//		interp.exec("print a");
//		interp.exec("x = 2+2");
//
//		// Obtain the value of an object from the PythonInterpreter and store it
//		// into a PyObject.
//		PyObject x = interp.get("x");
//		System.out.println("x: " + x);

		//JythonFactory
//		JythonObjectFactory jythonFactory = new JythonObjectFactory(TestPython.class, "nl.willemsenmedia.utwente.anonymization.nlp.TestPython", "nl.willemsenmedia.utwente.anonymization.nlp.testpythonpy");
//		System.out.println(((TestPython)jythonFactory.createObject()).getText());

//		OpenDutchWordNet wordNet = new OpenDutchWordNet();
//		PyObject result = wordNet.getWord("boom");
//		System.out.println(result);


		try {
			long time0 = System.currentTimeMillis();
			ODWNReader odwnReader = ODWNReader.getInstance();
			long time1 = System.currentTimeMillis();
			System.out.println("lemma: " + odwnReader.getWord("boom"));
			long time2 = System.currentTimeMillis();
			System.out.println("lemma_old: " + odwnReader.getWord_nonJAXB("boom"));
			long time3 = System.currentTimeMillis();

			System.out.println();
			System.out.println("---------");
			System.out.println("Tijd 1: " + (time1 - time0) + "+" + (time2 - time1) + "=" + (time2 - time0));
			System.out.println("Tijd 2: " + (time3 - time2));

		} catch (SAXPathException | XPathException e) {
			e.printStackTrace();
		}
	}
}
