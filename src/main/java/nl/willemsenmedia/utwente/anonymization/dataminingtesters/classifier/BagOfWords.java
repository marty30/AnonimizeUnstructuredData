package nl.willemsenmedia.utwente.anonymization.dataminingtesters.classifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * MODEL OF A BAG OF WORDS
 *
 * @author Christian Versloot, University of Twente
 */


public class BagOfWords implements Protocol {

	/**
	 * Instance variables.
	 */
	private static HashMap<String, HashMap<String, Integer>> bagOfWords;
	private static HashMap<String, Double> statistics;
	private static ArrayList<String> thisDocument;

	/**
	 * Constructor.
	 */
	public BagOfWords() {
		bagOfWords = new HashMap<String, HashMap<String, Integer>>();
		statistics = new HashMap<String, Double>();
	}

	/**
	 * Return the bag of words.
	 */
	public static HashMap<String, HashMap<String, Integer>> getBag() {
		return bagOfWords;
	}

	/**
	 * Main.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("There are two ways to use this classifier.");
		System.out.println("1. Run class naivebayesclassifier.TestClassifier.java to create a confusion matrix based on test data.");
		System.out.println("2. Create a class yourself, initialise naivebayesclassifier.Classifier and execute .run(String texttoclassify) on it.");
		System.out.println("In either case, the classifier uses data whose locations are provided in naivebayesclassifier.Protocol.java");
	}

	/**
	 * Return the statistics
	 */
	public HashMap<String, Double> getStats() {
		return statistics;
	}

	/**
	 * Read a directory and put the amounts into the bag of words.
	 *
	 * @throws IOException
	 */
	public void fill(String directory, String typeFill) throws IOException {
		/* Check whether the directory is given */
		assert directory != null : "We can't read non-existent directories!";
		
		/* Read the folder */
		File folder = new File(directory);
		String temp = "";
		double documentsInClass = 0;
		double totaalAantalBestanden = 0;
		double status = 0.0;
		int statusProcess = 0;

		if (!typeFill.equals(FIRST_CLASS) && !typeFill.equals(SECOND_CLASS)) {
			System.out.println("Fill failed: we have no valid class");
		} else {
			/* Bereken het aantal bestanden */
			for (final File fileEntry : folder.listFiles()) {
				totaalAantalBestanden++;
			}
			for (final File fileEntry : folder.listFiles()) {
				documentsInClass++;
				  
				  /* Set process stats */
				status = Math.round((documentsInClass / totaalAantalBestanden) * 100.0) / 100.0;

				  /* Show process messages */
				if (status == 0.25 && statusProcess == 0) {
					System.out.println("... 25% of " + typeFill + " training data processed.");
					statusProcess++;
				} else if (status == 0.5 && statusProcess == 1) {
					System.out.println("... 50% of " + typeFill + " training data processed.");
					statusProcess++;
				} else if (status == 0.75 && statusProcess == 2) {
					System.out.println("... 75% of " + typeFill + " training data processed.");
					statusProcess++;
				} else if (status == 1.0 && statusProcess == 3) {
					System.out.println("... 100% of " + typeFill + " training data processed.");
					statusProcess++;
				}

				thisDocument = new ArrayList<String>();
				if (fileEntry.isDirectory()) {
					// Do nothing with directories!
				} else {
					if (fileEntry.isFile()) {
						temp = fileEntry.getName();
						if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("txt")) {

			        	  /* Lees het specifieke bestand in */
							Scanner sc = new Scanner(fileEntry.toPath());
			        	  
			        	  /* Loop door de specifieke file heen */
							while (sc.hasNext()) {
			        		  
			        		  /* Welk woord is het ? */
								String woord = sc.next();
			        		  
			        		  /* Kijk of het in dit document nog niet is gescand */
								if (!thisDocument.contains(woord)) {
				        		  /* Kijk of het woord al voorkomt in de bag met words */
									if (getBag().containsKey(woord)) {
										HashMap<String, Integer> subMap = getBag().get(woord);
										if (typeFill.equals(FIRST_CLASS)) {
											subMap.put(FIRST_CLASS, subMap.get(FIRST_CLASS) + 1);
											subMap.put(SECOND_CLASS, subMap.get(SECOND_CLASS));
										} else {
											subMap.put(FIRST_CLASS, subMap.get(FIRST_CLASS));
											subMap.put(SECOND_CLASS, subMap.get(SECOND_CLASS) + 1);
										}
										getBag().put(woord, subMap);
									} else { // Het woord toevoegen.
										// System.out.println(woord); // Even printen..
										HashMap<String, Integer> subMap = new HashMap<String, Integer>();
										if (typeFill.equals(FIRST_CLASS)) {
											subMap.put(FIRST_CLASS, 1);
											subMap.put(SECOND_CLASS, 0);
										} else {
											subMap.put(FIRST_CLASS, 0);
											subMap.put(SECOND_CLASS, 2);
										}
										getBag().put(woord, subMap);
									}
									thisDocument.add(woord);
								}

							}
							sc.close();
						}
					}

				}
			      
			      /* Fill the stats */
				if (typeFill.equals(FIRST_CLASS)) {
					statistics.put(GET_TOTAL_FIRST, documentsInClass * 1.0);
				} else {
					statistics.put(GET_TOTAL_SECOND, documentsInClass * 1.0);
				}

			}
		}


	}

}
