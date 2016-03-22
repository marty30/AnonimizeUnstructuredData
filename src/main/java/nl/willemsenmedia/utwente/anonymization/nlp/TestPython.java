//package nl.willemsenmedia.utwente.anonymization.nlp;
//
//import org.python.core.PyFunction;
//import org.python.core.PyInteger;
//import org.python.util.PythonInterpreter;
//
///**
// * Created by Martijn on 21-3-2016.
// */
//public class TestPython {
//	private final PythonInterpreter intepreter;
//	private final PyFunction py_square;
//
//	public TestPython() {
//		// Get interpreter
//		intepreter = PythonInterpreterFactory.getInterpreter(null);
//
//		intepreter.execfile(getClass().getResourceAsStream("testpythonpy.py"));
//		py_square = (PyFunction) intepreter.get("square");
//	}
//
//	public int square(int val) {
//		return py_square.__call__(new PyInteger(val)).asInt();
//	}
//}
