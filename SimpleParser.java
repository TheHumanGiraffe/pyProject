package GroupProject;
import java.util.Stack;

public class SimpleParser {
	
	private static void runTest(String[] data, Tokenizer token, String[] tokenized, SimpleParser parser){
		for(int i =0; i < data.length; i++){
			tokenized = token.tokenize(data[i]);
			if(token.validateTokens(tokenized)){
				if(parser.isExpr(tokenized)){
					System.out.printf("PASS: %s\n", data[i]);
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Tokenizer token = new Tokenizer();
		SimpleParser parser = new SimpleParser();		
		String [] tokenized = null;
			
		String[] exprValid = new String[] {
				"3",
				"1337",
				"3.5",
				"0.123",
				".123",
				"5.",
				"1.23e+1",
				"1.23e10",
				"-- - + 3",
				"1 + 21",
				"2.1 + .21",
				"1--1",
				"1  +       3",
				"69+1",
				"123++++++1",
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
				"3-5++0",
				"(1+2)*4/6",
				"(((123+3)))",
				"2*(3+4)*((5+3)-2)",
				"(((((((((123**2+5)))))))))",
				"1- - - - - + 2**2//50%2",
				"(1)(2)",
				"(+1)",
				"+ + - + +(12*12)/12/(12/12)**(+15)**-+-++-+-+-++15**15//15",
				"234",
				"(1+3)",
				"(1+3) * 45",
				"(1+ (2+1)) * 45",
				"(1+(2+1)) * (78+3*15)+45",
				"123+56*num1",
				"(123+56*num1)",
				"(123+56*12-)",
				"(num1+56*12)",
				"(num1)"
				
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
				"5*((5)",
				"1+",
				"1+*2",
				"sum1"
		};
		System.out.println("-- Valid expresssions -- ");
		runTest(exprValid, token, tokenized, parser);
		System.out.println("-- Invalid expresssions -- ");
		runTest(exprInvalid, token, tokenized, parser);
	}	
	boolean isExpr(String[] tokens){
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
	
	private String arrayToString(String[] array){
		StringBuilder builder = new StringBuilder();
		for(String s: array){
			builder.append(s);
		}
		String str = builder.toString();
		return str;		
	}
	
	private String[] removeParentheses(String[] tokens){
		Stack<String> stack = new Stack();
		Stack<String> reverseStack = new Stack();
		String tmp = "";
		for(int i = 0; i < tokens.length; i++){
			if(!tokens[i].equals(")")){
				stack.push(tokens[i]);
			}
			else if(tokens[i].equals(")")){
				while(!stack.peek().equals("(")){
					reverseStack.push(stack.pop());
						
							
				}	
				while(reverseStack.size() >0) {
					tmp +=reverseStack.pop();

				}
				//Reverse Reverese #uno #1 hop this time
				
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
	
	private boolean validate(String s){
		String regexVar = "[a-zA-Z_]\\w*";
		String regexInteger = "\\d+";
		String regexFloat = "\\d*\\.\\d+|\\d+\\.\\d*|\\s*\\d*\\.\\d+";
		String regexSciPostfix = String.format("e[+\\-]?%s", regexInteger);
		String regexNumber = String.format("(%s|(^%s$))(%s)?|(%s|(^%s))(%s)|%s", regexInteger,regexFloat,regexSciPostfix, regexInteger,regexFloat,regexSciPostfix, regexVar);
		String regexUnaryOperator = "\\s*[\\-\\+\\s]*\\s*";
		String regexBinaryOperator = "\\s*[\\-\\+\\%]{1}\\s*|\\s*[\\*]{1,2}\\s*|\\s*[\\/]{1,2}\\s*|\\s*[+]{1,}\\s*|\\s*[\\-]{1,}\\s*";
		String regexExtendedNumber  = String.format("(%s)(%s)", regexUnaryOperator, regexNumber);	
		//System.out.println(regexNumber);
		String regexExpr = String.format("^(%s|%s)(?>(%s)(%s)+)*$" , regexNumber, regexExtendedNumber, regexBinaryOperator, regexNumber);
		if(s.matches(regexExpr)){
			return true;
		}
		else{
			return false;
		}
	}
}
