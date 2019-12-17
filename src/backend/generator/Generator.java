package backend.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import backend.entity.NGram;
import backend.utility.IO;

public class Generator{

	public static String generateStatement(int charLimit, String givenWord, String inputDir){
		List<String> statement= new ArrayList<>();
		for(String str: givenWord.split(" "))	statement.add(str.toLowerCase());

		List<NGram> nGrams= IO.readNGrams(inputDir);

		continueGeneration(nGrams, charLimit, statement);

		String result= statement.get(0);
		for (int i= 1; i< statement.size(); i++)
			result+= " "+ statement.get(i);
		return result;
	}

	private static boolean continueGeneration(List<NGram> nGrams, int charLimit, List<String> statement){
		
		// The initial part
		if (statement.size()== 1){
			String phrase= statement.get(0);

			// Get all the possible nGrams
			List<NGram> potentialAdditions= new ArrayList<>();
			for (NGram nGram : nGrams)
				if (nGram.getPhrase().contains(phrase))
					potentialAdditions.add(nGram);
			
			if(potentialAdditions.size()== 0)	return false;
			
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
		}
		// The rest
		else{
			List<NGram> potentialAdditions= new ArrayList<>();
			// The start portion
			for (int i= 0; i< 3; i++){
				String phrase= statement.get(0);
				// We have enough words in the statement
				if (statement.size()- 1< i)
					continue;

				for (int j= 1; j<= i; j++)
					phrase+= " "+ statement.get(j);
				phrase= phrase.trim();
//				System.out.println("Start phrase is:\t"+ phrase);

				for (NGram nGram : nGrams)
					if (nGram.getPhrase().contains(phrase)&& nGram.getWords()[nGram.getWords().length- 1].equals(statement.get(i))&& !nGram.getPhrase().equals(phrase)) {
						String nGramPhrase= nGram.getPhrase();
						if(!isOverLimit(charLimit, statement, nGramPhrase.replace(phrase, "").length()))
						potentialAdditions.add(nGram);
					}
			}
			// The end portion
			for (int i= statement.size()- 1; i> statement.size()- 4; i--){
				if(i< 0)	continue;
				
				String phrase= "";
				
				for (int j= i; j<= statement.size()- 1; j++)
					phrase+= " "+ statement.get(j);
				phrase= phrase.trim();
//				System.out.println("End phrase is:\t"+ phrase);
				
				for (NGram nGram : nGrams)
					if (nGram.getPhrase().contains(phrase)&& nGram.getWords()[0].equals(statement.get(i))&& !nGram.getPhrase().equals(phrase)) {
						String nGramPhrase= nGram.getPhrase();
						if(!isOverLimit(charLimit, statement, nGramPhrase.replace(phrase, "").length()))
						potentialAdditions.add(nGram);
					}
			}
			
			if(potentialAdditions.size()== 0)	return false;
			
			NGram pickedNGram= pickFromPool(potentialAdditions);
			
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
		}
		return continueGeneration(nGrams, charLimit, statement);
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
	private static boolean isOverLimit(int charLimit, List<String> statement){
		String result= statement.get(0);
		for (int i= 1; i< statement.size(); i++)
			result+= " "+ statement.get(i);
		return result.length()> charLimit;
	}
	
	// This method checks if the total statement strings are over the limit
	private static boolean isOverLimit(int charLimit, List<String> statement, int extraLength){
		String result= statement.get(0);
		for (int i= 1; i< statement.size(); i++)
			result+= " "+ statement.get(i);
		return result.length()+ extraLength+ 1> charLimit;
	}
}
