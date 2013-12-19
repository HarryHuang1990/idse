package cn.iscas.idse.rank.location;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.Document;
import cn.iscas.idse.storage.entity.LocationRelation;

/**
 * Implements the location relation mining.
 * collect the 3 aspects of location information, including
 * 		1. the mean depth of file A and B
 * 			E(a,b) = (|a| + |b| )/2
 * 		2. the path length from A to B
 * 			D(a,b)=(|a|-|c|)+(|b|-|c|)
 * 		3. the difference of depth between file A and B 
 * 			M(a,b) = ||a|-|b||
 * the score between A and B is as follow, 
 * 		score(a,b) = E(a,b)^0.5 / (1 + D(a,b)^2 + M(a,b)^2 + E(a,b)^0.5)
 * 
 * the pruning threshold on D(a,b), which is configured in the SystemConfiguration.class
 * 		D(a,b) <= gama = 3;
 * 
 * @author Harry Huang
 * @date 2013.11.18
 *
 */
public class LocationMining {
	
	private static final Logger log = Logger.getLogger(LocationMining.class);
	/**
	 * location relation graph:
	 * key : the document id;
	 * value : LocationRelation Entity, which includes the related documents and scores.
	 */
	private Map<Integer, LocationRelation> locationRelationGraph = new HashMap<Integer, LocationRelation>();
	/**
	 * the relation between two files is actually the relation between the directories the two files in.
	 * key : the directory id;;
	 * value : score 
	 */
	private Map<Integer, PathRelation> pathRelationGraph = new HashMap<Integer, PathRelation>();
	private IndexReader indexReader = new IndexReader();
	

	public void run(){
		log.info("generating the path relation graph...");
		this.generatePathRelation();
		log.info("generating the location relation graph...");
		this.generateLocationRelationGraph();
		log.info("pruning...");
		this.executePruning();
	}
	
	public Map<Integer, LocationRelation> getLocationRelationGraph() {
		return locationRelationGraph;
	}
	/**
	 * pruning the location graph
	 */
	private void executePruning(){
		Set<Integer> nodeToDelete = new HashSet<Integer>();
		// get the nodes to delete
		for(Entry<Integer, LocationRelation>entry : this.locationRelationGraph.entrySet()){
			if(entry.getValue().getRelatedDocumentIDs().size() > SystemConfiguration.neightborThreshold){
				nodeToDelete.add(entry.getKey());
			}
		}
		// delete the nodes
		for(int docID : nodeToDelete){
			Set<Integer> relatedKeys = this.locationRelationGraph.get(docID).getRelatedDocumentIDs().keySet();
			for(int relatedKey : relatedKeys){
				this.locationRelationGraph.get(relatedKey).getRelatedDocumentIDs().remove(docID);
			}
			this.locationRelationGraph.remove(docID);
		}
	}
	
	/**
	 * generate the location relation graph from directoryRelatedScoreMap
	 */
	private void generateLocationRelationGraph(){
		for(PathRelation pathRelation : this.pathRelationGraph.values()){
			List<Document> dir1Docs = indexReader.getDocumentsByDirectoryID(pathRelation.getDirectoryID());
			for(Entry<Integer, Float>relatedDir : pathRelation.getRelatedDirectories().entrySet()){
				List<Document> dir2Docs = indexReader.getDocumentsByDirectoryID(relatedDir.getKey());
				for(int i=0; i<dir1Docs.size(); i++)
					for(int j=0; j<dir2Docs.size(); j++){
						if(dir1Docs.get(i).getDocID() != dir2Docs.get(j).getDocID())
							this.addToLocationRelationGraph(dir1Docs.get(i).getDocID(), dir2Docs.get(j).getDocID(), relatedDir.getValue());
					}
						
			}
		}
	}
	
	private void addToPathRelationGraph(int dirID1, int dirID2, float score){
		if(this.pathRelationGraph.containsKey(dirID1)){
			this.pathRelationGraph.get(dirID1).putNewRelatedDirectory(dirID2, score);
		}
		else{
			PathRelation pathRelation = new PathRelation(dirID1);
			pathRelation.putNewRelatedDirectory(dirID2, score);
			this.pathRelationGraph.put(dirID1, pathRelation);
		}
		
		if(this.pathRelationGraph.containsKey(dirID2)){
			this.pathRelationGraph.get(dirID2).putNewRelatedDirectory(dirID1, score);
		}
		else{
			PathRelation pathRelation = new PathRelation(dirID2);
			pathRelation.putNewRelatedDirectory(dirID1, score);
			this.pathRelationGraph.put(dirID2, pathRelation);
		}
	}
	
	private void addToLocationRelationGraph(int docID1, int docID2, double score){
		if(this.locationRelationGraph.containsKey(docID1)){
			this.locationRelationGraph.get(docID1).putNewRelatedDocument(docID2, (float) score);
		}
		else{
			LocationRelation locationRelation = new LocationRelation(docID1);
			locationRelation.putNewRelatedDocument(docID2, (float) score);
			this.locationRelationGraph.put(docID1, locationRelation);
		}
		
//		if(this.locationRelationGraph.containsKey(docID2)){
//			this.locationRelationGraph.get(docID2).putNewRelatedDocument(docID1, score);
//		}
//		else{
//			LocationRelation locationRelation = new LocationRelation(docID2);
//			locationRelation.putNewRelatedDocument(docID1, score);
//			this.locationRelationGraph.put(docID2, locationRelation);
//		}
	}
	
	/**
	 * generate directoryRelatedScoreMap.
	 * Note that any directory have relation with itself. 
	 */
	private void generatePathRelation(){
		List<Directory>directories = indexReader.getDirectorys();
		for(int i=0; i<directories.size(); i++){
			for(int j=i; j<directories.size(); j++){
				Directory dir1 = directories.get(i);
				Directory dir2 = directories.get(j);
				double[]metrics = this.parsePaths(dir1.getDirectoryPath(), dir2.getDirectoryPath());
				if(metrics != null){
					double score = this.calculateTheMatrics(metrics);
					this.addToPathRelationGraph(dir1.getDirectoryID(), dir2.getDirectoryID(), (float)score);
				}
			}
		}
	}

	
	/**
	 * calculate the location relation score using the given metrics.
	 * the formula is as follow:
	 * 		
	 * 		score(a,b) = E(a,b)^0.5 / (1 + D(a,b)^2 + M(a,b)^2 + E(a,b)^0.5) 
	 * 
	 * @param metrics {D(a,b), E(a,b), M(a,b)}
	 * @return	the location relation score.
	 */
	public double calculateTheMatrics(double metrics[]){
		double score = 0;
		double sqrtE = Math.sqrt(metrics[1]);
		score = sqrtE / (1 + Math.pow(metrics[0], 2) + Math.pow(metrics[2], 2) + sqrtE);
		return score;
	}
	
	/**
	 * parse the two given paths and figure out 3 metric;
	 * @param path1
	 * @param path2
	 * @return 
	 * 		metrics{D(a,b), E(a,b), M(a,b)}; 
	 * 		if the D(a,b) doesn't satisfy the pruning threshold, return null;
	 */
	public double[] parsePaths(String path1, String path2){
		double metrics[] = new double[3];
		path1 = path1.replaceAll("//", "/");
		path2 = path2.replaceAll("//", "/");
		String[]paths1 = path1.split("/");
		String[]paths2 = path2.split("/");
		// D(a,b)
		int lowestCommonAncestor = -1;
		int minLength = paths1.length <= paths2.length ? paths1.length : paths2.length;
		for(int i=0; i < minLength; i++){
			if(!paths1[i].equals(paths2[i])){
				lowestCommonAncestor = i;
				break;
			}
		}
		if(lowestCommonAncestor == 0) //means the disk no. is different.
			return null;
		if(lowestCommonAncestor == -1)
			lowestCommonAncestor = minLength;
		double dDistance = (paths1.length - lowestCommonAncestor) + (paths2.length - lowestCommonAncestor); 
		if(dDistance > SystemConfiguration.dMAX_GAMA)
			return null;
		metrics[0] = dDistance;
		// E(a,b)
		metrics[1] = (paths1.length + paths2.length) / 2.0;
		// M(a,b)
		metrics[2] = Math.abs(paths1.length - paths2.length);
		return metrics;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocationMining lm = new LocationMining();
		//F:/
		//F://$RECYCLE.BIN
		//D:/迅雷下载/【铁血战士】【高清1280版HD-RMVB.中字】【2013最新美国科幻恐怖大片】
		//F://beijing gps/cluster/dataset
		double[] metrics = lm.parsePaths("D:/My DBank/总体部/上海专项编译构建环境、系统测试工作", "D:/My DBank/总体部/上海专项编译构建环境、系统测试工作");
		System.out.println(metrics);
		if(metrics != null){
			System.out.println("D(a,b)=" + metrics[0] + "\tE(a,b)=" + metrics[1] + "\tM(a,b)=" + metrics[2]);
			System.out.println(lm.calculateTheMatrics(metrics));
		}
	}

}
