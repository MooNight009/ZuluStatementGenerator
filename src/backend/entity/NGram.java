package backend.entity;

public class NGram{

	private String phrase;
	private int count;
	private int n;
	private String[] words;

	public NGram(String phrase, int count, int n){
		this.phrase= phrase;
		this.count= count;
		this.n= n;
		words= phrase.split(" ");
	}

	public int getN(){
		return n;
	}

	public void setN(int n){
		this.n= n;
	}

	public String getPhrase(){
		return phrase;
	}

	public void setPhrase(String words){
		this.phrase= words;
	}

	public int getCount(){
		return count;
	}

	public void setCount(int count){
		this.count= count;
	}

	public String[] getWords(){
		return words;
	}

	public void setWords(String[] words){
		this.words= words;
	}
	
	

}
