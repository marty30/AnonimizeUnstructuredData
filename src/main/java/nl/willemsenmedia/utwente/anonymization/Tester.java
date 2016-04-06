package nl.willemsenmedia.utwente.anonymization;

//import nl.willemsenmedia.utwente.anonymization.nlp.OpenDutchWordNet;
//import org.python.core.PyObject;

import nl.willemsenmedia.utwente.anonymization.data.DataModifier;

import javax.xml.bind.JAXBException;

/**
 * Created by Martijn on 14-3-2016.
 * <p>
 * Test class to bypass the gui
 */
public class Tester {
	private static String testdata = "nog een testje";

	public static void main(String[] args) throws JAXBException {
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


//		try {
//			long time0 = System.currentTimeMillis();
//			ODWNReader odwnReader = ODWNReader.getInstance();
//			long time1 = System.currentTimeMillis();
//			System.out.println("lemma: " + odwnReader.getWord("boom"));
//			long time2 = System.currentTimeMillis();
//			System.out.println("lemma_old: " + odwnReader.getWord_nonJAXB("boom"));
//			long time3 = System.currentTimeMillis();
//
//			System.out.println();
//			System.out.println("---------");
//			System.out.println("Tijd 1: " + (time1 - time0) + "+" + (time2 - time1) + "=" + (time2 - time0));
//			System.out.println("Tijd 2: " + (time3 - time2));
//
//		} catch (SAXPathException | XPathException e) {
//			e.printStackTrace();
//		}

//		String testdata = "Het gaat goed met Sjariefa. Ze zit op de VVE van de Wiltzangh,gaat daar straks ook naar het BaO.Ouders zijn tevreden. Behalve een verkoudheid is ze nooit ziek.Geen opvoedvragen van ouders.Sjariefa zou heel goed praten,maar zegt hier op CB geen woord! Verder wel ontw. cl,tekent alleen nog geen dichte cirkel. Gewichtscurve stijgt,S. krijgt veel zoete,mn zuiveldrankjes. Uitvoerig besproken,ook nav vraag van P. over het waarom van DM(bij M),en hypertensie bij fam. van P.UItgelegd dat bep. leefregels belangrijk zijn ter preventie.Ouders gaan nu meer water geven ipv Fristi etc.Sj. zit op tafel te spelen maar kijkt bij opdrachtjes schichtig naar P.Zit op haar tong te sabbelen en weigert te praten.M. zegt nog afspr. bij oogarts te hebben po 30-10,Landolt nu even uitgesteld. OOI jan. 2014. SaSt: M. is obv internist,gaat 2-wekelijks naar Zh.Prikt bloedsuikers en spuit 3x per dag insuline. Hefet begeleiding van diabetesverpleegkundige.Conc: Ouders hier beide betrokken,proberen S. te stimuleren,hebben geen hulpvraag.Zeggen: Op de VVE gaat het zo goed, en hier wil ze niet.Lijken geinteresserd in uitleg over de voeding.";
//		for (String word : testdata.split("\\s+")) {
//			System.out.println(word + " wordt " + DataModifier.getStem(word));
//		}
//
//		AnonymizationTechnique technique = new HashAll();
//		System.out.println(technique.doPreProcessing(new DataEntry(Lists.asList(new DataAttribute(DataType.UNSTRUCTURED, "Dit is de header!"), new DataAttribute[]{}), new DataAttribute(DataType.UNSTRUCTURED, "Dit is de tekst, p, *(&^&*hj sdiu!"), new DataAttribute(DataType.UNSTRUCTURED, "Dit is de tekst voor de Tweede keer, p, *(&^&*hj sdiu!")), Settings.getDefault()));
//		System.out.println(DataModifier.bin2base64("testje".getBytes()));
//		System.out.println(DataModifier.bin2hex("testje".getBytes()));
		System.out.println(DataModifier.getStem("opgetogen"));
	}
}
