package nl.willemsenmedia.utwente.anonymization.dataminingtesters;

import org.apache.commons.lang.StringEscapeUtils;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToString;

import java.io.File;
import java.io.IOException;

/**
 * Created by Martijn on 4-4-2016.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class ARFFCSVConverter {

	public static void convertArffToCsv(File in, File out) throws IOException {
		ArffLoader loader = new ArffLoader();
		loader.setSource(in);
		Instances data = loader.getDataSet();

		CSVSaver saver = new MyCSVSaver();
		saver.setInstances(data);
		saver.setFile(out);
		saver.writeBatch();
	}

	public static void convertCsvToArff(File in, File out) throws Exception {
		CSVLoader loader = new CSVLoader();
		loader.setSource(in);
		Instances data = loader.getDataSet();

		ArffSaver saver = new ArffSaver();
		//TODO bepalen of er een string van gemaakt moet worden.
//		data.get(0).numAttributes()
		NominalToString filter1 = new NominalToString();
		filter1.setOptions(new String[]{"-C", "1"});
		filter1.listOptions();
		filter1.setInputFormat(data);
		data = Filter.useFilter(data, filter1);
		saver.setInstances(data);
		saver.setFile(out);
		saver.writeBatch();
	}

//	public static void main(String[] args) throws IOException {
//		File in = new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\datasets\\SFU_Review_Corpus.arff");
//		File out = new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus.csv");
//		if (!out.exists())
//			out.createNewFile();
//		convertArffToCsv(in, out);
//	}

	public static void main(String[] args) throws Exception {
		File in = new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\export_2016-04-05_204422.465.csv");
		File out = new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus_preprocessed.arff");
		if (!out.exists())
			out.createNewFile();
		convertCsvToArff(in, out);
	}

	private static class MyCSVSaver extends CSVSaver {
		@Override
		protected String instanceToString(Instance inst) {
			StringBuffer result;
			Instance outInst;
			int i;
			String field;

			result = new StringBuffer();

			if (inst instanceof SparseInstance) {
				outInst = new DenseInstance(inst.weight(), inst.toDoubleArray());
				outInst.setDataset(inst.dataset());
			} else {
				outInst = inst;
			}

			for (i = 0; i < outInst.numAttributes(); i++) {
				if (i > 0) {
					result.append(m_FieldSeparator);
				}

				if (outInst.isMissing(i)) {
					field = m_MissingValue;
				} else {
					field = outInst.toString(i, m_MaxDecimalPlaces);
				}

				// make sure that custom field separators, like ";" get quoted correctly
				// as well (but only for single character field separators)
				if (field.startsWith("'")) {
					field = field.substring(1);
				}
				if (field.endsWith("'")) {
					field = field.substring(0, field.length() - 1);
				}
				field = field.replace("\r\n", "");
				field = StringEscapeUtils.escapeCsv(field);

				result.append(field);
			}

			return result.toString();
		}
	}
}
