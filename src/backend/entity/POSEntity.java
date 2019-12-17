package backend.entity;

public class POSEntity{
	
	private String phrase;
	private int count;
	private int n;
	private String[] words;

	public POSEntity(String phrase, int count, int n){
		this.phrase= phrase;
		this.count= count;
		this.n= n;
		words= phrase.split("_");
	}

	public String getPhrase(){
		return phrase;
	}

	public void setPhrase(String phrase){
		this.phrase= phrase;
	}

	public int getCount(){
		return count;
	}

	public void setCount(int count){
		this.count= count;
	}

	public int getN(){
		return n;
	}

	public void setN(int n){
		this.n= n;
	}

	public String[] getWords(){
		return words;
	}

	public void setWords(String[] words){
		this.words= words;
	}
	
	
}
