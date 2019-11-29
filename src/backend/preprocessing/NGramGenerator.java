package backend.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NGramGenerator{

	//	Generates files in the given location for as many grams as requested
	public static void generateNGramFile(String inputDir, String outputDir, int... ns) {
		List<String> inputLines= new ArrayList<>();
		
		try {
			BufferedReader myReader= new BufferedReader(new FileReader(inputDir));
			String myRead;
			
			while((myRead= myReader.readLine())!= null)	inputLines.add(StrProcessing.clearAllExceptDot(myRead));
			
			myReader.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		for(int en= 0; en< ns.length; en++) {
			int n= ns[en];
			Map<String, Integer> outputGrams= new HashMap<>();
			
			for(String inputLine: inputLines) {
				String[] inputChars= inputLine.split(" ");
				
				for(int i= 0; i< inputChars.length- (n- 1); i++) {
					String phrase= inputChars[i];
					for(int count= 1; count< n; count++)	phrase+= " "+ inputChars[i+ count];
					
					if(outputGrams.containsKey(phrase))	outputGrams.replace(phrase, outputGrams.get(phrase)+ 1);
					else	outputGrams.put(phrase, 1);
				}
			}
			
			try {
				BufferedWriter myWriter= new BufferedWriter(new FileWriter(outputDir+ n+ ".txt"));
				
				for(Map.Entry<String, Integer> entry: outputGrams.entrySet()) {
					myWriter.write(entry.getKey()+ "="+ entry.getValue());
					myWriter.newLine();
				}
				
				myWriter.close();
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
