package cn.iscas.idse.index;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sleepycat.persist.EntityCursor;

import cn.iscas.idse.config.InstanceManager;
import cn.iscas.idse.config.PropertiesManager;
import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.format.FileExtractor;
import cn.iscas.idse.format.FileExtractorFactory;
import cn.iscas.idse.index.segmentation.CamelCase;
import cn.iscas.idse.index.segmentation.StopWordFilter;
import cn.iscas.idse.index.segmentation.TermLemmatizer;
import cn.iscas.idse.index.segmentation.WordSegmentation;
import cn.iscas.idse.storage.DBManager;
import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.Document;
import cn.iscas.idse.storage.entity.FileType;
import cn.iscas.idse.storage.entity.PostingContent;
import cn.iscas.idse.storage.entity.PostingTitle;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.Term;
import cn.iscas.idse.storage.entity.accessor.AccessorFactory;
import cn.iscas.idse.storage.entity.accessor.DirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.DocumentAccessor;
import cn.iscas.idse.storage.entity.accessor.FileTypeAccessor;
import cn.iscas.idse.storage.entity.accessor.PostingContentAccessor;
import cn.iscas.idse.storage.entity.accessor.PostingTitleAccessor;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.TermAccessor;
import cn.iscas.idse.utilities.Converter;

/**
 * write the index into the Berkeley DB
 * @author Harry Huang
 *
 */
public class IndexWriter {
	
//	private DiskScanner scanner = null;
	
	public int directoryID = 1;
	public int documentID = 1;
//	/**
//	 * dictionary for index. store the term list temporarily in the memory.
//	 */
//	private Map<String, Term> dictionary = new HashMap<String, Term>();
//	/**
//	 * posting list for title of a document who is being indexed.
//	 * once index for this document is finished, the list writes into the database 
//	 * and the memory is released.
//	 */
//	private Map<String, PostingTitle> postingTitles = new HashMap<String, PostingTitle>();
//	private int postingTitleID = 1;
//	/**
//	 * posting list for title of a document who is being indexed.
//	 * once index for this document is finished, the list writes into the database 
//	 * and the memory is released.
//	 */
//	private Map<String, PostingContent> postingContents = new HashMap<String, PostingContent>();
//	private int postingContentID = 1;
	/**
	 * Accessor
	 */
	private TargetDirectoryAccessor targetDirectoryAccessor = null;
	private DirectoryAccessor directoryAccessor = null;
	private DocumentAccessor documentAccessor = null;
	private TermAccessor termAccessor = null;
//	private PostingTitleAccessor postingTitleAccessor = null;
//	private PostingContentAccessor postingContentAccessor = null;
	private FileTypeAccessor fileTypeAccessor = null;
	
	private WordSegmentation wordSegmentor = null;
	
	/**
	 * thread pool size
	 */
	private final int THREAD_SIZE = 2;
	
	/**
	 * Thread pool
	 */
//	private ThreadPool threadPool = null;
	private ExecutorService threadPool = null;
	
	public IndexWriter(){
		/*
		 * initialize the accessors
		 */
		this.targetDirectoryAccessor = AccessorFactory.getTargetDirectoryAccessor(SystemConfiguration.database.getIndexStore());
		this.directoryAccessor = AccessorFactory.getDirectoryAccessor(SystemConfiguration.database.getIndexStore());
		this.documentAccessor = AccessorFactory.getDocumentAccessor(SystemConfiguration.database.getIndexStore());
		this.termAccessor = AccessorFactory.getTermAccessor(SystemConfiguration.database.getIndexStore());
		this.fileTypeAccessor = AccessorFactory.getFileTypeAccessor(SystemConfiguration.database.getIndexStore());
		
		/*
		 * get the instance of wordSegmentation
		 */
		this.wordSegmentor = (WordSegmentation)InstanceManager.getInstance(InstanceManager.CLASS_WORDSEGMENTATION);
		/*
		 * initialize the thread pool
		 */
//		this.threadPool = new ThreadPool(this.THREAD_SIZE);
		this.threadPool = Executors.newFixedThreadPool(this.THREAD_SIZE);
		
		this.initParameter();
		
	}
	
	
	private void initParameter(){
		this.directoryID = Integer.parseInt(PropertiesManager.getKeyValue("berkeley.directory_id"));
		this.documentID = Integer.parseInt(PropertiesManager.getKeyValue("berkeley.document_id"));
	}
	
	private void saveParameter(){
		PropertiesManager.updateProperties("berkeley.directory_id", "" + directoryID);
		PropertiesManager.updateProperties("berkeley.document_id", "" + documentID);
	}
	
	/**
	 * start indexing the target directories.
	 */
	public void executeIndexing(){
		try {
			//put the target path into the Berkeley DB
			for(String targetPath : SystemConfiguration.targetDirectories){
				if(!this.targetDirectoryAccessor.getSecondaryTargetPath().contains(targetPath)){
					TargetDirectory targetAccessor = new TargetDirectory(targetPath);
					this.targetDirectoryAccessor.getPrimaryTargetID().putNoReturn(targetAccessor);
				}
			}
			
			//initialize the parameter
			IndexFileThread.initParameter();
			
			//scan the target directory, then
			//we can know how many files and directory will be indexed.
			//the directory and file info (includes name and type) will be write into the db.
			IndexFileThread.scanner = new DiskScanner(this.targetDirectoryAccessor);
			IndexFileThread.scanner.scanDisk();
			
			//initialize the segmentor
			this.wordSegmentor.initialize();
			//get target directories
			System.out.println("Indexing...");
			long start = System.currentTimeMillis();
			EntityCursor<TargetDirectory> targets = targetDirectoryAccessor.getPrimaryTargetID().entities();
			//index each directory.
			for(TargetDirectory target : targets){
				this.indexDirectory(new File(target.getTargetPath()), target.getTargetID());
			}
			/**
			 * wait until the thread pool is empty.
			 */
			this.threadPool.shutdown();
			this.threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			System.out.println("threads...finished...");
			
			this.writeTypeIndexIntoDB();
			//write dictionary into db.
			this.writeDictionaryIntoDB();
			
			
			long end = System.currentTimeMillis();
			System.out.println("index done. time = " + ((end - start) * 1.0 / 1000 / 60) + " min");
			// destroy the segmentor
			this.wordSegmentor.exitICTCLAS();
			this.wordSegmentor.destoryInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private int getNewDirectoryID(){
		return this.directoryID++;
	}
	
	private int getNewDocumentID(){
		return this.documentID++;
	}
	
	
	/**
	 * write the type index into the Berkeley db from memory buff.
	 * then flush the memory space.
	 */
	private void writeTypeIndexIntoDB(){
		for(Entry<String, FileType>entry : SystemConfiguration.fileTypeBuff.entrySet()){
			this.fileTypeAccessor.getPrimaryType().put(entry.getValue());
			SystemConfiguration.fileTypeBuff.get(entry.getKey()).getDocumentIDs().clear();
		}
	}
	
	/**
	 * write the term list of dictionary into the Berkeley db from memory buff.
	 * then flush the memory space.
	 */
	private void writeDictionaryIntoDB(){
		for(Entry<String, Term>entry : IndexFileThread.dictionary.entrySet()){
			this.termAccessor.getPrimaryTerm().put(entry.getValue());
		}
		//release the memory
		
		IndexFileThread.dictionary.clear();
		IndexFileThread.saveParameter();
		this.saveParameter();
	}
	
	/**
	 * index directory
	 * @param directory
	 */
	private void indexDirectory(File directory, short targetID){
		// directory number increases by 1
		int directoryID = this.getNewDirectoryID();
		// write the directory info into db.
		if(!this.directoryAccessor.getSecondaryDirectoryPath().contains(Converter.convertBackSlashToSlash(directory.getAbsolutePath())))
			this.directoryAccessor.getPrimaryDirectoryID().putNoReturn(
					new Directory(directoryID, targetID, Converter.convertBackSlashToSlash(directory.getAbsolutePath())));
		// get list of file and directory obj.
		File[]files = directory.listFiles();
		if(files != null && files.length != 0 && files.length < SystemConfiguration.maxFileCountPreDirectory){
			// index each file or directory.
			for(File object : files){
				if(object.isDirectory()){
					// index directory
					this.indexDirectory(object, targetID);
				}
				else{
					int documentID = this.getNewDocumentID();
					//write the file info into db
					if(!this.documentAccessor.getPrimaryDocumentID().contains(documentID))
						this.documentAccessor.getPrimaryDocumentID().putNoReturn(new Document(documentID, directoryID, object.getName()));
					
					// get the file type
					int suffixIndexStart = object.getName().lastIndexOf(".");
					String suffix = "";
					if(suffixIndexStart != -1){
						suffix = object.getName().substring(suffixIndexStart, object.getName().length()).toLowerCase();
					}
					else{
						suffix = ".";
					}
					
					// add the documentID to the type index
					if(SystemConfiguration.fileTypeBuff.containsKey(suffix)){
						SystemConfiguration.fileTypeBuff.get(suffix).getDocumentIDs().add(documentID);
					}
					
					// index file
					(new IndexFileThread(this.wordSegmentor, documentID, object, suffix)).run();
//					this.threadPool.execute(new IndexFileThread(this.wordSegmentor, this.numberOfFile, object, suffix));
//					this.indexFile(IndexFileThread.numberOfFinishedFile, object, suffix);
				}
			}
		}
//		this.numberOfDirectory++;
	}

	public static void main(String[] args){
		IndexWriter indexer = new IndexWriter();
		indexer.executeIndexing();
	}
}
