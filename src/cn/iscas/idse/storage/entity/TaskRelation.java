package cn.iscas.idse.storage.entity;

import static com.sleepycat.persist.model.DeleteAction.NULLIFY;
import static com.sleepycat.persist.model.Relationship.ONE_TO_MANY;

import java.util.HashSet;
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

	private Set<Integer> relatedDocumentIDs = new HashSet<Integer>();

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

	public Set<Integer> getRelatedDocumentIDs() {
		return relatedDocumentIDs;
	}

	public void setRelatedDocumentIDs(Set<Integer> relatedDocumentIDs) {
		this.relatedDocumentIDs = relatedDocumentIDs;
	}
	
}
