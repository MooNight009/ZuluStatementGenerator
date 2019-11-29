import backend.preprocessing.NGramGenerator;
import backend.preprocessing.StrProcessing;

public class mainTester{
	public static void main(String[] args){
//		System.out.println(StrProcessing.clearAllExceptDot("hello , what are you. doing?"));
		
		NGramGenerator.generateNGramFile("D:\\ZuluStatementGenerator\\input.txt", "D:\\ZuluStatementGenerator\\gram", 3, 2, 4);
	}
}
