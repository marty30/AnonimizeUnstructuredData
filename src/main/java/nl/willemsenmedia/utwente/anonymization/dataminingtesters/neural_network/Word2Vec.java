package nl.willemsenmedia.utwente.anonymization.dataminingtesters.neural_network;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataType;
import nl.willemsenmedia.utwente.anonymization.data.handling.AnonimizationController;
import nl.willemsenmedia.utwente.anonymization.data.handling.HashSentence;
import org.canova.api.records.reader.RecordReader;
import org.canova.api.records.reader.impl.CSVRecordReader;
import org.canova.api.split.FileSplit;
import org.deeplearning4j.datasets.canova.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.DataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.StringCleaning;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static nl.willemsenmedia.utwente.anonymization.data.handling.AnonimizationController.determineTechnique;

/**
 * Created by Martijn on 14-4-2016.
 */
public class Word2Vec {
	private static Logger log = LoggerFactory.getLogger(Word2Vec.class);

	public static void mainTxt(String[] args) throws Exception {
		// Config
		int max_words_for_testing = 100;
		File filePath_raw = new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus.csv");
		File filePath_anon = new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus_GeneralizeOrSuppress2.csv");
		AnonimizationController.setAnonyzationTechnique(new HashSentence());

		log.info("Load & Vectorize Sentences....");
		// Strip white space before and after for each line
		SentenceIterator iter_raw = new BasicLineIterator(filePath_raw);
		SentenceIterator iter_anon = new BasicLineIterator(filePath_anon);
		// Split on white spaces in the line to get words
		TokenizerFactory t = new DefaultTokenizerFactory();
		t.setTokenPreProcessor(new CommonPreprocessor());

		log.info("Building raw model....");
		org.deeplearning4j.models.word2vec.Word2Vec word2Vec_raw = new org.deeplearning4j.models.word2vec.Word2Vec.Builder()
				.minWordFrequency(5)
				.iterations(1)
				.layerSize(100)
				.seed(42)
				.windowSize(5)
				.iterate(iter_raw)
				.tokenizerFactory(t)
				.build();

		log.info("Fitting Raw Word2Vec model....");
		word2Vec_raw.fit();

		log.info("Building anonymous model....");
		org.deeplearning4j.models.word2vec.Word2Vec word2Vec_anon = new org.deeplearning4j.models.word2vec.Word2Vec.Builder()
				.minWordFrequency(5)
				.iterations(1)
				.layerSize(100)
				.seed(42)
				.windowSize(5)
				.iterate(iter_anon)
				.tokenizerFactory(t)
				.build();

		log.info("Fitting Anonymous Word2Vec model....");
		word2Vec_anon.fit();

		log.info("Writing word vectors to text file....");

		// Write word vectors (uncomment the next line to write the word-vector to a file)
//		WordVectorSerializer.writeWordVectors(word2Vec_raw, "pathToWriteto.txt");

		Iterator<VocabWord> iter = word2Vec_raw.getVocab().tokens().iterator();
		List<Double> equalities = new LinkedList<>();
		while (iter.hasNext()) {
			String word = iter.next().getWord();
			log.info("Closest Words for " + word + " / " + anonimizeString(word) + ":");
			Collection<String> lst_raw = word2Vec_raw.wordsNearest(word, 50);
			Collection<String> lst_anon = word2Vec_anon.wordsNearest(anonimizeString(word), 50);
			lst_anon.addAll(word2Vec_anon.wordsNearest(word, 50));
			Collection<String> raw_list_anonimized = lst_raw.stream().map(Word2Vec::anonimizeString).collect(Collectors.toList());
			raw_list_anonimized.addAll(lst_raw);
			//Now compare the two anonymous lists
			int the_same = 0;
			for (String item : raw_list_anonimized) {
				if (lst_anon.contains(item)) {
					the_same++;
				}
			}
			equalities.add((double) the_same / lst_anon.size());
			System.out.println("Raw list: " + lst_raw);
			System.out.println("Raw list anonimized: " + raw_list_anonimized);
			System.out.println("Anonymous list: " + lst_anon);
			System.out.println("Equality: " + ((double) the_same / lst_anon.size()));
			if (equalities.size() > max_words_for_testing && max_words_for_testing != -1)
				break;
		}
		System.out.println("Best average: " + equalities.stream().filter(d -> !d.isNaN()).mapToDouble(a -> a).max().orElse(-1));
		System.out.println("Worst average: " + equalities.stream().filter(d -> !d.isNaN()).mapToDouble(a -> a).min().orElse(-1));
		System.out.println("Total average of equalities: " + equalities.stream().filter(d -> !d.isNaN()).mapToDouble(a -> a).average().orElse(-1));
		System.out.println("There were " + equalities.stream().filter(d -> d.isNaN()).count() + " words where there was a problem in finding k-nearest neighbors in either the anonymous list or the regular list");
//		UiServer server = UiServer.getInstance();
//		System.out.println("Started on port " + server.getPort());
	}

	private static String anonimizeString(String word) {
		return StringCleaning.stripPunct(determineTechnique().anonymize(new DataEntry(null, new DataAttribute(DataType.UNSTRUCTURED, word)), null, null).getDataAttributes().get(0).getData()).toLowerCase();
	}

	public static void mainCsv(String[] args) throws Exception {

		//First: get the dataset using the record reader. CSVRecordReader handles loading/parsing
		int numLinesToSkip = 0;
		String delimiter = ",";
		RecordReader recordReader = new CSVRecordReader(numLinesToSkip, delimiter);
		recordReader.initialize(new FileSplit(new File(Word2Vec.class.getClassLoader().getResource("SFU_Review_Corpus.csv").getFile())));

		//Second: the RecordReaderDataSetIterator handles conversion to DataSet objects, ready for use in neural network
		int labelIndex = 4;     //5 values in each row of the iris.txt CSV: 4 input features followed by an integer label (class) index. Labels are the 5th value (index 4) in each row
		int numClasses = 3;     //3 classes (types of iris flowers) in the iris data set. Classes have integer values 0, 1 or 2
		int batchSize = 150;    //Iris data set: 150 examples total. We are loading all of them into one DataSet (not recommended for large data sets)
		DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);


		DataSet next = iterator.next();

		final int numInputs = 4;
		int outputNum = 3;
		int iterations = 1000;
		long seed = 6;


		log.info("Build model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(seed)
				.iterations(iterations)
				.learningRate(0.1)
				.regularization(true).l2(1e-4)
				.list(3)
				.layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(3)
						.activation("tanh")
						.weightInit(WeightInit.XAVIER)
						.build())
				.layer(1, new DenseLayer.Builder().nIn(3).nOut(3)
						.activation("tanh")
						.weightInit(WeightInit.XAVIER)
						.build())
				.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
						.weightInit(WeightInit.XAVIER)
						.activation("softmax")
						.nIn(3).nOut(outputNum).build())
				.backprop(true).pretrain(false)
				.build();

		//run the model
		MultiLayerNetwork model = new MultiLayerNetwork(conf);
		model.init();
		model.setListeners(new ScoreIterationListener(100));

		//Normalize the full data set. Our DataSet 'next' contains the full 150 examples
		next.normalizeZeroMeanZeroUnitVariance();
		next.shuffle();
		//split test and train
		SplitTestAndTrain testAndTrain = next.splitTestAndTrain(0.65);  //Use 65% of data for training

		DataSet trainingData = testAndTrain.getTrain();
		model.fit(trainingData);

		//evaluate the model on the test set
		Evaluation eval = new Evaluation(3);
		DataSet test = testAndTrain.getTest();
		INDArray output = model.output(test.getFeatureMatrix());
		eval.eval(test.getLabels(), output);
		log.info(eval.stats());
	}

	public static void main(String[] args) throws Exception {
		mainTxt(args);
	}
}
