package cn.iscas.idse.utilities;

/**
 * this is a Converting tool class containing all kinds of method to implement specific converting task.
 * 
 * @author Harry Huang
 *
 */
public class Converter {
	
	/**
	 * Convert backslash "\" to normal slash "/" 
	 * @param string
	 */
	public static String convertBackSlashToSlash(String string){
		
		return string != null ? string.replaceAll("\\\\", "/") : null;
	}
	
	public static void main(String args[]){
		String s = "dfs\\dsfdf\\sdfsd\\sdfs\\";
		System.out.println(convertBackSlashToSlash(s));
	}
}
