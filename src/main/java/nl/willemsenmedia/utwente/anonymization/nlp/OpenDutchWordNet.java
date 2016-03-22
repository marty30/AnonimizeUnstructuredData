//package nl.willemsenmedia.utwente.anonymization.nlp;
//
//import org.python.core.PyFunction;
//import org.python.core.PyObject;
//import org.python.core.PyString;
//import org.python.util.PythonInterpreter;
//
///**
// * Created by Martijn on 20-3-2016.
// */
//public class OpenDutchWordNet {
//	private final PythonInterpreter intepreter;
//	private final PyObject parser;
//
//	public OpenDutchWordNet() {
//		// Get interpreter
//		intepreter = PythonInterpreterFactory.getInterpreter(null);
//
//		// Read all functions from the file
//		String current_dir = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
//		intepreter.execfile("OpenDutchWordNetReader/__init__.py");
//		intepreter.execfile(getClass().getResourceAsStream("OpenDutchWordNetReader.py"));
//		PyFunction init = (PyFunction) intepreter.get("init");
//		parser = init.__call__();
//	}
//
//	public PyObject getWord(String word) {
//		return intepreter.get("getWord").__call__(parser, new PyString(word));
//	}
//}
