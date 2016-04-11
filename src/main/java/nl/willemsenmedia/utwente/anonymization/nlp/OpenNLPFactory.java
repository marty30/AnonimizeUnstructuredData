package nl.willemsenmedia.utwente.anonymization.nlp;

import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.IOException;

/**
 * Created by Martijn on 5-4-2016.
 */
public class OpenNLPFactory {

	private static ThreadLocal<POSTagger> en_postagger;
	private static ThreadLocal<POSTagger> nl_postagger;
	private static ThreadLocal<Tokenizer> en_tokenizer;
	private static ThreadLocal<Tokenizer> nl_tokenizer;

	public static POSTagger getPOSTagger() {
		if ("en".equals(System.getProperty("lang"))) {
			if (en_postagger == null)
				en_postagger = new ThreadLocal<POSTagger>() {
					protected POSTagger initialValue() {
						try {
							return new POSTaggerME(new POSModel(OpenNLPFactory.class.getClassLoader().getResourceAsStream("openNLP_models/en-pos-maxent.bin")));
						} catch (IOException e) {
							ErrorHandler.handleException(e);
							return null;
						}
					}
				};
			return en_postagger.get();
		} else {
			if (nl_postagger == null)
				nl_postagger = new ThreadLocal<POSTagger>() {
					protected POSTagger initialValue() {
						try {
							return new POSTaggerME(new POSModel(OpenNLPFactory.class.getClassLoader().getResourceAsStream("openNLP_models/nl-pos-maxent.bin")));
						} catch (IOException e) {
							ErrorHandler.handleException(e);
							return null;
						}
					}
				};
			return nl_postagger.get();
		}

	}

	public static Tokenizer getTokenizer() {
		if ("en".equals(System.getProperty("lang"))) {
			if (en_tokenizer == null)
				en_tokenizer = new ThreadLocal<Tokenizer>() {
					protected Tokenizer initialValue() {
						try {
							return new TokenizerME(new TokenizerModel(OpenNLPFactory.class.getClassLoader().getResourceAsStream("openNLP_models/en-token.bin")));
						} catch (IOException e) {
							ErrorHandler.handleException(e);
							return null;
						}
					}
				};
			return en_tokenizer.get();
		} else {
			if (nl_tokenizer == null)
				nl_tokenizer = new ThreadLocal<Tokenizer>() {
					protected Tokenizer initialValue() {
						try {
							return new TokenizerME(new TokenizerModel(OpenNLPFactory.class.getClassLoader().getResourceAsStream("openNLP_models/nl-token.bin")));
						} catch (IOException e) {
							ErrorHandler.handleException(e);
							return null;
						}
					}
				};
			return nl_tokenizer.get();
		}

	}
}
