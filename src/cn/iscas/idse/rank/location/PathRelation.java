package cn.iscas.idse.rank.location;

import java.util.HashMap;
import java.util.Map;

/**
 * Path relation graph 
 * @author Harry Huang
 *
 */
public class PathRelation {
	public int directoryID;
	public Map<Integer, Double> relatedDirectories = new HashMap<Integer, Double>();
	
	public PathRelation(int directoryID){
		this.directoryID = directoryID;
	}

	public int getDirectoryID() {
		return directoryID;
	}

	public void setDirectoryID(int directoryID) {
		this.directoryID = directoryID;
	}

	public Map<Integer, Double> getRelatedDirectories() {
		return relatedDirectories;
	}
	
	public void putNewRelatedDirectory(int directoryID, double score){
		this.putNewRelatedDirectory(directoryID, score);
	}
}
