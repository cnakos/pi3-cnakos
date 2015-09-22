import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

public class QuestionReader extends CollectionReader_ImplBase {
	/**
	 * This class is modeled after org.apache.uima.examples.cpe.FileSystemCollectionReader.java,
	 * especially where no alternative formulations seemed to provide any benefits. **/
	private static final String PARAM_INPUTDIR = "InputDirectory";
	private int mIndex = 0;
	private ArrayList<File> mFileList = new ArrayList<File>();

	public void initialize() throws ResourceInitializationException {
		File inputDirectory = new File(((String) getConfigParameterValue(PARAM_INPUTDIR)).trim());
		if (!inputDirectory.isDirectory()) {
			throw new ResourceInitializationException(ResourceConfigurationException.DIRECTORY_NOT_FOUND,
					new Object[] { PARAM_INPUTDIR, this.getMetaData().getName(), inputDirectory.getPath()});
		}
		for (File file : inputDirectory.listFiles())
			if (file.isFile())
				mFileList.add(file);
	}
	  
	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			throw new CollectionException(e);
		}
		
		File file = mFileList.get(mIndex);
		mIndex++;
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		try {
			byte[] rawText = new byte[(int) file.length()];
			inputStream.read(rawText);
			String text = new String(rawText);
			jcas.setDocumentText(text);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
		
		SourceDocumentInformation info = new SourceDocumentInformation(jcas);
		info.setUri(file.getAbsoluteFile().toURI().toString());
		info.setLastSegment(mIndex == mFileList.size());
		info.setDocumentSize((int) file.length());
		info.addToIndexes();
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return mIndex < mFileList.size();
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(mIndex, mFileList.size(), Progress.ENTITIES) };
	}

	@Override
	public void close() throws IOException {
	}

}
