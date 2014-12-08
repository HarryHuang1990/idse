package cn.iscas.idse.rank;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.demo.BerkelyDBDemo;
import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.rank.location.LocationMining;
import cn.iscas.idse.rank.task.mining.DefaultTaskMining;
import cn.iscas.idse.rank.topic.TopicSimilarity;
import cn.iscas.idse.search.entity.Score;
import cn.iscas.idse.storage.entity.LocationRelation;
import cn.iscas.idse.storage.entity.PageRankGraph;
import cn.iscas.idse.storage.entity.TaskRelation;
import cn.iscas.idse.storage.entity.TopicRelation;
import cn.iscas.idse.storage.entity.accessor.AccessorFactory;
import cn.iscas.idse.storage.entity.accessor.DocumentAccessor;
import cn.iscas.idse.storage.entity.accessor.LocationRelationAccessor;
import cn.iscas.idse.storage.entity.accessor.PageRankGraphAccessor;
import cn.iscas.idse.storage.entity.accessor.TopicRelationAccessor;
import cn.iscas.idse.storage.entity.accessor.TaskRelationAccessor;

public class MatrixWriter {
	
	private static final Logger log = Logger.getLogger(MatrixWriter.class);
	private String userActivityLogFile = "";
	private String LDAdocIDListFileName = "";
	private String LDADataFileName = "";
	
	private Map<Integer, TopicRelation> topicRelationGraph;
	private Map<Integer, LocationRelation> locationRelationGraph;
//	private Map<Integer, TaskRelation> taskRelationGraph;
	private Map<Integer, PageRankGraph> pageRankGraph = new TreeMap<Integer, PageRankGraph>();
	private int pageRankCount = 0;
	private int updateCount = 0;
	
	
	PageRankGraphAccessor pageRankGraphAccessor = AccessorFactory.getPageRankGraphAccessor(SystemConfiguration.database.getIndexStore());
	TopicRelationAccessor topicRelationAccessor = AccessorFactory.getTopicAccessor(SystemConfiguration.database.getIndexStore());
	LocationRelationAccessor locationRelationAccessor = AccessorFactory.getLocationAccessor(SystemConfiguration.database.getIndexStore());
//	TaskRelationAccessor taskRelationAccessor = AccessorFactory.getTaskAccessor(SystemConfiguration.database.getIndexStore());
	DocumentAccessor documentAccessor = AccessorFactory.getDocumentAccessor(SystemConfiguration.database.getIndexStore());
	
	public MatrixWriter(){
		this.userActivityLogFile = SystemConfiguration.userActivityLogFile;
		this.LDAdocIDListFileName = SystemConfiguration.LDAdocIDListFileName;
		this.LDADataFileName = SystemConfiguration.LDADataFileName;
	}
	
	public void run(){
//		this.writeTaskRelationMatrix();
//		this.writeTopicRelationMatrix();
//		this.writeLocationRelationMatrix();
		
		this.getTopicRelationMatrix();
//		this.getTaskRelationMatrix();
		this.getLocationRelationMatrix();
		this.getPageRankGraph();
		this.updatePageRankGraph();
		this.releaseSpace();
		this.convertScoreToProbs();
		this.getRecommends();
		this.writePageRankGraph();
		
//		this.updateRecommends();
	}
	
//	public void updateRecommends(){
//		log.info("serching relevent candidates...");
//		IndexReader reader = new IndexReader();
//		this.pageRankGraph = reader.getPageRankGraph();
//		for(Entry<Integer, PageRankGraph> docPageRankNode : this.pageRankGraph.entrySet()){
//			this.recommendReleventDocuments(docPageRankNode.getValue());
//		}
//		this.updatePageRankGraph();
//	}
//	
//	public void updatePageRankGraph(){
//		for(Entry<Integer, PageRankGraph> docPageRankNode : this.pageRankGraph.entrySet()){
//			this.pageRankGraphAccessor.getPrimaryDocumentID().putNoReturn(docPageRankNode.getValue());
//		}
//	}
	
	public void releaseSpace(){
		this.topicRelationGraph = null;
//		this.taskRelationGraph = null;
		this.locationRelationGraph = null;
	}
	
	/**
	 * Recommend 5 most related documents for every documents. 
	 */
	public void getRecommends(){
		log.info("serching relevent candidates...");
		int finished = 0;
		int total = this.pageRankGraph.size();
		for(Entry<Integer, PageRankGraph> docPageRankNode : this.pageRankGraph.entrySet()){
			++finished;
			this.recommendReleventDocuments(docPageRankNode.getValue());
			log.info("recommending finished: " + finished + "/" + total);
		}
	}
	
	/**
	 * recommend the 5 most related documents with the given documents.
	 * @param score
	 * @param pageRankGraph
	 */
	public void recommendReleventDocuments(PageRankGraph pageRankGraph){
		/*
		 * key : candidate document ID
		 * value : transfer probability
		 */
		Map<Integer, Double> candidatesProbsSum = new HashMap<Integer, Double>();
		PriorityQueue<Score> recommendedDocs = new PriorityQueue<Score>(5, new Comparator<Score>(){
			public int compare(final Score o1,final Score o2) 
	        {
	            double r = o1.getScore() - o2.getScore();
	            return r<0 ? 1 : -1;
	        }
		});
		int step = 0;
		int stopStep = 1;
		if(pageRankGraph.getRelatedDocumentIDs().size() < SystemConfiguration.neightborThreshold)
			stopStep = SystemConfiguration.step;
		this.recursionSearch(candidatesProbsSum, pageRankGraph, step, stopStep, pageRankGraph.getDocumentID(), -1, 1);
		for(Integer docID : candidatesProbsSum.keySet()){
			recommendedDocs.add(new Score(docID, candidatesProbsSum.get(docID)));
		}
		// get top 5.
		int k=0;
		while(recommendedDocs.size() != 0){
			pageRankGraph.getRecommendedDocs().add(recommendedDocs.poll().getDocID());
			k++;
			if(k == 5)break;
		}
	}
	
	/**
	 * search candidate documents and calculate the transfer probability recursively. 
	 */
	public void recursionSearch(
			Map<Integer, Double> candidatesProbsSum, 
			PageRankGraph pageRankGraph, 
			int step,
			int stopStep,
			int sourceDocID, 
			int formerDocID, 
			double probs){
		step++;
		if(step > stopStep)
			return;
		
		for(Entry<Integer, Double>doc : pageRankGraph.getRelatedDocumentIDs().entrySet()){
			// return the source node or former node is not allowed.
			if(doc.getKey() != formerDocID && doc.getKey() != sourceDocID){
				// the transfer probability from the source docNode to current docNode
				double newProbs = probs * doc.getValue();
				// add transfer probability of current docNode to the buffer
				this.addTransferProbability(step, doc.getKey(), newProbs, candidatesProbsSum);
				// get the nodes on the next level
				PageRankGraph docNode = this.pageRankGraphAccessor.getPrimaryDocumentID().get(doc.getKey());
				if(docNode != null)
					this.recursionSearch(candidatesProbsSum, docNode, step, stopStep, sourceDocID, pageRankGraph.getDocumentID(), newProbs);// search recursively
			}
		}
	}
	
	public void addTransferProbability(
			int step,
			int docID, 
			double probs, 
			Map<Integer, Double> candidatesProbsSum){
		if(candidatesProbsSum.containsKey(docID)){
			candidatesProbsSum.put(docID, candidatesProbsSum.get(docID) + probs);
		}
		else{
			candidatesProbsSum.put(docID, probs);
		}
	}
	
	
	/**
	 * integrate the 3 relation graph
	 */
	public void getPageRankGraph(){
		log.info("generate PageRank graph...");
		IndexReader indexReader = new IndexReader();
		Set<Integer> docIDSet = indexReader.getDocumentIDs();
		for(int docID : docIDSet){
			TopicRelation topicRelation = this.topicRelationGraph.get(docID);
			LocationRelation locationRelation = this.locationRelationGraph.get(docID);
			TaskRelation taskRelation = null; //this.taskRelationGraph.get(docID);
			Set<Integer> relatedDocIDs = new HashSet<Integer>();
			// get all related docs
			if(topicRelation != null)
				relatedDocIDs.addAll(topicRelation.getRelatedDocumentIDs().keySet());
			if(locationRelation != null)
				relatedDocIDs.addAll(locationRelation.getRelatedDocumentIDs().keySet());
			if(taskRelation != null)
				relatedDocIDs.addAll(taskRelation.getRelatedDocumentIDs().keySet());
			
			for(Iterator<Integer>it = relatedDocIDs.iterator(); it.hasNext();){
				Double jsValue = null;
				Float locationScore = null;
				Integer frequency = null;
				double score = 0;
				int doc = it.next();
				if(topicRelation != null)
					jsValue = topicRelation.getRelatedDocumentIDs().get(doc);
				if(locationRelation != null)
					locationScore = locationRelation.getRelatedDocumentIDs().get(doc);
				if(taskRelation != null)
					frequency = taskRelation.getRelatedDocumentIDs().get(doc);
				
				jsValue = jsValue == null ? Double.POSITIVE_INFINITY : jsValue;
				locationScore = locationScore == null ? 0 : locationScore;
				frequency = frequency == null ? 0 : frequency;
				score = this.getIntegratedScore(this.getTaskRelationScore(frequency), locationScore, this.getTopicRelationScore(jsValue));
				this.addToPageRankGraph(docID, doc, score);
			}
		}
	}
	
	public void addToPageRankGraph(int docID1, int docID2, double score){
		if(this.pageRankGraph.containsKey(docID1)){
			this.pageRankGraph.get(docID1).putNewRelatedDoc(docID2, score);
		}
		else{
			PageRankGraph pageRankGraph = new PageRankGraph(docID1);
			pageRankGraph.putNewRelatedDoc(docID2, score);
			this.pageRankGraph.put(docID1, pageRankGraph);
			this.pageRankCount++;
			if(this.pageRankCount >= SystemConfiguration.pageRankWriteCountThreshold){
				this.updatePageRankGraph();
				this.pageRankCount = 0;
			}
		}
	}
	
	public double getTaskRelationScore(int freq){
		return (Math.log(freq*freq + 1)/Math.log(4)) / (1 + Math.log(freq*freq + 1)/Math.log(4));
//		return Math.pow(freq + 1.0, 0.25) / (1 + Math.pow(freq + 1.0, 0.25));
	}
	
	public double getTopicRelationScore(double jsValue){
		return 1 / Math.pow(Math.E, jsValue);
	}
	
	public double getIntegratedScore(double taskScore, double locationScore, double topicScore){
		return taskScore * SystemConfiguration.taskFactor + 
				locationScore * SystemConfiguration.locationFactor + 
				topicScore * SystemConfiguration.topicFactor;
	}
	
	/**
	 * convert pageRank relatedScore to transfer probability
	 */
	public void convertScoreToProbs(){
		log.info("Converting Score to transfer probability...");
		this.pageRankGraph.clear();
		this.pageRankGraph.putAll(this.pageRankGraphAccessor.getPrimaryDocumentID().map());
		for(Entry<Integer, PageRankGraph>entry : this.pageRankGraph.entrySet()){
			entry.getValue().getRecommendedDocs().clear();
			entry.getValue().convertScoreToProbs();// convert the correlation score to the probability of transfer
		}
		BerkelyDBDemo.PagRankDistribution(this.pageRankGraph.values());
		log.info("Converting Done.");
	}
	
	/**
	 * write pageRank graph into the Berkeley DB
	 */
	public void writePageRankGraph(){
		log.info("saving PageRankGraph... size0="+this.pageRankGraph.size());
//		this.deletePageRankGraph();
		for(Entry<Integer, PageRankGraph>entry : this.pageRankGraph.entrySet()){
			this.pageRankGraphAccessor.getPrimaryDocumentID().putNoReturn(entry.getValue());
		}
		log.info("pageRank graph DONE.");
	}
	
	public void updatePageRankGraph(){
		this.updateCount++;
		if(this.updateCount == 1){
			this.deletePageRankGraph();
		}
		log.info("updating pageRankGraph...");
		for(Entry<Integer, PageRankGraph>entry : this.pageRankGraph.entrySet()){
			PageRankGraph pageRankGraph = this.pageRankGraphAccessor.getPrimaryDocumentID().get(entry.getKey());
			if(pageRankGraph != null){
				pageRankGraph.getRelatedDocumentIDs().putAll(entry.getValue().getRelatedDocumentIDs());
				this.pageRankGraphAccessor.getPrimaryDocumentID().put(pageRankGraph);
			}
			else
				this.pageRankGraphAccessor.getPrimaryDocumentID().putNoReturn(entry.getValue());
		}
		this.pageRankGraph.clear();
		log.info("pageRank graph update DONE.");
	}
	
	/**
	 * remove pageRank graph from Berkeley DB
	 * @return
	 */
	private void deletePageRankGraph(){
		log.info("removing pageRank graph...");
		Set<Integer> keys = this.pageRankGraphAccessor.getPrimaryDocumentID().sortedMap().keySet();
		if(keys != null)
			for(int key : keys)
				this.pageRankGraphAccessor.getPrimaryDocumentID().delete(key);
	}
	
	
	
	
	
	
	private void getTopicRelationMatrix(){
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
		log.info("start generating the Location relation graph...");
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
	
	
//	private void getTaskRelationMatrix(){
//		log.info("generating the task relation graph...");
//		DefaultTaskMining dtm = new DefaultTaskMining();
//		dtm.generateTaskRelationGraph(this.userActivityLogFile);
//		taskRelationGraph = dtm.getTaskRelationGraph();
//	}
	
	/**
	 * write task location matrix into the Berkeley DB
	 */
//	public void writeTaskRelationMatrix(){
//		this.deleteTaskRelationMatrix();
//		this.getTaskRelationMatrix();
//		log.info("saving...");
//		for(Entry<Integer, TaskRelation> taskRelation : taskRelationGraph.entrySet()){
//			this.taskRelationAccessor.getPrimaryDocumentID().put(taskRelation.getValue());
//		}
//		log.info("task relation graph DONE.");
//	}
	
	/**
	 * remove the task relation matrix from Berkeley DB
	 * @return
	 */
//	private void deleteTaskRelationMatrix(){
//		log.info("removing the task relation graph...");
//		Set<Integer> keys = this.taskRelationAccessor.getPrimaryDocumentID().sortedMap().keySet();
//		if(keys != null)
//			for(int key : keys)
//				this.taskRelationAccessor.getPrimaryDocumentID().delete(key);
//	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		MatrixWriter w = new MatrixWriter();
//		w.writeTaskRelationMatrix();
//		w.writeTopicRelationMatrix();
//		w.writeLocationRelationMatrix();
		
//		System.out.println(Math.E);
//		for(int i=1; i<100 ; i++)
//			System.out.println(i + "\t" + w.getTaskRelationScore(i));
		MatrixWriter w = new MatrixWriter();
		w.run();
		PersonalRank pr = new PersonalRank();
		pr.run();
		
	}

}
