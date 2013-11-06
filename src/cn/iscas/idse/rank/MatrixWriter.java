package cn.iscas.idse.rank;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityIndex;
import com.sleepycat.persist.evolve.Deleter;
import com.sleepycat.persist.evolve.Mutations;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.index.IndexReader;
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
	
	TopicRelationAccessor topicRelationAccessor = AccessorFactory.getTopicAccessor(SystemConfiguration.database.getIndexStore());
	LocationRelationAccessor locationRelationAccessor = AccessorFactory.getLocationAccessor(SystemConfiguration.database.getIndexStore());
	TaskRelationAccessor taskRelationAccessor = AccessorFactory.getTaskAccessor(SystemConfiguration.database.getIndexStore());
	DocumentAccessor documentAccessor = AccessorFactory.getDocumentAccessor(SystemConfiguration.database.getIndexStore());
	
	/**
	 * write topic relation matrix into the Berkeley DB
	 * @param topicDocsMap	topicID-docID
	 */
	public void writeTopicRelationMatrix(Map<Integer, List<Integer>>topicDocsMap){
		this.deleteTopicRelationMatrix();
		for(Entry<Integer, List<Integer>>entry : topicDocsMap.entrySet()){
			if(entry.getValue().size() > 1){
				for(int docID : entry.getValue()){
					TopicRelation topicRelation = new TopicRelation(docID);
					topicRelation.getRelatedDocumentIDs().addAll(entry.getValue());
					topicRelation.getRelatedDocumentIDs().remove(docID);
					this.topicRelationAccessor.getPrimaryDocumentID().putNoReturn(topicRelation);
				}
			}
			
		}
	}
	
	/**
	 * remove the topic relation matrix from Berkeley DB
	 * @return
	 */
	private void deleteTopicRelationMatrix(){
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
		IndexReader reader = new IndexReader();
		// get directory list
		EntityCursor<Integer> cursor = reader.getDirectoryIDs();
		int total = reader.getNumberDirectorys();
		int count = 0;
		// loop and get the documentIDs in the same directory
		for(int dirID : cursor){
			count++;
			Set<Integer> documentIDs = reader.getDocumentsByDirectoryIDCursor(dirID).sortedMap().keySet();
			if(documentIDs.size() > 1 && documentIDs.size() < SystemConfiguration.maxFileCountPreDirectory){
				for(int documentID : documentIDs){
					LocationRelation locationRelation = new LocationRelation(documentID);
					locationRelation.getRelatedDocumentIDs().addAll(documentIDs);
					locationRelation.getRelatedDocumentIDs().remove(documentID);
					this.locationRelationAccessor.getPrimaryDocumentID().put(locationRelation);
				}
			}
			System.out.println("finished " + count + "/" + total + "...");
		}
		cursor.close();
	}
	
	/**
	 * remove the location relation matrix from Berkeley DB
	 * @return
	 */
	private void deleteLocationRelationMatrix(){
		System.out.println("removing location matrix...");
		Set<Integer> keys = this.locationRelationAccessor.getPrimaryDocumentID().sortedMap().keySet();
		if(keys != null)
			for(int key : keys)
				this.locationRelationAccessor.getPrimaryDocumentID().delete(key);
	}
	
	/**
	 * write task location matrix into the Berkeley DB
	 */
	public void writeTaskRelationMatrix(Map<Integer, TaskRelation> taskRelationGraph){
		this.deleteTaskRelationMatrix();
		for(Entry<Integer, TaskRelation> taskRelation : taskRelationGraph.entrySet()){
			this.taskRelationAccessor.getPrimaryDocumentID().put(taskRelation.getValue());
		}
	}
	
	/**
	 * remove the task relation matrix from Berkeley DB
	 * @return
	 */
	private void deleteTaskRelationMatrix(){
		System.out.println("removing task matrix...");
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
		MatrixWriter w = new MatrixWriter();
		w.writeLocationRelationMatrix();
	}

}
