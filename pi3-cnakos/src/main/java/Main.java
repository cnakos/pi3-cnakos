import java.io.IOException;

import org.apache.uima.UIMAFramework;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.StatusCallbackListener;
import org.apache.uima.collection.metadata.CpeDescription;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

public class Main {

  /**
   * This method is the main program and entry point of your system for PI3. It runs a Collection
   * Processing Engine (CPE).
   * 
   * @param args
   */
  public static void main(String[] args) {
    // ### A guideline for implementing this method ###
    // 1. Accept integer n (1, 2, or 3) as a positional argument, specifying the length of n-grams.
    // 2. Initialize a CPE by loading your CPE descriptor at 'src/main/resources/cpeDescriptor.xml'.
    // 3. Pass the parameter n to your analysis engine(s) properly.
    // 4. Run the CPE.

    // Implement your code from here.
	  int n = Integer.parseInt(args[0]);
	  String inputDir = args[1];
	  String outputDir = args[2];
	  
	  try {
		  CpeDescription cpeDesc = UIMAFramework.getXMLParser().
			  	parseCpeDescription(new XMLInputSource("src/main/resources/cpeDescriptor.xml"));
		  CollectionProcessingEngine cpe = UIMAFramework.produceCollectionProcessingEngine(cpeDesc);
		  //cpe.addStatusCallbackListener(new StatusCallbackListener());
		  cpe.process();
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  }
}
