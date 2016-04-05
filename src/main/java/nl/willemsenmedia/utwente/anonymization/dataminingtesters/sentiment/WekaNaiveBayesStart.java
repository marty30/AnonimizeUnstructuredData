package nl.willemsenmedia.utwente.anonymization.dataminingtesters.sentiment;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Martijn on 4-4-2016.
 */
public class WekaNaiveBayesStart {
	private final AbstractClassifier classifier;
	private Instances dataRaw;

	public WekaNaiveBayesStart(File trainingsdata) throws Exception {
		ArffLoader loader = new ArffLoader();
		loader.setFile(trainingsdata);
		classifier = new NaiveBayesMultinomialText();
		this.dataRaw = loader.getDataSet();
		classifier.buildClassifier(dataRaw);
	}

	public WekaNaiveBayesStart(String model_filename) throws Exception {
		this.classifier = (NaiveBayesMultinomialText) weka.core.SerializationHelper.read(model_filename);
	}

	public WekaNaiveBayesStart(Instances dataRaw) throws Exception {
		classifier = new NaiveBayesMultinomialText();
		this.dataRaw = dataRaw;
		dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
		classifier.buildClassifier(dataRaw);
	}

	public static void main(String[] args) throws Exception {
		//get file
//		String filename = "C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\datasets\\SFU_Review_Corpus.arff";
		String filename = "C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus_anonimous2.arff";
		// read file
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
		Instances instances = new Instances(bufferedReader);
		instances.setClassIndex(instances.numAttributes() - 1);
		bufferedReader.close();

		// Create the classifier
		NaiveBayesMultinomialText naiveBayes = new NaiveBayesMultinomialText();
		naiveBayes.buildClassifier(instances);
		Evaluation eval = new Evaluation(instances);
		eval.crossValidateModel(naiveBayes, instances, 10, new Debug.Random(1));
		System.out.println(eval.toSummaryString("\nResults\n=====\n", true));
		System.out.println("F measure:"+eval.fMeasure(1) + " Precision: " + eval.precision(1));
		System.out.println(eval.toMatrixString());
		System.out.println("---------------------------------------------------------");
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(filename));
		Instances[] traintest = splitDataset(loader.getDataSet(), 75);
		WekaNaiveBayesStart clasif = new WekaNaiveBayesStart(traintest[0]);
		weka.classifiers.evaluation.Evaluation evalu = clasif.test(traintest[1]);
		evalu.toMatrixString();
	}

	public static Instances[] splitDataset(Instances inst, int percent) {
		int trainSize = Math.round(inst.numInstances() * percent / 100);
		int testSize = inst.numInstances() - trainSize;
		Instances train = new Instances(inst, 0, trainSize);
		Instances test = new Instances(inst, trainSize, testSize);
		return new Instances[]{train, test};
	}

	public weka.classifiers.evaluation.Evaluation test(File testdata) throws Exception {
		ArffLoader loader = new ArffLoader();
		loader.setFile(testdata);
		return test(loader.getDataSet());
	}

	public weka.classifiers.evaluation.Evaluation test(Instances testdata) throws Exception {
		testdata.setClassIndex(testdata.numAttributes()-1);
		weka.classifiers.evaluation.Evaluation eTest = new weka.classifiers.evaluation.Evaluation(dataRaw);
		eTest.evaluateModel(classifier, testdata);
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);
		return eTest;
	}

	public void showInstances() {
		System.out.println(dataRaw);
	}

	public Instances getDataRaw() {
		return dataRaw;
	}

	public void saveModel(String filename) throws Exception {
		weka.core.SerializationHelper.write(filename, classifier);
	}
}
