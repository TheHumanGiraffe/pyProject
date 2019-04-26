package src.apps;

import java.util.Stack;
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
	public static void main(String[] args) {
		//System.out.println("a"=="a");
		//System.out.println( "123".matches("\\d+"));
		// TODO Auto-generated method stub
		Tokenizer token = new Tokenizer();
		SimpleParser parser = new SimpleParser();		
		String [] tokenized = null;
			
		String[] exprValid = new String[] {
				"10 **2", "10 * 2", "1+1e-12","3","1337","3.5","0.123",".123","5.","1.23e+1","1.23e10","-- - + 3","1 + 21",
				"3*2","420/420","5%5","3.0%5","12%4","8%2.5",
				"3//2+5**2%5","110*2+2","2**3","1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1","3-5++0",
				"(((123+3)))","(1+2)*4/6",	"69+1","123++++++1","12//3","2.1 + .21","1--1","1  +       3",
				"2*(3+4)*((5+3)-2)","(((((((((123**2+5)))))))))",
				"- - - - -+ 1","1- - - - - + 2**2//50%2","1- + 2**2//50%2","10**2","(+1)","(1*2+num1)","num1","(num1)"
		};
		for (int i = 0;i<exprValid.length;i++) {
			boolean x = (isAcceptable(exprValid[i]));
			
			if (!x)
			{System.out.println(exprValid[i] + " true?: " +x);}
		}
		String[] exprInvalid = new String[] {
				"1 +","3.5.5","3 5",
				"3/*5","1.23**e","1.23e2.0","1.23e + 1","1.23 e1","5%%5","*/+-*2","2***3","12..3 + 2","1+1+1+","69++","70--",
				"6***9","+","-","<33333333333333333","123 123","4 5 + 9**7","6%%(6+5)","5*((5)","(1)(2)"
		};
		for (int i = 0;i<exprInvalid.length;i++) {
			boolean x = (isAcceptable(exprInvalid[i]));
			if (x)
			{System.out.println(exprInvalid[i] + " false?: " +x);}
		}
	}	
	
	/**
	 * Converts the elements of a given array of substrings (the 'result' array, etc.)
	 * @param array - an array of elements given from the 'removeParentheses' function
	 * @return An array converted back to a String
	 */
	private String arrayToString(String[] array){
		StringBuilder builder = new StringBuilder();
		for(String s: array){
			builder.append(s);
		}
		String str = builder.toString();
		return str;	
	}

	
	/**
	 * Main validator function. Given an equation with a type String, checks to see if the string matches the below regex.
	 * @param s - A String
	 * @return A Boolean that returns true or false depending if the entire string is valid or not.
	 */
	public static boolean isAcceptable(String s) {
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

			if (isAcceptable(s.substring(stringIndex, indexes[i-skipped][0])) &&
				(isAcceptable(s.substring(indexes[i][1], s.length())))){
				return true;
				} 
			if (isAcceptable(s.substring(stringIndex, indexes[i][0])) &&
				(isAcceptable(s.substring(indexes[i][1], s.length())))) {
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

	

}
