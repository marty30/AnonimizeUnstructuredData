package nl.willemsenmedia.utwente.anonymization.dataminingtesters.svm;

import weka.classifiers.functions.LibSVM;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SvmClassifierWeka {

	public static void save(LibSVM model, String path) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(path);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(model);
		out.close();
	}

	public static LibSVM load(String path) {
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			System.out.println("Try to load model... from " + path);
			long startTime = System.currentTimeMillis();
			Object obj = in.readObject();
			System.out.print("Load model done. "
					+ (System.currentTimeMillis() - startTime) / 1000 + "s");
			in.close();
			fileIn.close();
			if (obj instanceof LibSVM) {
				System.out.println("Using model from: " + path);
				return (LibSVM) obj;
			} else
				return null;
		} catch (FileNotFoundException e1) {
			System.err.println("Warning: File not found, retrain model...");
			return null;
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found, retrain model...");
			return null;
		} catch (IOException e) {
			System.err.println("Can't read object. retrain model...");
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		// System.setOut(new PrintStream("./svm.txt"));
		ConverterUtils.DataSource source = new ConverterUtils.DataSource("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\test.libsvm");
//				"experiment/abstract/2class_full.arff");
		// System.out.println(source.getStructure());
		Instances data = source.getDataSet();
//		if (data.classIndex() == -1)
			data.setClassIndex(0);
		System.out.println(data.classIndex());

		LibSVM svmClassifier = null;// = SvmClassifier.load("./svm.model");

		 String[] options = new String[1];
		 options[0] = "-V";
		if (svmClassifier == null) {
			svmClassifier = new LibSVM();
			svmClassifier.setSVMType(new SelectedTag(LibSVM.SVMTYPE_NU_SVC, LibSVM.TAGS_SVMTYPE));
//			svmClassifier.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_SIGMOID, LibSVM.TAGS_KERNELTYPE));
//			svmClassifier.setOptions(options);
			System.out.println("start training... ");
			svmClassifier.buildClassifier(data);
			SvmClassifierWeka.save(svmClassifier, "./svm.model");
		}

		ConverterUtils.DataSource test = new ConverterUtils.DataSource("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\test.libsvm");
//				"./experiment/abstract/2class_full.arff");
		Instances testData = test.getDataSet();
		if (testData.classIndex() == -1)
			testData.setClassIndex(testData.numAttributes() - 1);
		int total = 0;
		int right = 0;
		for (int i = 0; i < testData.numInstances(); i++) {
			Instance instance = testData.instance(i);
			System.out.print(testData.classAttribute().value(
					(int) instance.classValue())
					+ " -- ");
			double result = svmClassifier.classifyInstance(instance);
			System.out.print(result + " --- ");
			System.out.println(testData.classAttribute().value((int) result));
			if (Double.compare(result, instance.classValue()) == 0)
				right++;
			total++;
		}

		System.out.println("accuracy = " + right / (double) total);
	}

}