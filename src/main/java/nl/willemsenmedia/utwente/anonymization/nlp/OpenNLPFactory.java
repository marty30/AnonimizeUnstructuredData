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

	private static POSTagger en_postagger;
	private static POSTagger nl_postagger;
	private static Tokenizer en_tokenizer;
	private static Tokenizer nl_tokenizer;

	public static POSTagger getPOSTagger() {

		try {
			if ("en".equals(System.getProperty("lang"))) {
				if (en_postagger == null)
					en_postagger = new POSTaggerME(new POSModel(OpenNLPFactory.class.getClassLoader().getResourceAsStream("openNLP_models/en-pos-maxent.bin")));
				return en_postagger;
			} else {
				if (nl_postagger == null)
					nl_postagger = new POSTaggerME(new POSModel(OpenNLPFactory.class.getClassLoader().getResourceAsStream("openNLP_models/nl-pos-maxent.bin")));
				return nl_postagger;
			}

		} catch (IOException e) {
			ErrorHandler.handleException(e);
			return null;
		}
	}

	public static Tokenizer getTokenizer() {
		try {
			if ("en".equals(System.getProperty("lang"))) {
				if (en_tokenizer == null)
					en_tokenizer = new TokenizerME(new TokenizerModel(OpenNLPFactory.class.getClassLoader().getResourceAsStream("openNLP_models/en-token.bin")));
				return en_tokenizer;
			} else {
				if (nl_tokenizer == null)
					nl_tokenizer = new TokenizerME(new TokenizerModel(OpenNLPFactory.class.getClassLoader().getResourceAsStream("openNLP_models/nl-token.bin")));
				return nl_tokenizer;
			}

		} catch (IOException e) {
			ErrorHandler.handleException(e);
			return null;
		}
	}
}
