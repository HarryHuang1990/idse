package cn.iscas.idse.index;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.sleepycat.persist.EntityCursor;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.Document;
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
	private DirectoryAccessor directoryAccessor = null;
	private DocumentAccessor documentAccessor = null;
	private FileTypeAccessor fileTypeAccessor = null;
	
	private int fileNumber = 0;
	private int directoryNumber = 0;
//	private int maxDepth = 0;
//	private int averageDepth = 0;
	private Map<String, Integer> fileTypeDistribution = new HashMap<String, Integer>();
	
	public DiskScanner(
			TargetDirectoryAccessor targetDirectoryAccessor, 
			DirectoryAccessor directoryAccessor, 
			DocumentAccessor documentAccessor){
		this.targetDirectoryAccessor = targetDirectoryAccessor;
		this.documentAccessor = documentAccessor;
		this.fileTypeAccessor = AccessorFactory.getFileTypeAccessor(SystemConfiguration.database.getIndexStore());
	}
	
	/**
	 * scan target directory in disk.
	 */
	public void scanDisk(){
		//scan the target directories.
		System.out.println("scanning the disk...");
		EntityCursor<TargetDirectory> targets = targetDirectoryAccessor.getPrimaryTargetID().entities();
		for(TargetDirectory target : targets){
			this.scanDirectory(new File(target.getTargetPath()), target.getTargetID());
		}
		System.out.println("scanning done.");
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
		this.directoryNumber ++;
		// write the directory info into db.
		this.directoryAccessor.getPrimaryDirectoryID().putNoReturn(
				new Directory(targetID, Converter.convertBackSlashToSlash(directory.getAbsolutePath())));
		
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
					//write the file info into db
					this.documentAccessor.getPrimaryDocumentID().putNoReturn(
							new Document(
									this.directoryAccessor.getSecondaryDirectoryPath().get(directory.getAbsolutePath()).getDirectoryID(), 
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
					
					// TODO generate type index
					
					
					
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
