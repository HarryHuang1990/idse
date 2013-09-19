package cn.iscas.idse.index.segmentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import cn.iscas.idse.config.SystemConfiguration;

/**
 * remove stop words and punctuation from the results of segmentation
 * @author Harry Huang
 *
 */
public class StopWordAndPunctuationFilter {
	
	public static StopWordAndPunctuationFilter instance = null;
	private Set<String>stopWordEnglish = new HashSet<String>();
	private Set<String>stopWordChinese = new HashSet<String>();
	
	public static StopWordAndPunctuationFilter getInstance(){
		if(instance == null){
			instance = new StopWordAndPunctuationFilter();
		}
		return instance;
	}
	
	StopWordAndPunctuationFilter(){
		//TODO ddd
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(SystemConfiguration.rootPath+"/resource/stop_word_ch.txt")));
			reader.close();
			reader = new BufferedReader(new FileReader(new File(SystemConfiguration.rootPath+"/resource/stop_word_en.txt")));
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * the function runs the procedure of stop-word-remove and punctuation-remove 
	 * @param termSet
	 */
	public void execute(Set<TermOffset> termSet){
		if(termSet != null){
			for(TermOffset term : termSet){
				
			}
		}
	}
	
	/**
	 * remove the term if it is a stop word.
	 * @param term
	 * @return
	 */
	private String removeStopWord(String term){
		return term.replaceAll("[\\pP+~$`^=|<>¡«£à¡ç£Þ£«£½£ü£¼£¾£¤¡Á]", "");
	}
	
	/**
	 * remove the punctuation that is contained in the term string.
	 * @param term
	 * @return
	 */
	private String removePunctuation(String term){
		return term.replaceAll("[\\pP+~$`^=|<>¡«£à¡ç£Þ£«£½£ü£¼£¾£¤¡Á]", "").trim();
	}
	
	
	public static void main(String args[]){
		String s = "`~!@#$%^&*()_+-=[]\\{}|:\";',./<>?/*-+¡«£¡£À£££à¡ç£¥£Þ£¦£ª£¨£©£ß£«£­£½£û£ý£ü£Û£Ý£Ü£º£¢£»£§£¼£¾£¿£¬£®£¯£¯£ª£­£«~¡¤£¡@#£¤%¡­¡­&*£¨£©¡ª¡ª+-={}|¡¾¡¿¡¢£º¡°£»¡®¡¶¡·£¿£¬¡£¡¢/*-+¡«¡¤£¡£À£££¤£¥¡­¡­£¦¡Á£¨£©¡ª¡ª£«£­£½¡¾¡¿£Ü£û£ý£ü£»¡¯£º¡±£¬¡£¡¢¡¶¡·£¿£¯£ª£­£«";
		System.out.println(s.replaceAll("\\p{Punct}", ""));       
        System.out.println(s.replaceAll("[\\pP+~$`^=|<>¡«£à¡ç£Þ£«£½£ü£¼£¾£¤¡Á]", ""));       
        System.out.println(s.replaceAll("[\\p{P}+~$`^=|<>¡«£à¡ç£Þ£«£½£ü£¼£¾£¤¡Á]", "")); 
	}
}
