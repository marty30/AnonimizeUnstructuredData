package nl.willemsenmedia.utwente.anonymization.dataminingtesters.classifier;

/**
 * PROTOCOL INTERFACE
 *
 * @author Christian Versloot, University of Twente
 */
public interface Protocol {

	String FIRST_CLASS = "Ham";
	String SECOND_CLASS = "Spam";
	double FIRST_CLASS_NUM = 0.0;
	double SECOND_CLASS_NUM = 1.0;
	String GET_TOTAL_FIRST = FIRST_CLASS + "Total";
	String GET_TOTAL_SECOND = SECOND_CLASS + "Total";
	int GET_AMT_CLASSES = 2; // V as used in Laplacian Smoothing
	double GET_SMOOTHER_K = 1;
	String LOC_FIRST = "C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\Naive-Bayesian-Classifier-master\\spamtrain\\ham";
	String LOC_SECOND = "C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\Naive-Bayesian-Classifier-master\\spamtrain\\spam";
	String LOC_FIRST_TEST = "C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\Naive-Bayesian-Classifier-master\\spamtest\\ham";
	String LOC_SECOND_TEST = "C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\Naive-Bayesian-Classifier-master\\spamtest\\spam";
	/* Ready specifying data? Then run through running naivebayesclassifier.TestClassifier.java! */
}
