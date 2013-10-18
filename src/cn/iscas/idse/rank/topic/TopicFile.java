package cn.iscas.idse.rank.topic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * represents the topic of file
 * @author Harry Huang
 *
 */
public class TopicFile {
	
	private int documentID = 0;
	/**
	 * topic words of given file
	 */
	private Map<String, Double> topicWords = new HashMap<String, Double>();

	public int getDocumentID() {
		return documentID;
	}

	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}

	public Map<String, Double> getTopicWords() {
		return topicWords;
	}

	public void setTopicWords(Map<String, Double> topicWords) {
		this.topicWords = topicWords;
	}
	
	/**
	 * get sqrt of the sum of square of each topic word probability, which is used to normalize
	 * @return
	 */
	public double getSqrtSumOfSquare(){
		double sum = 0.0;
		for(Entry<String, Double>entry : this.topicWords.entrySet()){
			sum += Math.pow(entry.getValue(), 2);
		}
		
		return Math.sqrt(sum);
	}
	
	public String toString(){
		return this.topicWords.toString();
	}
}
