package cn.iscas.idse.demo;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.storage.DBManager;
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

import com.sleepycat.persist.EntityCursor;

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
			if(c.getTerm().contains("ÑÇ")||c.getTerm().contains("ÔÚ")||c.getTerm().contains("±Ê")){
				System.out.println(c.getTerm() + "\t" + c.getPostingTitle().toString() + "\t" + c.getPostingContent().toString());
				showPosting(c.getTerm());
			}
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
	
	public static void main(String[] args){
		
		BerkelyDBDemo demo = new BerkelyDBDemo();
		demo.showDictionary();
//		for(int i=0 ;i< 1000; i++){
//		demo.getNumberDocuments();
//		}
	}
}
