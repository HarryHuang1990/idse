package cn.iscas.idse.storage.entity.accessor;

import cn.iscas.idse.storage.entity.PostingContent;

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
 * this is an Accessor class for Entity PostingContent class.
 * </p>  
 * @author Harry Huang
 *
 */
public class PostingContentAccessor {
	
	/**
	 * primary key accessor
	 */
	private PrimaryIndex<Integer, PostingContent> primaryPostingID;
	/**
	 * Secondary key accessor
	 */
	private SecondaryIndex<Integer, Integer, PostingContent> SecondaryDocID;
	
	public PostingContentAccessor(EntityStore store){
		/*
		 * create a primary accessor.
		 */
		this.primaryPostingID = store.getPrimaryIndex(Integer.class, PostingContent.class);
		/*
		 * create secondary accessors.
		 */
		this.SecondaryDocID = store.getSecondaryIndex(this.primaryPostingID, Integer.class, "docID");
	}

	public PrimaryIndex<Integer, PostingContent> getPrimaryPostingID() {
		return primaryPostingID;
	}

	public SecondaryIndex<Integer, Integer, PostingContent> getSecondaryDocID() {
		return SecondaryDocID;
	}

}



