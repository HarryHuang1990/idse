package cn.iscas.idse.storage.entity.accessor;

import cn.iscas.idse.storage.entity.Category;
import cn.iscas.idse.storage.entity.Directory;

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
 * this is an Accessor class for Entity Category class.
 * </p>  
 * @author Harry Huang
 *
 */
public class CategoryAccessor {
	
	/**
	 * primary key accessor
	 */
	private PrimaryIndex<Byte, Category> primaryCategoryID;
	/**
	 * Secondary key accessor
	 */
	private SecondaryIndex<String, Byte, Category> SecondaryCategoryName;
	
	public CategoryAccessor(EntityStore store){
		/*
		 * create a primary accessor.
		 */
		this.primaryCategoryID = store.getPrimaryIndex(Byte.class, Category.class);
		/*
		 * create a secondary accessor.
		 */
		this.SecondaryCategoryName = store.getSecondaryIndex(this.primaryCategoryID, String.class, "categoryName");
	}

	public PrimaryIndex<Byte, Category> getPrimaryCategoryID() {
		return primaryCategoryID;
	}

	public SecondaryIndex<String, Byte, Category> getSecondaryCategoryName() {
		return SecondaryCategoryName;
	}
	
}
