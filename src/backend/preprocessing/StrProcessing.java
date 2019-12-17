package backend.preprocessing;

public class StrProcessing{

	//Lower cases, removes all special char except .!?
	public static String clearAllExceptDotQE(String str) {
		str= str.toLowerCase();
		str= str.replaceAll("[^?!.a-zA-Z ]", "");
		while(str.contains("  "))	str= str.replaceAll("  ", " ");
		return str;
	}
	
	//Lower cases, removes all special char except ?
	public static String clearAllExceptDot(String str) {
		str= str.toLowerCase();
		str= str.replaceAll("[^.a-zA-Z ]", "");
		while(str.contains("  "))	str= str.replaceAll("  ", " ");
		return str;
	}
	
	//Lower cases, removes all special char
	public static String clearAll(String str) {
		str= str.toLowerCase();
		str= str.replaceAll("[^a-zA-Z ]", "");
		while(str.contains("  "))	str= str.replaceAll("  ", " ");
		return str;
	}
}
