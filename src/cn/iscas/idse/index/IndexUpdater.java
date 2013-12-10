package cn.iscas.idse.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.sleepycat.persist.EntityCursor;

import cn.iscas.idse.config.InstanceManager;
import cn.iscas.idse.config.PropertiesManager;
import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.index.segmentation.WordSegmentation;
import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.Document;
import cn.iscas.idse.storage.entity.FileType;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.Term;
import cn.iscas.idse.storage.entity.accessor.AccessorFactory;
import cn.iscas.idse.storage.entity.accessor.DirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.DocumentAccessor;
import cn.iscas.idse.storage.entity.accessor.FileTypeAccessor;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.TermAccessor;
import cn.iscas.idse.utilities.Converter;

/**
 * update the the Berkeley DB including the index and knowledge repository.
 * <p>
 * 		the index is updated partly. the knowledge repository will be completely removed and rebuilt.
 * </p> 
 * @author Harry Huang
 * @date 2013.10.23
 */
public class IndexUpdater {

	private int directoryID = 1;
	private int documentID = 1;
	private IndexReader reader = null;
	private WordSegmentation wordSegmentor = null;
	
	public IndexUpdater(WordSegmentation wordSegmentor){
		this.reader = new IndexReader();
		/*
		 * get the instance of wordSegmentation
		 */
		this.wordSegmentor = wordSegmentor;
//		this.wordSegmentor = (WordSegmentation)InstanceManager.getInstance(InstanceManager.CLASS_WORDSEGMENTATION);
		
	}
	
	private void initParameter(){
		this.directoryID = Integer.parseInt(PropertiesManager.getKeyValue("berkeley.directory_id"));
		this.documentID = Integer.parseInt(PropertiesManager.getKeyValue("berkeley.document_id"));
	}
	
	private void saveParameter(){
		PropertiesManager.updateProperties("berkeley.directory_id", "" + directoryID);
		PropertiesManager.updateProperties("berkeley.document_id", "" + documentID);
	}
	
	public void execute(){
		this.executeIndexUpdate();
		this.executeKnowledgeUpdate();
	}
	
	/**
	 * update index
	 * @param targetDir
	 * @param targetID
	 */
	public void executeIndexUpdate(String targetPath){
		this.initParameter();

		//put the target path into the Berkeley DB
		if(!this.reader.getTargetDirectoryAccessor().getSecondaryTargetPath().contains(targetPath)){
			TargetDirectory targetAccessor = new TargetDirectory(targetPath);
			this.reader.getTargetDirectoryAccessor().getPrimaryTargetID().putNoReturn(targetAccessor);
		}
		
		UpdateFileThread.initParameter();
		UpdateFileThread.scanner = new DiskScanner();
		UpdateFileThread.scanner.scanDirectory(new File(targetPath));
		
		//get target directories
		//System.out.println("Updating...");
		long start = System.currentTimeMillis();
		
		// update directory
		TargetDirectory targetDirectory = this.reader.getTargetDirectoryAccessor().getSecondaryTargetPath().get(targetPath);
		this.updateDirectory(new File(targetPath), targetDirectory.getTargetID());
		
		//update type index into db.
		this.updateTypeIndexIntoDB();
		//update dictionary into db.
		this.updateDictionaryIntoDB();
		
		long end = System.currentTimeMillis();
		//System.out.println("update done. time = " + ((end - start) * 1.0 / 1000 / 60) + " min");
	}
	
	/**
	 * update index
	 */
	private void executeIndexUpdate(){

		this.initParameter();
		this.updateTargetDirectories();
		
		UpdateFileThread.initParameter();
		
		UpdateFileThread.scanner = new DiskScanner(reader.getTargetDirectoryAccessor());
		UpdateFileThread.scanner.scanDisk();
		
		//initialize the segmentor
//		this.wordSegmentor.initialize();
		//get target directories
		//System.out.println("Updating...");
		long start = System.currentTimeMillis();
		// update directory
		this.updateDirectory();
		
		//update type index into db.
		this.updateTypeIndexIntoDB();
		//update dictionary into db.
		this.updateDictionaryIntoDB();
		
		long end = System.currentTimeMillis();
		//System.out.println("update done. time = " + ((end - start) * 1.0 / 1000 / 60) + " min");
		// destroy the segmentor
//		this.wordSegmentor.exitICTCLAS();
//		this.wordSegmentor.destoryInstance();
	}
	
	/**
	 * update the directory recursively
	 */
	private void updateDirectory(){
		/*
		 *  delete the old ones which are not in target directories now.
		 */
		Collection<Directory> directories = reader.getDirectorys();
		for(Directory directory : directories){
			if(!UpdateFileThread.scanner.getDirectorySet().contains(directory.getDirectoryPath())){
				this.reader.removeDirectoryByID(directory.getDirectoryID());
			}
		}
		
		EntityCursor<TargetDirectory> targets = reader.getTargetDirectoriesCursor();
		for(TargetDirectory target : targets){
			this.updateDirectory(new File(target.getTargetPath()), target.getTargetID());
		}
		targets.close();
	}
	
	/**
	 * update the specific directory recursively.
	 * @param directory
	 * @param targetID
	 */
	private void updateDirectory(File directory, short targetID){
		int directoryID = -1;
		String[]files = directory.list();
		List<String> remains = new ArrayList<String>(); // existed old files since the last indexing.
		// existed or new ?  and get the directory ID
		Directory directoryEntity = this.reader.getDirectoryByPath(Converter.convertBackSlashToSlash(directory.getAbsolutePath()));
		
		if(directoryEntity != null){
			directoryID = directoryEntity.getDirectoryID();

			// get post files
			Collection<Document> postDocuments = reader.getDocumentsByDirectoryID(directoryID);
			
			// get current file names 
			List<String> currentFiles = new ArrayList<String>();
			if(files != null && files.length != 0){
				for(String file : files){
					File object = new File(directory, file);
					if(object.isFile())
						currentFiles.add(object.getName());
				}
			}
			
			// remove post documents
			for(Document document : postDocuments){
				if(!currentFiles.contains(document.getDocumentName())){
					this.reader.removeDocumentByID(document.getDocID());
				}
				else
					remains.add(document.getDocumentName());
			}
		}		
		else{
			if(files != null && files.length != 0 && files.length < SystemConfiguration.maxFileCountPreDirectory){
				directoryID = this.getNewDirectoryID();
				this.reader.addAndUpdateDirectory(new Directory(directoryID, targetID, Converter.convertBackSlashToSlash(directory.getAbsolutePath())));
			}
			else
				files = null;
		}
		
		
		// add new documents
		if(files != null && files.length != 0){
			// update each file or directory.
			for(String file : files){
				File object = new File(directory, file);
				if(object.isDirectory()){
					// update directory
					this.updateDirectory(object, targetID);
				}
				else{
					//write the new file info into db
					if(!remains.contains(object.getName())){
						int documentID = this.getNewDocumentID();
						this.reader.addAndUpdateDocument(new Document(documentID, directoryID, object.getName()));
						
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
						(new UpdateFileThread(this.wordSegmentor, documentID, object, suffix)).run();
						
					}
				}
			}
		}
	}
		
	/**
	 * check and update targetDirectory
	 */
	private void updateTargetDirectories(){
		/*
		 *		delete old ones
		 */
		List<String> remains = new ArrayList<String>();	// the remaining target directories 
		Collection<TargetDirectory> targets = reader.getTargetDirectories();
		for(TargetDirectory target : targets){
			if(SystemConfiguration.targetDirectories.contains(target.getTargetPath()))
				remains.add(target.getTargetPath());
			else
				reader.removeTargetDirectoryByID(target.getTargetID());
		}
		/*
		 * 		add new ones
		 */
		for(String newTargetDir : SystemConfiguration.targetDirectories){
			if(!remains.contains(newTargetDir)){
				reader.addAndUpdateTargetDirectory(new TargetDirectory(newTargetDir));
			}
		}
	}
	
	private int getNewDirectoryID(){
		return this.directoryID++;
	}
	
	private int getNewDocumentID(){
		return this.documentID++;
	}
	
	
	/**
	 * update the existing type index into the Berkeley db from memory buff.
	 * then flush the memory space.
	 */
	private void updateTypeIndexIntoDB(){
		
		for(Entry<String, FileType> entry : SystemConfiguration.fileTypeBuff.entrySet()){
			if(!entry.getValue().getDocumentIDs().isEmpty()){
				FileType fileType = this.reader.getFileTypeByType(entry.getKey());
				if(fileType != null){
					Set<Integer> documentIDs = new HashSet<Integer>();
					documentIDs.addAll(fileType.getDocumentIDs());
					documentIDs.addAll(entry.getValue().getDocumentIDs());
					fileType.setDocumentIDs(documentIDs);
					this.reader.addAndUpdateFileType(fileType);
				}
				else{
					this.reader.addAndUpdateFileType(entry.getValue());
				}
				SystemConfiguration.fileTypeBuff.get(entry.getKey()).getDocumentIDs().clear();
			}
		}
		
//		EntityCursor<FileType> fileTypeCursor = this.reader.getFileTypesCursor();
//		for(FileType entity : fileTypeCursor){
//			if(!entity.getDocumentIDs().isEmpty()){
//				Set<Integer> documentIDs = new HashSet<Integer>();
//				documentIDs.addAll(entity.getDocumentIDs());
//				documentIDs.addAll(SystemConfiguration.fileTypeBuff.get(entity.getType()).getDocumentIDs());
//				entity.setDocumentIDs(documentIDs);
//				fileTypeCursor.update(entity);
//			}
//		}
//		fileTypeCursor.close();
	}
	
	/**
	 * updating the term list of dictionary into the Berkeley db from memory buff.
	 * then flush the memory space.
	 */
	private void updateDictionaryIntoDB(){
		for(Entry<String, Term>entry : UpdateFileThread.dictionary.entrySet()){
			Term term = this.reader.getTermByTerm(entry.getKey());
			if(term == null){
				this.reader.addAndUpdateTerm(entry.getValue());
			}
			else{
				
				Set<Integer> postingContent = new HashSet<Integer>();
				Set<Integer> postingTitle = new HashSet<Integer>();
				
				postingContent.addAll(term.getPostingContent());
				postingContent.addAll(entry.getValue().getPostingContent());
				postingTitle.addAll(term.getPostingTitle());
				postingTitle.addAll(entry.getValue().getPostingTitle());
				term.setPostingConteng(postingContent);
				term.setPostingTitle(postingTitle);
				this.reader.addAndUpdateTerm(term);
			}
		}
		
		//release the memory
		UpdateFileThread.dictionary.clear();
		UpdateFileThread.saveParameter();
		this.saveParameter();
	}
	
	
	
	/**
	 * update knowledge repository
	 */
	public void executeKnowledgeUpdate(){
		
	}
	
	public static void main(String args[]){
		WordSegmentation wordSegmentor = (WordSegmentation)InstanceManager.getInstance(InstanceManager.CLASS_WORDSEGMENTATION);
		wordSegmentor.initialize();
		IndexUpdater iu = new IndexUpdater(wordSegmentor);
		iu.execute();
		wordSegmentor.exitICTCLAS();
//		wordSegmentor.destoryInstance();
	}
}
