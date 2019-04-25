package apps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;  // Import the Scanner class

import static apps.Constants.REGEXVAR;


public class Interpreter {

	private static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) {
		HashMap<String, String> varMap = new HashMap<>(); 
		HashMap<String, InstructionContent> functionMap = new HashMap<>();
		String input = null;
		
		String[] tokens;
		
		//This keeps from saying that "print" is not a assigned Var when using the print() function
		varMap.put("print", "");
		/*
  ___      _   _                   _   __  
 | _ \_  _| |_| |_  ___ _ _    ___/ | /  \ 
 |  _/ || |  _| ' \/ _ \ ' \  |___| || () |
 |_|  \_, |\__|_||_\___/_||_|     |_(_)__/ 
      |__/                                 

		 */	
		System.out.println("  ___      _   _                   _   __ \n"+
							" | _ \\_  _| |_| |_  ___ _ _    ___/ | /  \\ \n"+
							" |  _/ || |  _| ' \\/ _ \\ ' \\  |___| || () |\n"+
							" |_|  \\_, |\\__|_||_\\___/_||_|     |_(_)__/ \n"+
									"     |__/                                 \n");	
		while(true){
			System.out.printf("\n>>>");
			input = scan.nextLine();
			tokens = Tokenizer.tokenize(input);
			if(SimpleParser.isForLoop(input)){
				HashMap<String, InstructionContent> blockInstructionsMap = generateBlockMap();
				 generateForLoop(input, varMap, blockInstructionsMap);
				 Evaluator.runBlock(blockInstructionsMap, input, varMap);
				continue;
			}
			
			if(SimpleParser.isIf(input)){
				HashMap<String, InstructionContent> blockInstructionsMap = generateBlockMap();
				generateIf(input, varMap, blockInstructionsMap);	
				 Evaluator.runBlock(blockInstructionsMap, input,  varMap);
				continue;
			}
			if(tokens.length > 2){
				if(SimpleParser.isFunctionDef(tokens[0], tokens[2])){
					generateFunction(input,varMap, functionMap);
					continue;
				}
				if(SimpleParser.isFunctionCall(input)){
					tokens[2] =Evaluator.updateInput(input, varMap, tokens[2].split("((?<=,)|(?=,))"));
					input = arrayToString(tokens, 0, tokens.length);
					Evaluator.runBlock(functionMap, input,  varMap);
					continue;
				}
			}
			
			if(input.equals("quit")){
				System.out.println("Goodbye. Hope you had fun!");
				break;
			}
			interpret(input, tokens, varMap, null );
		}
	}
	
	public static void interpret(String input, String[] tokens,  Map<String, String> map, HashMap<String, InstructionContent> blockInstructionsMap){
		if(SimpleParser.isForLoop(input)|| SimpleParser.isIf(input) ){
			Evaluator.runBlock(blockInstructionsMap, input,  map);
		}
		if(tokens.length > 2){
			if(SimpleParser.isFunctionCall(input)){
				tokens[2] = Evaluator.updateInput(input, map, tokens[2].split("((?<=,)|(?=,))"));
				input = arrayToString(tokens, 0, tokens.length);
				Evaluator.runBlock(blockInstructionsMap, input,  map);
			}
		}
		if(SimpleParser.isPrint(input)){
			StringBuilder builder = new StringBuilder();
			for(int i = input.indexOf('(')+1; i < input.lastIndexOf(')'); i++){
				builder.append(input.charAt(i));
			}
			String[] printTokens;
			String whatToPrint= builder.toString();
			printTokens = Tokenizer.tokenize(whatToPrint);
			interpret(whatToPrint,  printTokens, map, blockInstructionsMap);
			
			return;
		}
		
		
		if(checkForVars(input, map, tokens)){
			input = Evaluator.updateInput(input, map, tokens);
			print(Evaluator.eval(input));
		}
		else{
			print(Evaluator.eval(input));
		}
	}

	protected static HashMap<String, InstructionContent> generateBlockMap(){
		HashMap<String, InstructionContent> blockInstructionsMap = new HashMap<>(); 
		
		return blockInstructionsMap;
	}
	protected static String generateIf(String input, Map<String, String> map, HashMap<String, InstructionContent> blockInstructionsMap) {
		String blockName = input;
		
		List<String> instructions = new ArrayList<String>();
		List<String> elseInstructions = new ArrayList<String>();
		while(true){
			input = scan.nextLine();
			SimpleParser.isInstruction(input, map,  blockInstructionsMap);
			if(input.equals("q")){
				InstructionContent content = new InstructionContent(blockName, instructions);
				blockInstructionsMap.put(blockName, content);
				break;
			}
			if(input.equals("else:")){
				while(true){
					input = scan.nextLine();
					SimpleParser.isInstruction(input, map, blockInstructionsMap);
					if (input.equals("q")){
						InstructionContent content = new InstructionContent(blockName,instructions, elseInstructions);
						blockInstructionsMap.put(blockName, content);
						break;
					}
					elseInstructions.add(input);
				}
				break;
			}
			instructions.add(input);
			
		}	
		return input;
	}

	protected static void generateFunction(String input, HashMap<String, String> varMap, HashMap<String, InstructionContent> functionMap) {
		    List<String> paramNames = new ArrayList<String>();
			String[] tokens = Tokenizer.tokenize(input);
			String funcName = tokens[0].replace("def ", "");
			String[] varTokens = tokens[2].split(",");
			int varCount = varTokens.length;
			
			for(int i = 0; i <varCount; i ++ ){
				paramNames.add(varTokens[i].trim());
			}
			
			
		List<String> instructions = new ArrayList<String>();
		
		while(true){
			input = scan.nextLine();
			
			SimpleParser.isInstruction(input, varMap, functionMap);
			
			if(input.equals("q")){
				InstructionContent content = new InstructionContent(funcName, instructions, paramNames, varCount);
				functionMap.put(funcName, content);
				break;
			}
			
			instructions.add(input);
			
		}
		
	}

	protected static String generateForLoop(String input, Map<String, String> map, HashMap<String, InstructionContent> blockInstructionsMap) {
		String blockName = input;
		
		List<String> instructions = new ArrayList<String>();
		
		while(true){
			input = scan.nextLine();		
			SimpleParser.isInstruction(input, map, blockInstructionsMap);		
			if(input.equals("q")){
				InstructionContent content = new InstructionContent(blockName, instructions);
				blockInstructionsMap.put(blockName, content);
				break;
			}		
			instructions.add(input);		
		}
	
		
		return input;
	}
	
	private static void print(String input){
		if(input != null){
			System.out.println(input);
		}
		
	}

	private static boolean checkForVars(String input, Map<String, String> map, String[] tokens) {
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].matches(REGEXVAR)){
				if(SimpleParser.isAssignment(input)){
						String right = "";
						String left = tokens[i];
						String[] rightTokens;
						if(SimpleParser.containsVars(tokens, i +2, tokens.length)){
							/*Resolve all variables, the assign then evaluate them */
							right = arrayToString(tokens,i+2, tokens.length);
							rightTokens= Tokenizer.tokenize(right);
							right = Evaluator.updateInput("", map, rightTokens);
							right = Evaluator.eval(right);
						}
						else{
							right = Evaluator.eval(arrayToString(tokens, i+2, tokens.length));				
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

	
	protected static String arrayToString(String[] array, int start, int end){
		StringBuilder builder = new StringBuilder();
		for (int i =start; i < end; i++){
			builder.append(array[i]);
		}
		String str = builder.toString();
		return str;	
	}
}
