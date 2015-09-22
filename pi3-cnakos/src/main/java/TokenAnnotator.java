import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.TOP;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import type.Answer;
import type.InputDocument;
import type.InputDocument_Type;
import type.Question;

public class TokenAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		TokenizerFactory factory = PTBTokenizerFactory.newTokenizerFactory();
	    AnnotationIndex<InputDocument> index = aJCas.getAnnotationIndex(InputDocument.class);
		FSIterator<InputDocument> iterator = index.iterator();
		while (iterator.hasNext()) {
			InputDocument doc = iterator.next();
			Question question = doc.getQuestion();
			FSList answerList = doc.getAnswers();
			ArrayList<Answer> answers = new ArrayList<Answer>();
			int i = 0;
			TOP answer = answerList.getNthElement(i);
			while (answer != null) {
				answers.add((Answer) answer);
				i++;
				answer = answerList.getNthElement(i);
			}
			
			String qSentence = question.getSentence();
			Tokenizer tokenizer = factory.getTokenizer(new StringReader(qSentence));
			for (Object rawToken : tokenizer.tokenize()) {
				rawToken = (String) rawToken;
				// TODO: Figure out best way to get span information from these tokens.
			}
		}
	}
}
