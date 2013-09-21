package cn.iscas.idse.storage;

import java.lang.annotation.Target;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

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
	
	@PrimaryKey
	private short targetID;
	
	private String targetPath;
	
	public TargetDirectory(short targetID, String targetPath){
		this.targetID = targetID;
		this.targetPath = targetPath;
	}
	
	public TargetDirectory(){}
	

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
