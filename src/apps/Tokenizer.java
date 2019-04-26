package apps;
import java.util.ArrayList;
import java.util.List;
import static apps.Constants.REGEXVAR;
import static apps.Constants.REGEXNUMBER;
import static apps.Constants.REGEXUNARYOPERATOR;
import static apps.Constants.REGEXBINARYOPERATOR;
import static apps.Constants.REGEXPARENTHESES;
/**
 * 
 * Tokenizer.java
 * 
 * Given a equation as a stirng, validates the individual elements of the equation
 * (e.g. Numbers, Operators, etc.) are considered valid for the regex strings 'regexVar',
 * 'regexInteger', 'regexFloat', 'regexSciPostFix', 'regexNumber', 'regexUnaryOperator',
 * 'regexBinaryOperator', and 'regexParentheses'.
 * 
 * @author Mathias Ham
 * @author Micheal Walburn
 * @author Morgan Patterson
 * 
 */
public class Tokenizer {	
	/**
	 * Validates a given set of substrings to see if they are all individually valid.
	 * Does not validate the given string as a whole.
	 * 
	 * 
	 * @param tokens - An array of tokenized substrings from the generateTokens
	 * function
	 * @return A boolean determining if the all the elements of the array are valid.
	 */
	public static boolean validateTokens(String[] tokens){
		for (int i=0; i<tokens.length; i++){
			if(tokens[i].matches(REGEXNUMBER)|| tokens[i].matches(REGEXUNARYOPERATOR)|| tokens[i].matches(REGEXBINARYOPERATOR)
			|| tokens[i].matches(REGEXPARENTHESES)|| tokens[i].matches(REGEXVAR)){
				//System.out.printf("Token %d: %s Is Valid\n",i, tokens[i]);
			}
			else{
				//System.out.printf("Token %d: %s Is NOT VALID\n", i, tokens[i]);
				return false;
			}
		}
	 return true;
	}	
	
	/**
	 * Converts a given string into an array of substrings.
	 * 
	 * @param s - A String
	 * @return A Type String[] array of tokenized substrings
	 */	
	public static String[] tokenize(String s){
		List<String> tokens = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for(int i =0; i < s.length(); i++){
			//Add any char that is not an operator, e, or = to a String
			if(Character.toString(s.charAt(i)).matches("[^+\\-\\*\\/\\(\\)\\%e=><!]")){			
				builder.append(s.charAt(i)); 				
			}
			else if(Character.toString(s.charAt(i)).matches("([+\\-\\*\\/\\(\\)\\%e=]|[><!])")){
				try{
					if(s.charAt(i)=='e'&& (s.substring(i-1,i).matches("[a-zA-Z_]")|| s.substring(i+1,i+2).matches("[a-zA-Z_]"))){
						builder.append(s.charAt(i)); 
						continue;
					}
				}
				catch(Exception e){
					builder.append(s.charAt(i)); 
					continue;
				}
				addToken(tokens, builder);	
				/*Checks for ** and // to group together as 1 token */
				if((Character.toString(s.charAt(i)).matches("[\\*]") && Character.toString(s.charAt(i+1)).matches("[\\*]"))
						||Character.toString(s.charAt(i)).matches("[\\/]") && Character.toString(s.charAt(i+1)).matches("[\\/]")){
					char temp1 = s.charAt(i);
					char temp2 = s.charAt(i+1);				
					String temp3 = Character.toString(temp1) + Character.toString(temp2);				
					tokens.add(temp3.trim());
					i++; //increment i to avoid checking the second * or / since both are added to the same token
					continue;
				}
				if((Character.toString(s.charAt(i)).matches("[\\=]") && Character.toString(s.charAt(i+1)).matches("[\\=]"))
						||Character.toString(s.charAt(i)).matches("[\\!]") && Character.toString(s.charAt(i+1)).matches("[\\=]") ){
					char temp1 = s.charAt(i);
					char temp2 = s.charAt(i+1);				
					String temp3 = Character.toString(temp1) + Character.toString(temp2);				
					tokens.add(temp3.trim());
					i++; //increment i to avoid checking the second = since both are added to the same token
					continue;
				}
				/*Looks for extened amount of +'s and -'s (ex: 2++++---+-+-3) and reduces it down to a single + or - based on the number of -'s */
				else if((Character.toString(s.charAt(i)).matches("[+]")) ||(Character.toString(s.charAt(i)).matches("[\\-]") )){
					int minusCount = 0;
					try{
						while(s.charAt(i) == '+' || s.charAt(i)=='-'|| s.charAt(i)==' '){
							if(s.charAt(i)=='-'){
								minusCount++;
								i++;
							}
							else{
								i++;
							}
						}
						
					}
					catch(IndexOutOfBoundsException e){
						;
					}
					/* If there is an Odd amount of -'s then push a - token. Otherwise push a + */
					if(minusCount%2 != 0){
						--i; //Decrement count by 1 to account for the above while loop passing the last + or -. Other wise you skip the next item after the last +/-
						tokens.add("-");
						
					}
					else{
						--i; //Decrement count by 1 to account for the above while loop passing the last + or -. Other wise you skip the next item after the last +/-
						tokens.add("+");
						
					}
					continue;
				}
				else{
					tokens.add(Character.toString(s.charAt(i)).trim());
				}							
			}
		}			
		addToken(tokens, builder);		
		String[] result = new String[tokens.size()];
		return tokens.toArray(result);
	}
	private static void addToken(List<String> tokens, StringBuilder builder) {
		if(builder.length() != 0){
			String temp2 = builder.toString().trim();					
			if(temp2.length() != 0){
				if(temp2.charAt(temp2.length()-1)=='e'){
					tokens.add(builder.toString().trim());
				}
				else{
					tokens.add(builder.toString().trim());
				}
				builder.setLength(0);				
			}
		}
	}	
}
