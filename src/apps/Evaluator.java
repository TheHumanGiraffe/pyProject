package apps;

import static apps.Constants.REGEXCOMP;
import static apps.Constants.REGEXNUMBER;
import static apps.Constants.REGEXPARTIALMETHODSIG;
import static apps.Constants.REGEXVAR;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * 
 * Evaluates a given string and returns the result.
 * 
 * @author Mathias Ham
 * @author Micheal Walburn
 * @author Morgan Patterson
 *
 */

class NumberValues{
	Integer i;
	Double d;
	
	
	public Integer getI() {
		return i;
	}
	public void setI(Integer i) {
		this.i = i;
	}
	public Double getD() {
		return d;
	}
	public void setD(Double d) {
		this.d = d;
	}	
}

public class Evaluator {
	public static String eval(String s){
		String[] tokenized;		
		tokenized = Tokenizer.tokenize(s);
		
		if(Tokenizer.validateTokens(tokenized)){
			if(SimpleParser.isExpr(s)){	
				String result = toPreFix(tokenized);
				return result;
			}			
		}	
		return null;		
	}
	/**
	 * Converts given tokens into separate values and operators to determine the order of operations 
	 * for the equation.
	 * 
	 * @param tokens - Array of tokenized substrings.
	 * @return 
	 */
	private static String toPreFix(String[] tokens){
		Stack<NumberValues> values = new Stack<NumberValues>();
		Stack<String> ops = new Stack<String>();
		
		for(int i=0; i < tokens.length; i++){		
			if(tokens[i] == " "){
				continue;
			}					
			if(tokens[i].matches(REGEXNUMBER)){
				NumberValues input = new NumberValues();
				double toDouble;
				try{
					Integer toInt = Integer.parseInt(tokens[i]);
					input.setI(toInt);
					input.setD(null);
				}
				catch(NumberFormatException e){
					toDouble = Double.parseDouble(tokens[i]);
					input.setD(toDouble);
					input.setI(null);
				}		
				values.push(input);
			}
			
			else if(tokens[i].equals("(")){
				ops.push(tokens[i]);
			}
			
			else if(tokens[i].equals(")")){		
				while (!ops.isEmpty() && !ops.peek().equals("(")){					
					chooseType(values, ops);							
				}
				ops.pop();
			}
			
			else if(tokens[i].matches("[\\+\\-\\*\\/\\%]|[\\*]{2}|[\\/]{2}|e")){
				while(!ops.empty() && hasPrecedence(tokens[i], ops.peek())){
					chooseType(values, ops);
				}
				ops.push(tokens[i]);
			}
		}
		
		while(!ops.empty()){
			try{
				if(values.size() > 1){
					chooseType(values, ops);
				}
			}catch( EmptyStackException e){}
		}
		try{
			if(values.peek().getD()!=null){
				return values.pop().getD().toString();
			}
			else{
				return values.pop().getI().toString();
			}
		}catch( EmptyStackException e){}
		return null;	
	}

	private static void chooseType(Stack<NumberValues> values, Stack<String> ops) {
		if(values.size() >1){
			if(values.peek().getD() != null){
				NumberValues tmp = new NumberValues();
				tmp = values.pop();
				if(values.peek().getD() != null){
					values.push(tmp);
					values.push(applyOp(ops.pop(), values.pop().getD(), values.pop().getD()));
				}
				else{
					values.push(tmp);
					values.push(applyOp(ops.pop(), values.pop().getD(), values.pop().getI().doubleValue()));
				}
			}
			else{
				NumberValues tmp = new NumberValues();
				tmp = values.pop();
				if(values.peek().getD() != null){
					values.push(tmp);
					values.push(applyOp(ops.pop(), values.pop().getI().doubleValue(), values.pop().getD()));
	
				}
				else{
					values.push(tmp);
					values.push(applyOp(ops.pop(), values.pop().getI(), values.pop().getI()));
	
				}
			}
		}
		else{
			if(values.peek().getD() != null){
				values.push(applyOp(ops.pop(), values.pop().getD(),(double) 0.0));
			}
			else{
				values.push(applyOp(ops.pop(), values.pop().getI(),0));

			}
		}
	}
	
	/**
	 * Compares two operators to see which one has precendence.
	 * @param op1
	 * @param op2
	 * @return A boolean that determines which operator has precedence.
	 */
	public static boolean hasPrecedence(String op1, String op2){
		if(op2.equals("(") || op2.equals(")"))
				return false;
		if(((op1.equals("*")||op1.equals("/")||op1.equals("%")||op1.equals("//")) && (op2.equals("+") || op2.equals("-"))))
			return false;
		if((op1.equals("**")|| op1.equals("e"))  && !op2.equals("**")){
			return false;
		}
		else
			return true;
	}
	
	/**
	 * Applies an operator to two floating point integers and returns the result of the equation
	 * @param op - Operator
	 * @param b - double
	 * @param a - double
	 * @return Result of an equation relating to a given operation
	 */
	
	public static NumberValues applyOp(String op, double b, double a){
		NumberValues input = new NumberValues();
		switch(op){
		case "+":
			input.setD(a+b);
			return input;
		
		case "-":
			input.setD(a-b);
			return input;
		case "*":
			input.setD(a*b);
			return input;
		case "%":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			input.setD(a%b);
			return input;
		case"**":
			input.setD((double) Math.pow(a,b));
			return input;
			
		case"e":
			input.setD((double) (a * Math.pow(10, b)));
			return input;
			
		case "/":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			input.setD((double)a/b);
			return input;
		
		case"//":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			input.setI((int) (a/b));
			return input;
		}
		input.setD(null);
		input.setI(null);
		return input;
		
	}
	
	public static NumberValues applyOp(String op, int b, int a){
		NumberValues input = new NumberValues();
		switch(op){
		case "+":
			input.setI(a+b);
			return input;
		
		case "-":
			input.setI(a-b);
			return input;
		case "*":
			input.setI(a*b);
			return input;
		case "%":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			input.setI(a%b);
			return input;
		case"**":
			input.setI((int) Math.pow(a,b));
			return input;
			
		case"e":
			input.setI((int) (a * Math.pow(10, b)));
			return input;
			
		case "/":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			input.setD((double) ((double)a/(double)b));
			return input;
		
		case"//":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			input.setI((int) (a/b));
			return input;
		}
		input.setD(null);
		input.setI(null);
		return input;
		
	}
	
	protected static void runBlock(HashMap<String, InstructionContent> blockInstructionsMap, String input, Map<String, String> map) {	
		try{
			if(SimpleParser.isFunctionCall(input)){
				runFunction(map, input);
				return;
			}
			if(SimpleParser.isForLoop(blockInstructionsMap.get(input).getBlockName())){
				runForLoop(map, blockInstructionsMap.get(input), blockInstructionsMap);
				return;
			}
			if(SimpleParser.isIf(blockInstructionsMap.get(input).getBlockName())){
				runIfBlock(map, blockInstructionsMap.get(input), blockInstructionsMap);
				return;
			}
		
		}
		catch(Exception e){
			System.out.println("CATCH IN RUNBLOCK");
		}
		
		
	}

	
	protected static InstructionContent runFunction(Map<String, String> map, String input) {
		
		String[] tokens = Tokenizer.tokenize(input);
		String funcName = tokens[0];
		String params = input.substring(input.indexOf('(')+1, input.lastIndexOf(')'));
		String[] paramValues = params.split(",");
		InstructionContent functionProperties = Interpreter.functionMap.get(funcName);
		List<String> functionInstructions = functionProperties.getInstructions();
		List<String> paramNames = functionProperties.getParamNames();
		int numberOfParams = functionProperties.getNumOfParams();
		//HashMap<String,String> localVarMap = functionProperties.getLocalVarMap();
		HashMap<String, String> localVarMap = new HashMap<>();
		HashMap<String,InstructionContent> instructionBlock = functionProperties.getBlockMap();
		String[] functionInstructionTokens;
		
		for(int i = 0; i < paramValues.length; i++){
			paramValues[i] = updateInput(map, Tokenizer.tokenize(paramValues[i]));
			paramValues[i] = eval(paramValues[i]);
		}
		
		for(int i = 0; i < numberOfParams; i++){
			localVarMap.put(paramNames.get(i), paramValues[i]);
		}
		
		for(int j = 0; j < functionInstructions.size(); j ++){
			String instructions = functionInstructions.get(j);
			functionInstructionTokens = Tokenizer.tokenize(instructions);

			if(SimpleParser.isReturn(instructions)) {
				Interpreter.funcReturn(input, functionInstructionTokens, localVarMap, instructionBlock, functionProperties);
				return functionProperties;
			}
			Interpreter.interpret(instructions, functionInstructionTokens,localVarMap, instructionBlock);
			try {
				if(!instructionBlock.get(instructions).getReturnStatment().equals("")) {
					functionProperties.setReturnStatment(instructionBlock.get(instructions).getReturnStatment());
					return functionProperties;
				}
			}catch(Exception e) {};
		}
		return functionProperties;
	}

	private static void runIfBlock(Map<String, String> map,  InstructionContent content, HashMap<String, InstructionContent> blockInstructionsMap) {
		
		StringBuilder builder = new StringBuilder();
		String[] conditionTokens;
		String input = content.getBlockName();
		List<String> ifInstructions = content.getInstructions();
		List<String> elseInstructions = content.getAltInstructions();
	
		for(int i = input.indexOf(' ')+1; i <input.lastIndexOf(':'); i ++){
			builder.append(input.charAt(i));
		}
		String condition = builder.toString();
		conditionTokens= Tokenizer.tokenize(condition);
			
		if(evalCondition(conditionTokens, map)){
			String[] instructionTokens;
			for(int j = 0; j < ifInstructions.size(); j ++){
				String instructions = ifInstructions.get(j);

				instructionTokens = Tokenizer.tokenize(instructions);
				if(SimpleParser.isReturn(instructions)) {
					Interpreter.funcReturn(input, instructionTokens, map, blockInstructionsMap, content);
					return;
				}
				Interpreter.interpret(ifInstructions.get(j), instructionTokens, map, blockInstructionsMap);
			}
		}
		else{
			String[] instructionTokens;
			for(int j = 0; j < elseInstructions.size(); j ++){
				String instructions = elseInstructions.get(j);
				instructionTokens = Tokenizer.tokenize(instructions);

				if(SimpleParser.isReturn(instructions)) {
					Interpreter.funcReturn(input, instructionTokens, map, blockInstructionsMap, content);
					return;
				}
				instructionTokens = Tokenizer.tokenize(elseInstructions.get(j));
				Interpreter.interpret(elseInstructions.get(j), instructionTokens, map, blockInstructionsMap);
			}
		}
	}

	private static void runForLoop(Map<String, String> map, InstructionContent content,
			HashMap<String, InstructionContent> blockInstructionsMap) {
		List<String> forInstructions = content.getInstructions();
		String input = content.getBlockName();
		String [] forLoopTokens = content.getBlockName().split("\\s");
		String range = Interpreter.arrayToString(forLoopTokens, 3, forLoopTokens.length);
		StringBuilder startRangeBuild = new StringBuilder();
		StringBuilder endRangeBuild = new StringBuilder();
		for (int i = 6; range.charAt(i)!=',' || range.charAt(i) != ')';i ++){
			
			if(range.charAt(i) == ','){
				for(int j = i+1; range.charAt(j)!=')'; j ++){
					endRangeBuild.append(range.charAt(j));
				}
				break;
			}
			if(range.charAt(i) == ')'){
				endRangeBuild.append(startRangeBuild.toString());
				startRangeBuild.setLength(0);
				startRangeBuild.append("0");
				break;
			}
			startRangeBuild.append(range.charAt(i));
		}
		
		String startRange = startRangeBuild.toString();
		String endRange= endRangeBuild.toString();
		String rangeVar = forLoopTokens[1];
		
		startRange = updateInput( map, Tokenizer.tokenize(startRange));
		endRange = updateInput(map, Tokenizer.tokenize(endRange));
		
		startRange = eval(startRange);
		endRange = eval(endRange);
		
		map.put(rangeVar, startRange);
		String[] instructionTokens;
		for(int i=Integer.parseInt(map.get(rangeVar)); i< Integer.parseInt(endRange); i++){
			map.put(rangeVar, Integer.toString(i));
					
			for(int j = 0; j < forInstructions.size(); j ++){	
				String instructions = forInstructions.get(j);
				instructionTokens = Tokenizer.tokenize(instructions);
				if(SimpleParser.isReturn(instructions)) {
					Interpreter.funcReturn(input, instructionTokens, map, blockInstructionsMap, content);
					return;
				}
				
				Interpreter.interpret(instructions, instructionTokens, map,blockInstructionsMap );
			}
			
		}
	}
	
	private static boolean evalCondition(String[] conditionTokens, Map<String, String> map) {
		String conditonOp = getconditionOp(conditionTokens);
		String statment = Interpreter.arrayToString(conditionTokens, 0, conditionTokens.length);	
		if(SimpleParser.containsVars(conditionTokens, 0, conditionTokens.length)){		
			statment = updateInput( map, conditionTokens);
		}
		conditionTokens = statment.split(REGEXCOMP);
		
		String leftString = conditionTokens[0];
		
		double left = Double.parseDouble(eval(leftString));
		String rightString = conditionTokens[1];	
		double right = Double.parseDouble(eval(rightString));
	
		switch(conditonOp){
		case ">":
			return left > right;
		case "<":
			return left < right;
		case "==":
			return left == right;
		case "!=":
			return left != right;
		default:
			return false;
		}
	}
	
	protected static String updateInput(Map<String, String> map, String[] tokens) {
		String[] tempToken;
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].matches(REGEXVAR)){
					if(map.containsKey(tokens[i])){
						tempToken = Tokenizer.tokenize(map.get(tokens[i]));
						if(SimpleParser.containsVars(tempToken, 0, tempToken.length)){
							updateInput(map, tempToken);
						}
						tokens[i] = map.get(tokens[i]);
					}
					else{
						return "";
					}
				}
			}
		
		if(SimpleParser.containsVars(tokens, 0, tokens.length)){
			updateInput(map, tokens);
		}
		return Interpreter.arrayToString(tokens, 0, tokens.length);
	}
	
	private static String getconditionOp(String[] conditionTokens) {
		for(int i = 0; i < conditionTokens.length; i ++){
			if(conditionTokens[i].matches(REGEXCOMP)){
				return conditionTokens[i];
			}
		}
		return null;
	}
	public static String updateFunction(Map<String, String> map,String input, InstructionContent functionProperties) {	
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < input.length(); i ++) {
			
			if(Character.toString(input.charAt(i)).matches("\\s*\\/{2}|\\s*\\*{2}\\s*|\\s*(?<!e)[\\+|\\-|\\*|\\/|%]\\s*") && !builder.toString().matches(REGEXPARTIALMETHODSIG)) {
				builder.setLength(0);
				continue;
			}
			builder.append(input.charAt(i));
			if(SimpleParser.isFunctionCall(builder.toString())) {
				input = input.replace(builder.toString(), functionProperties.getReturnStatment());
				builder.setLength(0);
				i = 0;
				
			}
		}
		return input;
	}

}