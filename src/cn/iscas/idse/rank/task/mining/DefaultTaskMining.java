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
	 * record the interaction frequency between two given documentIDs.  
	 * Note that adjacent duplicated record exists in the raw task. 
	 * e.g., 
	 * 			A
	 * 			A
	 * 			A
	 * 			B
	 * 			C
	 * @param logFile	the user activity log file path
	 */
	int count = 0;
	public void generateTaskRelationGraph(String logFile){
		CSVParser parser = new CSVParser();
		this.rawtasks = parser.execute(logFile);
		if(this.rawtasks.size() > 0){
			for(List<Log> task : this.rawtasks){
				// a sequence of docID after filtering.
				List<Integer> docSequence = new ArrayList<Integer>();
				int lastDocID = -1;
				int currentDocID = -1;
				// handle the adjacent duplicated record and none-existed record.
				for(int i=0; i<task.size(); i++){
					currentDocID = this.identityFile(task.get(i).getFileName());
					if(currentDocID == -1)continue;
					else if(currentDocID != lastDocID){
						docSequence.add(currentDocID);
						lastDocID = currentDocID;
					}
				}
				
				// get the interaction frequency and occurrences
				// Note that after filtering, the tasks containing only 1 recored exits, which result in 
				// that the size of occurrences is larger than size of graph.
				if(docSequence.size() == 1) System.out.println(++count);
				for(int i=0; i<docSequence.size(); i++){
					this.addOccurrences(docSequence.get(i));
					if(i < docSequence.size() - 1)
						this.addToGraph(docSequence.get(i), docSequence.get(i + 1));
				}
			}
		}
		this.calculateEValue();
	}
	
	
	
	/**
	 * add the occurrences of files
	 */
	private void addOccurrences(int docID){
		this.totalOccurrence ++;
		if(this.docOccurrences.containsKey(docID))
			this.docOccurrences.put(docID, this.docOccurrences.get(docID) + 1);
		else
			this.docOccurrences.put(docID, 1);
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
