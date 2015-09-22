package type;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;

public class InputDocumentAnnotator extends JCasAnnotator_ImplBase {
	private Pattern mFileNamePattern = Pattern.compile("(q\\d+)\\.txt$");
	private Pattern mQuestionPattern = Pattern.compile("Q (.*?)");
	private Pattern mAnswerPattern = Pattern.compile("A(\\d+)\\s([01])\\s(.*?)");	

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
		
		InputDocument inputDocument = new InputDocument(aJCas);
		inputDocument.setBegin(info.getBegin());
		inputDocument.setEnd(info.getEnd());
		inputDocument.setScore(1.0);
		inputDocument.setComponentId(this.getClass().getName());
		
		Question question = null;
		ArrayList<Answer> answers = new ArrayList<Answer>();
		
		Matcher qMatcher = mQuestionPattern.matcher(documentText);
		int position = 0;
		while (qMatcher.find(position)) {
			if (question == null) {
				String questionText = qMatcher.group(1);
				question = new Question(aJCas);
				
				question.setBegin(qMatcher.start());
				question.setEnd(qMatcher.end());
				
				question.setScore(1.0);
				question.setComponentId(this.getClass().getName());
				
				question.setSentence(questionText);
				question.setId(questionId);
				
				position = qMatcher.end();
			} else {
				// Multiple questions present.
				throw new AnalysisEngineProcessException();
			}
		}
		
		Matcher aMatcher = mAnswerPattern.matcher(documentText);
		position = 0;
		while (aMatcher.find(position)) {
			String answerId = aMatcher.group(1);
			boolean answerLabel = aMatcher.group(2).equals("1");
			String answerText = aMatcher.group(3);
			Answer answer = new Answer(aJCas);
			answer.setBegin(aMatcher.start());
			answer.setEnd(aMatcher.end());
			
			answer.setScore(1.0);
			answer.setComponentId(this.getClass().getName());
			
			answer.setSentence(answerText);
			answer.setId(answerId);
			answer.setLabel(answerLabel);
			
			answer.addToIndexes();
			position = aMatcher.end();
			answers.add(answer);
		}	
		
		question.addToIndexes();
		inputDocument.setQuestion(question);
		FSList answerList;
		if (answers.size() == 0) {
			answerList = new EmptyFSList(aJCas);
		} else {
			answerList = new NonEmptyFSList(aJCas);
			NonEmptyFSList head = (NonEmptyFSList) answerList;
			for (int i = 0; i < answers.size(); i++) {
				head.setHead(answers.get(i));
				if (i == answers.size() - 1) {
					head.setTail(new EmptyFSList(aJCas));
				} else {
					head.setTail(new NonEmptyFSList(aJCas));
					head = (NonEmptyFSList) head.getTail();
				}
			}
		}
		inputDocument.setAnswers(answerList);
		inputDocument.addToIndexes();
	}
}
