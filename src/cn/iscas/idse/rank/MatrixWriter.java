package cn.iscas.idse.rank;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityIndex;
import com.sleepycat.persist.evolve.Deleter;
import com.sleepycat.persist.evolve.Mutations;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.index.Index;
import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.rank.location.LocationMining;
import cn.iscas.idse.rank.task.mining.DefaultTaskMining;
import cn.iscas.idse.rank.topic.TopicSimilarity;
import cn.iscas.idse.storage.entity.Document;
import cn.iscas.idse.storage.entity.LocationRelation;
import cn.iscas.idse.storage.entity.PageRankGraph;
import cn.iscas.idse.storage.entity.TaskRelation;
import cn.iscas.idse.storage.entity.TopicRelation;
import cn.iscas.idse.storage.entity.accessor.AccessorFactory;
import cn.iscas.idse.storage.entity.accessor.DocumentAccessor;
import cn.iscas.idse.storage.entity.accessor.LocationRelationAccessor;
import cn.iscas.idse.storage.entity.accessor.TopicRelationAccessor;
import cn.iscas.idse.storage.entity.accessor.TaskRelationAccessor;

public class MatrixWriter {
	
	private static final Logger log = Logger.getLogger(MatrixWriter.class);
	private String userActivityLogFile = "";
	private String LDAdocIDListFileName = "";
	private String LDADataFileName = "";
	
	private Map<Integer, TopicRelation> topicRelationGraph;
	private Map<Integer, LocationRelation> locationRelationGraph;
	private Map<Integer, TaskRelation> taskRelationGraph;
	private Map<Integer, PageRankGraph> pageRankGraph;
	
	TopicRelationAccessor topicRelationAccessor = AccessorFactory.getTopicAccessor(SystemConfiguration.database.getIndexStore());
	LocationRelationAccessor locationRelationAccessor = AccessorFactory.getLocationAccessor(SystemConfiguration.database.getIndexStore());
	TaskRelationAccessor taskRelationAccessor = AccessorFactory.getTaskAccessor(SystemConfiguration.database.getIndexStore());
	DocumentAccessor documentAccessor = AccessorFactory.getDocumentAccessor(SystemConfiguration.database.getIndexStore());

	
	public MatrixWriter(){}
	public MatrixWriter(
			String userActivityLogFile,
			String LDAdocIDListFileName,
			String LDADataFileName
			){
		this.userActivityLogFile = userActivityLogFile;
		this.LDAdocIDListFileName = LDAdocIDListFileName;
		this.LDADataFileName = LDADataFileName;
	}
	
	public void run(){
		this.writeTaskRelationMatrix();
		this.writeTopicRelationMatrix();
		this.writeLocationRelationMatrix();
	}
	
	/**
	 * integrate the 3 relation graph
	 */
	public void getPageRankGraph(){
		// TODO 
	}
	
	public double getTaskRelationScore(int freq){
		return (Math.log(freq + 1)/Math.log(2)) / (1 + Math.log(freq + 1)/Math.log(2));
	}
	
	public double getTopicRelationScore(int jsValue){
		return 1 / Math.pow(Math.E, jsValue);
	}
	
	public double getIntegratedScore(double taskScore, double locationScore, double topicScore){
		return taskScore * SystemConfiguration.taskFactor + 
				locationScore * SystemConfiguration.locationFactor + 
				topicScore * SystemConfiguration.topicFactor;
	}
	
	
	
	
	public void getTopicRelationMatrix(){
		log.info("generating the topic relation graph...");
		TopicSimilarity ts = new TopicSimilarity(this.LDAdocIDListFileName, this.LDADataFileName);
		ts.run();
		topicRelationGraph = ts.getTopicRelationGraph();
	}
	
	/**
	 * write topic relation matrix into the Berkeley DB
	 */
	public void writeTopicRelationMatrix(){
		this.deleteTopicRelationMatrix();
		this.getTopicRelationMatrix();
		log.info("saving...");
		for(Entry<Integer, TopicRelation>entry : topicRelationGraph.entrySet()){
			this.topicRelationAccessor.getPrimaryDocumentID().putNoReturn(entry.getValue());
		}
		log.info("topic relation graph DONE.");
	}
	
	/**
	 * remove the topic relation matrix from Berkeley DB
	 * @return
	 */
	private void deleteTopicRelationMatrix(){
		log.info("removing the topic relation graph...");
		Set<Integer> keys = this.topicRelationAccessor.getPrimaryDocumentID().sortedMap().keySet();
		if(keys != null)
			for(int key : keys)
				this.topicRelationAccessor.getPrimaryDocumentID().delete(key);
	}
	
	private void getLocationRelationMatrix(){
		log.info("start generating the topic relation graph...");
		LocationMining locationMining = new LocationMining();
		locationMining.run();
		locationRelationGraph = locationMining.getLocationRelationGraph();
	}
	/**
	 * write location relation matrix into the Berkeley DB
	 */
	public void writeLocationRelationMatrix(){
		this.deleteLocationRelationMatrix();
		this.getLocationRelationMatrix();
		log.info("saving...");
		for(Entry<Integer, LocationRelation>entry : locationRelationGraph.entrySet()){
			this.locationRelationAccessor.getPrimaryDocumentID().putNoReturn(entry.getValue());
		}
		log.info("location relation graph DONE.");
	}
	
	/**
	 * remove the location relation matrix from Berkeley DB
	 * @return
	 */
	private void deleteLocationRelationMatrix(){
		log.info("removing location matrix...");
		Set<Integer> keys = this.locationRelationAccessor.getPrimaryDocumentID().sortedMap().keySet();
		if(keys != null)
			for(int key : keys)
				this.locationRelationAccessor.getPrimaryDocumentID().delete(key);
	}
	
	
	private void getTaskRelationMatrix(){
		log.info("generating the task relation graph...");
		DefaultTaskMining dtm = new DefaultTaskMining();
		dtm.generateTaskRelationGraph(this.userActivityLogFile);
		taskRelationGraph = dtm.getTaskRelationGraph();
	}
	
	/**
	 * write task location matrix into the Berkeley DB
	 */
	public void writeTaskRelationMatrix(){
		this.deleteTaskRelationMatrix();
		this.getTaskRelationMatrix();
		log.info("saving...");
		for(Entry<Integer, TaskRelation> taskRelation : taskRelationGraph.entrySet()){
			this.taskRelationAccessor.getPrimaryDocumentID().put(taskRelation.getValue());
		}
		log.info("task relation graph DONE.");
	}
	
	/**
	 * remove the task relation matrix from Berkeley DB
	 * @return
	 */
	private void deleteTaskRelationMatrix(){
		log.info("removing the task relation graph...");
		Set<Integer> keys = this.taskRelationAccessor.getPrimaryDocumentID().sortedMap().keySet();
		if(keys != null)
			for(int key : keys)
				this.taskRelationAccessor.getPrimaryDocumentID().delete(key);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MatrixWriter w = new MatrixWriter("F:/user_activity_log/log.csv", "00000000.map", "00000000");
//		w.writeTaskRelationMatrix();
//		w.writeTopicRelationMatrix();
//		w.writeLocationRelationMatrix();
		
		System.out.println(Math.E);
		for(int i=1; i<20 ; i++)
			System.out.println(i + "\t" + w.getTaskRelationScore(i));
		
	}

}
