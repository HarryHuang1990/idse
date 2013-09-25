package cn.iscas.idse.index;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import com.sleepycat.persist.EntityCursor;

import cn.iscas.idse.storage.DBManager;
import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.accessor.AccessorFactory;
import cn.iscas.idse.storage.entity.accessor.DirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.DocumentAccessor;
import cn.iscas.idse.storage.entity.accessor.PostingContentAccessor;
import cn.iscas.idse.storage.entity.accessor.PostingTitleAccessor;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.TermAccessor;

/**
 * write the index into the Berkeley DB
 * @author Harry Huang
 *
 */
public class IndexWriter {
	
	private DBManager database = null;
	private DiskScanner scanner = null;
	
	private int numberOfFinishedDirectory = 0;
	private int numberOfFinishedFile = 0;
	
	/**
	 * Accessor
	 */
	private TargetDirectoryAccessor targetDirectoryAccessor = null;
	private DirectoryAccessor directoryAccessor = null;
	private DocumentAccessor documentAccessor = null;
	private TermAccessor termAccessor = null;
	private PostingTitleAccessor postingTitleAccessor = null;
	private PostingContentAccessor postingContentAccessor = null;
	
	public IndexWriter(){
		this.database = new DBManager();
		/*
		 * initialize the accessors
		 */
		this.targetDirectoryAccessor = AccessorFactory.getTargetDirectoryAccessor(this.database.getIndexStore());
		this.directoryAccessor = AccessorFactory.getDirectoryAccessor(this.database.getIndexStore());
		this.documentAccessor = AccessorFactory.getDocumentAccessor(this.database.getIndexStore());
		this.termAccessor = AccessorFactory.getTermAccessor(this.database.getIndexStore());
		this.postingTitleAccessor = AccessorFactory.getPostingTitleAccessor(this.database.getIndexStore());
		this.postingContentAccessor = AccessorFactory.getPostingContentAccessor(this.database.getIndexStore());
	}
	
	//索引文件的名
	
	
	/**
	 * put a new <term, offset> tuple into the index. 
	 * Once size of the index in memory is larger than a given threshold, 
	 * the index is written into the Berkeley DB. 
	 * @param termOffset
	 * @return
	 */
	public void write(int docID, String term, int offset){
		
		
	}
	
	/**
	 * start indexing the target directories.
	 */
	public void executeIndexing(){
		
		//scan the target directory, then
		//we can know how many files and directory will be indexed.
		//the directory where ingdexMall
		this.scanner = new DiskScanner(this.targetDirectoryAccessor, this.directoryAccessor, this.documentAccessor);
		scanner.scanDisk();
		
		//get target directories
		EntityCursor<TargetDirectory> targets = targetDirectoryAccessor.getPrimaryTargetID().entities();
		//index each directory.
		for(TargetDirectory target : targets){
			this.indexDirectory(new File(target.getTargetPath()));
		}
		
	}
	
	/**
	 * index directory
	 * @param directory
	 */
	private void indexDirectory(File directory){
		// get list of file and directory obj.
		File[]files = directory.listFiles();
		if(files != null && files.length != 0){
			// index each file or directory.
			for(File object : files){
				if(object.isDirectory()){
					// index directory
					this.indexDirectory(object);
				}
				else{
					// index file
					this.indexFile(object);
				}
			}
		}
	}
	
	/**
	 * index file. Extract the text from files of specific types. 
	 * parser the text and generate the term-postings index, then 
	 * integrate them into the globle index stored in Berkeley DB.
	 * @param file
	 */
	private void indexFile(File file){
		
		/*
		 * index title
		 */
		
		
		/*
		 * index content
		 */
	}
	
	public static void main(String[] args){
		IndexWriter indexer = new IndexWriter();
		indexer.executeIndexing();
	}
}
