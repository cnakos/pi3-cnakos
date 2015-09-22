import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;

import type.Answer;
import type.Question;

public class AnswerAnnotator extends JCasAnnotator_ImplBase {
	private Pattern mAnswerPattern = Pattern.compile("^A(\\d+)\\s([01])\\s(.*?)$");	

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		// Modeled after org.apache.uima.tutorial.ex1.RoomAnnotator.java.
		String documentText = aJCas.getDocumentText();
		Matcher matcher = mAnswerPattern.matcher(documentText);
		int position = 0;
		while (matcher.find(position)) {
			String answerId = matcher.group(1);
			boolean answerLabel = matcher.group(2).equals("1");
			String answerText = matcher.group(3);
			Answer answer = new Answer(aJCas);
			answer.setBegin(matcher.start());
			answer.setEnd(matcher.end());
			
			answer.setScore(1.0);
			answer.setComponentId(this.getClass().getName());
			
			answer.setSentence(answerText);
			answer.setId(answerId);
			answer.setLabel(answerLabel);
			
			answer.addToIndexes();
			position = matcher.end();
		}	
	}
}
