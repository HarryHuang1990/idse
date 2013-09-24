package cn.iscas.idse.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import static com.sleepycat.persist.model.Relationship.*;
import static com.sleepycat.persist.model.DeleteAction.*;

/**
 * <p>
 * an Entity used to represent the directory
 * </p>
 * <p>structure:</p> 
 * <ol>
 * 	<li>directory id (primary key, unique)</li>
 * 	<li>directory path (e.g., /movie/love/)</li>
 * 	<li>target id (the directory containing this directory path, which is specified by user at the configuration)</li>
 * </ol>
 * @author Harry Huang
 *
 */
@Entity
public class Directory {

	@PrimaryKey
	private int directoryID;
	
	@SecondaryKey(relate=MANY_TO_ONE, relatedEntity=TargetDirectory.class, onRelatedEntityDelete=CASCADE)
	private short targetID;
	
	private String directoryPath;
	
	public Directory(int directoryID, short targetID, String directoryPath){
		this.directoryID = directoryID;
		this.targetID = targetID;
		this.directoryPath = directoryPath;
	}
	
	public Directory(){}

	public int getDirectoryID() {
		return directoryID;
	}

	public void setDirectoryID(int directoryID) {
		this.directoryID = directoryID;
	}

	public short getTargetID() {
		return targetID;
	}

	public void setTargetID(short targetID) {
		this.targetID = targetID;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}
}