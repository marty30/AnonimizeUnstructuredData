package nl.willemsenmedia.utwente.anonymization.dataminingtesters.svm;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Debug;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by Martijn on 18-4-2016.
 */
public class WekaSVM {
	public static void main(String[] args) throws Exception {
		//get file
		String filename = "C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\datasets\\SFU_Review_Corpus.arff";
//		String filename = "C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus_anonimous2.arff";
//		String filename = "C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus_preprocessed.arff";
		// read file
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
		Instances instances = new Instances(bufferedReader);
		instances.setClassIndex(instances.numAttributes() - 1);
		bufferedReader.close();

		//initialize svm classifier
		LibSVM svm = new LibSVM();
		svm.buildClassifier(instances);

		Evaluation eval = new Evaluation(instances);
		eval.crossValidateModel(svm, instances, 4, new Debug.Random(1));
		System.out.println(eval.toSummaryString("\nResults\n=====\n", true));
		System.out.println("F measure:" + eval.fMeasure(1) + " Precision: " + eval.precision(1));
		System.out.println(eval.toMatrixString());
	}
}
