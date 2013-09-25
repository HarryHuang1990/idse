package cn.iscas.idse.demo;

import java.util.Map;
import java.util.Map.Entry;

import cn.iscas.idse.storage.DBManager;
import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.accessor.DirectoryAccessor;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;

import com.sleepycat.persist.EntityCursor;

/**
 * all demo about usage BerkeleyDB
 * @author Harry Huang
 *
 */
public class BerkelyDBDemo {
	
	
	public static void main(String[] args){
		
		DBManager db = new DBManager();
		TargetDirectoryAccessor accessor = new TargetDirectoryAccessor(db.getIndexStore());
		Map<Short, TargetDirectory> map = accessor.getPrimaryTargetID().sortedMap();
		for(Entry<Short, TargetDirectory>entry:map.entrySet()){
			System.out.println(entry.getKey() + "\t" + entry.getValue().getTargetPath());
		}
		
		DirectoryAccessor da = new DirectoryAccessor(db.getIndexStore());
		for(int i=0; i<200; i++){
			da.getPrimaryDirectoryID().put(new Directory(i,  (short) 1,  "/a/b/c/d/e"));
		}
		
		EntityCursor<Directory> entities = da.getSecondaryTargetID().subIndex((short)101).entities(); 
		for(Directory entity : entities){
			System.out.println(entity.getDirectoryID() + "\t" + entity.getDirectoryPath());
		}
		
		entities.close();
//		accessor.getPrimaryTargetID().delete((short)301);
		da.getPrimaryDirectoryID().delete(100);
		
		System.out.println("É¾³ýd:/my bankºó targetdirectory");
		EntityCursor<TargetDirectory> es = accessor.getPrimaryTargetID().entities(); 
		for(TargetDirectory entity : es){
			System.out.println(entity.getTargetID() + "\t" + entity.getTargetPath());
		}
		es.close();
		
		System.out.println("É¾³ýd:/my bankºó directory");
		entities = da.getPrimaryDirectoryID().entities(); 
		for(Directory entity : entities){
			System.out.println(entity.getDirectoryID() + "\t" + entity.getDirectoryPath());
		}
		entities.close();
		db.close();
	}
}
