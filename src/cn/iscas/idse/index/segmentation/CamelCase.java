package cn.iscas.idse.index.segmentation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class is used to split words based on the camel case, such as "CamelCase", "SquashOurMethodNamesTogetherLikeThis", etc.
 * the name of class or method in the programming language is usually written as the camel case style.
 * @author Harry Huang
 *
 */
public class CamelCase {
	/**
	 * split words based on the camel case.the regex using in this function can identify standard Camel Case Style, such as "CamelCase" and ""SquashOurMethodNamesTogetherLikeThis"", 
	 * and non-standard Camel Case Style, such as "UPPER2000UPPER", "hasABREVIATIONEmbedded", "Client2Server2012"
	 * @param word
	 * @return
	 */
	public static String[] splitCamelCase(String word){
		String[] words = word.split("(?<!(^|[A-Z0-9]))(?=[A-Z0-9])|(?<!(^|[^A-Z]))(?=[0-9])|(?<!(^|[^0-9]))(?=[A-Za-z])|(?<!^)(?=[A-Z][a-z])");
		return words;
	}
	
	public static void main(String args[]){
		String[]words = CamelCase.splitCamelCase("hasABREVIATIONEmbedded");
		for(String s : words)
			System.out.println(s);
	}
}
