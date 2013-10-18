package cn.iscas.idse.rank.topic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jgibblda.LDA;

/**
 * implements the calculation of similarity between 
 * cluster and cluster , file and file.
 * @author Harry Huang
 *
 */
public class TopicSimilarity {

	/**
	 * calculate the similarity of given two files
	 * @param fileA
	 * @param fileB
	 */
	public double similarityBetweenFiles(TopicFile fileA, TopicFile fileB){
		
		double sqrtNormalA = fileA.getSqrtSumOfSquare();
		double sqrtNormalB = fileB.getSqrtSumOfSquare();
		double similarity = 0.0;
		
		for(Entry<String, Double> fileAWord : fileA.getTopicWords().entrySet()){
			if(fileB.getTopicWords().containsKey(fileAWord.getKey())){
				similarity += fileAWord.getValue()*fileB.getTopicWords().get(fileAWord.getKey());
				System.out.println(fileAWord.getKey() + " : " + fileAWord.getValue() + "\t" + fileB.getTopicWords().get(fileAWord.getKey()));
			}
		}
		similarity /= (sqrtNormalA * sqrtNormalB);
		
		return similarity;
	}
	
	/**
	 * calculate the similarity of given two clusters
	 * @param clusterA
	 * @param clusterB
	 */
	public void similarityBetweenClusters(TopicCluster clusterA, TopicCluster clusterB){
		
	}
	
	
	public static void main(String args[]){
		LDA lda = new LDA();
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		TopicCluster cluster = new TopicCluster();
		for(int i=0; i<=7; i++){
			cluster.addTopicFile(lda.getTopicFile("0000000" + i));
		}
		System.out.println(cluster.toString());

		
//		TopicSimilarity ts = new TopicSimilarity();
//		System.out.println(ts.similarityBetweenFiles(lda.getTopicFile("00000001"), lda.getTopicFile("00000002")));
	}
	
}
