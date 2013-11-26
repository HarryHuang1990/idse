package cn.iscas.idse.search;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import cn.iscas.idse.config.InstanceManager;
import cn.iscas.idse.index.segmentation.CamelCase;
import cn.iscas.idse.index.segmentation.StopWordFilter;
import cn.iscas.idse.index.segmentation.TermLemmatizer;
import cn.iscas.idse.index.segmentation.WordSegmentation;
import cn.iscas.idse.search.entity.Query;

/**
 * parse the query given by user and generate a 
 * Query Entity for subsequent search. this is mainly used for key word search.
 * @author Harry Huang
 *
 */
public class QueryParser {
	
	/**
	 * query Entity
	 */
	private Query queryEntity;
	/**
	 * query string
	 */
	private String query;
	
	public QueryParser(){}
	
	public QueryParser(String query){
		this.query = query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * parse the query string and get the basic info of query
	 */
	public void parse(){
		this.queryEntity = new Query(this.query);
		WordSegmentation segmentation = (WordSegmentation)InstanceManager.getInstance(InstanceManager.CLASS_WORDSEGMENTATION);
		segmentation.initialize();
		String segmentResult = segmentation.segmentString(this.query);
		segmentation.exitICTCLAS();
//		segmentation.destoryInstance();
		if(segmentResult != null){
			StringTokenizer tokenizer = new StringTokenizer(segmentResult);
			String currentTerm = "";
			int offset = -1;
			while(tokenizer.hasMoreTokens()){
				currentTerm = tokenizer.nextToken().trim();
				//handle Camel Case style
				String[] words = CamelCase.splitCamelCase(currentTerm);
				if(words != null){
					for(String word : words){
						offset++;
						//lowercase the word
						word = word.toLowerCase();
						// handle Lemmatize
						word = ((TermLemmatizer)InstanceManager.getInstance(InstanceManager.CLASS_TERMLEMMATIZER)).adornText(word);
						// filter stop word
						if(word != null && !((StopWordFilter)InstanceManager.getInstance(InstanceManager.CLASS_STOPWORDFILTER)).isStopWord(word)){
							if(this.queryEntity.getQueryPosting().containsKey(word)){
								this.getQueryEntity().getQueryPosting().get(word).add(offset);
							}
							else{
								List<Integer> offsetList = new ArrayList<Integer>();
								offsetList.add(offset);
								this.getQueryEntity().getQueryPosting().put(word, offsetList);
							}
						}
					}
				}
			}
		}
	}

	public Query getQueryEntity() {
		return queryEntity;
	}
}
