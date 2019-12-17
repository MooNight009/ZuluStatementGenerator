package pos;

//import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTester{
	public static void main(String[] args){
//		MaxentTagger tagger= new MaxentTagger("models/english-left3words-distsim.tagger");
//		
//		System.out.println(tagger.tagString("Humanitarion"));
		String s= ".........";
		System.out.println(s.replaceAll("((.)\\2{2})\\2+",""));
		System.out.println(s);
		System.out.println(s);
	}
}
