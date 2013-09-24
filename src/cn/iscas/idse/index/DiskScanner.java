package cn.iscas.idse.index;

import java.io.File;

import com.sleepycat.persist.EntityCursor;

import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;

/**
 * This class is responsible for scan the disk and collect the information for indexing.
 * includes : 
 * 		the amount of the files to index.
 * 		the amount of the directories.
 * 		max depth of the target directories.
 * 		average depth of the target directories.
 * 		
 * @author Harry Huang
 *
 */
public class DiskScanner {
	
	private TargetDirectoryAccessor targetDirectoryAccessor = null;
	private int fileNumber = 0;
	private int directoryNumber = 0;
//	private int maxDepth = 0;
//	private int averageDepth = 0;
	
	public DiskScanner(TargetDirectoryAccessor targetDirectoryAccessor){
		this.targetDirectoryAccessor = targetDirectoryAccessor;
	}
	
	/**
	 * scan target directory in disk.
	 */
	public void scanDisk(){
		//scan the target directories.
		System.out.println("scanning the disk...");
		EntityCursor<TargetDirectory> targets = targetDirectoryAccessor.getPrimaryTargetID().entities();
		for(TargetDirectory target : targets){
			this.scanDirectory(new File(target.getTargetPath()));
		}
		System.out.println("scanning done.");
		System.out.println("directory : " + this.directoryNumber);
		System.out.println("file : " + this.fileNumber);
	}
	
	/**
	 * scan directory
	 * @param directory
	 */
	private void scanDirectory(File directory){
		// get list of file and directory obj.
		File[]files = directory.listFiles();
		if(files != null && files.length != 0){
			// scan each file or directory.
			for(File object : files){
				if(object.isDirectory()){
					// directory number increases by 1
					this.directoryNumber ++;
					// scan directory
					this.scanDirectory(object);
				}
				else{
					// file number increases by 1
					this.fileNumber ++;
				}
			}
		}
	}

	public int getFileNumber() {
		return fileNumber;
	}

	public int getDirectoryNumber() {
		return directoryNumber;
	}

	
	
}
