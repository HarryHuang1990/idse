package cn.iscas.idse.index;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sleepycat.persist.EntityCursor;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.Document;
import cn.iscas.idse.storage.entity.FileType;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.accessor.AccessorFactory;
import cn.iscas.idse.storage.entity.accessor.DirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.DocumentAccessor;
import cn.iscas.idse.storage.entity.accessor.FileTypeAccessor;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;
import cn.iscas.idse.utilities.Converter;

/**
 * This class is responsible for scan the disk and collect the information for indexing.
 * includes : 
 * 		the amount of the files to index.
 * 		the amount of the directories.
 * 		max depth of the target directories.
 * 		average depth of the target directories.
 * 		the file type set
 * 		the file type distribution
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
	private Map<String, Integer> fileTypeDistribution = new HashMap<String, Integer>();
	
	public DiskScanner(	TargetDirectoryAccessor targetDirectoryAccessor){
		this.targetDirectoryAccessor = targetDirectoryAccessor;
	}
	
	/**
	 * scan target directory in disk.
	 */
	public void scanDisk(){
		//scan the target directories.
		System.out.println("scanning the disk...");
		long start = System.currentTimeMillis();
		EntityCursor<TargetDirectory> targets = targetDirectoryAccessor.getPrimaryTargetID().entities();
		for(TargetDirectory target : targets){
			this.scanDirectory(new File(target.getTargetPath()), target.getTargetID());
		}
		long end = System.currentTimeMillis();
		System.out.println("scanning done. time = " + ((end-start)/1000/60.0) + " min");
		System.out.println("directory : " + this.directoryNumber);
		System.out.println("file : " + this.fileNumber);
		System.out.println("file type distribution : \n" + this.fileTypeDistribution.toString());
	}
	

	
	/**
	 * scan directory
	 * @param directory
	 */
	private void scanDirectory(File directory, short targetID){
		// directory number increases by 1
		int directoryID = ++this.directoryNumber;
//		// write the directory info into db.
//		if(!this.directoryAccessor.getSecondaryDirectoryPath().contains(Converter.convertBackSlashToSlash(directory.getAbsolutePath())))
//			this.directoryAccessor.getPrimaryDirectoryID().putNoReturn(
//					new Directory(directoryID, targetID, Converter.convertBackSlashToSlash(directory.getAbsolutePath())));
		
		// get list of file and directory obj.
		File[]files = directory.listFiles();
		if(files != null && files.length != 0){
			// scan each file or directory.
			for(File object : files){
				if(object.isDirectory()){
					// scan directory
					this.scanDirectory(object, targetID);
				}
				else{
					// file number increases by 1
					this.fileNumber ++;
//					//write the file info into db
//					if(!this.documentAccessor.getPrimaryDocumentID().contains(this.fileNumber))
//						this.documentAccessor.getPrimaryDocumentID().putNoReturn(
//								new Document(
//										this.fileNumber,
//										directoryID, 
//										object.getName()));
					
					// get the file type
					int suffixIndexStart = object.getName().lastIndexOf(".");
					String suffix = "";
					if(suffixIndexStart != -1){
						suffix = object.getName().substring(suffixIndexStart, object.getName().length()).toLowerCase();
					}
					else{
						suffix = ".";
					}
					
//					// add the documentID to the type index
//					if(SystemConfiguration.fileTypeBuff.containsKey(suffix)){
//						SystemConfiguration.fileTypeBuff.get(suffix).getDocumentIDs().add(this.fileNumber);
//					}
					
					// update the file type distribution
					if(this.fileTypeDistribution.containsKey(suffix)){
						this.fileTypeDistribution.put(suffix, this.fileTypeDistribution.get(suffix) + 1);
					}
					else{
						this.fileTypeDistribution.put(suffix, 1);
					}
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
