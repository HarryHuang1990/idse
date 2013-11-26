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
public class StopWordFilter {
	
//	private static StopWordFilter instance = null;
	private static Set<String>stopWordEnglish = new HashSet<String>();
	private static Set<String>stopWordChinese = new HashSet<String>();
	
//	public static StopWordFilter getInstance(){
//		if(instance == null){
//			instance = new StopWordFilter();
//		}
//		return instance;
//	}
	
	public StopWordFilter(){
//		if(stopWordChinese == null && stopWordEnglish == null){
			stopWordEnglish = new HashSet<String>();
			stopWordChinese = new HashSet<String>();
			try {
				String stopWord;
				/*
				 * load the Chinese stop word list;
				 */
				BufferedReader reader = new BufferedReader(new FileReader(new File(SystemConfiguration.rootPath+"/resource/stop_word_ch.txt")));
				while((stopWord = reader.readLine()) != null){
					stopWordChinese.add(stopWord.trim());
				}
				reader.close();
				/*
				 * load the English stop word list;
				 */
				reader = new BufferedReader(new FileReader(new File(SystemConfiguration.rootPath+"/resource/stop_word_en.txt")));
				while((stopWord = reader.readLine()) != null){
					stopWordEnglish.add(stopWord.trim());
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
//		}
	}
	
	/**
	 * destroy the instance and release its memory.
	 */
//	public void destoryInstance(){
//		instance = null;
//	}
	
	/**
	 * the function runs the procedure of stop-word-remove
	 * @param termSet
	 */
	public void execute(Set<TermOffset> termSet){
		if(termSet != null){
			Set<TermOffset> wordToRemoveSet = new HashSet<TermOffset>();
			for(TermOffset termOffset : termSet){
				if(this.isStopWord(termOffset.getTerm()))
					wordToRemoveSet.add(termOffset);
			}
			termSet.removeAll(wordToRemoveSet);
		}
	}
	
	/**
	 * remove the term if it is a stop word.
	 * @param term
	 * @return if term is a stop word, then return true; else return false.
	 */
	public boolean isStopWord(String term){
		
		if(stopWordChinese.contains(term) || stopWordEnglish.contains(term))
			return true;
		return false;
		
	}
	
	public static void main(String args[]){
		StopWordFilter swf = new StopWordFilter();
		
		Set<TermOffset> s = new HashSet<TermOffset>();
		TermOffset t1 = new TermOffset("a", 12);
		TermOffset t2 = new TermOffset("µÄ", 12);
		TermOffset t3 = new TermOffset("Ëã·¨", 12);
		TermOffset t4 = new TermOffset("pagerank", 14);
		s.add(t1);s.add(t2);s.add(t3);s.add(t4);
		
		swf.execute(s);
		
		for(TermOffset set: s){
			System.out.println(set.getTerm() + "\t" + set.getOffset());
		}
	}

}
