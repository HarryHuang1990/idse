package cn.iscas.idse.storage.entity.accessor;

import cn.iscas.idse.storage.DBManager;

import com.sleepycat.persist.EntityStore;

/**
 * this is a Accessor factory where you can get accessor for specific Entity
 * @author Harry Huang
 *
 */
public class AccessorFactory {
	
	public static TargetDirectoryAccessor getTargetDirectoryAccessor(EntityStore store) {
		return new TargetDirectoryAccessor(store);
	}
	public static DirectoryAccessor getDirectoryAccessor(EntityStore store) {
		return new DirectoryAccessor(store);
	}
	public static DocumentAccessor getDocumentAccessor(EntityStore store) {
		return new DocumentAccessor(store);
	}
	public static TermAccessor getTermAccessor(EntityStore store) {
		return new TermAccessor(store);
	}
	public static PostingTitleAccessor getPostingTitleAccessor(EntityStore store) {
		return new PostingTitleAccessor(store);
	}
	public static PostingContentAccessor getPostingContentAccessor(EntityStore store) {
		return new PostingContentAccessor(store);
	}
	public static CategoryAccessor getCategoryAccessor(EntityStore store){
		return new CategoryAccessor(store);
	}
	public static FileTypeAccessor getFileTypeAccessor(EntityStore store){
		return new FileTypeAccessor(store);
	}
	public static LocationRelationAccessor getLocationAccessor(EntityStore store){
		return new LocationRelationAccessor(store);
	}
	public static TaskRelationAccessor getTaskAccessor(EntityStore store){
		return new TaskRelationAccessor(store);
	}
	public static TopicRelationAccessor getTopicAccessor(EntityStore store){
		return new TopicRelationAccessor(store);
	}
	public static PageRankGraphAccessor getPageRankGraphAccessor(EntityStore store){
		return new PageRankGraphAccessor(store);
	}
}
