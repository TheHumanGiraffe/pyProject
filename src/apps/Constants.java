package apps;

public final class Constants {
	
	private Constants(){
		
	}
	
	public static final String REGEXVAR = "^[a-zA-Z_]+\\w*$";
	public static final String REGEXINTEGER = "\\d+";
	public static final String REGEXFLOAT = "\\d*\\.\\d+|\\d+\\.\\d*|\\s*\\d*\\.\\d+";
	public static final String REGEXSCIPOSTFIX = String.format("[eE][+\\-]?(%s)?", REGEXINTEGER);
	public static final String REGEXNUMBER = String.format("(%s|(^%s$))(%s)?|(%s|(^%s))(%s)?", REGEXINTEGER,REGEXFLOAT,REGEXSCIPOSTFIX, REGEXINTEGER,REGEXFLOAT,REGEXSCIPOSTFIX);
	public static final String REGEXUNARYOPERATOR = "\\s*[\\-\\+\\s]*\\s*";
	public static final String REGEXBINARYOPERATOR = "\\s*[\\-\\+\\%]{1}\\s*|\\s*[\\*]{1,2}\\s*|\\s*[\\/]{1,2}\\s*|\\s*[+]{1,}\\s*|\\s*[\\-]{1,}\\s*";
	public static final String REGEXPARENTHESES = "\\s*[\\(\\)]{1}\\s*";
	public static final String REGEXEXTENDEDNUMBER  = String.format("(%s)(%s)", REGEXUNARYOPERATOR, REGEXNUMBER);	
	public static final String REGEXEXPR = String.format("^(%s|%s)(?>(%s)(%s)+)*$" , REGEXNUMBER, REGEXEXTENDEDNUMBER, REGEXBINARYOPERATOR, REGEXNUMBER);
}
