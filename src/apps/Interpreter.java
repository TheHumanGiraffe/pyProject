package apps;
import static apps.Constants.REGEXVAR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;  // Import the Scanner class

/*
     _____       _   _                    ___              
	|  __ \     | | | |                  / _ \             
	| |__) |   _| |_| |__   ___  _ __   | | | | _ __   ___ 
	|  ___/ | | | __| '_ \ / _ \| '_ \  | | | || '_ \ / _ \
	| |   | |_| | |_| | | | (_) | | | | | |_| || |_) |  __/
	|_|    \__, |\__|_| |_|\___/|_| |_|  \___(_) .__/ \___|
			__/ |                              | |         
    		|___/                              |_|         
	"lemme just ope right on past ya there"
	A wannabe python compiler by Mathias Michael and Morgan

 */


public class Interpreter {

	private static Scanner scan = new Scanner(System.in);
	private static IO io = new IO();
	public static Scanner scanner = io.getScanner();

	
	static HashMap<String, InstructionContent> functionMap = new HashMap<>();
	public static void main(String[] args) {
		
		
		
		
		
			HashMap<String, String> varMap = new HashMap<>();
			
			String input = null;
	
			String[] tokens;
	
			//This keeps from saying that "print" is not a assigned Var when using the print() function
			varMap.put("print", "");

			System.out.println(
					"  _____       _   _                    ___ \n"+             
					" |  __ \\     | | | |                  / _ \\ \n "+             
					"| |__) |   _| |_| |__   ___  _ __   | | | | _ __   ___ \n"+
					" |  ___/ | | | __| '_ \\ / _ \\| '_ \\  | | | || '_ \\ / _ \\ \n"+
					" | |   | |_| | |_| | | | (_) | | | | | |_| || |_) |  __/ \n"+
					" |_|    \\__, |\\__|_| |_|\\___/|_| |_|  \\___(_) .__/ \\___| \n"+
					"         __/ |                              | |           \n"+
					"        |___/                               |_|            \n"+
					"lemme just ope right on past ya there\n"+
					"A wannabe python compiler by Mathias Michael and Morgan");
						
			while(true){
				System.out.printf("\n>>> ");
				
				input = getInput();
				tokens = Tokenizer.tokenize(input);
				if(input.equals("")) {
					continue;
				}
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
						generateFunction(input,varMap);
						continue;
					}
					if(SimpleParser.isFunctionCall(input)){
						String update = input.substring(input.indexOf('(')+1, input.lastIndexOf(')'));
						tokens[2] =Evaluator.updateInput(varMap, update.split("((?<=,)|(?=,))"));
						input = arrayToString(tokens, 0, tokens.length);
						Evaluator.runBlock(functionMap, input,  varMap);
						continue;
					}
				}
	
				if(input.equals("quit")){
					System.out.println("Goodbye. Hope you had fun!");
					break;
				}
				interpret(input, tokens, varMap, functionMap );
			}
		}

	/***
	 * Turn String array into a String based on start and end index 
	 * @param array - array to be converted to string
	 * @param start - start index of the array
	 * @param end - end index of the array
	 * @return - String between the given indexs
	 */
	protected static String arrayToString(String[] array, int start, int end){
		StringBuilder builder = new StringBuilder();
		for (int i =start; i < end; i++){
			builder.append(array[i]);
		}
		String str = builder.toString();
		return str;
	}

	/**
	 * Checks the a string for varaibles and adds it to the hash map if it is an assignment operation
	 * @param input - A String to be checked for variables
	 * @param map - The hash map of variables
	 * @param tokens - A tokenized version of input
	 * @return - boolean if there was vars
	 */
	private static boolean checkForVars(String input, Map<String, String> map, String[] tokens) {
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].matches(REGEXVAR)){
				if(SimpleParser.isAssignment(input)){
						String right = "";
						String left = tokens[i];
						String[] rightTokens;
						right = arrayToString(tokens,i+2, tokens.length);
						rightTokens= Tokenizer.tokenize(right);
						if(SimpleParser.containsFunctionCall(right)) {
							right = handleFunc(map, right, rightTokens);
						}
						else if(SimpleParser.containsVars(tokens, i +2, tokens.length)){
							/*Resolve all variables, the assign then evaluate them */
							
							right = Evaluator.updateInput(map, rightTokens);
							right = Evaluator.eval(right);
						}
						else{
							right = Evaluator.eval(right);
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

	private static String handleFunc(Map<String, String> map, String input, String[] rightTokens) {
		String update = input.substring(input.indexOf('(')+1, input.lastIndexOf(')'));
		//rightTokens[2] =Evaluator.updateInput(map, update.split("((?<=,)|(?=,))"));
		while(SimpleParser.containsFunctionCall(input)) {
			input = arrayToString(rightTokens, 0, rightTokens.length);
			String functionCall = SimpleParser.getFunctionCall(input);
			String[] functionCallTokens =Tokenizer.tokenize(functionCall);
			update = input.substring(functionCall.indexOf('(')+1, functionCall.lastIndexOf(')'));
			functionCallTokens[2] =Evaluator.updateInput(map, update.split("((?<=,)|(?=,))"));
			functionCall = arrayToString(functionCallTokens, 0, 3) + ")";
			
			InstructionContent content = Evaluator.runFunction(map, functionCall);
			
			input = Evaluator.updateFunction(map, input, content);
		}
		//interpret(input, rightTokens, map, null);
		//input = Evaluator.eval(input);
		//right = content.getReturnStatment();
		return input;
	}

	protected static void funcReturn(String input, String[] tokens,  Map<String, String> map, HashMap<String, InstructionContent> blockInstructionsMap, InstructionContent functionProperties) {
		String toReturn ="";
		tokens[0] = tokens[0].replace("return ", "");
		toReturn = arrayToString(tokens, 0, tokens.length);
		String[] toReturnTokens = Tokenizer.tokenize(toReturn);
		if(SimpleParser.containsVars(tokens, 0, tokens.length)) {
			
			if(SimpleParser.containsFunctionCall(toReturn)) {
				toReturn = handleFunc(map, toReturn, toReturnTokens);
			}
			toReturn = Evaluator.updateInput(map, Tokenizer.tokenize(toReturn));
			toReturn = Evaluator.eval(toReturn);
			
		}
		else {
			toReturn = Evaluator.eval(toReturn);
		}
		functionProperties.setReturnStatment(toReturn);
	}
	/**
	 * 
	 * @return - new hashmap to store Block Instructions
	 */
	protected static HashMap<String, InstructionContent> generateBlockMap(){
		HashMap<String, InstructionContent> blockInstructionsMap = new HashMap<>();

		return blockInstructionsMap;
	}
	/**
	 * 
	 * @param input - String that contains the name of the For loop
	 * @param map - Hash Map of variables
	 * @param blockInstructionsMap - HashMap of InstructionsContent
	 * 
	 */
	protected static void generateForLoop(String input, Map<String, String> map, HashMap<String, InstructionContent> blockInstructionsMap) {
		System.out.print("... ");

		String blockName = input;
		List<String> instructions = new ArrayList<String>();
		
		//Get input for instructions to be associated with the Loop
		while(true){
			input = getInput();
			
			SimpleParser.isInstruction(input, map, blockInstructionsMap);
			if(input.equals("q")){
				InstructionContent content = new InstructionContent(blockName, instructions);
				blockInstructionsMap.put(blockName, content);
				break;
			}
			instructions.add(input);
		}
	}

	private static String getInput() {
		String input="";
	//Use this if else block for BOTH  File read in AND  user input
		if(scanner!= null && scanner.hasNextLine()) {
			input = scanner.nextLine();
			System.out.println(input);
			input = input.trim();
		}
		else {
			input = scan.nextLine().trim();
		}
	//Use this if else block for ONLY file read in 
		/*if(scanner!= null && scanner.hasNextLine()) {
		input = scanner.nextLine();
			System.out.println(input);
			input = input.trim();	
		}
		else {
			return "quit";
		}*/
		
	//Use this line for ONLY user input 
		//input = scan.nextLine().trim();
		return input;
	}

	/***
	 * 
	 * @param input- Function defention name
	 * @param varMap - HashMap of variables
	 */
	protected static void generateFunction(String input, HashMap<String, String> varMap) {
		    List<String> paramNames = new ArrayList<String>();
			String[] tokens = Tokenizer.tokenize(input);
			String funcName = tokens[0].replace("def ", "");
			String[] varTokens = tokens[2].split(",");
			int varCount = varTokens.length;
			HashMap<String, InstructionContent> blockInstructionsMap = generateBlockMap();
			
			for(int i = 0; i <varCount; i ++ ){
				paramNames.add(varTokens[i].trim());
			}


		List<String> instructions = new ArrayList<String>();

		//Get input for instructions to be associated with the function
		while(true){
			System.out.print("... ");
			input = getInput();			
			SimpleParser.isInstruction(input, varMap, blockInstructionsMap);
			if(input.equals("q")){
				InstructionContent content = new InstructionContent(funcName, instructions, paramNames, varCount, blockInstructionsMap);
				functionMap.put(funcName, content);
				break;
			}
			instructions.add(input);
		}
	}

	/***
	 * 
	 * @param input - String, Contains the if statment instruction
	 * @param map - HashMap of InstructionContent
	 * @param blockInstructionsMap
	 */
	protected static void generateIf(String input, Map<String, String> map, HashMap<String, InstructionContent> blockInstructionsMap) {
		String blockName = input;

		List<String> instructions = new ArrayList<String>();
		List<String> elseInstructions = new ArrayList<String>();
		//Get input for instructions to be associated with the If Statement
		while(true){
			System.out.print("... ");

			input = getInput();
			
			SimpleParser.isInstruction(input, map,  blockInstructionsMap);
			if(input.equals("q")){
				InstructionContent content = new InstructionContent(blockName, instructions);
				blockInstructionsMap.put(blockName, content);
				break;
			}
			if(input.equals("else:")){
				//Get input for instructions to be associated with the Else Statement
				while(true){
					System.out.print("... ");
					input = getInput();
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
	}
/**
 * 
 * @param input - The instruction to be ran
 * @param tokens - tokenized version of the instruction
 * @param map - HasMap of variables
 * @param blockInstructionsMap - HasMap of InstructionContent
 */
	public static void interpret(String input, String[] tokens,  Map<String, String> map, HashMap<String, InstructionContent> blockInstructionsMap){
		if(SimpleParser.isForLoop(input)|| SimpleParser.isIf(input) ){
			Evaluator.runBlock(blockInstructionsMap, input,  map);
			return;
		}
		if(tokens.length > 2){
			if(SimpleParser.isFunctionCall(input)){
				String update = input.substring(input.indexOf('(')+1, input.lastIndexOf(')'));
				tokens[2] =Evaluator.updateInput(map, update.split("((?<=,)|(?=,))"));
				
				input = arrayToString(tokens, 0, 3) + ")";
				
				Evaluator.runBlock(blockInstructionsMap, input,  map);
				return;
			}
		}

		if(SimpleParser.isPrint(input)){
				StringBuilder builder = new StringBuilder();
				for(int j = input.indexOf('(')+1; j < input.lastIndexOf(')'); j++){
					builder.append(input.charAt(j));
				}
				String[] printTokens;
				String whatToPrint= builder.toString();
				String[] whatToPrintTokens = whatToPrint.split(";");
				for(int i = 0; i < whatToPrintTokens.length; i ++) {
					printTokens = Tokenizer.tokenize(whatToPrintTokens[i]);
					interpret(whatToPrintTokens[i],  printTokens, map, blockInstructionsMap);
					
				}
				
			return;
		}
		//If the input is a String then print it out
		if(input.charAt(0) == '"' && input.charAt(input.length()-1) == '"'){
			print(input.substring(input.indexOf('"')+1, input.lastIndexOf('"')));
			return;
		}
		if(checkForVars(input, map, tokens)){
			input = Evaluator.updateInput(map, tokens);
			print(Evaluator.eval(input));
			return;
		}
		else{
			print(Evaluator.eval(input));
		}
	}

	private static void print(String input){
		if(input != null){
			System.out.println(input);
		}
	}
}