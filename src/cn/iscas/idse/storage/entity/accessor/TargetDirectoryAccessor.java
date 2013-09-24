package cn.iscas.idse.storage.entity.accessor;

import cn.iscas.idse.storage.entity.TargetDirectory;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

/**
 * <p>
 * Berkeley DB DPL API organize primary and secondary indexes using a specialize data accessor class.
 * the main reason for the accessor class to exist is to provide convenient access to all the indexes
 * in use for Entity class.
 * </p>
 * <p>
 * this is an Accessor class for Entity TargetDirectory class.
 * </p>  
 * @author Harry Huang
 *
 */
public class TargetDirectoryAccessor {
	
	/**
	 * primary key accessor for TargetDirectory
	 * Short - the data type of TargetID
	 * TargetDirectory - the Entity
	 */
	private PrimaryIndex<Short, TargetDirectory> primaryTargetID;
	/**
	 * Secondary key accessor for TargetDirectory
	 * String - the data type of TargetPath
	 * Short - the data type of TargetID
	 * TargetDirectory - the Entity
	 */
	private SecondaryIndex<String, Short, TargetDirectory> SecondaryTargetPath;
	
	public TargetDirectoryAccessor(EntityStore store){
		/*
		 * <p>
		 * create a primary accessor.
		 * Short.class - the type class of primary key
		 * TargetDirectory.class - the class containing the primary key.
		 */
		this.primaryTargetID = store.getPrimaryIndex(Short.class, TargetDirectory.class);
		/*
		 * create a secondary accessor.
		 * this.primaryTargetID - the primary key accessor.
		 * String.class - the data type class of the secrodary key.
		 * "targetPath" - the name of secondary key
		 */
		this.SecondaryTargetPath = store.getSecondaryIndex(this.primaryTargetID, String.class, "targetPath");
	}

	public PrimaryIndex<Short, TargetDirectory> getPrimaryTargetID() {
		return primaryTargetID;
	}

	public SecondaryIndex<String, Short, TargetDirectory> getSecondaryTargetPath() {
		return SecondaryTargetPath;
	}
	
	
}
