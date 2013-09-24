package cn.iscas.idse.storage.entity;


import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import static com.sleepycat.persist.model.Relationship.*;


/**
 * <p>
 * an Entity used to represent the directory specified by user to 
 * index and search at the beginning of using this application
 * </p>
 * <p>structure:</p> 
 * <ol>
 * 	<li>target id (primary key, unique)</li>
 * 	<li>target path (e.g., c:/, /home)</li>
 * </ol>
 * @author Harry Huang
 *
 */
@Entity
public class TargetDirectory {
	
	@PrimaryKey(sequence="TargetDirectoryID")
	private short targetID;
	
	@SecondaryKey(relate = ONE_TO_ONE)
	private String targetPath;
	
	public TargetDirectory(String targetPath){
		this.targetPath = targetPath;
	}
	
	private TargetDirectory(){}
	

	public short getTargetID() {
		return targetID;
	}

	public void setTargetID(short targetID) {
		this.targetID = targetID;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	
}
