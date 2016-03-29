package nl.willemsenmedia.utwente.anonymization.dataminingtesters.classifier;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Martijn on 29-3-2016.
 */
public class NaiveBayesClassifier implements Protocol {

	/**
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Christian Versloot, University of Twente
	 * We use the following confusion matrix to find out about our classifier's performance.
	 * See interface Protocol.java for the definitions of FIRST_CLASS and SECOND_CLASS.
	 * Run this Class to find out about whether the classifier works.
	 * Make sure to have the directories correct in Protocol.java!
	 * Make sure to have folders called "testData" in the folders given in Protocol.java filled with test data!
	 * This could be spam/ham, male/female, et cetera.
	 * Matrix:
	 * <p>
	 * ---------------------------------------------
	 * |        / pred| FIRST_CLASS | SECOND_CLASS |
	 * |   act /      |             |              |
	 * |--------------|-------------|--------------|
	 * | FIRST_CLASS  |      a      |       b      |
	 * |--------------|-------------|--------------|
	 * | SECOND_CLASS |      c      |       d      |
	 * ---------------------------------------------
	 */

	public static void main(String[] args) throws IOException, InterruptedException {

		/* Determine type of checker */
		String type = FIRST_CLASS;

		/* Variables of the matrix */
		int a = 0; // FIRST_CLASS x FIRST_CLASS
		int b = 0; // FIRST_CLASS x SECOND_CLASS
		int c = 0; // SECOND_CLASS x FIRST_CLASS
		int d = 0; // SECOND_CLASS x SECOND_CLASS

		/* Fill the bag of words */
		BagOfWords bag = new BagOfWords();
		System.out.println("---------- Starting to fill bag -------------");
		System.out.println("... Just a few moments, please.");
		bag.fill(LOC_FIRST, FIRST_CLASS);
		bag.fill(LOC_SECOND, SECOND_CLASS);
		System.out.println("---------- Fill of bag completed ------------");
		System.out.println("---------- Start classifying test data ------------");
		System.out.println("... Just a few moments, please.");

		/* Initialise our classifier */
		Classifier nbc = new Classifier();
		String temp = "";

		/* Classify all "first class" test data */
		File folder = new File(LOC_FIRST_TEST);
		try {
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					// Do nothing with directories!
				} else {
					if (fileEntry.isFile()) {
						temp = fileEntry.getName();
						if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("txt")) {

			        	  /* Lees het specifieke bestand in */
							Scanner sc = new Scanner(fileEntry.toPath());
							String text = "";
							while (sc.hasNext()) text += sc.nextLine() + "\n";
							sc.close();

			        	  /* Classify the document */
							HashMap<String, Double> classfResult = Classifier.classify(bag, text);

			        	  /* Give the verdict */
							String v = Classifier.verdict(classfResult.get(FIRST_CLASS), classfResult.get(SECOND_CLASS));

			        	  /* Fill the matrix! */
							if (type.equals(FIRST_CLASS) && v.equals(FIRST_CLASS)) { // ACT FIRST x PRED FIRST => a
								a++;
							} else if (type.equals(SECOND_CLASS) && v.equals(FIRST_CLASS)) { // ACT SECOND x PRED FIRST => c
								b++;
							} else if (type.equals(FIRST_CLASS) && v.equals(SECOND_CLASS)) { // ACT FIRST x PRED FIRST => b
								c++;
							} else { // ACT SECOND x PRED SECOND => d
								d++;
							}
						}
					}

				}


			}
		} catch (Exception e) {
		}

		/* Classify all "second class" test data */
		folder = new File(LOC_SECOND_TEST);
		type = SECOND_CLASS;

		try {
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					// Do nothing with directories!
				} else {
					if (fileEntry.isFile()) {
						temp = fileEntry.getName();
						if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("txt")) {

		        	  /* Lees het specifieke bestand in */
							Scanner sc = new Scanner(fileEntry.toPath());
							String text = "";
							while (sc.hasNext()) text += sc.nextLine() + "\n";
							sc.close();

		        	  /* Classify the document */
							HashMap<String, Double> classfResult = Classifier.classify(bag, text);

		        	  /* Give the verdict */
							String v = Classifier.verdict(classfResult.get(FIRST_CLASS), classfResult.get(SECOND_CLASS));

		        	  /* Fill the matrix! */
							if (type.equals(FIRST_CLASS) && v.equals(FIRST_CLASS)) { // ACT FIRST x PRED FIRST => a
								a++;
							} else if (type.equals(SECOND_CLASS) && v.equals(FIRST_CLASS)) { // ACT SECOND x PRED FIRST => c
								b++;
							} else if (type.equals(FIRST_CLASS) && v.equals(SECOND_CLASS)) { // ACT FIRST x PRED FIRST => b
								c++;
							} else { // ACT SECOND x PRED SECOND => d
								d++;
							}
						}
					}

				}


			}
		} catch (Exception e) {
		}

		/* Print the result */
		System.out.println("");
		System.out.println("Confusion matrix of n=" + (a + b + c + d) + " classifications.");
		System.out.println("---------------------------------------------");
		System.out.println("| act / pred   | FIRST_CLASS | SECOND_CLASS |");
		System.out.println("|--------------|-------------|--------------|");
		System.out.println("| FIRST_CLASS  |      " + a + "     |       " + b + "      |");
		System.out.println("|--------------|-------------|--------------|");
		System.out.println("| SECOND_CLASS |      " + c + "      |       " + d + "      |");
		System.out.println("---------------------------------------------");
		System.out.println("Accuracy of classifier = " + 100 * ((double) (a + d) / (a + b + c + d)) + "%");
		System.out.println("Error rate of classifier = " + 100 * ((double) (b + c) / (a + b + c + d)) + "%");
		System.out.println("Smoothing properties used: k=" + GET_SMOOTHER_K + "; V=" + GET_AMT_CLASSES);


	}

}
