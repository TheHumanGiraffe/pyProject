package apps;

import static apps.Constants.REGEXCOMP;
import static apps.Constants.REGEXPARTIALMETHODSIG;
import static apps.Constants.REGEXVAR;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * SimpleParser.java
 * 
 * Given a tokenized array, validates that the string as a whole is syntaxically vaild.
 * Fails if invalid syntax, but does not evaluate the given equation.
 * 
 * @author Mathias Ham
 * @author Micheal Walburn
 * @author Morgan Patterson
 * 
 */

public class SimpleParser {		
	/**
	 * Main validator function. Given an equation with a type String, checks to see if the string matches the below regex.
	 * @param s - A String
	 * @return A Boolean that returns true or false depending if the entire string is valid or not.
	 */
	public static boolean isExpr(String s) {
//		System.out.println("running isAcceptable on string '" + s + "'");
		
		//if there's parenthesis, we should probably take care of that
		if (s.contains("(")){s = handleParentheses(s);}
		
//		if (s == null || s == "" || s.matches("\\s*")) {return false;}
		//if it's a single number, obv we want it to be valid
		if (isNumber(s)) {return true;}		
		
		//then check it 
		int[][] indexes = getMatches(s,"\\s*\\/{2}|\\s*\\*{2}\\s*|\\s*(?<!e)[\\+|\\-|\\*|\\/|%]\\s*");
		int i = 0;
		int stringIndex = 0;
		int skipped = 0;
		while (!(indexes[i][0] ==0 && indexes[i][1] ==0)) {
//			System.out.println("skipped: " + skipped);
			if (indexes[i+1][0] !=indexes[i][1]) {
//				System.out.println(s.substring(stringIndex, indexes[i-1][0]));
//				System.out.println(s.substring(indexes[i][1], s.length()));

			if (isExpr(s.substring(stringIndex, indexes[i-skipped][0])) &&
				(isExpr(s.substring(indexes[i][1], s.length())))){
				return true;
				} 
			if (isExpr(s.substring(stringIndex, indexes[i][0])) &&
				(isExpr(s.substring(indexes[i][1], s.length())))) {
				return true;
			}
			}
			else {skipped++;}
			i++;

		}
		return false;
		
	}
	
	/**
	 * Gets all matches for regex within a string and returns their positions in an array
	 * @text the thing to check the regex against
	 * @regex A regex to match
	 */
	public static int[][] getMatches(String text, String regex) {
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(text);
	    int[][] locations = new int[500][2];
	    int i = 0;
	    // Check all occurrences
	    String lastFound = new String();
	    int lastEnd = 0;
	    while (matcher.find()) {
//	        System.out.print("Start index: " + matcher.start());
//	        System.out.print(" End index: " + matcher.end());
//	        System.out.println(" Found: " + matcher.group());	    	
		    if (lastEnd == matcher.start()) {
	        	if (lastFound.matches("//s*\\/\\/\\s*") || lastFound.matches("\\s*\\*\\*\\s*") ||
	        			lastFound.matches("\\s*%\\s*") || lastFound.matches("\\s*\\*\\s*")    ||
	        			lastFound.matches("\\s*\\/\\s*")){
	        		if (matcher.group().matches("\\s*\\/\\s*") || matcher.group().matches("\\s*\\*\\s*")|| matcher.group().matches("\\s*%\\s*")) {
		        		int[][] x = new int[1][2];
		        		x[0][0] = 0;
		        		x[0][1] = 0;
//			        	System.out.println("returning fail case");
		        		return x;
		        	}
	        	}
		    }
	        lastFound = matcher.group();
	        lastEnd = matcher.end();

//	        System.out.println(i);
	        locations[i][0] = matcher.start();
	        locations[i++][1] = matcher.end();
//	        System.out.println("stuck in a while");
	    }
	    //System.out.println("returning");
	    return locations;
//	    System.out.println("match not found");
	}
		
	/**
	 * Removes the parentheses within a given string to determine if equations within parentheses are valid.
	 * @param tokens - Array of tokenized substrings
	 * @return An array containing given problems without parentheses.
	 */
	public static String handleParentheses(String s) {
//		System.out.println(s);
		String temp = "";
		
		for (int i=0;i<s.length();i++) {
			if (s.charAt(i) == ')'){
				int end = i-1;
				int j;
				for (j = end;s.charAt(j)!='(';j--){
					temp = s.charAt(j) + temp;
				}
				//System.out.println(s.charAt(j));
				//System.out.println(s.charAt(end+1));
//				System.out.println(temp); //pass this to a calculate function of some sort
				//pretend this is the calculated value
				temp = s.substring(0,j)+ "123 "+s.substring(end+2,s.length());
				
				s = handleParentheses(temp);
				return s;
			}
		}
		return s;
	}
	
////////////////////////////////////////context free grammar base cases /////////////////////////////////////
	public static boolean isInteger(String s) {return s.matches("\\s*\\d+\\s*");}
	
	public static boolean isFloat(String s) {return s.matches("\\s*\\d+\\.\\d*\\s*|\\s*\\d*\\.\\d+\\s*");}
	
	public static boolean isVar(String s) {return s.matches("\\s*[a-zA-Z_]\\w+|[a-df-zA-DF-Z_]\\w*\\s*");}
	
	public static boolean isSciPostfix(String s) {return s.matches("\\s*\\d+(\\.\\d+)?[eE][+-]?\\d+\\s*");}
			
	//this needs to handle spaces between operators but not between numbers
	public static boolean isExtendedNumber(String s) {
		s = s.replaceAll("\\s", "");
		//System.out.println(s);
		int i = 0;
		String s2 = new String();
		for(i=0;i<s.length();i++) {
			if (i == 0 && !(s.charAt(0) == '+' || s.charAt(0) == '-')) {return false;}
			if (s.charAt(i) == '+' || s.charAt(i) == '-' ) {continue;}
			else {s2 = s2 + s.charAt(i);}
		}
		if (s2.matches("\\d+\\s+\\d+")) {return false;}
		s2 = s2.replaceAll("\\s", "");
		return (isInteger(s2) || isFloat(s2) || isSciPostfix(s2));
	}
	
	public static boolean isNumber(String s) {return (isInteger(s) || isFloat(s) || isSciPostfix(s) || isExtendedNumber(s) || isVar(s));}

	protected static boolean isAssignment(String input) {		
		String[] tokens = input.split("=");
		if(tokens.length == 2){
			return true;
		}
		
		return false;
	}

	protected static boolean isForLoop(String input) {
		String[] tokens;
		tokens = input.split("\\s");
		
		String REGEXRANGE = "range\\(.+,.+\\)|range\\(.+\\)";
		try{
			if(tokens[0].equals("for")){
				if(tokens[1].matches(REGEXVAR)){
					if(tokens[2].equals("in")){
						if(Interpreter.arrayToString(tokens, 3, tokens.length).trim().matches(REGEXRANGE)){
							return true;
						}
					}
				}
			}
		}
		catch(Exception e){
			return false;
		}
		return false;
	}

	protected static boolean isIf(String input){
		String[] tokens = Tokenizer.tokenize(input);
		int compIndex;
		if(tokens[0].contains("if ")){
			tokens[0] = tokens[0].replace("if", "");
			for(int i =0; i< tokens.length; i++){					
				if(tokens[i].matches(REGEXCOMP)){
					compIndex = i;
					/*String left = arrayToString(tokens,0,compIndex);*/
					String right= Interpreter.arrayToString(tokens,compIndex+1,tokens.length);
					if(right.contains(":")){
						return true;
					}
					break;
				}
			}		
		}
		return false;
	}

	protected static boolean isPrint(String input) {
		if(input.contains("print(") && input.charAt(input.length()-1) == ')'){
			return true;
		}
		return false;
	}

	protected static void isInstruction(String input,  Map<String,String> map , HashMap<String, InstructionContent> blockInstructionsMap  ) {
		
		if(isForLoop(input)){
			Interpreter.generateForLoop(input, map, blockInstructionsMap);
			
		}
		if(isIf(input)){
			Interpreter.generateIf(input, map, blockInstructionsMap);
		}
		
	}

	protected static boolean isFunctionDef(String defAndName, String vars) {
		String[] tokens;
		tokens = defAndName.split("\\s");
				if(tokens[0].equals("def")){
					if(tokens[1].matches(REGEXVAR)){
							if(vars.matches("([a-zA-Z0-9_]+)+(,\\s*[a-zA-Z0-9_]+)*|\\s*")){
								return true;
							}						
						}
					}
		return false;
	}

	protected static boolean isFunctionCall(String input) {
		if(input.contains("(") && input.contains(")")){
			if(input.substring(0,input.indexOf('(')).matches(REGEXVAR) && !input.substring(0,input.indexOf('(')).equals("print")){
				//"([a-zA-Z0-9_]+)+(,\\s*[a-zA-Z0-9_]+)*|\\s*"
				String tmp = input.substring(input.indexOf('(')+1, input.lastIndexOf(')'));
				String[] params= tmp.split(",");
				for(int i = 0; i < params.length; i++){
					if(!SimpleParser.isExpr(params[i].trim())){
						return false;
					}
					
				}
				return true;
			}
		}
			
		return false;
	}
	
	protected static boolean isReturn(String input) {
		String[] tokens = input.split("\\s");
		if(tokens[0].equals("return")) {
			return true;
		}
		return false;
	}
	
	protected static boolean containsVars(String[] tokens, int start, int end) {
		for(int i =start; i < end; i++){
			if(tokens[i].matches(REGEXVAR)){
				return true;		
				}
			}
		return false;
	}

	public static boolean containsFunctionCall(String input) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < input.length(); i ++) {
			if(Character.toString(input.charAt(i)).matches("\\s*\\/{2}|\\s*\\*{2}\\s*|\\s*(?<!e)[\\+|\\-|\\*|\\/|%]\\s*") && !builder.toString().matches(REGEXPARTIALMETHODSIG)) {
				builder.setLength(0);
			}
			builder.append(input.charAt(i));
			
			if(SimpleParser.isFunctionCall(builder.toString())) {
				return true;			
			}
		}
		return false;
	}

	public static String getFunctionCall(String input) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < input.length(); i ++) {
			
			if(Character.toString(input.charAt(i)).matches("\\s*\\/{2}|\\s*\\*{2}\\s*|\\s*(?<!e)[\\+|\\-|\\*|\\/|%]\\s*") && !builder.toString().matches(REGEXPARTIALMETHODSIG)) {
				builder.setLength(0);
				continue;
			}
			builder.append(input.charAt(i));
			if(SimpleParser.isFunctionCall(builder.toString())) {
				return builder.toString();
				
			}
		}
		return input;
	}
}