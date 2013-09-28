package cn.iscas.idse.storage.entity;

import static com.sleepycat.persist.model.DeleteAction.CASCADE;
import static com.sleepycat.persist.model.Relationship.*;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * <p>
 * an Entity used to represent the file type
 * </p>
 * <p>structure:</p> 
 * <ol>
 * 	<li>file type</li>
 * </ol>
 * @author Harry Huang
 *
 */
@Entity
public class FileType {
	
	@PrimaryKey
	private String type;
	
	@SecondaryKey(relate=MANY_TO_ONE, relatedEntity=Category.class, onRelatedEntityDelete=CASCADE)
	private byte categoryID;
	
	/**
	 * the set of document which belongs to type.
	 */
	@SecondaryKey(relate=ONE_TO_MANY, relatedEntity=Document.class, onRelatedEntityDelete=DeleteAction.NULLIFY)
	private Set<Integer> documentIDs = new HashSet<Integer>(); 
	
	private FileType(){};
	
	public FileType(String type, byte categoryID){
		this.type = type;
		this.categoryID = categoryID;
	}

	public String getType() {
		return type;
	}

	public byte getCategoryID() {
		return categoryID;
	}

	public Set<Integer> getDocumentIDs() {
		return documentIDs;
	}

	public void setDocumentIDs(Set<Integer> documentIDs) {
		this.documentIDs = documentIDs;
	}
	
	
}
