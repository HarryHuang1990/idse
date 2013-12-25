package cn.iscas.idse.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityIndex;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.storage.entity.Category;
import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.Document;
import cn.iscas.idse.storage.entity.FileType;
import cn.iscas.idse.storage.entity.LocationRelation;
import cn.iscas.idse.storage.entity.PageRankGraph;
import cn.iscas.idse.storage.entity.PostingContent;
import cn.iscas.idse.storage.entity.PostingTitle;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.TaskRelation;
import cn.iscas.idse.storage.entity.Term;
import cn.iscas.idse.storage.entity.TopicRelation;
import cn.iscas.idse.storage.entity.accessor.AccessorFactory;
import cn.iscas.idse.storage.entity.accessor.CategoryAccessor;
import cn.iscas.idse.storage.entity.accessor.DirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.DocumentAccessor;
import cn.iscas.idse.storage.entity.accessor.FileTypeAccessor;
import cn.iscas.idse.storage.entity.accessor.LocationRelationAccessor;
import cn.iscas.idse.storage.entity.accessor.PageRankGraphAccessor;
import cn.iscas.idse.storage.entity.accessor.PostingContentAccessor;
import cn.iscas.idse.storage.entity.accessor.PostingTitleAccessor;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.TaskRelationAccessor;
import cn.iscas.idse.storage.entity.accessor.TermAccessor;
import cn.iscas.idse.storage.entity.accessor.TopicRelationAccessor;


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
	private PageRankGraphAccessor pageRankGraphAccessor = AccessorFactory.getPageRankGraphAccessor(SystemConfiguration.database.getIndexStore());
	private TopicRelationAccessor topicRelationAccessor = AccessorFactory.getTopicAccessor(SystemConfiguration.database.getIndexStore());
	private LocationRelationAccessor locationRelationAccessor = AccessorFactory.getLocationAccessor(SystemConfiguration.database.getIndexStore());
	private TaskRelationAccessor taskRelationAccessor = AccessorFactory.getTaskAccessor(SystemConfiguration.database.getIndexStore());
	
	
	public CategoryAccessor getCategoryAccessor() {
		return categoryAccessor;
	}

	public DirectoryAccessor getDirectoryAccessor() {
		return directoryAccessor;
	}

	public DocumentAccessor getDocumentAccessor() {
		return documentAccessor;
	}

	public FileTypeAccessor getFileTypeAccessor() {
		return fileTypeAccessor;
	}

	public TargetDirectoryAccessor getTargetDirectoryAccessor() {
		return targetDirectoryAccessor;
	}

	public TermAccessor getTermAccessor() {
		return termAccessor;
	}

	public PostingTitleAccessor getPostingTitleAccessor() {
		return postingTitleAccessor;
	}

	public PostingContentAccessor getPostingContentAccessor() {
		return postingContentAccessor;
	}
	
	public TopicRelationAccessor getTopicRelationAccessor() {
		return topicRelationAccessor;
	}

	public TaskRelationAccessor getTaskRelationAccessor() {
		return taskRelationAccessor;
	}

	public LocationRelationAccessor getLocationRelationAccessor() {
		return locationRelationAccessor;
	}

	/**
	 * get the documents by the given documen name
	 * @param documentName
	 * @return
	 */
	public List<Document> getDocumentsByName(String documentName){
		EntityIndex<Integer, Document> entityIndex = this.documentAccessor.getSecondaryDocumentName().subIndex(documentName);
		if(entityIndex != null)
			return new ArrayList<Document>(entityIndex.map().values());
		return null;
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
	public EntityIndex<Integer, Document> getDocumentsByDirectoryIDCursor(int directoryID){
		return this.documentAccessor.getSecondaryDirectoryID().subIndex(directoryID);
	}
	
	public List<Document> getDocumentsByDirectoryID(int directoryID){
		List<Document> documents = new ArrayList<Document>(
				this.documentAccessor.getSecondaryDirectoryID().subIndex(directoryID).map().values());
		return documents;
	}
	
	/**
	 * extract the directory entity list
	 * @return
	 */
	public EntityCursor<Directory> getDirectorysCursor(){
		return this.directoryAccessor.getPrimaryDirectoryID().entities();
	}
	
	public List<Directory> getDirectorys(){
		List<Directory>directorys = new ArrayList<Directory>(this.directoryAccessor.getPrimaryDirectoryID().map().values());
		return directorys;
	}
	
	/**
	 * return the direcory entity list of specific rootDir
	 * @param rootDir
	 * @return
	 */
	public Collection<Directory> getDirectorys(String rootDir){
		Collection<Directory> dirs = new ArrayList<Directory>();
		for(Directory directory : this.directoryAccessor.getPrimaryDirectoryID().map().values()){
			if(directory.getDirectoryPath().startsWith(rootDir)){
				dirs.add(directory);
			}
		}
		return dirs;
	}
	
	/**
	 * extract the directory entity list
	 * @return
	 */
	public EntityCursor<Integer> getDirectoryIDs(){
		return this.directoryAccessor.getPrimaryDirectoryID().keys();
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
	
	/**
	 * return the documents list
	 * @return
	 */
	public EntityCursor<Document> getDocumentsCursor(){
		return this.documentAccessor.getPrimaryDocumentID().entities();
	}
	
	public Set<Integer> getDocumentIDs(){
		return this.documentAccessor.getPrimaryDocumentID().map().keySet();
	}
	
	public List<Document> getDocuments(){
		List<Document> documents = new ArrayList<Document>(this.documentAccessor.getPrimaryDocumentID().map().values());
		return documents;
	}
	
	/**
	 * get the documentID by the directory and document name
	 * @param directoryID
	 * @param documentName
	 * @return
	 */
	public int getDocumentIDByDirectory(int directoryID, String documentName){
		int documentID = -1;
		EntityCursor<Document> cursor = this.documentAccessor.getSecondaryDirectoryID().entities();
		for(Document document : cursor){
			if(documentName.equals(document.getDocumentName())){
				documentID = document.getDocID();
			}
		}
		return documentID;
	}
	
	/**
	 * return the target directory list
	 * @return
	 */
	public EntityCursor<TargetDirectory> getTargetDirectoriesCursor(){
		return this.targetDirectoryAccessor.getPrimaryTargetID().entities();
	}
	
	public Collection<TargetDirectory> getTargetDirectories(){
		return this.targetDirectoryAccessor.getPrimaryTargetID().map().values();
	}
	
	/**
	 * return the document number
	 * @return
	 */
	public int getNumberDocuments(){
		return (int) this.documentAccessor.getPrimaryDocumentID().count();
	}

	/**
	 * return the directory number
	 * @return
	 */
	public int getNumberDirectorys(){
		return (int) this.directoryAccessor.getPrimaryDirectoryID().count();
	}
	
	public Directory getDirectoryByPath(String path){
		return this.directoryAccessor.getSecondaryDirectoryPath().get(path);
	}
	
	public EntityCursor<FileType> getFileTypesCursor(){
		return this.fileTypeAccessor.getPrimaryType().entities();
	}
	
	public Collection<FileType> getFileTypes(){
		return this.fileTypeAccessor.getPrimaryType().map().values();
	}
	
	public TaskRelation getTaskRelationByDocID(int docID){
		return this.taskRelationAccessor.getPrimaryDocumentID().get(docID);
	}
	
	public TopicRelation getTopicRelationByDocID(int docID){
		return this.topicRelationAccessor.getPrimaryDocumentID().get(docID);
	}
	
	public LocationRelation getLocationRelationByDocID(int docID){
		return this.locationRelationAccessor.getPrimaryDocumentID().get(docID);
	}
	
	public Map<Integer, LocationRelation> getLocationRelationMap(){
		return this.locationRelationAccessor.getPrimaryDocumentID().map();
	}
	
	public Collection<LocationRelation> getLocationRelation(){
		return this.locationRelationAccessor.getPrimaryDocumentID().map().values();
	}
	
	public PageRankGraph getPageRankGraphByID(int docID){
		return this.pageRankGraphAccessor.getPrimaryDocumentID().get(docID);
	}
	
	public Map<Integer, PageRankGraph> getPageRankGraph(){
		return this.pageRankGraphAccessor.getPrimaryDocumentID().map();
	}
	
	public Map<Integer, TaskRelation> getTaskRelation(){
		return this.taskRelationAccessor.getPrimaryDocumentID().map();
	}
	
	/**
	 * remove the targetDirectory entity of given ID
	 * @param ID
	 */
	public void removeTargetDirectoryByID(short ID){
		this.targetDirectoryAccessor.getPrimaryTargetID().delete(ID);
	}
	
	public void removeDirectoryByID(int ID){
		this.directoryAccessor.getPrimaryDirectoryID().delete(ID);
	}
	
	public void removeDocumentByID(int ID){
		this.documentAccessor.getPrimaryDocumentID().delete(ID);
	}
	
	public void removeTerm(String term){
		this.termAccessor.getPrimaryTerm().delete(term);
	}
	
	
	
	
	
	
	public void addAndUpdateTargetDirectory(TargetDirectory entity){
		this.targetDirectoryAccessor.getPrimaryTargetID().put(entity);
	}
	
	public void addAndUpdateDirectory(Directory entity){
		this.directoryAccessor.getPrimaryDirectoryID().put(entity);
	}
	
	public void addAndUpdateDocument(Document entity){
		this.documentAccessor.getPrimaryDocumentID().put(entity);
	}
	
	public void addAndUpdateFileType(FileType entity){
		this.fileTypeAccessor.getPrimaryType().put(entity);
	}
	
	public void addAndUpdateTerm(Term entity){
		this.termAccessor.getPrimaryTerm().put(entity);
	}
	
	public void addAndUpdatePageRankGraph(PageRankGraph entity){
		this.pageRankGraphAccessor.getPrimaryDocumentID().put(entity);
	}
	
	public boolean isExistDirectoryByPath(String path){
		return this.directoryAccessor.getSecondaryDirectoryPath().contains(path);
	}
	
	public boolean isExistDocumentByDocID(int docID){
		return this.documentAccessor.getPrimaryDocumentID().contains(docID);
	}

	public int getDocContentLength(int docID){
		Collection<PostingContent> contents = this.postingContentAccessor.getSecondaryDocID().subIndex(docID).map().values();
		int length = 0;
		for(PostingContent posting : contents)
			length += posting.getTermFrequency();
		return length;
	}
	
	
	public static void main(String args[]){
		IndexReader indexReader = new IndexReader();
//		int[] documents = new int[]{77337, 82036};
//		for(int i=0; i<documents.length; i++)
//			System.out.println(indexReader.getAbsolutePathOfDocument(documents[i]));
		
		System.out.println(indexReader.getDocumentsByName("DataOS-20130502.pptx").get(0).getDocID());
////		System.out.println(indexReader.getDocumentsByDirectoryIDCursor(25735).count());
////		System.out.println(indexReader.getDirectoryByDirectoryID(25735).getDirectoryPath());
//		System.out.println(indexReader.getDocContentLength(136045));
	}
}

