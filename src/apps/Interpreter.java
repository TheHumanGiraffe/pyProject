package apps;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;  // Import the Scanner class

import jdk.nashorn.internal.parser.TokenKind;

import static apps.Constants.REGEXVAR;

public class Interpreter {

	public static void main(String[] args) {
		HashMap<String, String> map = new HashMap<>(); 
		String input = null;
		Scanner scan = new Scanner(System.in);
		Evaluator eval = new Evaluator();
		String[] tokens;
		Tokenizer tokenizer = new Tokenizer();
		System.out.println(">>> Python -3.0 by Team");
		while(true){
			System.out.printf("\n>>>");
			input = scan.nextLine();
			if(input.equals("quit")){
				System.out.println("Goodbye. Hope you had fun!q");
				break;
			}
			//System.out.println();
			tokens = tokenizer.tokenize(input);;
			if(checkForVars(input, map, tokens, tokenizer)){
				input = updateInput(input, map, tokens, tokenizer);
				eval.eval(input);
			}
			else{
				eval.eval(input);
			}
		}
	}
	private static String updateInput(String input, Map<String, String> map, String[] tokens, Tokenizer tokenizer) {
		String[] tempToken;
		
		
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].matches(REGEXVAR)){
					if(map.containsKey(tokens[i])){
						tempToken = tokenizer.tokenize(map.get(tokens[i]));
						if(containsVars(tempToken, 0, tempToken.length)){
							updateInput(input, map, tempToken, tokenizer);
						}
						tokens[i] = map.get(tokens[i]);
					}			
				}
			}
		
		if(containsVars(tokens, 0, tokens.length)){
			updateInput(arrayToString(tokens, 0, tokens.length), map, tokens, tokenizer);
		}
		return arrayToString(tokens, 0, tokens.length);
	}

	private static boolean containsVars(String[] tokens, int start, int end) {
		for(int i =start; i <end; i++){
			if(tokens[i].matches(REGEXVAR)){
				return true;		
				}
			}
		return false;
	}
	private static String arrayToString(String[] array, int start, int end){
		StringBuilder builder = new StringBuilder();
		for (int i =start; i < end; i++){
			builder.append(array[i]);
		}
		String str = builder.toString();
		return str;	
	}
	private static boolean checkForVars(String input, Map<String, String> map, String[] tokens, Tokenizer tokenizer) {
	Evaluator eval = new Evaluator();
		
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].matches(REGEXVAR)){
				if(isAssignment(input)){
						String right = "";
						String left = tokens[i];
						String[] rightTokens;
						if(containsVars(tokens, i +2, tokens.length)){
							/*Resolve all variables, the assign then evaluate them */
							right = arrayToString(tokens,i+2, tokens.length);
							rightTokens=tokenizer.tokenize(right);
							right = updateInput("", map, rightTokens, tokenizer);
							right = eval.eval(right);
						}
						else{
							right = eval.eval(arrayToString(tokens, i+2, tokens.length));				
						}
						map.put(left,right);					
						break;
				}
				else{
					if(map.containsKey(tokens[i])){
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

	private static boolean isAssignment(String input) {		
		String[] tokens = input.split("=");
		if(tokens.length == 2){
			return true;
		}
		
		return false;
	}

}
