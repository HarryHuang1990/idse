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
	
	public int numberOfDirectory = 0;
	public int numberOfFile = 0;
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
//		this.postingTitleAccessor = AccessorFactory.getPostingTitleAccessor(SystemConfiguration.database.getIndexStore());
//		this.postingContentAccessor = AccessorFactory.getPostingContentAccessor(SystemConfiguration.database.getIndexStore());
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
		
	}
	
	
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
		
		
		
		
        
		try {
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
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * write the type index into the Berkeley db from memory buff.
	 * then flush the memory space.
	 */
	private void writeTypeIndexIntoDB(){
		for(Entry<String, FileType>entry : SystemConfiguration.fileTypeBuff.entrySet()){
			this.fileTypeAccessor.getPrimaryType().put(entry.getValue());
		}
		//release the memory
		SystemConfiguration.fileTypeBuff = null;
	}
	
	/**
	 * write the term list of dictionary into the Berkeley db from memory buff.
	 * then flush the memory space.
	 */
	private void writeDictionaryIntoDB(){
		for(Entry<String, Term>entry : IndexFileThread.dictionary.entrySet()){
//			System.out.println(entry.getValue().getTerm() + "\t" + entry.getValue().getPostingTitle().toString() + "\t" + entry.getValue().getPostingContent().toString());
			this.termAccessor.getPrimaryTerm().put(entry.getValue());
		}
		//release the memory
//		this.postingTitles.clear();
//		this.postingContents.clear();
		IndexFileThread.dictionary.clear();
	}
	
	/**
	 * index directory
	 * @param directory
	 */
	private void indexDirectory(File directory, short targetID){
		// directory number increases by 1
		int directoryID = ++this.numberOfDirectory;
		// write the directory info into db.
		if(!this.directoryAccessor.getSecondaryDirectoryPath().contains(Converter.convertBackSlashToSlash(directory.getAbsolutePath())))
			this.directoryAccessor.getPrimaryDirectoryID().putNoReturn(
					new Directory(directoryID, targetID, Converter.convertBackSlashToSlash(directory.getAbsolutePath())));
		// get list of file and directory obj.
		File[]files = directory.listFiles();
		if(files != null && files.length != 0){
			// index each file or directory.
			for(File object : files){
				if(object.isDirectory()){
					// index directory
					this.indexDirectory(object, targetID);
				}
				else{
					// file number increases by 1
					this.numberOfFile ++;
					//write the file info into db
					if(!this.documentAccessor.getPrimaryDocumentID().contains(this.numberOfFile))
						this.documentAccessor.getPrimaryDocumentID().putNoReturn(
								new Document(
										this.numberOfFile,
										directoryID, 
										object.getName()));
					
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
						SystemConfiguration.fileTypeBuff.get(suffix).getDocumentIDs().add(this.numberOfFile);
					}
					
					// index file
					this.threadPool.execute(new IndexFileThread(this.wordSegmentor, this.numberOfFile, object, suffix));
//					this.indexFile(IndexFileThread.numberOfFinishedFile, object, suffix);
				}
			}
		}
		this.numberOfDirectory++;
	}
//	
//	/**
//	 * index file. Extract the text from files of specific types. 
//	 * parser the text and generate the term-postings index, then 
//	 * integrate them into the globle index stored in Berkeley DB.
//	 * @param docID		document id
//	 * @param file		file object of specific document
//	 * @param suffix	suffix of the specific document
//	 */
//	private void indexFile(int docID, File file, String suffix){
//		/*
//		 * clear the posting list for this document.
//		 */
//		this.postingTitles.clear();
//		this.postingContents.clear();
//		/*
//		 * index title.
//		 */
//		String title = file.getName();
//		int typeIndexStart = title.lastIndexOf(".");
//		if(typeIndexStart != -1){
//			title = title.substring(0, typeIndexStart);
//		}
//		String segmentResult = this.wordSegmentor.segmentString(title);
//		if(segmentResult != null)
//			this.moreProcedure(docID, true, title, segmentResult);
//		
//		/*
//		 * index content.
//		 */
//		//get the content extractor.
//		FileExtractor extractor = FileExtractorFactory.getInstance().getFileExtractor(suffix);
//		System.err.println(file.getAbsolutePath());
//		if(extractor != null){
//			System.err.println("parsable!");
//			extractor.setFilePath(file.getAbsolutePath());
//			String content = extractor.getContent();
//			segmentResult = this.wordSegmentor.segmentString(content);
//			if(segmentResult != null)
//				this.moreProcedure(docID, false, content, segmentResult);
//		}
//		/*
//		 * write title postings into the database
//		 */
//		for(Entry<String, PostingTitle>entry : this.postingTitles.entrySet()){
//			this.postingTitleAccessor.getPrimaryPostingID().put(entry.getValue());
//		}
//		/*
//		 * write content postings into the database
//		 */
//		for(Entry<String, PostingContent>entry : this.postingContents.entrySet()){
//			this.postingContentAccessor.getPrimaryPostingID().put(entry.getValue());
//		}
//		
//		System.out.println("finished : " + this.numberOfFinishedFile + "/" + this.scanner.getFileNumber() + "(" + (this.numberOfFinishedFile*1.0/this.scanner.getFileNumber()) + "%)");
//	}
//	
//	/**
//	 * subsequent steps to handle text after segmentation.
//	 * @param docID		document id
//	 * @param isTitle	the text to handle is title or not
//	 * @param beforeSeg		the original text before segmentation.
//	 * @param afterSeg		the text after segmentation and it is without punctuation.
//	 */
//	private void moreProcedure(int docID, boolean isTitle, String beforeSeg, String afterSeg){
//		//split the result-string and localize the term in text.
//		int offset = -1;
//		String currentTerm = "";
//		StringTokenizer tokenizer = new StringTokenizer(afterSeg);
//		while(tokenizer.hasMoreTokens()){
//			currentTerm = tokenizer.nextToken().trim();
//			//handle Camel Case style
//			String[] words = CamelCase.splitCamelCase(currentTerm);
//			if(words != null){
//				for(String word : words){
//					//localize and get the offset.
//					int nextIndex = beforeSeg.indexOf(word, ++offset);
//					if(nextIndex != -1){
//						offset = nextIndex;
//						//lowercase the word
//						word = word.toLowerCase();
//						// handle Lemmatize
//						word = ((TermLemmatizer)InstanceManager.getInstance(InstanceManager.CLASS_TERMLEMMATIZER)).adornText(word);
//						if(word != null){
//							// handle stop word
//							if(!((StopWordFilter)InstanceManager.getInstance(InstanceManager.CLASS_STOPWORDFILTER)).isStopWord(word)){
//								//add into index
//								//put the term info into the index.
//								//System.out.println(word + "\t" + offset);
//								if(isTitle){
//									//add offset
//									if(this.postingTitles.containsKey(word)){
//										this.postingTitles.get(word).getOffsets().add(offset);
//									}
//									else{
//										PostingTitle posting = new PostingTitle(this.postingTitleID, docID);
//										posting.getOffsets().add(offset);
//										this.postingTitles.put(word, posting);
//										// add new posting to posting list of the corresponding term 
//										if(this.dictionary.containsKey(word)){
//											this.dictionary.get(word).getPostingTitle().add(this.postingTitleID);
//										}
//										else{
//											Term term = new Term(word);
//											term.getPostingTitle().add(this.postingTitleID);
//											this.dictionary.put(word, term);
//										}
//										this.postingTitleID++;
//									}
//								}
//								else{
//									if(this.postingContents.containsKey(word)){
//										this.postingContents.get(word).getOffsets().add(offset);
//									}
//									else{
//										PostingContent posting = new PostingContent(this.postingContentID, docID);
//										posting.getOffsets().add(offset);
//										this.postingContents.put(word, posting);
//										// add new posting to posting list of the corresponding term 
//										if(this.dictionary.containsKey(word)){
//											this.dictionary.get(word).getPostingContent().add(this.postingContentID);
//										}
//										else{
//											Term term = new Term(word);
//											term.getPostingContent().add(this.postingContentID);
//											this.dictionary.put(word, term);
//										}
//										this.postingContentID++;
//									}
//								}
//							}
//						}
//					}
//					else
//						offset += word.length();
//
//				}
//			}
//		}
//	}
//	
	public static void main(String[] args){
		IndexWriter indexer = new IndexWriter();
		indexer.executeIndexing();
	}
}
