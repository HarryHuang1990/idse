package cn.iscas.idse.storage.entity;

import static com.sleepycat.persist.model.DeleteAction.NULLIFY;
import static com.sleepycat.persist.model.Relationship.ONE_TO_MANY;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * <p>
 * an Entity used to represent the TopicRelation component extracted from the user log
 * </p>
 * <p>structure:</p> 
 * <ol>
 * 	<li>documentID</li>
 * 	<li>related documentIDs</li>
 * </ol>
 * @author Harry Huang
 *
 */
@Entity
public class TopicRelation {
	@PrimaryKey
	private int documentID;
	
	/**
	 * the key is the ID of related document,
	 * the value is the KL distance value between the two file. 
	 */
	private Map<Integer, Double> relatedDocumentIDs = new HashMap<Integer, Double>();

	private TopicRelation(){}
	
	public TopicRelation(int documentID){
		this.documentID = documentID;
	}
	
	public TopicRelation(int documentID, Map<Integer, Double>relatedDocumentIDs){
		this.documentID = documentID;
		this.relatedDocumentIDs = relatedDocumentIDs;
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
	/**
	 * put a new relation into graph.
	 */
	public void putNewRelation(int targetDocID, double KLDistance){
		this.relatedDocumentIDs.put(targetDocID, KLDistance);
	}
}
