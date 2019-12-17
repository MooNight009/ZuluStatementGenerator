import java.io.IOException;

import backend.generator.Generator4;
import backend.generator.Generator5;
import backend.preprocessing.ESGenerator;
import backend.preprocessing.NGramGenerator;
import backend.preprocessing.POSGenerator;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class mainTester{
	public static void main(String[] args) throws IOException{
		// System.out.println(StrProcessing.clearAllExceptDot("hello , what are you.
		// doing?"));

//		 NGramGenerator.generateNGramFile("D:\\ZuluStatementGenerator\\input.txt", "D:\\ZuluStatementGenerator\\", 2, 3, 4);
//		 POSGenerator.generateNPOS("D:\\ZuluStatementGenerator\\input.txt", "D:\\ZuluStatementGenerator\\", 2, 3, 4, 5, 6);
//		ESGenerator.generateESFiles("D:\\ZuluStatementGenerator\\input.txt", "D:\\ZuluStatementGenerator\\");
		
		String result;
		for(int i= 0; i< 2; i++) {
			result= Generator5.generateStatement(255, "he", "D:\\ZuluStatementGenerator\\");
			System.out.println(result);
		}
		
		
//		MaxentTagger tagger= new MaxentTagger("models\\english-bidirectional-distsim.tagger");
//		System.out.println(tagger.tagString(result));
	}
}
