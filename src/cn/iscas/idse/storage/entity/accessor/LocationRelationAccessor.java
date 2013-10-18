package cn.iscas.idse.storage.entity.accessor;

import cn.iscas.idse.storage.entity.Category;
import cn.iscas.idse.storage.entity.LocationRelation;

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
 * this is an Accessor class for Entity location relation class.
 * </p>  
 * @author Harry Huang
 *
 */
public class LocationRelationAccessor {
	
	/**
	 * primary key accessor
	 */
	private PrimaryIndex<Integer, LocationRelation> primaryDocumentID;
	
	public LocationRelationAccessor(EntityStore store){
		/*
		 * create a primary accessor.
		 */
		this.primaryDocumentID = store.getPrimaryIndex(Integer.class, LocationRelation.class);
	}

	public PrimaryIndex<Integer, LocationRelation> getPrimaryDocumentID() {
		return primaryDocumentID;
	}
	
}
