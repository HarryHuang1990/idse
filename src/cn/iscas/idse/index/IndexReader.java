package cn.iscas.idse.index;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityIndex;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.storage.entity.Category;
import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.Document;
import cn.iscas.idse.storage.entity.FileType;
import cn.iscas.idse.storage.entity.PostingContent;
import cn.iscas.idse.storage.entity.PostingTitle;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.Term;
import cn.iscas.idse.storage.entity.accessor.AccessorFactory;
import cn.iscas.idse.storage.entity.accessor.CategoryAccessor;
import cn.iscas.idse.storage.entity.accessor.DirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.DocumentAccessor;
import cn.iscas.idse.storage.entity.accessor.FileTypeAccessor;
import cn.iscas.idse.storage.entity.accessor.PostingContentAccessor;
import cn.iscas.idse.storage.entity.accessor.PostingTitleAccessor;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.TermAccessor;


/**
 * Implement the APIs to access the index and return the necessary data set 
 * to calculate the query-document similarity. 
 * @author Harry Huang
 *
 */
public class IndexReader {
	
	private CategoryAccessor categoryAccessor = AccessorFactory.getCategoryAccessor(SystemConfiguration.database.getIndexStore());
	private DirectoryAccessor directoryAccessor = AccessorFactory.getDirectoryAccessor(SystemConfiguration.database.getIndexStore());
	private DocumentAccessor documentAccessor = AccessorFactory.getDocumentAccessor(SystemConfiguration.database.getIndexStore());
	private FileTypeAccessor fileTypeAccessor = AccessorFactory.getFileTypeAccessor(SystemConfiguration.database.getIndexStore());
	private TargetDirectoryAccessor targetDirectoryAccessor = AccessorFactory.getTargetDirectoryAccessor(SystemConfiguration.database.getIndexStore());
	private TermAccessor termAccessor = AccessorFactory.getTermAccessor(SystemConfiguration.database.getIndexStore());
	private PostingTitleAccessor postingTitleAccessor = AccessorFactory.getPostingTitleAccessor(SystemConfiguration.database.getIndexStore());
	private PostingContentAccessor postingContentAccessor = AccessorFactory.getPostingContentAccessor(SystemConfiguration.database.getIndexStore());
	
	
	public long getNumberDocuments(){
		return documentAccessor.getPrimaryDocumentID().count();
	}
	
	/**
	 * Get the postings of specific term in the dictionary.
	 * the postings includes title-part (denoted as Set[0]) and content-part (denoted as Set[1]).
	 * @param term
	 * @return
	 */
	public Set[] getPostings(String term){
		
		Set<PostingTitle> postingTitleSet = new HashSet<PostingTitle>();
		Set<PostingContent> postingContentSet = new HashSet<PostingContent>();
		Term t = this.getTermByTerm(term);
		// get the title posting
		for(Integer postingID : t.getPostingTitle()){
			postingTitleSet.add(this.getPostingTitleByPostingID(postingID));
		}
		// get the content posting.
		for(Integer postingID : t.getPostingContent()){
			postingContentSet.add(this.getPostingContentByPostingID(postingID));
		}
		
		return new Set[]{postingTitleSet, postingContentSet};
	}
	
	/**
	 * extract the specific posting for title from index by posting id. 
	 * @param postingID
	 * @return
	 */
	public PostingTitle getPostingTitleByPostingID(int postingID){
		return this.postingTitleAccessor.getPrimaryPostingID().get(postingID);
	}
	/**
	 * extract the specific postings list for a title of a document denoted as docID
	 * @param docID
	 * @return
	 */
	public EntityCursor<PostingTitle> getPostingTitlesByDocID(int docID){
		return this.postingTitleAccessor.getSecondaryDocID().subIndex(docID).entities();
	}
	
	/**
	 * extract the specific posting for content from index by posting id. 
	 * @param postingID
	 * @return
	 */
	public PostingContent getPostingContentByPostingID(int postingID){
		return this.postingContentAccessor.getPrimaryPostingID().get(postingID);
	}
	/**
	 * extract the specific postings list for content of a document denoted as docID
	 * @param docID
	 * @return
	 */
	public EntityCursor<PostingContent> getPostingContentsByDocID(int docID){
		return this.postingContentAccessor.getSecondaryDocID().subIndex(docID).entities();
	}
	
	/**
	 * extract the specific target directory by target id
	 * @param targetID
	 * @return
	 */
	public TargetDirectory getTargetDirectoryByTargetID(short targetID){
		return this.targetDirectoryAccessor.getPrimaryTargetID().get(targetID);
	}
	
	/**
	 * extract the specific target term by term string.
	 * @param term
	 * @return
	 */
	public Term getTermByTerm(String term){
		return this.termAccessor.getPrimaryTerm().get(term);
	}
	
	/**
	 * extract the specific fileType entity by type.
	 * @param type
	 * @return
	 */
	public FileType getFileTypeByType(String type){
		return this.fileTypeAccessor.getPrimaryType().get(type);
	}
	/**
	 * extract the file type list of specific category denoted as categoryID
	 * @param categoryID
	 * @return
	 */
	public EntityCursor<FileType> getFileTypesByCategoryID(byte categoryID){
		return this.fileTypeAccessor.getSecondaryCategoryID().subIndex(categoryID).entities();
	}
	
	/**
	 * extract the document entity by the given document id
	 * @param docID
	 * @return
	 */
	public Document getDocumentByDocID(int docID){
		return this.documentAccessor.getPrimaryDocumentID().get(docID);
	}
	
	/**
	 * extract the document entities in the directory of given directory id.
	 * @param directoryID
	 * @return
	 */
	public EntityIndex<Integer, Document> getDocumentsByDirectoryID(int directoryID){
		return this.documentAccessor.getSecondaryDirectoryID().subIndex(directoryID);
	}
	/**
	 * extract the directory entity by the given directory id
	 * @param directoryID
	 * @return
	 */
	public Directory getDirectoryByDirectoryID(int directoryID){
		return this.directoryAccessor.getPrimaryDirectoryID().get(directoryID);
	}
	/**
	 * extract the category entity by the given category id.
	 * @param categoryID
	 * @return
	 */
	public Category getCategoryByCategoryID(byte categoryID){
		return this.categoryAccessor.getPrimaryCategoryID().get(categoryID);
	}
	
	/**
	 * return the absolute path of a document by the given document id.
	 * @param docID
	 * @return
	 */
	public String getAbsolutePathOfDocument(int docID){
		Document document = this.getDocumentByDocID(docID);
		Directory directory = this.getDirectoryByDirectoryID(document.getDirectoryID());
		return directory.getDirectoryPath() + "/" + document.getDocumentName();
	}
}

