package cn.iscas.idse.rank.topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.iscas.idse.utilities.Sort;

/**
 * represents the topic of cluster
 * @author Harry Huang
 *
 */
public class TopicCluster {
	
	private int clusterID = 0;
	/**
	 * topic words of given cluster
	 */
	private Map<String, Integer> topicWords = new HashMap<String, Integer>();

	public int getClusterID() {
		return clusterID;
	}

	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}

	public Map<String, Integer> getTopicWords() {
		return topicWords;
	}

	public void setTopicWords(Map<String, Integer> topicWords) {
		this.topicWords = topicWords;
	}

	/**
	 * add a topic of file to the cluster
	 * @param topicFile
	 */
	public void addTopicFile(TopicFile topicFile){
		for(String word : topicFile.getTopicWords().keySet()){
			if(this.topicWords.containsKey(word)){
				this.topicWords.put(word, (this.getTopicWords().get(word) + 1));
			}
			else{
				this.topicWords.put(word, 1);
			}
		}
	}
	
	public String toString(){
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(topicWords.entrySet());
		Sort.sortStringIntegerMapDesc(list);
		return topicWords.toString();
	}
	
}
