package nl.willemsenmedia.utwente.anonymization.dataminingtesters.classifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;


/**
 * NAIVE BAYES CLASSIFIER
 *
 * @author Christian Versloot, University of Twente
 */

public class Classifier implements Protocol {

	/**
	 * Main.
	 *
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("There are two ways to use this classifier.");
		System.out.println("1. Run class naivebayesclassifier.TestClassifier.java to create a confusion matrix based on test data.");
		System.out.println("2. Create a class yourself, initialise naivebayesclassifier.Classifier and execute .run(String texttoclassify) on it.");
		System.out.println("In either case, the classifier uses data whose locations are provided in naivebayesclassifier.Protocol.java");
	}

	/**
	 * Run a classification based on a certain text.
	 *
	 * @throws IOException
	 */
	public static void run(String classfText) throws IOException {
		BagOfWords bag = new BagOfWords();
		bag.fill(LOC_FIRST, FIRST_CLASS);
		bag.fill(LOC_SECOND, SECOND_CLASS);
		HashMap<String, Double> classfResult = classify(bag, classfText);
		System.out.println("********************************************");
		System.out.println("CLASSIFICATION STATISTICS");
		System.out.println("********************************************");
		System.out.println("Classification text=" + classfText);
		System.out.println("Probability of " + FIRST_CLASS + "=" + classfResult.get(FIRST_CLASS));
		System.out.println("Probability of " + SECOND_CLASS + "=" + classfResult.get(SECOND_CLASS));
		System.out.println("Classified as " + verdict(classfResult.get(FIRST_CLASS), classfResult.get(SECOND_CLASS)));
	}

	/**
	 * Classify a certain text using a bag of words.
	 *
	 * @param bag   - the bag of words
	 * @param tekst - the text that has to be classified
	 * @return - a hashmap with probabilities for first/second class
	 */
	public static HashMap<String, Double> classify(BagOfWords bag, String tekst) {
		HashMap<String, Double> tempResult = new HashMap<String, Double>();
		Double tempProb = 1.0;
		
		/* Calculate prior probabilities */
		double pFst = bag.getStats().get(GET_TOTAL_FIRST) / (bag.getStats().get(GET_TOTAL_FIRST) + bag.getStats().get(GET_TOTAL_SECOND));
		double pSnd = bag.getStats().get(GET_TOTAL_SECOND) / (bag.getStats().get(GET_TOTAL_FIRST) + bag.getStats().get(GET_TOTAL_SECOND));
			
		/* Calculate first class probabilities */
		Scanner sc = new Scanner(tekst);
		double pClass = 1.0;
		while (sc.hasNext()) {
			String woord = sc.next();
			try {
				pClass *= ((BagOfWords.getBag().get(woord).get(FIRST_CLASS) + GET_SMOOTHER_K) / (bag.getStats().get(GET_TOTAL_FIRST) + GET_SMOOTHER_K * GET_AMT_CLASSES));
			} catch (NullPointerException e) {
				pClass *= (GET_SMOOTHER_K) / (GET_SMOOTHER_K * GET_AMT_CLASSES);
			}
		}

		pClass *= pFst;

		tempResult.put(FIRST_CLASS, pClass);
		
		/* Calculate second class probabilities */
		sc = new Scanner(tekst);
		pClass = 1.0;
		while (sc.hasNext()) {
			String woord = sc.next();
			try {
				pClass *= ((BagOfWords.getBag().get(woord).get(SECOND_CLASS) + GET_SMOOTHER_K) / (bag.getStats().get(GET_TOTAL_SECOND) + GET_SMOOTHER_K * GET_AMT_CLASSES));
			} catch (NullPointerException e) {
				pClass *= GET_SMOOTHER_K / (GET_SMOOTHER_K * GET_AMT_CLASSES);
			}
		}

		pClass *= pSnd;

		tempResult.put(SECOND_CLASS, pClass);

		sc.close();
		return tempResult;
	}

	/**
	 * Determine whether it's class one, or class two.
	 *
	 * @param pFST
	 * @param pSND
	 * @return
	 */
	public static String verdict(double pFST, double pSND) {
		if (pFST > pSND) {
			return FIRST_CLASS;
		} else {
			return SECOND_CLASS;
		}
	}

}
