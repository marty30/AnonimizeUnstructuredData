package nl.willemsenmedia.utwente.anonymization.dataminingtesters;

import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;

import java.io.File;
import java.io.IOException;

/**
 * Created by Martijn on 4-4-2016.
 */
public class ARFFCSVConverter {

	public static void convertArffToCsv(File in, File out) throws IOException {
		ArffLoader loader = new ArffLoader();
		loader.setSource(in);
		Instances data = loader.getDataSet();

		CSVSaver saver = new CSVSaver();
		saver.setInstances(data);
		saver.setFile(out);
		saver.writeBatch();
	}

	public static void convertCsvToArff(File in, File out) throws IOException {
		CSVLoader loader = new CSVLoader();
		loader.setSource(in);
		Instances data = loader.getDataSet();

		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(out);
		saver.writeBatch();
	}
}
