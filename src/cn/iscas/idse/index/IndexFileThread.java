package cn.iscas.idse.index;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cn.iscas.idse.config.InstanceManager;
import cn.iscas.idse.config.PropertiesManager;
import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.format.FileExtractor;
import cn.iscas.idse.format.FileExtractorFactory;
import cn.iscas.idse.index.segmentation.CamelCase;
import cn.iscas.idse.index.segmentation.StopWordFilter;
import cn.iscas.idse.index.segmentation.TermLemmatizer;
import cn.iscas.idse.index.segmentation.WordSegmentation;
import cn.iscas.idse.storage.entity.PostingContent;
import cn.iscas.idse.storage.entity.PostingTitle;
import cn.iscas.idse.storage.entity.Term;
import cn.iscas.idse.storage.entity.accessor.AccessorFactory;
import cn.iscas.idse.storage.entity.accessor.PostingContentAccessor;
import cn.iscas.idse.storage.entity.accessor.PostingTitleAccessor;
import cn.iscas.idse.storage.entity.accessor.TermAccessor;

public class IndexFileThread{
	private static final Logger log = Logger.getLogger(IndexFileThread.class);
	
	public static DiskScanner scanner = null;
	public static int numberOfFinishedFile = 0;
	
	/**
	 * dictionary for index. store the term list temporarily in the memory.
	 */
	public static Map<String, Term> dictionary = new HashMap<String, Term>();

	/**
	 * count of title and content posting
	 */
	public static int postingCount = 0;
	/**
	 * In order the out of memory, once the posting count reaches the threshold,
	 * the dictionary will be written into the DB 
	 */
	public static int dictWriteThreshold = SystemConfiguration.dictionaryWriteCountThreshold;

	public static int postingTitleID = 1;
	public static int postingContentID = 1;
	
	/**
	 * posting list for title of a document who is being indexed.
	 * once index for this document is finished, the list writes into the database 
	 * and the memory is released.
	 */
	private Map<String, PostingTitle> postingTitles = new HashMap<String, PostingTitle>();
	/**
	 * posting list for title of a document who is being indexed.
	 * once index for this document is finished, the list writes into the database 
	 * and the memory is released.
	 */
	private Map<String, PostingContent> postingContents = new HashMap<String, PostingContent>();
	
	private PostingTitleAccessor postingTitleAccessor = null;
	private PostingContentAccessor postingContentAccessor = null;
	private TermAccessor termAccessor = null;
	private WordSegmentation wordSegmentor = null;
	
	private int docID;
	private File file;
	private String suffix;
	
	public IndexFileThread(WordSegmentation wordSegmentor, int docID, File file, String suffix){
		this.wordSegmentor = wordSegmentor;
		this.postingTitleAccessor = AccessorFactory.getPostingTitleAccessor(SystemConfiguration.database.getIndexStore());
		this.postingContentAccessor = AccessorFactory.getPostingContentAccessor(SystemConfiguration.database.getIndexStore());
		this.termAccessor = AccessorFactory.getTermAccessor(SystemConfiguration.database.getIndexStore());
		this.docID = docID;
		this.file = file;
		this.suffix = suffix;
	}
	
	/**
	 * initialize the parameter including the posting_content_id, posting_title_id
	 */
	public static void initParameter(){
		postingTitleID = Integer.parseInt(PropertiesManager.getKeyValue("berkeley.posting_title_id"));
		postingContentID = Integer.parseInt(PropertiesManager.getKeyValue("berkeley.posting_content_id"));
		postingCount = 0;
	}
	
	/**
	 * save the parameters
	 */
	public static void saveParameter(){
		// save posting_title_id
		PropertiesManager.writeProperties("berkeley.posting_title_id", "" + postingTitleID);
		// save posting_content_id
		PropertiesManager.writeProperties("berkeley.posting_content_id", "" + postingContentID);
	}
	
	public void run(){
		this.indexFile(this.docID, this.file, this.suffix);
	}
	
	/**
	 * return current posting title id and increase the id by 1 for the next at once
	 * @return
	 */
	private static synchronized int getPostingTitleID(){
		return postingTitleID++;
	}
	/**
	 * return current posting content id and increase the id by 1 for the next at once
	 * @return
	 */
	private static synchronized int getPostingContentID(){
		return postingContentID++;
	}
	
	/**
	 * index file. Extract the text from files of specific types. 
	 * parser the text and generate the term-postings index, then 
	 * integrate them into the globle index stored in Berkeley DB.
	 * @param docID		document id
	 * @param file		file object of specific document
	 * @param suffix	suffix of the specific document
	 */
	private void indexFile(int docID, File file, String suffix){
		/*
		 * clear the posting list for this document.
		 */
		this.postingTitles.clear();
		this.postingContents.clear();
		/*
		 * index title.
		 */
		String title = file.getName();
		int typeIndexStart = title.lastIndexOf(".");
		if(typeIndexStart != -1){
			title = title.substring(0, typeIndexStart);
		}
		String segmentResult = this.wordSegmentor.segmentString(title);
		if(segmentResult != null)
			this.moreProcedure(docID, true, title, segmentResult);
		
		/*
		 * index content.
		 */
		//get the content extractor.
		FileExtractor extractor = FileExtractorFactory.getInstance().getFileExtractor(suffix);
		System.err.println(file.getAbsolutePath());
		if(extractor != null){
			System.err.println("parsable!");
			extractor.setFilePath(file.getAbsolutePath());
			String content = extractor.getContent();
			segmentResult = this.wordSegmentor.segmentString(content);
			if(segmentResult != null)
				this.moreProcedure(docID, false, content, segmentResult);
		}
		/*
		 * write title postings into the database
		 */
		for(Entry<String, PostingTitle>entry : this.postingTitles.entrySet()){
			this.postingTitleAccessor.getPrimaryPostingID().put(entry.getValue());
		}
		/*
		 * write content postings into the database
		 */
		for(Entry<String, PostingContent>entry : this.postingContents.entrySet()){
			this.postingContentAccessor.getPrimaryPostingID().put(entry.getValue());
		}
		
		/*
		 * write dictionary into DB if the posting count satisfies the threshold
		 */
		if(IndexFileThread.postingCount >= IndexFileThread.dictWriteThreshold){
			this.writeDictionaryIntoDB();
			IndexFileThread.postingCount = 0;
		}

		IndexFileThread.numberOfFinishedFile ++;
		//System.out.println("finished : " + IndexFileThread.numberOfFinishedFile + "/" + IndexFileThread.scanner.getFileNumber() + "(" + (IndexFileThread.numberOfFinishedFile*100.0/IndexFileThread.scanner.getFileNumber()) + "%)");
	}
	
	private void writeDictionaryIntoDB(){
		log.info("writing directory...");
		for(Entry<String, Term>entry : IndexFileThread.dictionary.entrySet()){
			Term term = this.termAccessor.getPrimaryTerm().get(entry.getKey());
			if(term != null){
				term.getPostingTitle().addAll(entry.getValue().getPostingTitle());
				term.getPostingContent().addAll(entry.getValue().getPostingContent());
			}
			else
				term = entry.getValue();
			this.termAccessor.getPrimaryTerm().put(term);
		}
		IndexFileThread.dictionary.clear();
		log.info("writing done.");
	}
	
	/**
	 * subsequent steps to handle text after segmentation.
	 * @param docID		document id
	 * @param isTitle	the text to handle is title or not
	 * @param beforeSeg		the original text before segmentation.
	 * @param afterSeg		the text after segmentation and it is without punctuation.
	 */
	private void moreProcedure(int docID, boolean isTitle, String beforeSeg, String afterSeg){
		// split the result-string and localize the term in text.
		// here, the offset denotes the relative sequential number of term 
		//(which starts from 0), not the position in the string.
		int offset = -1;
		String currentTerm = "";
		StringTokenizer tokenizer = new StringTokenizer(afterSeg);
		while(tokenizer.hasMoreTokens()){
			currentTerm = tokenizer.nextToken().trim();
			//handle Camel Case style
			String[] words = CamelCase.splitCamelCase(currentTerm);
			if(words != null){
				for(String word : words){
					//localize and get the offset.
					int nextIndex = ++offset;//beforeSeg.indexOf(word, ++offset);
					if(nextIndex != -1){
						offset = nextIndex;
						//lowercase the word
						word = word.toLowerCase();
						// handle Lemmatize
						word = ((TermLemmatizer)InstanceManager.getInstance(InstanceManager.CLASS_TERMLEMMATIZER)).adornText(word);
						if(word != null){
							// handle stop word
							if(!((StopWordFilter)InstanceManager.getInstance(InstanceManager.CLASS_STOPWORDFILTER)).isStopWord(word)){
								//add into index
								//put the term info into the index.
								////System.out.println(word + "\t" + offset);
								if(isTitle){
									//add offset
									if(this.postingTitles.containsKey(word)){
										this.postingTitles.get(word).getOffsets().add(offset);
									}
									else{
										int postingID = IndexFileThread.getPostingTitleID();
										PostingTitle posting = new PostingTitle(postingID, docID);
										posting.getOffsets().add(offset);
										this.postingTitles.put(word, posting);
										// add new posting to posting list of the corresponding term 
										IndexFileThread.postingCount++;
										if(IndexFileThread.dictionary.containsKey(word)){
											IndexFileThread.dictionary.get(word).getPostingTitle().add(postingID);
										}
										else{
											Term term = new Term(word);
											term.getPostingTitle().add(postingID);
											IndexFileThread.dictionary.put(word, term);
										}
									}
								}
								else{
									if(this.postingContents.containsKey(word)){
										this.postingContents.get(word).getOffsets().add(offset);
									}
									else{
										int postingID = IndexFileThread.getPostingContentID();
										PostingContent posting = new PostingContent(postingID, docID);
										posting.getOffsets().add(offset);
										this.postingContents.put(word, posting);
										// add new posting to posting list of the corresponding term
										IndexFileThread.postingCount++;
										if(IndexFileThread.dictionary.containsKey(word)){
											IndexFileThread.dictionary.get(word).getPostingContent().add(postingID);
										}
										else{
											Term term = new Term(word);
											term.getPostingContent().add(postingID);
											IndexFileThread.dictionary.put(word, term);
										}
									}
								}
							}
						}
					}
					else
						offset += word.length();

				}
			}
		}
	}
	  
}
