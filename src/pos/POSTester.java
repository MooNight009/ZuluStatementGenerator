package pos;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTester{
	public static void main(String[] args){
		MaxentTagger tagger= new MaxentTagger("models/english-left3words-distsim.tagger");
		
		System.out.println(tagger.tagString("Humanitarion"));
	}
}
