package cn.iscas.idse.demo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.storage.DBManager;
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

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.evolve.Deleter;
import com.sleepycat.persist.evolve.Mutations;

/**
 * all demo about usage BerkeleyDB
 * @author Harry Huang
 *
 */
public class BerkelyDBDemo {
	
	/**
	 * test the cascade mechanism of db 
	 */
	public void testCASCADERemove(){
		DBManager db = SystemConfiguration.database;
		TargetDirectoryAccessor accessor = new TargetDirectoryAccessor(db.getIndexStore());
		DirectoryAccessor da = new DirectoryAccessor(db.getIndexStore());
		for(int i=0; i<10; i++){
			da.getPrimaryDirectoryID().put(new Directory(i,  (short) 1,  "/a/b/c/d/e"));
		}
		
		this.showEntityTargetDirectory();
		this.showEntityDirectory();
		
//		accessor.getPrimaryTargetID().delete((short)301);
		da.getPrimaryDirectoryID().delete(100);
		
		this.showEntityTargetDirectory();
		this.showEntityDirectory();
	}
	
	/**
	 * test the cascade remove in ONE-TO-MANY situation
	 */
	public void testCASCADERemoveOnOneToMany(){
		DBManager db = SystemConfiguration.database;
		TargetDirectoryAccessor accessor = new TargetDirectoryAccessor(db.getIndexStore());
		DirectoryAccessor da = new DirectoryAccessor(db.getIndexStore());
		DocumentAccessor doa = AccessorFactory.getDocumentAccessor(db.getIndexStore());
		FileTypeAccessor fa = AccessorFactory.getFileTypeAccessor(db.getIndexStore());
		for(int i=0; i<3; i++){
			da.getPrimaryDirectoryID().put(new Directory(i+1,  (short) 1,  "/dir" + i));
			for(int k=0; k<3; k++){
				doa.getPrimaryDocumentID().put(new Document(k+1, i+1, "doc" + (i+1) + "." + (k+1)));
				FileType ft = fa.getPrimaryType().get(".7z");
				ft.getDocumentIDs().add(k+1);
				fa.getPrimaryType().putNoReturn(ft);
			}
		}
		
		this.showEntityCategory();
		this.showEntityFileType();
		this.showEntityDirectory();
		this.showEntityDocument();
		
		System.out.println("////////////////////////////////////after removing////////////////////////////////////////");
		
		doa.getPrimaryDocumentID().delete(1);
		this.showEntityCategory();
		this.showEntityFileType();
		this.showEntityDirectory();
		this.showEntityDocument();
	}
	
	
	public void showEntityCategory(){
		System.out.println("========================Category==========================");
		CategoryAccessor ca = AccessorFactory.getCategoryAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<Category> cursor = ca.getPrimaryCategoryID().entities();
		for(Category category : cursor){
			System.out.println(category.getCategoryID() + "\t" + category.getCategoryName());
		}
		cursor.close();
	}
	
	public void showEntityFileType(){
		System.out.println("========================FileType==========================");
		FileTypeAccessor ca = AccessorFactory.getFileTypeAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<FileType> cursor = ca.getPrimaryType().entities();
		for(FileType type : cursor){
			System.out.println(type.getType() + "\t" + type.getCategoryID() + "\t" + type.getDocumentIDs().toString());
		}
		cursor.close();
	}
	
	public void showEntityTargetDirectory(){
		System.out.println("========================TargetDirectory==========================");
		TargetDirectoryAccessor ca = AccessorFactory.getTargetDirectoryAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<TargetDirectory> cursor = ca.getPrimaryTargetID().entities();
		for(TargetDirectory c : cursor){
			System.out.println(c.getTargetID() + "\t" + c.getTargetPath());
		}
		cursor.close();
	}
	
	public void showEntityDirectory(){
		System.out.println("========================Directory==========================");
		DirectoryAccessor ca = AccessorFactory.getDirectoryAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<Directory> cursor = ca.getPrimaryDirectoryID().entities();
		for(Directory c : cursor){
			System.out.println(c.getDirectoryID() + "\t" + c.getDirectoryPath() + "\t" + c.getTargetID());
		}
		cursor.close();
	}
	
	public void showEntityDocument(){
		System.out.println("========================Document==========================");
		DocumentAccessor ca = AccessorFactory.getDocumentAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<Document> cursor = ca.getPrimaryDocumentID().entities();
		for(Document c : cursor){
			System.out.println(c.getDocID() + "\t" + c.getDocumentName() + "\t" + c.getDirectoryID());
		}
		cursor.close();
	}
	
	public void showDictionary(){
		System.out.println("========================Term==========================");
		TermAccessor ca = AccessorFactory.getTermAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<Term> cursor = ca.getPrimaryTerm().entities();
		for(Term c : cursor){
			//if(c.getTerm().contains("亚")||c.getTerm().contains("在")||c.getTerm().contains("笔")){
				System.out.println(c.getTerm() + "\t" + c.getPostingTitle().toString() + "\t" + c.getPostingContent().toString());
				showPosting(c.getTerm());
			//}
		}
		cursor.close();
	}
	
	public void showPosting(String term){
		System.out.println("========================Posting==========================");
		TermAccessor ca = AccessorFactory.getTermAccessor(SystemConfiguration.database.getIndexStore());
		PostingTitleAccessor pta = AccessorFactory.getPostingTitleAccessor(SystemConfiguration.database.getIndexStore());
		PostingContentAccessor pca = AccessorFactory.getPostingContentAccessor(SystemConfiguration.database.getIndexStore());
		Set<PostingTitle> postingTitleSet = new HashSet<PostingTitle>();
		Set<PostingContent> postingContentSet = new HashSet<PostingContent>();
		Term t = ca.getPrimaryTerm().get(term);
		for(Integer postingID : t.getPostingTitle()){
			PostingTitle p = pta.getPrimaryPostingID().get(postingID);
			System.out.println(p.toString());
			postingTitleSet.add(pta.getPrimaryPostingID().get(postingID));
			
		}
		for(Integer postingID : t.getPostingContent()){
			PostingContent p = pca.getPrimaryPostingID().get(postingID);
			System.out.println(p.toString());
			postingContentSet.add(pca.getPrimaryPostingID().get(postingID));
		}
	}
	
	public void showPostingTitle(){
		PostingTitleAccessor pta = AccessorFactory.getPostingTitleAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<PostingTitle> cursor = pta.getPrimaryPostingID().entities();
		for(PostingTitle title : cursor){
			System.out.println(title.toString());
		}
	}
	
	public void showPostingContent(){
		PostingContentAccessor pta = AccessorFactory.getPostingContentAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<PostingContent> cursor = pta.getPrimaryPostingID().entities();
		for(PostingContent Content : cursor){
			System.out.println(Content.toString());
		}
	}
	
	public void getNumberDocuments(){
		DocumentAccessor documentAccessor = AccessorFactory.getDocumentAccessor(SystemConfiguration.database.getIndexStore());
		System.out.println(documentAccessor.getPrimaryDocumentID().count());
	}
	
	public void showTopicRelationMatrix(){
		TopicRelationAccessor topicRelationAccessor = AccessorFactory.getTopicAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<TopicRelation> cursor = topicRelationAccessor.getPrimaryDocumentID().entities();
		for(TopicRelation entity : cursor)
			System.out.println(entity.getDocumentID() + "\t" + entity.getRelatedDocumentIDs().toString());
		cursor.close();
	}
	
	public void showTaskRelationMatrix(){
		TaskRelationAccessor topicRelationAccessor = AccessorFactory.getTaskAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<TaskRelation> cursor = topicRelationAccessor.getPrimaryDocumentID().entities();
		for(TaskRelation entity : cursor)
			System.out.println(entity.getDocumentID() + "\t" + entity.geteValue() + "\t" + entity.getRelatedDocumentIDs().toString());
		cursor.close();
	}
	
	public void showLocationRelationMatrix(){
		LocationRelationAccessor locationRelationAccessor = AccessorFactory.getLocationAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<LocationRelation> cursor = locationRelationAccessor.getPrimaryDocumentID().entities();
		for(LocationRelation entity : cursor)
			System.out.println(entity.getDocumentID() + "\t" + entity.getRelatedDocumentIDs().toString());
		cursor.close();
	}
	
	public void showPageRankGraphMatrix(){
		PageRankGraphAccessor pageRankGraphAccessor = AccessorFactory.getPageRankGraphAccessor(SystemConfiguration.database.getIndexStore());
		EntityCursor<PageRankGraph> cursor = pageRankGraphAccessor.getPrimaryDocumentID().entities();
		for(PageRankGraph entity : cursor)
			System.out.println(entity.getDocumentID() + "\t" + entity.getPageRankScore() + "\t" + entity.getRelatedDocumentIDs().toString());
		cursor.close();
	}
	
	public void showTopicRelationByDocID(int docID){
		IndexReader indexReader = new IndexReader();
		TopicRelationAccessor topicRelationAccessor = AccessorFactory.getTopicAccessor(SystemConfiguration.database.getIndexStore());
		TopicRelation topicRelation = topicRelationAccessor.getPrimaryDocumentID().get(docID);
		System.out.println(docID + "\t" + indexReader.getAbsolutePathOfDocument(docID));
		for(Entry<Integer, Double> doc : topicRelation.getRelatedDocumentIDs().entrySet()){
			System.out.println("\t" + doc.getValue() + "\t" + doc.getKey() + "\t" + indexReader.getAbsolutePathOfDocument(doc.getKey()));
		}
		
	}
	
	public void showTaskRelationByDocID(int docID){
		IndexReader indexReader = new IndexReader();
		TaskRelationAccessor taskRelationAccessor = AccessorFactory.getTaskAccessor(SystemConfiguration.database.getIndexStore());
		TaskRelation taskRelation = taskRelationAccessor.getPrimaryDocumentID().get(docID);
		System.out.println(docID + "\t" + taskRelation.geteValue() + "\t" + indexReader.getAbsolutePathOfDocument(docID));
		for(Entry<Integer, Integer> doc : taskRelation.getRelatedDocumentIDs().entrySet()){
			System.out.println("\t" + doc.getValue() + "\t" + doc.getKey() + "\t" + indexReader.getAbsolutePathOfDocument(doc.getKey()));
		}
	}
	
	public void showLocationRelationByDocID(int docID){
		IndexReader indexReader = new IndexReader();
		LocationRelationAccessor LocationRelationAccessor = AccessorFactory.getLocationAccessor(SystemConfiguration.database.getIndexStore());
		LocationRelation locationRelation = LocationRelationAccessor.getPrimaryDocumentID().get(docID);
		System.out.println(docID + "\t" + indexReader.getAbsolutePathOfDocument(docID));
		for(Entry<Integer, Float> doc : locationRelation.getRelatedDocumentIDs().entrySet())
			System.out.println("\t" + doc.getValue() + "\t" + doc.getKey() + "\t" + indexReader.getAbsolutePathOfDocument(doc.getKey()));
	}
	
	public void showPageRankGraphByDocID(int docID){
		IndexReader indexReader = new IndexReader();
		PageRankGraph pageRankGraph = indexReader.getPageRankGraphByID(docID);
		System.out.println(pageRankGraph.getPageRankScore() + "\t" + docID + "\t" + indexReader.getAbsolutePathOfDocument(docID));
		for(int id : pageRankGraph.getRecommendedDocs())
			System.out.println("\t" + id + "\t" + indexReader.getAbsolutePathOfDocument(id));
		for(Entry<Integer, Double> doc : pageRankGraph.getRelatedDocumentIDs().entrySet()){
			System.out.println("\t" + doc.getValue() + "\t" + doc.getKey() + "\t" + indexReader.getAbsolutePathOfDocument(doc.getKey()));
		}
	}
	
	public void showPostingCount(){
		IndexReader indexReader = new IndexReader();
		long contentCount = indexReader.getPostingContentAccessor().getPrimaryPostingID().count();
		long titleCount = indexReader.getPostingTitleAccessor().getPrimaryPostingID().count();
		System.out.println("contentPost=" + contentCount + "\ttitleCount=" + titleCount + "\tsum=" + (contentCount + titleCount));
	}
	
	public void showTerm(String term){
		IndexReader indexReader = new IndexReader();
		System.out.println(indexReader.getTermByTerm(term));
	}
	
	public void showPageRankCount(){
		IndexReader indexReader = new IndexReader();
		long pageRankCount = indexReader.getPageRankGraph().size();
		long documentCount = indexReader.getDocuments().size();
		System.out.println("pageRank = "+pageRankCount + "\tdocument size = " + documentCount);
	}
	
	public void showDocumentCount(){
		IndexReader indexReader = new IndexReader();
		long docCount = indexReader.getDocumentIDs().size();
		System.out.println("document size=" + docCount);
	}
	
	public void locationPruning(){
		IndexReader indexReader = new IndexReader();
		Map<Integer, LocationRelation> locationRelationGraph = new HashMap<Integer, LocationRelation>();
		Set<Integer> nodeToDelete = new HashSet<Integer>();
		locationRelationGraph.putAll(indexReader.getLocationRelationMap());
		// get the nodes to delete
		for(Entry<Integer, LocationRelation>entry : locationRelationGraph.entrySet()){
			if(entry.getValue().getRelatedDocumentIDs().size() > SystemConfiguration.neightborThreshold){
				nodeToDelete.add(entry.getKey());
			}
		}
		LocationDistribution(locationRelationGraph.values());
		// delete the nodes
		for(int docID : nodeToDelete){
			Set<Integer> relatedKeys = locationRelationGraph.get(docID).getRelatedDocumentIDs().keySet();
			for(int relatedKey : relatedKeys){
				locationRelationGraph.get(relatedKey).getRelatedDocumentIDs().remove(docID);
			}
			locationRelationGraph.remove(docID);
		}
		LocationDistribution(locationRelationGraph.values());
	}
	
	public void LocationDistribution(Collection<LocationRelation> list){
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for(int i=1; i<=11; i++)
			map.put(i*100, 0);
		for(LocationRelation graph : list){
			int size = graph.getRelatedDocumentIDs().size();
			if(size <= 100)
				map.put(100, map.get(100)+1);
			else if(size <= 200)
				map.put(200, map.get(200)+1);
			else if(size <= 300)
				map.put(300, map.get(300)+1);
			else if(size <= 400)
				map.put(400, map.get(400)+1);
			else if(size <= 500)
				map.put(500, map.get(500)+1);
			else if(size <= 600)
				map.put(600, map.get(600)+1);
			else if(size <= 700)
				map.put(700, map.get(700)+1);
			else if(size <= 800)
				map.put(800, map.get(800)+1);
			else if(size <= 900)
				map.put(900, map.get(900)+1);
			else if(size <= 1000)
				map.put(1000, map.get(1000)+1);
			else
				map.put(1100, map.get(1100)+1);
		}
		System.out.println(map.toString());
		
	}
	
	public void LocationDistribution(){
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for(int i=1; i<=11; i++)
			map.put(i*100, 0);
		IndexReader indexReader = new IndexReader();
		Collection<LocationRelation> list = indexReader.getLocationRelation();
		for(LocationRelation graph : list){
			int size = graph.getRelatedDocumentIDs().size();
			if(size <= 100)
				map.put(100, map.get(100)+1);
			else if(size <= 200)
				map.put(200, map.get(200)+1);
			else if(size <= 300)
				map.put(300, map.get(300)+1);
			else if(size <= 400)
				map.put(400, map.get(400)+1);
			else if(size <= 500)
				map.put(500, map.get(500)+1);
			else if(size <= 600)
				map.put(600, map.get(600)+1);
			else if(size <= 700)
				map.put(700, map.get(700)+1);
			else if(size <= 800)
				map.put(800, map.get(800)+1);
			else if(size <= 900)
				map.put(900, map.get(900)+1);
			else if(size <= 1000)
				map.put(1000, map.get(1000)+1);
			else
				map.put(1100, map.get(1100)+1);
		}
		System.out.println(map.toString());
		
	}
	
	public void PagRankDistribution(){
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for(int i=1; i<=11; i++)
			map.put(i*100, 0);
		IndexReader indexReader = new IndexReader();
		Collection<PageRankGraph> list = indexReader.getPageRankGraph().values();
		for(PageRankGraph graph : list){
			int size = graph.getRelatedDocumentIDs().size();
			if(size <= 100)
				map.put(100, map.get(100)+1);
			else if(size <= 200)
				map.put(200, map.get(200)+1);
			else if(size <= 300)
				map.put(300, map.get(300)+1);
			else if(size <= 400)
				map.put(400, map.get(400)+1);
			else if(size <= 500)
				map.put(500, map.get(500)+1);
			else if(size <= 600)
				map.put(600, map.get(600)+1);
			else if(size <= 700)
				map.put(700, map.get(700)+1);
			else if(size <= 800)
				map.put(800, map.get(800)+1);
			else if(size <= 900)
				map.put(900, map.get(900)+1);
			else if(size <= 1000)
				map.put(1000, map.get(1000)+1);
			else
				map.put(1100, map.get(1100)+1);
		}
		System.out.println(map.toString());
		
	}
	
	public static void PagRankDistribution(Collection<PageRankGraph> list){
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for(int i=1; i<=11; i++)
			map.put(i*100, 0);
		for(PageRankGraph graph : list){
			int size = graph.getRelatedDocumentIDs().size();
			if(size <= 100)
				map.put(100, map.get(100)+1);
			else if(size <= 200)
				map.put(200, map.get(200)+1);
			else if(size <= 300)
				map.put(300, map.get(300)+1);
			else if(size <= 400)
				map.put(400, map.get(400)+1);
			else if(size <= 500)
				map.put(500, map.get(500)+1);
			else if(size <= 600)
				map.put(600, map.get(600)+1);
			else if(size <= 700)
				map.put(700, map.get(700)+1);
			else if(size <= 800)
				map.put(800, map.get(800)+1);
			else if(size <= 900)
				map.put(900, map.get(900)+1);
			else if(size <= 1000)
				map.put(1000, map.get(1000)+1);
			else
				map.put(1100, map.get(1100)+1);
		}
		System.out.println(map.toString());
		
	}
	
	public void getFileSystemInfo(){
		IndexReader iReader = new IndexReader();
		int docCount = iReader.getDocumentIDs().size();
		int dirCount = iReader.getDirectorys().size();
		int maxdepth = 0;
		int sumdepth = 0;
		List<Directory> dirs = iReader.getDirectorys();
		for(Directory dir : dirs){
			String path = dir.getDirectoryPath().replaceAll("//", "/");
			int depth = path.split("/").length;
			sumdepth += depth;
			if(depth > maxdepth)maxdepth = depth;
		}
		System.out.println("文件数=" + docCount);
		System.out.println("目录数=" + dirCount);
		System.out.println("最大深度=" + maxdepth);
		System.out.println("深度和=" + sumdepth);
		
	}
	
	public static void main(String[] args){
		BerkelyDBDemo demo = new BerkelyDBDemo();
//		demo.showEntityDirectory();
//		demo.showEntityDocument();
//		demo.showEntityFileType();
//		demo.showDictionary();
//		demo.showPostingTitle();
//		demo.showPostingContent();
//		demo.showLocationRelationMatrix();
//		demo.showTaskRelationMatrix();
//		demo.showTopicRelationMatrix();
//		demo.showPageRankGraphMatrix();
//		demo.showPageRankGraphByDocID(135905);
//		demo.showLocationRelationByDocID(112370);
//		demo.showTopicRelationByDocID(112370);
//		demo.showTaskRelationByDocID(112370);
//		demo.showPostingCount();
//		demo.showTerm("21212f5s4rwefsf");
//		demo.showPageRankCount();
//		demo.showDocumentCount();
//		demo.PagRankDistribution();
//		demo.LocationDistribution();
//		demo.locationPruning();
		demo.getFileSystemInfo();
		
//		LocationRelation local = demo.getLocationRelationByDocID(82124);
//		TaskRelation task = demo.getTaskRelationByDocID(82124);
//		TopicRelation topic = demo.getTopicRelationByDocID(82124);
//		
//		if(local != null)
//			System.out.println("location : " + local.getRelatedDocumentIDs().toString());
//		if(task != null)
//			System.out.println("task : " + task.getRelatedDocumentIDs().toString());
//		if(topic != null)
//			System.out.println("topic : " + topic.getRelatedDocumentIDs().toString());
	}
}
