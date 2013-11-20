package cn.iscas.idse.rank.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.rank.MatrixWriter;
import cn.iscas.idse.storage.entity.Directory;
import cn.iscas.idse.storage.entity.Document;
import cn.iscas.idse.storage.entity.TaskRelation;

/**
 * responsible for task mining from the user activity log.
 * the current solution includes :
 * 	1. time interval filter: filter the the file from the activity sequence. split the sequence into some parts which task denotes as tasks.
 *  2. association rules mining 
 * @author dell
 *
 */
public class TaskMining {
	private static final Logger log = Logger.getLogger(TaskMining.class);
	protected IndexReader indexReader = new IndexReader();
	protected Map<Integer, TaskRelation> taskRelationGraph = new HashMap<Integer, TaskRelation>();
	/**
	 * the occurrences of every document.
	 */
	protected Map<Integer, Integer> docOccurrences = new HashMap<Integer, Integer>(); 
	protected int totalOccurrence = 0;
	
	public Map<Integer, TaskRelation> getTaskRelationGraph() {
		return taskRelationGraph;
	}
	
	/**
	 * calculate the eValue of pageRank for each document using occurrences, 
	 * then add the value into the graph 
	 */
	protected void calculateEValue(){
		log.info("calculating e-value...");
		for(int docID : this.taskRelationGraph.keySet())
			this.taskRelationGraph.get(docID).seteValue(this.docOccurrences.get(docID) * 1.0 / this.totalOccurrence);
	}
	
	
	/**
	 * identity the doc name in log from the Berkeley DB.
	 * Return the docID if the given doc exists in the 
	 * current index.
	 * 
	 * if we can not sure the exact document. we use the log context to ensure the exact path.
	 * 
	 * @param nameInLog	  the value of attribute "Name" in log
	 * @return	-1 : if the given doc not exits, docID : if the given doc exits.
	 */
	public int identityFile(String nameInLog){
		//将所有的\替换成/
		nameInLog = nameInLog.replaceAll("\\\\", "/");
		nameInLog = nameInLog.replaceAll("\\*", "").trim();
		int docNameStartIndex = nameInLog.lastIndexOf("/");
		String prefixInfo = "";
		String docNameInLog = "";
		if(docNameStartIndex == -1){
			docNameInLog = nameInLog;
		}
		else{
			prefixInfo = nameInLog.substring(0, docNameStartIndex);
			docNameInLog = nameInLog.substring(docNameStartIndex + 1);
		}
		return this.identityFile(prefixInfo, docNameInLog);
	}
	

	/**
	 * identity the doc name in log from the Berkeley DB.
	 * Return the docID if the given doc exists in the 
	 * current index.
	 * 
	 * if we can not sure the exact document. we use the log context to ensure the exact path.
	 * 
	 * @param prefixInfo	: the information may be absolute path or relative path or null;
	 * @param docNameInLog	
	 * @return	-1 : if the given doc not exits, docID : if the given doc exits.
	 */
	public int identityFile(String prefixInfo, String docNameInLog){
		int documentID = -1;
		// 得到同名的文件集合
		List<Document> documents = this.indexReader.getDocumentsByName(docNameInLog);
		
		if(prefixInfo == null || "".equals(prefixInfo)){
			if(documents != null && documents.size() != 0){
				documentID = documents.get(0).getDocID();
			}
		}
		else{
			if(documents != null && documents.size() != 0){
				// index中目录是没有/结尾的，所以这里做一个校验
				if("/".equals(prefixInfo) || "\\".equals(prefixInfo)){
					prefixInfo = prefixInfo.substring(0, prefixInfo.length()-1);
				}
				// 根据所在目录信息确定是哪个文件
				for(Document document : documents){
					Directory dir = this.indexReader.getDirectoryByDirectoryID(document.getDirectoryID());
					if(dir.getDirectoryPath().endsWith(prefixInfo)){
						documentID = document.getDocID();
						break;
					}
				}
			}
		}
		return documentID;
	}

	
	public static void main(String args[]){
		String s[] = new String[]{
			"20130916_IDSE/src/cn/iscas/idse/search/Search.java",
			"皇甫杨简历_5_1.pdf",
			"D:\\My DBank\\总体部\\周报\\皇甫杨-201309160922周报.docx",
			"C:\\Users\\Administrator\\Downloads\\btm-v0.3\\btm-v0.3\\batch\\main.cpp",
			"2006-SIGIR- LDA-based document models for ad-hoc retrieval.pdf *"
		};
		IndexReader index = new IndexReader();
		TaskMining	tm = new TaskMining();
		for(String name : s){
			int ID = tm.identityFile(name);
			System.out.println(ID + "\t" + index.getAbsolutePathOfDocument(ID));
		}
	}
	
}
