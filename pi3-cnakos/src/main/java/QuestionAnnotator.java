import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import type.Question;

public class QuestionAnnotator extends JCasAnnotator_ImplBase {

	private Pattern mQuestionPattern = Pattern.compile("^Q (.*?)$");
	private Pattern mFileNamePattern = Pattern.compile("(q\\d+)\\.txt$");
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Modeled after org.apache.uima.tutorial.ex1.RoomAnnotator.java.
		String documentText = aJCas.getDocumentText();
		SourceDocumentInformation info;
		try {
			info = aJCas.getAnnotationIndex(SourceDocumentInformation.class).iterator().next();
		} catch (NoSuchElementException e) {
			throw new AnalysisEngineProcessException();
		}
		Matcher fileNameMatcher = mFileNamePattern.matcher(info.getUri());
		String questionId = "NULL";
		int fileNamePosition = 0;
		while (fileNameMatcher.find(fileNamePosition)) {
			questionId = fileNameMatcher.group(1);
			fileNamePosition = fileNameMatcher.end();
		}
		
		Matcher matcher = mQuestionPattern.matcher(documentText);
		int position = 0;
		while (matcher.find(position)) {
			String questionText = matcher.group(1);
			Question question = new Question(aJCas);
			
			question.setBegin(matcher.start());
			question.setEnd(matcher.end());
			
			question.setScore(1.0);
			question.setComponentId(this.getClass().getName());
			
			question.setSentence(questionText);
			question.setId(questionId);
			
			question.addToIndexes();
			position = matcher.end();
		}	
	}

}
