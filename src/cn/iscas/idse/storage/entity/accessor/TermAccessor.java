package cn.iscas.idse.storage.entity.accessor;

import java.util.Set;

import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.Document;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.Term;

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
 * this is an Accessor class for Entity Term class.
 * </p>  
 * @author Harry Huang
 *
 */
public class TermAccessor {
	
	/**
	 * primary key accessor
	 */
	private PrimaryIndex<String, Term> primaryTerm;
	
	
	public TermAccessor(EntityStore store){
		/*
		 * create a primary accessor.
		 */
		this.primaryTerm = store.getPrimaryIndex(String.class, Term.class);
	}

	public PrimaryIndex<String, Term> getPrimaryTerm() {
		return primaryTerm;
	}

	
}


