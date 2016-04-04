package nl.willemsenmedia.utwente.anonymization.dataminingtesters.sentiment;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.core.Debug;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by Martijn on 4-4-2016.
 */
public class WekaNaiveBayesStart {
	public static void main(String[] args) throws Exception {
		//get file
		String filename = "C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\datasets\\SFU_Review_Corpus.arff";
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
		System.err.println(eval.fMeasure(1) + " " + eval.precision(1));
		System.out.println(eval.toMatrixString());
	}
}
