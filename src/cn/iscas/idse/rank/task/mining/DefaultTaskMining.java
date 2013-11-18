package cn.iscas.idse.rank.task.mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.iscas.idse.rank.task.CSVParser;
import cn.iscas.idse.rank.task.Log;
import cn.iscas.idse.rank.task.TaskMining;
import cn.iscas.idse.storage.entity.TaskRelation;

/**
 * the default task mining strategy.
 * @author Harry Huang
 *
 */
public class DefaultTaskMining extends TaskMining{
	
	private List<List<Log>> rawtasks = new ArrayList<List<Log>>();
	
	public void outputGraph(){
		for(Entry<Integer, TaskRelation>entry : this.taskRelationGraph.entrySet()){
			System.out.print(entry.getKey() + " \t : \t");
			for(Entry<Integer, Integer> record : entry.getValue().getRelatedDocumentIDs().entrySet()){
				System.out.print("<" + record.getKey() + ", " + record.getValue() + ">");
			}
			System.out.println();
		}
	}
	
	
	/**
	 * generate the task relation graph.
	 * record the interaction frequency between two given documentID 
	 * @param logFile	the user activity log file path
	 */
	public void generateTaskRelationGraph(String logFile){
		CSVParser parser = new CSVParser();
		this.rawtasks = parser.execute(logFile);
		if(this.rawtasks.size() > 1){
			for(List<Log> task : this.rawtasks){
				int lastDocID = this.identityFile(task.get(0).getFileName());
				int currentDocID = -1;
				for(int i=1; i<task.size(); i++){
					currentDocID = this.identityFile(task.get(i).getFileName());
					if(lastDocID == -1){
						lastDocID = currentDocID;
						continue;
					}
					else{
						if(currentDocID == -1) continue;
						else{
							this.addToGraph(lastDocID, currentDocID);
							lastDocID = currentDocID;
						}
					}
				}
			}
		}
	}
	
	private void addToGraph(int docID1, int docID2){
		if(docID1 == docID2)return;
		if(this.taskRelationGraph.containsKey(docID1)){
			this.taskRelationGraph.get(docID1).putInteractionRecord(docID2);
		}
		else{
			TaskRelation taskRelation = new TaskRelation(docID1);
			taskRelation.putInteractionRecord(docID2);
			this.taskRelationGraph.put(docID1, taskRelation);
		}
		
		if(this.taskRelationGraph.containsKey(docID2)){
			this.taskRelationGraph.get(docID2).putInteractionRecord(docID1);
		}
		else{
			TaskRelation taskRelation = new TaskRelation(docID2);
			taskRelation.putInteractionRecord(docID1);
			this.taskRelationGraph.put(docID2, taskRelation);
		}
	}
	
	public static void main(String[] args) {
		DefaultTaskMining dtm = new DefaultTaskMining();
		dtm.generateTaskRelationGraph("F:/user_activity_log/ManicTimeData_doc.csv");
		dtm.outputGraph();
	}

}
