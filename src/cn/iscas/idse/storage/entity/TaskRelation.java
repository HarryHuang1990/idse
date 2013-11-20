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
 * an Entity used to represent the Task component extracted from the user log
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
public class TaskRelation {
	@PrimaryKey
	private int documentID;
	
	/**
	 * this is the value of specific document in the personalization vector E 
	 * eValue  =  occurrence of documentID / total occurrence of log.
	 */
	private double eValue = 0;

	/**
	 * IDs of documents interacting with the current document, 
	 * and the corresponding frequency of interacting.
	 */
	private Map<Integer, Integer> relatedDocumentIDs = new HashMap<Integer, Integer>();

	private TaskRelation(){}
	
	public TaskRelation(int documentID){
		this.documentID = documentID;
	}
	
	public int getDocumentID() {
		return documentID;
	}

	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}

	public Map<Integer, Integer> getRelatedDocumentIDs() {
		return relatedDocumentIDs;
	}

	public void setRelatedDocumentIDs(Map<Integer, Integer> relatedDocumentIDs) {
		this.relatedDocumentIDs = relatedDocumentIDs;
	}
	
	public double geteValue() {
		return eValue;
	}

	public void seteValue(double eValue) {
		this.eValue = eValue;
	}

	/**
	 * put the id of document interacting with the one, 
	 * and increase the frequency by 1 
	 * @param docID
	 */
	public void putInteractionRecord(int docID){
		if(this.relatedDocumentIDs.containsKey(docID))
			this.relatedDocumentIDs.put(docID, this.relatedDocumentIDs.get(docID) + 1);
		else 
			this.relatedDocumentIDs.put(docID, 1);
	}
	
	
}
