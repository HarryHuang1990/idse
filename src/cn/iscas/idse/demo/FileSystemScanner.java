package cn.iscas.idse.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.utilities.Sort;

import com.sleepycat.persist.EntityCursor;

/**
 * Scanner of the file system
 * @author Administrator
 *
 */
public class FileSystemScanner {
	
	private static int fileNumber = 0;
	private static int directoryNumber = 0;
//	private int maxDepth = 0;
//	private int averageDepth = 0;
	private static Map<String, Integer> fileTypeDistribution = new HashMap<String, Integer>();
	
	public static void scanDisk(){
		String[] roots = new String[]{
				"C:/Users/Administrator/AppData/Local/Microsoft/Windows/Temporary Internet Files"
//				"D:/",
//				"E:/",
//				"F:/",
//				"G:/",
//				"O:/",
//				"P:/",
				
		};

		//scan the target directories.
		System.out.println("scanning the disk...");
		for(String root : roots){
			scanDirectory(new File(root));
		}
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(fileTypeDistribution.entrySet());
		Sort.sortStringIntegerMapDesc(list);
		System.out.println("scanning done.");
		System.out.println("directory : " + directoryNumber);
		System.out.println("file : " + fileNumber);
		System.out.println("file type distribution : " + list.size());
		for(int i=0; i<600; i++){
			Entry<String, Integer> entry = list.get(i);
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}
	
	/**
	 * scan directory
	 * @param directory
	 */
	public static void scanDirectory(File directory){
		// get list of file and directory obj.
		File[]files = directory.listFiles();
		if(files != null && files.length != 0){
			// scan each file or directory.
			for(File object : files){
				if(object.isDirectory()){
					// directory number increases by 1
					directoryNumber ++;
					System.out.println(object.getAbsolutePath());
					// scan directory
					scanDirectory(object);
				}
				else{
					// file number increases by 1
					fileNumber ++;
					System.out.println(object.getAbsolutePath());
					// get the file type
					int suffixIndexStart = object.getName().lastIndexOf(".");
					String suffix = "";
					if(suffixIndexStart != -1){
						suffix = object.getName().substring(suffixIndexStart, object.getName().length()).toLowerCase();
					}
					else{
						suffix = ".";
					}
					// update the file type distribution
					if(fileTypeDistribution.containsKey(suffix)){
						fileTypeDistribution.put(suffix, fileTypeDistribution.get(suffix) + 1);
					}
					else{
						fileTypeDistribution.put(suffix, 1);
					}
				}
			}
		}
	}
	
	
	public static void main(String args[]){
		scanDisk();
	}
}
