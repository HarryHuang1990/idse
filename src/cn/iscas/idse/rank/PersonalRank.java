package cn.iscas.idse.rank;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.storage.entity.PageRankGraph;
import cn.iscas.idse.storage.entity.TaskRelation;

/**
 * <p>
 * implements the personal pageRank. calculate the importance of each document.
 * there are 3 relationship between every two documents.
 * </p>
 * <ol>
 * <li>task relationship : the two file in the same task have a task relationship.</li>
 * <li>location relationship : the two file under the same directory have a location relationship.</li>
 * <li>topic relationship : the two file in the same topic cluster have a topic relationship.</li>
 * </ol>
 * 
 * @author Harry Huang
 *
 */
public class PersonalRank {
	
	private static final Logger log = Logger.getLogger(PersonalRank.class);
	
	private IndexReader indexReader = new IndexReader();
	private Map<Integer, PageRankGraph> pageRankGraph = null;
	private Map<Integer, Double> pageRank = new HashMap<Integer, Double>();
	private Map<Integer, Double> E = new HashMap<Integer, Double>();
	private double d = 0.85;
	private int iteration = 100;
	private double epision = 1E-10;
	
	public void run(){
		this.initialize();
		this.executePageRankWithIteration();
		log.info("Done.");
	}
	
	private void initialize(){
		log.info("initializing...");
		Map<Integer, TaskRelation> taskGraph = indexReader.getTaskRelation();
		this.pageRankGraph = indexReader.getPageRankGraph();
		int docTotal = this.pageRankGraph.size();
		
		// init E and pageRank
		for(int docID : this.pageRankGraph.keySet()){
			if(taskGraph.containsKey(docID)){
				this.E.put(docID, taskGraph.get(docID).geteValue());
				this.pageRank.put(docID, taskGraph.get(docID).geteValue());
			}
			else{
				this.E.put(docID, 1.0 / docTotal);
				this.pageRank.put(docID, 1.0 / docTotal);
			}
		}
	}
	
	/**
	 * execute pageRank and end with given iteration
	 */
	private void executePageRankWithIteration(){
		for(int iter=1; iter<=this.iteration; iter++){
			log.info("running iteration " + iter + "...");
			int count = 0;
			int total = this.pageRankGraph.size();
			Map<Integer, Double> newPageRank = new HashMap<Integer, Double>();
			for(Entry<Integer, PageRankGraph> node : this.pageRankGraph.entrySet()){
				double newPageRankScore = this.calculatePageRank(this.d, this.E.get(node.getKey()), node.getKey(), node.getValue());
				newPageRank.put(node.getKey(), newPageRankScore);
			}
			double error = this.calculateError(this.pageRank, newPageRank);
			System.out.println("Error = " + error);
			this.pageRank = newPageRank;
			if(error < this.epision)
				break;
		}
		this.writePageRankScoreIntoDB();
	}
	/**
	 * execute pageRank and end with given epision
	 */
	public void executePageRankWithEpision(){
		int iter = 0;
		while(true){
			iter++;
			log.info("running iteration " + iter + "...");
			int count = 0;
			int total = this.pageRankGraph.size();
			Map<Integer, Double> newPageRank = new HashMap<Integer, Double>();
			for(Entry<Integer, PageRankGraph> node : this.pageRankGraph.entrySet()){
				double newPageRankScore = this.calculatePageRank(this.d, this.E.get(node.getKey()), node.getKey(), node.getValue());
				newPageRank.put(node.getKey(), newPageRankScore);
			}
			double error = this.calculateError(this.pageRank, newPageRank);
			System.out.println("Error = " + error);
			this.pageRank = newPageRank;
			if(error < this.epision)
				break;
		}
		this.writePageRankScoreIntoDB();
	}
	
	/**
	 * set attribute "pageRankScore" of PageRankGraph as the corresponding value. 
	 * then update the PageRankGraph entity into the Berkeley DB.
	 */
	public void writePageRankScoreIntoDB(){
		log.info("saving PageRank results...");
		for(Entry<Integer, PageRankGraph> doc : this.pageRankGraph.entrySet()){
			PageRankGraph pageRankGraph = doc.getValue();
			pageRankGraph.setPageRankScore(this.pageRank.get(doc.getKey()));
			this.indexReader.addAndUpdatePageRankGraph(pageRankGraph);
		}
	}
	
	/**
	 * calculate new pagerank score for a document
	 * @param d
	 * @param e
	 * @param curDocID
	 * @param m
	 * @param oriPageRankScore
	 * @return
	 */
	public double calculatePageRank(double d, double e, int curDocID, PageRankGraph m){
		double pageRankScore = (1 - d) * e;
		double surfPart = 0;
		for(int relatedDocID : m.getRelatedDocumentIDs().keySet()){
			double transferProbs = this.pageRankGraph.get(relatedDocID).getRelatedDocumentIDs().get(curDocID);
			double oriPageRank = this.pageRank.get(relatedDocID);
			surfPart += transferProbs * oriPageRank;
		}
		pageRankScore += d * surfPart;
		return pageRankScore;
	}
	
	public double calculateError(Map<Integer, Double> oldPageRank, Map<Integer, Double> newPageRank){
		double error = 0;
		for(int docID : oldPageRank.keySet())
			error += Math.pow(oldPageRank.get(docID) - newPageRank.get(docID), 2);
		return Math.sqrt(error);
	}
	
	public static void main(String args[]){
		PersonalRank pr = new PersonalRank();
		pr.run();
	}
}
