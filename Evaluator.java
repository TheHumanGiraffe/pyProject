package GroupProject;
//work in progress
public class Evaluator {
	public int evalExpr(String expression) {
		String[] tokenized;
		Tokenizer token = new Tokenizer();
		SimpleParser parser = new SimpleParser();
		Evaluator eval = new Evaluator();
		
		tokenized = token.tokenize(expression);
		if(token.validateTokens(tokenized)){
			if(parser.isExpr(tokenized)){
				System.out.printf("PASS: %s\n", expression);
				eval.tokenToInt(tokenized);
			}
			else{
				System.out.printf("FAIL: %s\n", expression );
			}
			
		}
		else{
			System.out.printf("FAIL!: %s\n", expression);
		}
		return 0;
		
	}
	
	private void tokenToInt(String[] tokenized) {
		
	}

	public static void main(String[] args) {
		Evaluator eval = new Evaluator();
		System.out.println(eval.evalExpr("234+1234"));
	}

}
