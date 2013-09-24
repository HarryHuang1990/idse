package cn.iscas.idse.index;

import java.io.File;

import com.sleepycat.persist.EntityCursor;

import cn.iscas.idse.storage.DBManager;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;

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
	
	public IndexWriter(){
		database = new DBManager();
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
		//get target directories
		TargetDirectoryAccessor targetDirectoryAccessor = new TargetDirectoryAccessor(this.database.getIndexStore());
		EntityCursor<TargetDirectory> targets = targetDirectoryAccessor.getPrimaryTargetID().entities();
		
		//scan the target directory
		this.scanner = new DiskScanner(targetDirectoryAccessor);
		scanner.scanDisk();
		
		//index each directory.
//		for(TargetDirectory target : targets){
//			this.indexDirectory(new File(target.getTargetPath()));
//		}
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
					indexFile(object);
				}
			}
		}
	}
	
	/**
	 * index file
	 * @param file
	 */
	private void indexFile(File file){
		
	}
	
	public static void main(String[] args){
		IndexWriter indexer = new IndexWriter();
		indexer.executeIndexing();
//		DBManager db = new DBManager();
//		TargetDirectoryAccessor accessor = new TargetDirectoryAccessor(db.getIndexStore());
//		for(int i=0; i<10; i++){
//			System.out.println(i);
//			accessor.getPrimaryTargetID().put(new TargetDirectory("target" + i));
//		}
//		Map<Short, TargetDirectory> map = accessor.getPrimaryTargetID().sortedMap();
//		for(Entry<Short, TargetDirectory>entry:map.entrySet()){
//			System.out.println(entry.getKey() + "\t" + entry.getValue().getTargetPath());
//		}
//		db.close();
	}
}
