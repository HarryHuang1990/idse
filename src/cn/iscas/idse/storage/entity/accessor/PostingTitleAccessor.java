package cn.iscas.idse.storage.entity.accessor;

import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.PostingTitle;

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
 * this is an Accessor class for Entity PostingTitle class.
 * </p>  
 * @author Harry Huang
 *
 */
public class PostingTitleAccessor {
	
	/**
	 * primary key accessor
	 */
	private PrimaryIndex<Integer, PostingTitle> primaryPostingID;
	/**
	 * Secondary key accessor
	 */
	private SecondaryIndex<Integer, Integer, PostingTitle> SecondaryDocID;
	
	public PostingTitleAccessor(EntityStore store){
		/*
		 * create a primary accessor.
		 */
		this.primaryPostingID = store.getPrimaryIndex(Integer.class, PostingTitle.class);
		/*
		 * create secondary accessors.
		 */
		this.SecondaryDocID = store.getSecondaryIndex(this.primaryPostingID, Integer.class, "docID");
	}

	public PrimaryIndex<Integer, PostingTitle> getPrimaryPostingID() {
		return primaryPostingID;
	}

	public SecondaryIndex<Integer, Integer, PostingTitle> getSecondaryDocID() {
		return SecondaryDocID;
	}
}


