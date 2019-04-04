package GroupProject;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class Tokenizer {	
	public boolean validateTokens(String[] tokens){
		String regexVar = "^[a-zA-Z_]*\\w*$";
		String regexInteger = "\\d+";
		String regexFloat = "\\d*\\.\\d+|\\d+\\.\\d*|\\s*\\d*\\.\\d+";	// Need fix to reject "."
		String regexSciPostfix = String.format("[eE][+\\-]?(%s)?", regexInteger);
		String regexNumber = String.format("(%s|(^%s$))(%s)?|(%s|(^%s))(%s)?", regexInteger,regexFloat,regexSciPostfix, regexInteger,regexFloat,regexSciPostfix);
		String regexUnaryOperator = "\\s*[\\-\\+\\s]*\\s*";
		String regexBinaryOperator = "\\s*[\\-\\+\\%]{1}\\s*|\\s*[\\*]{1,2}\\s*|\\s*[\\/]{1,2}\\s*|\\s*[+]{1,}\\s*|\\s*[\\-]{1,}\\s*";
		String regexParentheses = "\\s*[\\(\\)]{1}\\s*";
		//String regexExtendedNumber  = String.format("(%s)(%s)", regexUnaryOperator, regexNumber);

	     for (int i=0; i<tokens.length; i++){
	    	 if(tokens[i].matches(regexNumber)|| tokens[i].matches(regexUnaryOperator)|| tokens[i].matches(regexBinaryOperator)
	    			 || tokens[i].matches(regexParentheses)|| tokens[i].matches(regexVar)){
	    		 //System.out.printf("Token %d: %s Is Valid\n",i, tokens[i]);
	    	 }
	    	 else{
	    		 //System.out.printf("Token %d: %s Is NOT VALID\n", i, tokens[i]);
	    		 return false;
	    	 }
	     }
	     return true;
	}
	
	public String[] tokenize(String s){
		List<String> tokens = new ArrayList<String>();
		String temp = "";
		for(int i =0; i < s.length(); i++){
			if(Character.toString(s.charAt(i)).matches("[^+\\-\\*\\/\\(\\)\\%]")){ //(\\d||\\.||e||[a-zA-z])||\\s")
				
				temp += s.charAt(i);				
			}
			else if(Character.toString(s.charAt(i)).matches("([+\\-\\*\\/\\(\\)\\%])")){
				if(temp.length()!=0){
					String temp2 = temp.trim();
					
					if(temp2.length() != 0){
						if(temp2.charAt(temp2.length()-1)=='e'){
							tokens.add(temp);
						}
						else{
							tokens.add(temp.trim());
						}
						
						temp="";
						
					}
					
					
				}
				if((Character.toString(s.charAt(i)).matches("[\\*]") && Character.toString(s.charAt(i+1)).matches("[\\*]"))
						||Character.toString(s.charAt(i)).matches("[\\/]") && Character.toString(s.charAt(i+1)).matches("[\\/]")){
					char temp1 = s.charAt(i);
					char temp2 = s.charAt(i+1);				
					String temp3 = Character.toString(temp1) + Character.toString(temp2);				
					tokens.add(temp3);
					i++; //increment i to avoid checking the second * or / since both are added to the same token
				}
				else{
					tokens.add(Character.toString(s.charAt(i)));
				}
							
			}
		}
		
		String temp2 = temp.trim();
		
		if(temp2.length() != 0){
			if(temp2.charAt(temp2.length()-1)=='e'){
				tokens.add(temp);
			}
			else{
				tokens.add(temp.trim());
			}
			
			temp="";
			
		}
		
		String[] result = new String[tokens.size()];
		return tokens.toArray(result);
		
		
		
	}
	
	public static void main(String[] args) {
	
		Tokenizer token = new Tokenizer();
		String[] result;
		result = token.tokenize("1.23e + 1");
		
		token.validateTokens(result);
	}

}
