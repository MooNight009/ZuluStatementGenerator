package backend.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import backend.entity.NGram;
import backend.entity.POSEntity;
import backend.utility.IO;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

//	2 adds the POS part as well
//	3 adds end and start
//	4 is for bug fixing
//	5 Finish sentences before appending stuff
public class Generator5{
	private static int wordSinceLastEnding= 0;

	public static String generateStatement(int charLimit, String givenWord, String inputDir) throws FileNotFoundException{
		List<String> statement= new ArrayList<>();
		wordSinceLastEnding= 0;
		for(String str: givenWord.split(" "))	statement.add(str.toLowerCase());

		List<NGram> nGrams= IO.readNGrams(inputDir);
		List<POSEntity> posList= IO.readPOS(inputDir);
		InputStream is= new FileInputStream(new File("models\\english-bidirectional-distsim.tagger"));
		MaxentTagger tagger= new MaxentTagger(is);
		List<String> startList= IO.readStart(inputDir);
		List<String> endList= IO.readEnd(inputDir);
		
		continueGeneration(nGrams, posList, charLimit, statement, tagger, startList, endList);

		String result= statement.get(0);
//		for (int i= 1; i< statement.size(); i++)
//			result+= " "+ statement.get(i);
//		
//		String[] taggedString= tagger.tagString(result).split(" ");
//		loop:
//		for(int i= 0; i< taggedString.length- 1; i++) {
//			if(endList.contains(taggedString[i])&& startList.contains(taggedString[i+ 1])) {
//				statement.set(i, statement.get(i)+ ".");
//			}
//		}
//		System.out.println(Arrays.toString(taggedString));
		
		result= statement.get(0);
		for (int i= 1; i< statement.size(); i++)
			result+= " "+ statement.get(i);
		
		return result;
	}

	private static boolean continueGeneration(List<NGram> nGrams, List<POSEntity> posList, int charLimit, List<String> statement, MaxentTagger tagger, List<String> startList, List<String> endList){
		Collections.shuffle(nGrams);
		// If we only have one word
		if (statement.size()== 1){
			if(!firstRun(nGrams, posList, statement, tagger, startList, endList))	return false;
		}
		// The rest
		else{
			List<NGram> potentialAdditions= new ArrayList<>();
			boolean canEnd= canEnd(statement, tagger, endList);
			boolean canStart= canStart(statement, tagger, startList);
			if(canEnd)	wordSinceLastEnding= 0;
			System.out.println(canStart+ "-"+ canEnd);
			
			if(!canStart) {
				System.out.println("Adding to the top");
				potentialAdditions= finishStart(nGrams, posList, charLimit, statement, tagger, startList, endList);
			}
			else if(!canEnd) {
				System.out.println("Adding to the bot");
				if(wordSinceLastEnding> 2) {
					Random r= new Random();
					if(r.nextInt(10)> wordSinceLastEnding)	potentialAdditions= finishEnd(nGrams, posList, charLimit, statement, tagger, startList, endList);
					else	System.out.println("We broke because too long");
				}
				else	potentialAdditions= finishEnd(nGrams, posList, charLimit, statement, tagger, startList, endList);
				
				wordSinceLastEnding++;
			}
			else {
				//	There is a chance it might stop here
				double threshhold= charLimit- getLength(statement);
				Random r= new Random();
				if(r.nextInt(charLimit)* 1.5> threshhold) {
					System.out.println("We stopped");
					return true;
				}
				
				//	If it fails to stop we keep going
				System.out.println("Adding randomly in both directions");
				potentialAdditions.addAll(finishStart(nGrams, posList, charLimit, statement, tagger, startList, endList));
				potentialAdditions.addAll(finishEnd(nGrams, posList, charLimit, statement, tagger, startList, endList));
			}
			System.out.println(potentialAdditions.size());
			
			
			if(potentialAdditions.size()== 0) {
				if(!canStart)	statement.set(0, "..."+ statement.get(0));
				if(!canEnd)	statement.set(statement.size()- 1, statement.get(statement.size()- 1)+ "...");
				return false;	//	If it can't anything else to the application
			}
			
			NGram pickedNGram= pickFromPool(potentialAdditions);	//	Pick the nGram from pool and start adding
			System.out.println("Picked phrase was "+ pickedNGram.getPhrase());
			System.out.println("Before adding "+ statement);
			
			boolean isStart= isStart(statement, pickedNGram);
			if(isStart) {
				String[] nGramWords= pickedNGram.getWords();
				int position= -1;
				for (int i= 0; i< nGramWords.length; i++)
					if (nGramWords[i].equals(statement.get(0)))
						position= i;
				
				for(int i= position- 1; i>= 0; i--)	statement.add(0, nGramWords[i]);
			}
			else {
				String[] nGramWords= pickedNGram.getWords();
				int position= -1;
				for (int i= 0; i< nGramWords.length; i++)
					if (nGramWords[i].equals(statement.get(statement.size()- 1))) {
						position= i;
						break;
					}
				
				for(int i= position+ 1; i< nGramWords.length; i++)	statement.add(nGramWords[i]);
			}
			
			System.out.println("After adding "+ statement);
			System.out.println("-----------------------");
		}
//		System.out.println(statement+ "-"+ canEnd(statement, tagger, endList));
//		System.out.println(statement+ "-"+ canStart(statement, tagger, endList));
		return continueGeneration(nGrams, posList, charLimit, statement, tagger, startList, endList);
	}
	
	//	This method adds to the start of the sentence
	private static List<NGram> finishStart(List<NGram> nGrams, List<POSEntity> posList, int charLimit, List<String> statement, MaxentTagger tagger, List<String> startList, List<String> endList) {
		List<NGram> potentialAdditions= new ArrayList<>();
		
		for (int i= 0; i< 3; i++){
			String phrase= statement.get(0);
			// We have enough words in the statement
			if (statement.size()- 1< i)
				continue;

			for (int j= 1; j<= i; j++)
				phrase+= " "+ statement.get(j);
			phrase= phrase.trim();
//			System.out.println("Start phrase is:\t"+ phrase);

			loopO:
			for (NGram nGram : nGrams) {
				if(nGram.getN()<= phrase.split(" ").length) continue;
				if (nGram.getPhrase().contains(phrase)&& !nGram.getPhrase().equals(phrase)&& !nGram.getWords()[0].equals(statement.get(0))) {
					for(int j= 0; j<= i; j++)	if(!nGram.getWords()[nGram.getWords().length- 1- j].equals(statement.get(i- j)))	continue loopO;	//	Make sure all the words match and not just contain
					
					if(isOverLimit(charLimit, statement, nGram.getPhrase().replace(phrase, "").length()))	continue;	//	Skip if it makes it go over char limit
					

					String expectedSentece= nGram.getPhrase();
					for(int t= i+ 1; t< statement.size(); t++) {
						expectedSentece+= " "+ statement.get(t);
					}
					String[] taggedString= tagger.tagString(expectedSentece).split(" ");
					
					
					if(canStart(statement, tagger, endList)) {	//	If this is a potential start make sure the other word is a potential ending
						String addedWords= expectedSentece.substring(0, expectedSentece.indexOf(phrase));
						int n= addedWords.trim().split(" ").length- 1;
						if(!endList.contains(taggedString[n]))	continue;
					}
					
					//	We make sure this is grammatically correct
					int posInstances= 0;
					loop:
					for(POSEntity posEntity: posList) {
						if(posEntity.getN()< nGram.getN()+ 1&& posEntity.getN()>= statement.size())	continue;
						for(int n= 0; n< posEntity.getN(); n++) {
							if(!taggedString[n].split("_")[1].equals(posEntity.getWords()[n]))	continue loop;
						}
						posInstances++;
						if(posInstances!= 0)	break;
					}
					if(posInstances== 0)	continue;	//	Skip if it doesn't grammatically make sense
					
					//	Add to list
					potentialAdditions.add(nGram);
				}
				if(potentialAdditions.size()> 250)	return potentialAdditions;
			}
		}
		
		return potentialAdditions;
	}
	

	//	This method adds to the end of the sentence
	private static List<NGram> finishEnd(List<NGram> nGrams, List<POSEntity> posList, int charLimit, List<String> statement, MaxentTagger tagger, List<String> startList, List<String> endList) {
		List<NGram> potentialAdditions= new ArrayList<>();
		
		for (int i= statement.size()- 1; i> statement.size()- 4; i--){
			if(i< 0)	continue;
			
			String phrase= "";
			
			for (int j= i; j<= statement.size()- 1; j++)
				phrase+= " "+ statement.get(j);
			phrase= phrase.trim();
//			System.out.println("End phrase is:\t"+ phrase);

			loopO:
			for (NGram nGram : nGrams) {
				if(nGram.getN()<= phrase.split(" ").length) continue;
				if (nGram.getPhrase().contains(phrase)&& !nGram.getPhrase().equals(phrase)&& !nGram.getWords()[nGram.getWords().length- 1].equals(statement.get(statement.size()- 1))) {
					for(int j= i; j< statement.size(); j++)	if(!nGram.getWords()[j- i].equals(statement.get(j)))	continue loopO;	// Make sure it's not containing but also matching
					
					if(isOverLimit(charLimit, statement, nGram.getPhrase().replace(phrase, "").length()))	continue;
					
					String expectedSentece= "";
					for(int t= 0; t< statement.size(); t++) {
						expectedSentece+= statement.get(t)+ " ";
					}
					expectedSentece= expectedSentece.replace(phrase, nGram.getPhrase());
//					String[] nGramWords= nGram.getWords();
//					for(int t= 0; t< nGramWords.length; t++) {
//						if(!expectedSentece.contains(nGramWords[t]))
//							expectedSentece+= nGramWords[t]+ " ";
//					}
					String[] taggedString= tagger.tagString(expectedSentece.trim()).split(" ");
					
					
					if(canEnd(statement, tagger, endList)) {	//	If this is a potential end make sure the other word is a potential start
						String addedWords= expectedSentece.substring(0, expectedSentece.indexOf(phrase)+ phrase.length());
						int n= addedWords.trim().split(" ").length;
						try {
						if(!startList.contains(taggedString[n]))	continue;
						}
						catch (Exception e) {
							System.out.println(nGram.getPhrase());
							System.out.println(addedWords);
							System.out.println(expectedSentece);
							System.out.println(phrase);
							e.printStackTrace();
							return null;
						}
					}
					
					//	Here we make sure this is grammatically correct
					int posInstances= 0;
					loop:
					for(POSEntity posEntity: posList) {
						if(posEntity.getN()< nGram.getN()+ 1&& posEntity.getN()>= statement.size())	continue;
						if(posEntity.getWords().length!= posEntity.getN())	System.out.println(posEntity.getN()+ "-"+ posEntity.getWords().length);
						for(int n= 1; n<= posEntity.getN(); n++) {
							if(!taggedString[taggedString.length- n].split("_")[1].equals(posEntity.getWords()[posEntity.getWords().length- n]))	continue loop;
						}
						posInstances++;
						if(posInstances!= 0)	break;
					}
					if(posInstances== 0)	continue;
					//	Finish POS part
					
					
					potentialAdditions.add(nGram);
				}
				if(potentialAdditions.size()> 250)	return potentialAdditions;
			}
		}
		
		return potentialAdditions;
	}
	
	private static boolean firstRun(List<NGram> nGrams, List<POSEntity> posList, List<String> statement, MaxentTagger tagger, List<String> startList, List<String> endList){
		String phrase= statement.get(0);
		// Get all the possible nGrams
		List<NGram> potentialAdditions= new ArrayList<>();
		for (NGram nGram : nGrams)
			if (nGram.getPhrase().contains(phrase)) {
//				System.out.println(potentialAdditions.size());
//				System.out.println("0-"+ nGram.getPhrase());
				boolean wordAvailable= false;
				for(String word: nGram.getWords()) {
					if(word.equals(phrase))	wordAvailable= true;
				}
				if(!wordAvailable)	continue;
//				System.out.println("1-"+ nGram.getPhrase());
				String[] taggedString= tagger.tagString(nGram.getPhrase()).split(" ");
				
				loop:
				for(POSEntity posEntity: posList) {
					if(posEntity.getN()!= nGram.getN()) {
//						System.out.println(posEntity.getPhrase()+ "-"+ posEntity.getN());
//						System.out.println(nGram.getN()+ "-"+ posList.size());
//						System.out.println("---------------------");
						continue;
					}
					for(int n= 0; n< posEntity.getN(); n++) {
						if(!taggedString[n].split("_")[1].equals(posEntity.getWords()[n]))	continue loop;
					}
				}
				
				potentialAdditions.add(nGram);
			}
		
		if(potentialAdditions.size()== 0) {
			return false;
		}
		
		// Pick an nGram and add it to the statement list
		
		NGram pickedNGram= pickFromPool(potentialAdditions);
		String[] nGramWords= pickedNGram.getWords();
		
		int position= -1;
		for (int i= 0; i< nGramWords.length; i++)
			if (nGramWords[i].equals(phrase))
				position= i;

		// Add words before
		for (int i= 0; i< position; i++){
			int wordPosition= getWordPositionInList(phrase, statement);
			statement.add(wordPosition, nGramWords[i]);
		}
		// Add words after

		for (int i= nGramWords.length- 1; i> position; i--){
			int wordPosition= getWordPositionInList(phrase, statement);
			statement.add(wordPosition+ 1, nGramWords[i]);
		}
		
		return true;
	}
	
	//	Returns true if we can end here
	private static boolean canEnd(List<String> statement, MaxentTagger tagger, List<String> endList) {
		String result= statement.get(0);
		for (int i= 1; i< statement.size(); i++)
			result+= " "+ statement.get(i);
		
		String[] taggedSentence= tagger.tagString(result).split(" ");
		return endList.contains(taggedSentence[taggedSentence.length- 1]);
	}
	
	//	Returns true if this can be a start
	private static boolean canStart(List<String> statement, MaxentTagger tagger, List<String> startList) {
		String result= statement.get(0);
		for (int i= 1; i< statement.size(); i++)
			result+= " "+ statement.get(i);
		
		String[] taggedSentence= tagger.tagString(result).split(" ");
		return startList.contains(taggedSentence[0]);
	}

	//	Returns true if this should be added to the start and false if it should be added to the end
	private static boolean isStart(List<String> statement, NGram nGram) {
		String[] wordsInNGram= nGram.getWords();
		if(statement.contains(wordsInNGram[0]))	return false;
		else	return true;
	}
	
	// This method returns the position of the given word in the list
	private static int getWordPositionInList(String word, List<String> statement){
		for (int i= 0; i< statement.size(); i++)
			if (statement.get(i).equals(word))
				return i;
		return -1;
	}

	// This method picks a random NGram from the pool
	private static NGram pickFromPool(List<NGram> nGrams){
		List<NGram> pool= new ArrayList<>();

		for (NGram nGram : nGrams)
			for (int i= 0; i< nGram.getCount(); i++)
				for(int j= 0; j< nGram.getN(); j++)
				pool.add(nGram);

		Random r= new Random();

		return pool.get(r.nextInt(pool.size()));
	}
	
	// This method checks if the total statement strings are over the limit
	private static boolean isOverLimit(int charLimit, List<String> statement, int extraLength){
		String result= statement.get(0);
		for (int i= 1; i< statement.size(); i++)
			result+= " "+ statement.get(i);
		return result.length()+ extraLength+ 1> charLimit;
	}
	
	//	This method returns length of the current statemnt
	private static double getLength(List<String> statement) {
		double length= 0;
		
		for(String word: statement)	length+= word.length();
		
		return length;
	}
}
