package apps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
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
public class Evaluator {
	String regexVar = "[a-zA-Z_]\\w+|[a-df-zA-DF-Z_]\\w*";
	String regexInteger = "\\d+";
	String regexFloat = "\\d*\\.\\d+|\\d+\\.\\d*|\\s*\\d*\\.\\d+";
	String regexSciPostfix = String.format("[eE][+\\-]?%s", regexInteger);
	String regexNumber = String.format("(%s|(^%s$))(%s)?|(%s|(^%s))(%s)|(%s)", regexInteger,regexFloat,regexSciPostfix, regexInteger,regexFloat,regexSciPostfix,regexVar);

	private static void runTest(String[] data, Tokenizer token, String[] tokenized, SimpleParser parser, Evaluator eval){
		for(int i =0; i < data.length; i++){
			tokenized = token.generateTokens(data[i]);
			if(token.validateTokens(tokenized)){
				if(parser.parseTokens(tokenized)){
					
					
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
		Stack<Float> values = new Stack<Float>();
		Stack<String> ops = new Stack<String>();
		
		for(int i=0; i < tokens.length; i++){
			
			if(tokens[i] == " "){
				continue;
			}
						
			if(tokens[i].matches(regexNumber)){
				float toFloat;
				try{
					Integer toInt = Integer.parseInt(tokens[i]);
					toFloat = toInt.floatValue();
				}
				catch(NumberFormatException e){
					toFloat = Float.parseFloat(tokens[i]);
				}
				
				values.push(toFloat);
			}
			
			else if(tokens[i].equals("(")){
				ops.push(tokens[i]);
			}
			
			else if(tokens[i].equals(")")){
				
				while (!ops.isEmpty() && !ops.peek().equals("(")){
					if(values.size() >1){
						values.push(applyOp(ops.pop(), values.pop(), values.pop()));
					}
					else{
						values.push(applyOp(ops.pop(), values.pop(), (float) 0.0));
					}				
				}
				ops.pop();
			}
			
			else if(tokens[i].matches("[\\+\\-\\*\\/\\%]|[\\*]{2}|[\\/]{2}|e")){
				while(!ops.empty() && hasPrecedence(tokens[i], ops.peek())){
					values.push(applyOp(ops.pop(), values.pop(), values.pop()));
				}
				ops.push(tokens[i]);
			}
		}
		
		while(!ops.empty()){
			try{
				if(values.size() > 1){
					values.push(applyOp(ops.pop(), values.pop(), values.pop()));
				}
				ops.pop();
			}
			catch( EmptyStackException e){
				;
			}
		}
		
		return values.pop().toString();
		
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
	public static float applyOp(String op, float b, float a){
		switch(op){
		case "+":
			return a+b;
		
		case "-":
			return a-b;
		case "*":
			return a*b;
		case "%":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			return a%b;
		case"**":
			return (float) Math.pow(a,b);
		case"e":
			return (float) (a * Math.pow(10, b));
			
		case "/":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			return a/b;		
		
		case"//":
			if(b==0){
				throw new
                	UnsupportedOperationException("Cannot divide by zero"); 
			}
			int c =  (int) (a/b);
			return c;
		}
		return 0;
		
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
