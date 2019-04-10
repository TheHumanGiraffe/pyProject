package apps;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;  // Import the Scanner class

/**
 * 
 * Interpreter.java
 * 
 * Asks for input in the console, and then runs Tokenizer, SimpleParser, and Evaluator
 * to return the result.
 * 
 * @author Mathias Ham
 * @author Micheal Walburn
 * @author Morgan Patterson
 * 
 */
public class Interpreter {

	public static void main(String[] args) {
		HashMap<String, String> map = new HashMap<>(); 
		String input = null;
		Scanner scan = new Scanner(System.in);
		Evaluator eval = new Evaluator();
		while(input != "quit"){
			input = scan.nextLine();
			if(checkForVars(input, map)){
				input = updateInput(input, map);
				eval.eval(input);
			}
			else{
				eval.eval(input);
			}
		}
		scan.close();
	}

	/**
	 * If the given string has a variable, look for it in the HashMap.
	 * @param input - A given String
	 * @param map - A set of all defined variables for the current Interpreter session
	 * @return An updated string with the variables replaced with their values.
	 */
	private static String updateInput(String input, Map<String, String> map) {
		
		String[] tokens;
		String regexVar = "[a-zA-Z_]\\w+|[a-df-zA-DF-Z_]\\w*";

		Tokenizer tokenizer = new Tokenizer();
		tokens = tokenizer.generateTokens(input);
		
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].matches(regexVar)){
				if(map.containsKey(tokens[i])){
					tokens[i] = map.get(tokens[i]);
				}
				
			}
		
			}
		
		return arrayToString(tokens, 0, tokens.length);
		
	}

	/**
	 * Converts the given tokenized array to a String
	 * @param array - Array to Convert
	 * @param start - Starting index to convert
	 * @param end - ending index to convert
	 * @return a String 'str'
	 */
	private static String arrayToString(String[] array, int start, int end){
		StringBuilder builder = new StringBuilder();
		for (int i =start; i < end; i++){
			builder.append(array[i]);
		}
		
		String str = builder.toString();
		return str;	
	}
	
	/**
	 * Checks if a variable has been created in the HashMap
	 * @param input - String to be tested.
	 * @param map - The HashMap
	 * @return If the variable has been created return true, if not, false.
	 */
	private static boolean checkForVars(String input, Map<String, String> map) {
		String[] tokens;
		String regexVar = "[a-zA-Z_]\\w+|[a-df-zA-DF-Z_]\\w*";
		Evaluator eval = new Evaluator();
		Tokenizer tokenizer = new Tokenizer();
		tokens = tokenizer.generateTokens(input);
		
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].matches(regexVar)){
				if(isAssignment(input)){
						String right = "";
						String left = tokens[i];
						if(checkForVars(arrayToString(tokens, i+2, tokens.length), map)){
							String tmp = updateInput(input, map);
							checkForVars(tmp, map);
						}
						else{
							right = eval.eval(arrayToString(tokens, i+2, tokens.length));
						}
						map.put(left, right);
						
						break;
					
					
				}
				else{
					if(map.containsKey(tokens[i])){
						String var = map.get(tokens[i]);
						System.out.println(tokens[i] +" = " +var);
						return true;
					}
					else{
						System.out.printf("Var: %s is not saved\n", tokens[i]);
						return false;
					}
					
				}
			}
		}
		
		return false;
	}

	/**
	 * Checks if the given statement is an assignment (e.g. 'a = 2')
	 * @param input - given String
	 * @return true if the given statement is an assignment, false if not
	 */
	private static boolean isAssignment(String input) {		
		String[] tokens = input.split("=");
		if(tokens.length == 2){
			return true;
		}
		
		return false;
	}

}
