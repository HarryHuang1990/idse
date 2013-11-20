package cn.iscas.idse.storage.entity;

import java.util.HashMap;
import java.util.Map;

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
	/**
	 * key : id of the related document
	 * value : the Integrated Correlation score between the two documents.
	 */
	private Map<Integer, Double> relatedDocumentIDs = new HashMap<Integer, Double>();
	
	private PageRankGraph(){}
	
	public void PageRankGraph(int documentID){
		this.documentID = documentID;
	}

	public int getDocumentID() {
		return documentID;
	}

	public void setDocumentID(int documentID) {
		this.documentID = documentID;
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
}
