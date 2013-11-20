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
 * an Entity used to represent the LocationRelation component extracted from the user log
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
public class LocationRelation {
	@PrimaryKey
	private int documentID;
	/**
	 * key : id of the related document
	 * value : the location relation score between the two documents.
	 */
	private Map<Integer, Float> relatedDocumentIDs = new HashMap<Integer, Float>();

	private LocationRelation(){}
	
	public LocationRelation(int documentID){
		this.documentID = documentID;
	}
	
	public int getDocumentID() {
		return documentID;
	}

	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}

	public Map<Integer, Float> getRelatedDocumentIDs() {
		return relatedDocumentIDs;
	}

	public void setRelatedDocumentIDs(Map<Integer, Float> relatedDocumentIDs) {
		this.relatedDocumentIDs = relatedDocumentIDs;
	}
	/**
	 * add a new related document and the relation score to the graph.
	 * @param docID
	 * @param score
	 */
	public void putNewRelatedDocument(int docID, float score){
		this.relatedDocumentIDs.put(docID, score);
	}
}
