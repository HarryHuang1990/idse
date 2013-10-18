package cn.iscas.idse.storage.entity.accessor;

import cn.iscas.idse.storage.entity.TaskRelation;
import cn.iscas.idse.storage.entity.TopicRelation;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

/**
 * <p>
 * Berkeley DB DPL API organize primary and secondary indexes using a specialize data accessor class.
 * the main reason for the accessor class to exist is to provide convenient access to all the indexes
 * in use for Entity class.
 * </p>
 * <p>
 * this is an Accessor class for Entity topic relation class.
 * </p>  
 * @author Harry Huang
 *
 */
public class TopicRelationAccessor {
	
	/**
	 * primary key accessor
	 */
	private PrimaryIndex<Integer, TopicRelation> primaryDocumentID;
	
	public TopicRelationAccessor(EntityStore store){
		/*
		 * create a primary accessor.
		 */
		this.primaryDocumentID = store.getPrimaryIndex(Integer.class, TopicRelation.class);
	}

	public PrimaryIndex<Integer, TopicRelation> getPrimaryDocumentID() {
		return primaryDocumentID;
	}
	
}
