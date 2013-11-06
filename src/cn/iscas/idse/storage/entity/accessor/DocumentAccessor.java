package cn.iscas.idse.storage.entity.accessor;

import cn.iscas.idse.storage.entity.Document;

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
 * this is an Accessor class for Entity Document class.
 * </p>  
 * @author Harry Huang
 *
 */
public class DocumentAccessor {
	
	/**
	 * primary key accessor
	 */
	private PrimaryIndex<Integer, Document> primaryDocumentID;
	/**
	 * Secondary key accessor
	 */
	private SecondaryIndex<Integer, Integer, Document> SecondaryDirectoryID;
	private SecondaryIndex<String, Integer, Document> SecondaryDocumentName;
	
	public DocumentAccessor(EntityStore store){
		/*
		 * create a primary accessor.
		 */
		this.primaryDocumentID = store.getPrimaryIndex(Integer.class, Document.class);
		/*
		 * create a secondary accessor.
		 */
		this.SecondaryDirectoryID = store.getSecondaryIndex(this.primaryDocumentID, Integer.class, "directoryID");
		this.SecondaryDocumentName = store.getSecondaryIndex(this.primaryDocumentID, String.class, "documentName");
	}

	public PrimaryIndex<Integer, Document> getPrimaryDocumentID() {
		return primaryDocumentID;
	}

	public SecondaryIndex<Integer, Integer, Document> getSecondaryDirectoryID() {
		return SecondaryDirectoryID;
	}

	public SecondaryIndex<String, Integer, Document> getSecondaryDocumentName() {
		return SecondaryDocumentName;
	}
}

