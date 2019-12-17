package backend.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSGenerator{

	public static void generateNPOS(String inputDir, String outputDir, int... ns) {
		List<String> inputLines= new ArrayList<>();
		
		try {
			BufferedReader myReader= new BufferedReader(new FileReader(inputDir));
			String myRead;
			
			while((myRead= myReader.readLine())!= null) {
				inputLines.add(StrProcessing.clearAll(myRead));
			}
			
			myReader.close();
		}catch (Exception e) {
			System.out.println("Error while loading file for Generation");
			e.printStackTrace();
		}
		
		MaxentTagger tagger= new MaxentTagger("models\\english-bidirectional-distsim.tagger");
		
		for(int en= 0; en< ns.length; en++) {
			int n= ns[en];
			Map<String, Integer> outputPos= new HashMap<>();
			
			for(String line: inputLines) {
				String[] taggedString= tagger.tagString(line).split(" ");
				for(int i= 0; i< taggedString.length- 1; i++) {
					String POSorder= taggedString[i].split("_")[1]+ "_"+ taggedString[i+ 1].split("_")[1];
					
					if(outputPos.containsKey(POSorder)) {
						outputPos.put(POSorder, outputPos.get(POSorder)+ 1);
					}
					else {
						outputPos.put(POSorder, 1);
					}
				}
			}
			
			try {
				BufferedWriter myWriter= new BufferedWriter(new FileWriter(outputDir+ "\\pos_"+ n+ ".txt"));
				
				for(Map.Entry<String, Integer> entry: outputPos.entrySet()) {
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
