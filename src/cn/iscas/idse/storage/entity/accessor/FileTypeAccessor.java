package cn.iscas.idse.storage.entity.accessor;

import cn.iscas.idse.storage.entity.FileType;

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
 * this is an Accessor class for Entity FileType class.
 * </p>  
 * @author Harry Huang
 *
 */
public class FileTypeAccessor {
	/**
	 * primary key accessor
	 */
	private PrimaryIndex<String, FileType> primaryType;
	/**
	 * Secondary key accessor
	 */
	private SecondaryIndex<Byte, String, FileType> SecondaryCategoryID;
	
	public FileTypeAccessor(EntityStore store){
		/*
		 * create a primary accessor.
		 */
		this.primaryType = store.getPrimaryIndex(String.class, FileType.class);
		/*
		 * create a secondary accessor.
		 */
		this.SecondaryCategoryID = store.getSecondaryIndex(this.primaryType, Byte.class, "categoryID");
	}

	public PrimaryIndex<String, FileType> getPrimaryType() {
		return primaryType;
	}

	public SecondaryIndex<Byte, String, FileType> getSecondaryCategoryID() {
		return SecondaryCategoryID;
	}
	
	
}
