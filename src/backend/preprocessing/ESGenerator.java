package backend.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class ESGenerator{

	public static void generateESFiles(String inputDir, String outputDir) {
		List<String> startWords= new ArrayList<>();
		List<String> endWords= new ArrayList<>();
//		String[] classesThatCantEndASentence= {"CC", "LS", "DT", "PRP$", "NNS", "PRP", "WRB", "IN", "CD", "WP", "VBZ", "MD", "UH", "WDT", "VBP", "VBD", "JJ"};
		String[] classesThatCantEndASentence= {"CC", "LS", "DT", "PRP$", "NNS", "PRP", "WRB", "IN", "CD", "WP", "VBZ", "MD", "UH", "WDT", "VBP", "VBD", "JJ"};
	

		MaxentTagger tagger= new MaxentTagger("models\\english-bidirectional-distsim.tagger");
		
		try {
			BufferedReader myReader= new BufferedReader(new FileReader(inputDir));
			String myRead;
			
			while((myRead= myReader.readLine())!= null) {
				
				myRead= StrProcessing.clearAllExceptDotQE(myRead);
				myRead= myRead.replaceAll("mrs.", "").replaceAll("mr.", "").replaceAll("((.)\\2{2})\\2+","").trim();
//				System.out.println(myRead);
//				System.out.println(myRead.contains("."));
//				System.out.println(myRead.split("\\.").length);
				for(int d= 0; d< myRead.split("\\.").length; d++) {

//					System.out.println(myRead.split("\\.")[d]);
					for(int q= 0; q< myRead.split("\\.")[d].trim().split("\\?").length; q++) {
						
//						System.out.println("\t"+ myRead.split("\\.")[d].split("\\?")[q]);
						for(int e= 0; e< myRead.split("\\.")[d].split("\\?")[q].trim().split("\\!").length; e++) {
							
							if(myRead.split("\\.")[d].split("\\?")[q].split("\\!")[e].trim().split(" ").length<= 3)	continue;
							
							String[] taggedSentence= tagger.tagString(myRead.split("\\.")[d].split("\\?")[q].split("\\!")[e].trim()).split(" ");
							startWords.add(taggedSentence[0]);
							
							boolean canEnd= true;
							if(taggedSentence[taggedSentence.length- 1].trim().length()== 0)	continue;
							for(String classThatCantEndSentence: classesThatCantEndASentence)
								if(classThatCantEndSentence.equals(taggedSentence[taggedSentence.length- 1].split("_")[1]))
									canEnd= false;
							if(canEnd)	endWords.add(taggedSentence[taggedSentence.length- 1]);
						}
					}
				}
			}
			
			myReader.close();
		}catch (Exception e) {
			System.out.println("Error in Generating the file");
			e.printStackTrace();
		}
		
//		System.out.println(startWords);
//		System.out.println(endWords);
		
		try {
			BufferedWriter myStartWriter= new BufferedWriter(new FileWriter(outputDir+ "\\start.txt"));
			BufferedWriter myEndWriter= new BufferedWriter(new FileWriter(outputDir+ "\\end.txt"));
			
			for(String start: startWords) {
				if(start.trim().length()== 0)	continue;
				myStartWriter.write(start);
				myStartWriter.newLine();
			}
			
			for(String end: endWords) {
				if(end.trim().length()== 0)	continue;
				myEndWriter.write(end);
				myEndWriter.newLine();
			}
			
			myStartWriter.close();
			myEndWriter.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void generateESFilesString(String input, String outputDir) {
		List<String> startWords= new ArrayList<>();
		List<String> endWords= new ArrayList<>();
//		String[] classesThatCantEndASentence= {"CC", "LS", "DT", "PRP$", "NNS", "PRP", "WRB", "IN", "CD", "WP", "VBZ", "MD", "UH", "WDT", "VBP", "VBD", "JJ"};
		String[] classesThatCantEndASentence= {"CC", "LS", "DT", "PRP$", "NNS", "PRP", "WRB", "IN", "CD", "WP", "VBZ", "MD", "UH", "WDT", "VBP", "VBD", "JJ"};
	

		MaxentTagger tagger= new MaxentTagger("models\\english-bidirectional-distsim.tagger");
		
		try {
			List<String> inputLines= new ArrayList<>(Arrays.asList(input.split("\n")));
			
			for(String myRead: inputLines) {
				
				myRead= StrProcessing.clearAllExceptDotQE(myRead);
				myRead= myRead.replaceAll("mrs.", "").replaceAll("mr.", "").replaceAll("((.)\\2{2})\\2+","").trim();
//				System.out.println(myRead);
//				System.out.println(myRead.contains("."));
//				System.out.println(myRead.split("\\.").length);
				for(int d= 0; d< myRead.split("\\.").length; d++) {

//					System.out.println(myRead.split("\\.")[d]);
					for(int q= 0; q< myRead.split("\\.")[d].trim().split("\\?").length; q++) {
						
//						System.out.println("\t"+ myRead.split("\\.")[d].split("\\?")[q]);
						for(int e= 0; e< myRead.split("\\.")[d].split("\\?")[q].trim().split("\\!").length; e++) {
							
							if(myRead.split("\\.")[d].split("\\?")[q].split("\\!")[e].trim().split(" ").length<= 3)	continue;
							
							String[] taggedSentence= tagger.tagString(myRead.split("\\.")[d].split("\\?")[q].split("\\!")[e].trim()).split(" ");
							startWords.add(taggedSentence[0]);
							
							boolean canEnd= true;
							if(taggedSentence[taggedSentence.length- 1].trim().length()== 0)	continue;
							for(String classThatCantEndSentence: classesThatCantEndASentence)
								if(classThatCantEndSentence.equals(taggedSentence[taggedSentence.length- 1].split("_")[1]))
									canEnd= false;
							if(canEnd)	endWords.add(taggedSentence[taggedSentence.length- 1]);
						}
					}
				}
			}
		}catch (Exception e) {
			System.out.println("Error in Generating the file");
			e.printStackTrace();
		}
		
//		System.out.println(startWords);
//		System.out.println(endWords);
		
		try {
			BufferedWriter myStartWriter= new BufferedWriter(new FileWriter(outputDir+ "\\start.txt"));
			BufferedWriter myEndWriter= new BufferedWriter(new FileWriter(outputDir+ "\\end.txt"));
			
			for(String start: startWords) {
				if(start.trim().length()== 0)	continue;
				myStartWriter.write(start);
				myStartWriter.newLine();
			}
			
			for(String end: endWords) {
				if(end.trim().length()== 0)	continue;
				myEndWriter.write(end);
				myEndWriter.newLine();
			}
			
			myStartWriter.close();
			myEndWriter.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
}

