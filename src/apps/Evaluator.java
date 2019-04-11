package apps;

import static apps.Constants.REGEXNUMBER;

import java.util.EmptyStackException;
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

class Input{
	Integer i;
	Float f;
	
	public Integer getI() {
		return i;
	}
	public void setI(Integer i) {
		this.i = i;
	}
	public Float getF() {
		return f;
	}
	public void setF(Float f) {
		this.f = f;
	}
	
}

public class Evaluator {
	public String eval(String s){
		Tokenizer token = new Tokenizer();
		String[] tokenized;
		SimpleParser parser = new SimpleParser();
		
		tokenized = token.tokenize(s);
		if(token.validateTokens(tokenized)){
			if(parser.isExpr(s)){
				
				String result = toPreFix(tokenized);
				//System.out.printf("PASS: %s\n", s);
				System.out.printf(result);
				return result;
			}
				else{
					//System.out.printf("FAIL: %s\n", s );
				}				
		}
		else{
			//System.out.printf("FAIL!: %s\n", s);
		}
		return null;
			
	}
	private static void runTest(String[] data, Tokenizer token, String[] tokenized, SimpleParser parser, Evaluator eval){
		for(int i =0; i < data.length; i++){
			tokenized = token.tokenize(data[i]);
			if(token.validateTokens(tokenized)){
				if(parser.isExpr(data[i])){
					
					
					System.out.printf("PASS: %s\n", data[i]);
					System.out.printf("Result for %s: %s \n", data[i], eval.toPreFix(tokenized));
				}
				else{
					System.out.printf("FAIL: %s\n", data[i] );
				}				
			}
			else{
				System.out.printf("FAIL!: %s\n", data[i]);
			}
		}
	}

	/**
	 * Converts given tokens into separate values and operators to determine the order of operations 
	 * for the equation.
	 * 
	 * @param tokens - Array of tokenized substrings.
	 * @return 
	 */
	private String toPreFix(String[] tokens){
		Stack<Input> values = new Stack<Input>();
		Stack<String> ops = new Stack<String>();
		
		for(int i=0; i < tokens.length; i++){
			
			if(tokens[i] == " "){
				continue;
			}
						
			if(tokens[i].matches(REGEXNUMBER)){
				Input input = new Input();
				float toFloat;
				try{
					Integer toInt = Integer.parseInt(tokens[i]);
					toFloat = toInt.floatValue();
					
					input.setI(toInt);
					input.setF(null);
				}
				catch(NumberFormatException e){
					toFloat = Float.parseFloat(tokens[i]);
					input.setF(toFloat);
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
				//ops.pop();
			}
			catch( EmptyStackException e){
				;
			}
		}
		if(values.peek().getF()!=null){
			return values.pop().getF().toString();
		}
		else{
			return values.pop().getI().toString();
		}
		
	}

	private void chooseType(Stack<Input> values, Stack<String> ops) {
		if(values.size() >1){
			if(values.peek().getF() != null){
				Input tmp = new Input();
				tmp = values.pop();
				if(values.peek().getF() != null){
					values.push(tmp);
					values.push(applyOp(ops.pop(), values.pop().getF(), values.pop().getF()));
				}
				else{
					values.push(tmp);
					values.push(applyOp(ops.pop(), values.pop().getF(), values.pop().getI().floatValue()));
				}
			}
			else{
				Input tmp = new Input();
				tmp = values.pop();
				if(values.peek().getF() != null){
					values.push(tmp);
					values.push(applyOp(ops.pop(), values.pop().getI().floatValue(), values.pop().getF()));
	
				}
				else{
					values.push(tmp);
					values.push(applyOp(ops.pop(), values.pop().getI(), values.pop().getI()));
	
				}
			}
		}
		else{
			if(values.peek().getF() != null){
				values.push(applyOp(ops.pop(), values.pop().getF(),(float) 0.0));
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
	 * Applies an operator to two floats and returns the result of the equation
	 * @param op - Operator
	 * @param b - float
	 * @param a - float
	 * @return Result of an equation relating to a given operation
	 */
	public static Input applyOp(String op, float b, float a){
		Input input = new Input();
		switch(op){
		case "+":
			input.setF(a+b);
			return input;
		
		case "-":
			input.setF(a-b);
			return input;
		case "*":
			input.setF(a*b);
			return input;
		case "%":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			input.setF(a%b);
			return input;
		case"**":
			input.setF((float) Math.pow(a,b));
			return input;
			
		case"e":
			input.setF((float) (a * Math.pow(10, b)));
			return input;
			
		case "/":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			input.setF(a/b);
			return input;
		
		case"//":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			input.setI((int) (a/b));
			return input;
		}
		input.setF(null);
		input.setI(null);
		return input;
		
	}
	
	public static Input applyOp(String op, int b, int a){
		Input input = new Input();
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
			input.setF((float) (a/b));
			return input;
		
		case"//":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			input.setI((int) (a/b));
			return input;
		}
		input.setF(null);
		input.setI(null);
		return input;
		
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Tokenizer token = new Tokenizer();
		SimpleParser parser = new SimpleParser();
		Evaluator eval = new Evaluator();
		
		String [] tokenized = null;
		
			
		String[] exprValid = new String[] {
				"12e2",
				"1+2",
				"2*2",
				"3/4",
				"5%4",
				"11//10",
				"2**10",
				"1.5+2",
				"2.5*6.9",
				"5.5//4.25",
				"2.3**10.5",
				"8.05-6.1",
				"(2*5)+(2+5**2)",
				"9.9+1.1",
				"12+1.5",
				"1.5*2"
				/*"9-11",
				"-9-11"
				/*"3",
				"1337",
				"3.5",
				"0.123",
				".123",
				"5.",
				"1.23e+1",
				"1.23e10",
				//"-- - + 3",
				"1 + 21",
				"2.1 + .21",
				//"1--1",
				"1  +       3",
				"69+1",
				//"123++++++1",
				"12//3",
				"3*2",
				"420/420",
				"5%5",
				"3.0%5",
				"12%4",
				"8%2.5",
				"3//2+5**2%5",
				"110*2+2",
				"2**3",
				"1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1",
				//"3-5++0",
				"(1+2)*4/6",
				"(((123+3)))",
				"2*(3+4)*((5+3)-2)",
				"(((((((((123**2+5)))))))))",
				"1- - - - - + 2**2//50%2",
				"(1)(2)",
				"(+1)"
				/*"(1*2+num1)",
				"num1",
				"(num1)"*/
		};
		String[] exprInvalid = new String[] {
				"1 +",
				"Yes",
				"3.5.5",
				"3 5",
				"3/*5",
				"1.23**e",
				"1.23e2.0",
				"1.23e + 1",
				"1.23 e1",
				"5%%5",
				"*/+-*2",
				"2***3",
				"12..3 + 2",
				"1+1+1+",
				"69++",
				"70--",
				"+",
				"-",
				"6***9",
				"<33333333333333333",
				"123 123",
				"4 5 + 9**7",
				"6%%(6+5)",
				"5*((5)"
		};
		System.out.println("-- Valid expresssions -- ");
		runTest(exprValid, token, tokenized, parser,eval);
		//System.out.println("-- Invalid expresssions -- ");
		//runTest(exprInvalid, token, tokenized, parser,eval);
	}

}
