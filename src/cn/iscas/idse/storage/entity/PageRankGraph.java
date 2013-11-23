package cn.iscas.idse.storage.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;

import cn.iscas.idse.search.entity.Score;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * represents the General knowledge graph integrating  
 * task relation graph, topic relation graph, location relation graph. 
 * the graph is ready for pageRank. 
 * @author Harry Huang
 *
 */
@Entity
public class PageRankGraph {
	@PrimaryKey
	private int documentID;
	
	private double pageRankScore;
	/**
	 * key : id of the related document
	 * value : the Integrated Correlation score between the two documents before written into DB;
	 * 		   the exact value is the transfer probability converted by {@link Function convertScoreToProbs()}
	 */
	private Map<Integer, Double> relatedDocumentIDs = new HashMap<Integer, Double>();
	/**
	 * recommended candidates to user. only the top5 doc IDs. 
	 */
	private List<Integer>recommendedDocs = new ArrayList<Integer>();
	
	private PageRankGraph(){}
	
	public PageRankGraph(int documentID){
		this.documentID = documentID;
	}

	public int getDocumentID() {
		return documentID;
	}

	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}

	public double getPageRankScore() {
		return pageRankScore;
	}

	public void setPageRankScore(double pageRankScore) {
		this.pageRankScore = pageRankScore;
	}

	public Map<Integer, Double> getRelatedDocumentIDs() {
		return relatedDocumentIDs;
	}

	public void setRelatedDocumentIDs(Map<Integer, Double> relatedDocumentIDs) {
		this.relatedDocumentIDs = relatedDocumentIDs;
	}
	
	public void putNewRelatedDoc(int docID, double score){
		this.relatedDocumentIDs.put(docID, score);
	}
	
	public List<Integer> getRecommendedDocs() {
		return recommendedDocs;
	}

	public void setRecommendedDocs(List<Integer> recommendedDocs) {
		this.recommendedDocs = recommendedDocs;
	}

	/**
	 * convert the correlation score to transfer probability from this document to related ones.  
	 * the transfered probs replace the value of relatedDocumentIDs.map
	 */
	public void convertScoreToProbs(){
		double sumOfCorrelation = 0;
		for(double score : this.relatedDocumentIDs.values())
			sumOfCorrelation += score;
		
		for(Entry<Integer, Double>doc : this.relatedDocumentIDs.entrySet()){
			this.relatedDocumentIDs.put(doc.getKey(), doc.getValue() / sumOfCorrelation);
		}
	}
}
