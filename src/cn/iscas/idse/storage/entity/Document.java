package cn.iscas.idse.storage.entity;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import static com.sleepycat.persist.model.Relationship.*;
import static com.sleepycat.persist.model.DeleteAction.*;

/**
 * <p>
 * an Entity used to represent the Document
 * </p>
 * <p>structure:</p> 
 * <ol>
 * 	<li>document id (primary key, unique)</li>
 * 	<li>directory id (the directory containing this document)</li>
 * 	<li>document name</li>
 * </ol>
 * @author Harry Huang
 *
 */
@Entity
public class Document {
	
	@PrimaryKey(sequence="DocID")
	private int docID;
	
	@SecondaryKey(relate=MANY_TO_ONE, relatedEntity=Directory.class, onRelatedEntityDelete=CASCADE)
	private int directoryID;
	
	@SecondaryKey(relate=MANY_TO_ONE)
	private String documentName;
	
	private Document(){}
	
	public Document(int docID, int directoryID, String documentName){
		this.docID = docID;
		this.directoryID = directoryID;
		this.documentName = documentName;
	}
	
	public Document(int directoryID, String documentName){
		this.directoryID = directoryID;
		this.documentName = documentName;
	}

	public int getDocID() {
		return docID;
	}

	public void setDocID(int docID) {
		this.docID = docID;
	}

	public int getDirectoryID() {
		return directoryID;
	}

	public void setDirectoryID(int directoryID) {
		this.directoryID = directoryID;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	
	
}
