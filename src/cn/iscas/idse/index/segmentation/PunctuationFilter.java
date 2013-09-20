package cn.iscas.idse.index.segmentation;

/**
 * Filter the punctuation from the input string. 
 * @author Harry Huang
 *
 */
public class PunctuationFilter {
	/**
	 * remove the punctuation that is contained in the term string.
	 * @param term
	 * @return
	 */
	public static String removePunctuation(String term){
		return term.replaceAll("[\\pP+~$`^=|<>¡«£à¡ç£Þ£«£½£ü£¼£¾£¤¡Á]", "").trim();
	}
	
	public static void main(String args[]){
		String s = "`~!@#$%^&*()_+-=[]\\{}|:\";',./<>?/*-+¡«£¡£À£££à¡ç£¥£Þ£¦£ª£¨£©£ß£«£­£½£û£ý£ü£Û£Ý£Ü£º£¢£»£§£¼£¾£¿£¬£®£¯£¯£ª£­£«~¡¤£¡@#£¤%¡­¡­&*£¨£©¡ª¡ª+-={}|¡¾¡¿¡¢£º¡°£»¡®¡¶¡·£¿£¬¡£¡¢/*-+¡«¡¤£¡£À£££¤£¥¡­¡­£¦¡Á£¨£©¡ª¡ª£«£­£½¡¾¡¿£Ü£û£ý£ü£»¡¯£º¡±£¬¡£¡¢¡¶¡·£¿£¯£ª£­£«";
		System.out.println(s.replaceAll("\\p{Punct}", ""));       
        System.out.println(s.replaceAll("[\\pP+~$`^=|<>¡«£à¡ç£Þ£«£½£ü£¼£¾£¤¡Á]", ""));       
        System.out.println(s.replaceAll("[\\p{P}+~$`^=|<>¡«£à¡ç£Þ£«£½£ü£¼£¾£¤¡Á]", "")); 
	}

}
