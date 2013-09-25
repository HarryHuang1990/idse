package cn.iscas.idse.storage.entity.accessor;

import cn.iscas.idse.storage.entity.Directory;
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
 * this is an Accessor class for Entity Directory class.
 * </p>  
 * @author Harry Huang
 *
 */
public class DirectoryAccessor {
	
	/**
	 * primary key accessor
	 */
	private PrimaryIndex<Integer, Directory> primaryDirectoryID;
	/**
	 * Secondary key accessor
	 */
	private SecondaryIndex<Short, Integer, Directory> SecondaryTargetID;
	private SecondaryIndex<String, Integer, Directory> SecondaryDirectoryPath;
	
	public DirectoryAccessor(EntityStore store){
		/*
		 * create a primary accessor.
		 */
		this.primaryDirectoryID = store.getPrimaryIndex(Integer.class, Directory.class);
		/*
		 * create a secondary accessor.
		 */
		this.SecondaryTargetID = store.getSecondaryIndex(this.primaryDirectoryID, Short.class, "targetID");
		this.SecondaryDirectoryPath = store.getSecondaryIndex(this.primaryDirectoryID, String.class, "directoryPath");
	}

	public PrimaryIndex<Integer, Directory> getPrimaryDirectoryID() {
		return primaryDirectoryID;
	}

	public SecondaryIndex<Short, Integer, Directory> getSecondaryTargetID() {
		return SecondaryTargetID;
	}

	public SecondaryIndex<String, Integer, Directory> getSecondaryDirectoryPath() {
		return SecondaryDirectoryPath;
	}
	
}
