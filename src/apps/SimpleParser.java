package apps;
import java.util.Stack;
import static apps.Constants.REGEXEXPR;
/**
 * SimpleParser.java
 * 
 * Given a tokenized array, validates that the string as a whole is syntaxically vaild.
 * Fails if invalid syntax, but does not evaluate the given equation.
 * 
 * @author Mathias Ham
 * @author Micheal Walburn
 * @author Morgan Patterson
 */
public class SimpleParser {	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Tokenizer token = new Tokenizer();
		SimpleParser parser = new SimpleParser();		
		String [] tokenized = null;		
		
		System.out.println(parser.isExpr("234"));
		System.out.println(parser.isExpr("1+3"));
		System.out.println(parser.isExpr("(1+(2+1))*(78+3*15)+45"));
		System.out.println(parser.isExpr("1+"));
		System.out.println(parser.isExpr("1+*2"));

	}		

	protected boolean isExpr(String input){
		Tokenizer tokenizer= new Tokenizer();
		return isExprHelper(tokenizer.tokenize(input));
	}
	
	/**
	 * Runs the 'removeParentheses' and 'arrayToString' functions and determines if the entire string syntax is valid.
	 * @param tokens
	 * @return
	 */
	protected boolean isExprHelper(String[] tokens){
		String[] result;
		if(removeParentheses(tokens) != null){
			result = removeParentheses(tokens);	
			String statment = arrayToString(result);
			if(validate(statment)){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}		
	}	
	/**
	 * Converts the elemnents of a given array of substrings (the 'result' array, etc.)
	 * @param array
	 * @return
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
	 * Removes the parentheses within a given string to determine if equations within parentheses are valid.
	 * @param tokens - Array of tokenized substrings
	 * @return An array containing given problems without parentheses.
	 */
	private String[] removeParentheses(String[] tokens){
		Stack<String> stack = new Stack<String>();
		Stack<String> reverseStack = new Stack<String>();
		String tmp = "";
		for(int i = 0; i < tokens.length; i++){
			if(!tokens[i].equals(")")){
				stack.push(tokens[i]);
			}
			else if(tokens[i].equals(")")){				
				while(!stack.peek().equals("(")){
					reverseStack.push(stack.pop());
				}
				int x =reverseStack.size();
				for(int j = 0; j <x; j++ ){
					tmp += reverseStack.pop();
				}			
				if(!validate(tmp)){
					return null;
				}
				else{
					stack.pop();
					stack.push("99");					
				}
			}			
		}
		String[] result = stack.toArray(new String[stack.size()]);
		return result;
	}	
	/**
	 * Main validator function
	 * @param s - A String
	 * @return A Boolean that returns true or false depending if the entire string is valid or not.
	 */
	private boolean validate(String s){		
		return s.matches(REGEXEXPR);
	}
}
