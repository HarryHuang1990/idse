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
	
	/**
	 * write topic relation matrix into the Berkeley DB
	 */
	public void writeTopicRelationMatrix(){
		this.deleteTopicRelationMatrix();
		log.info("generating the topic relation graph...");
		TopicSimilarity ts = new TopicSimilarity(this.LDAdocIDListFileName, this.LDADataFileName);
		ts.run();
		log.info("saving...");
		Map<Integer, TopicRelation> topicRelationGraph = ts.getTopicRelationGraph();
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
	
	/**
	 * write location relation matrix into the Berkeley DB
	 */
	public void writeLocationRelationMatrix(){
		this.deleteLocationRelationMatrix();
		log.info("start generating the topic relation graph...");
		LocationMining locationMining = new LocationMining();
		locationMining.run();
		log.info("saving...");
		Map<Integer, LocationRelation> locationRelationGraph = locationMining.getLocationRelationGraph();
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
	
	/**
	 * write task location matrix into the Berkeley DB
	 */
	public void writeTaskRelationMatrix(){
		this.deleteTaskRelationMatrix();
		log.info("generating the task relation graph...");
		DefaultTaskMining dtm = new DefaultTaskMining();
		dtm.generateTaskRelationGraph(this.userActivityLogFile);
		log.info("saving...");
		Map<Integer, TaskRelation> taskRelationGraph = dtm.getTaskRelationGraph();
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
//		w.writeLocationRelationMatrix();
//		w.writeTaskRelationMatrix();
//		w.writeTopicRelationMatrix();
		w.writeLocationRelationMatrix();
		
	}

}
