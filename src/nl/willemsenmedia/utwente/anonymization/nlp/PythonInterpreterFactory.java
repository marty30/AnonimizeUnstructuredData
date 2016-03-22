package nl.willemsenmedia.utwente.anonymization.nlp;

import org.python.util.PythonInterpreter;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Martijn on 21-3-2016.
 */
public class PythonInterpreterFactory {
	private static PythonInterpreter interpreter;
	private static PythonInterpreterFactory instance;
	private final String[] args;

	private PythonInterpreterFactory(String[] args) {
		this.args = args;
		PythonInterpreter.initialize(System.getProperties(), System.getProperties(), args);
		interpreter = new PythonInterpreter();
	}

	public static PythonInterpreterFactory getInstance(String[] args) {
		if (instance == null) {
			if (args == null) {
				args = new String[0];
			}
			instance = new PythonInterpreterFactory(args);
		} else if (args != null && match_arrays(args, instance.args)) {
			instance = new PythonInterpreterFactory(args);
		}
		return instance;
	}

	/**
	 * Checks if the sub array is a subset of the full array
	 *
	 * @param full_array aka haystack
	 * @param sub_array  aka needle
	 * @return true if the needle is in the haystack, otherwise false
	 */
	private static boolean match_arrays(String[] full_array, String[] sub_array) {
		//Some quick checks
		// If the sub_array is bigger, it will never be a subset of the full array
		if (sub_array.length > full_array.length) {
			return false;
		}
		//Make the full array a set
		HashSet<String> full_set = new HashSet<>(Arrays.asList(full_array));
		for (String item : sub_array) {
			if (!full_set.contains(item)) {
				return false;
			}
		}
		return true;
	}

	public static PythonInterpreter getInterpreter(String[] args) {
		if (interpreter == null)
			new PythonInterpreterFactory(args);
		return interpreter;
	}

	public static PythonInterpreter getInterpreter() {
		return getInterpreter(null);
	}
}
