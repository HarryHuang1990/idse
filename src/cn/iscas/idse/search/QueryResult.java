package cn.iscas.idse.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.search.entity.Score;
import cn.iscas.idse.storage.entity.Document;

/**
 * Implements the query results representation. 
 * @author Harry Huang
 *
 */
public class QueryResult {
	
	/**
	 * score queue of the search result. this is a priority queue based on the heap struct.
	 * the sequence of the member is order by the Comparator defined by yourself.
	 */
	private PriorityQueue<Score> scoreQueue;
	/**
	 * the query result list size.
	 */
	private int resultCount;
	
	private IndexReader indexReader;
	
	/**
	 * 
	 * @param resultCount
	 */
	public QueryResult(int resultCount){
		/*
		 * initialize the number of result
		 */
		this.resultCount = resultCount;
		/*
		 * create a priority queue whose members are ordered by score descently.
		 */
		this.scoreQueue = new PriorityQueue<Score>(5, new Comparator<Score>(){
			public int compare(final Score o1,final Score o2) 
	        {
	            double r = o1.getScore() - o2.getScore();
	            return r<0 ? 1 : -1;
	        }
		});
		
		this.indexReader = new IndexReader();
	}
	
	/**
	 * put the document result into the priority queue.
	 * @param score
	 */
	public void put(Score score){
		this.scoreQueue.add(score);
	}
	
	public void clear(){
		this.scoreQueue.clear();
		this.resultCount = 0;
	}
	
	public PriorityQueue<Score> getScoreQueue() {
		return scoreQueue;
	}

	public void setScoreQueue(PriorityQueue<Score> scoreQueue) {
		this.scoreQueue = scoreQueue;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	/**
	 * return the K document with the highest score ordered by score descently.
	 * @param K
	 * @return
	 */
	public List<Score> getTopK(int K){
		List<Score> topKList = new ArrayList<Score>();
		int i=0;
		while(this.scoreQueue.size() != 0){
			topKList.add(this.scoreQueue.poll());
			if(++i == K)break;
		}
		return topKList;
	}
	
	/**
	 * return the result list showed on the page N. (N is the page No.)
	 * @param pageNo	-	page No.
	 * @param pageCount		-	result number displayed on each page.
	 * @return
	 */
//	public List<Document> getPageN(int pageNo, int pageCount){
//		List<Document> pageList = new ArrayList<Document>();
//		Iterator<Score> iterator = this.scoreQueue.iterator();
//		int pageStart = 1 + (pageNo - 1) * pageCount;
//		int pageEnd = pageNo * pageCount;
//		for(int i=1; iterator.hasNext() && (i >= pageStart && i <= pageEnd); i++){
//			pageList.add(this.indexReader.getDocumentByDocID(iterator.next().getDocID()));
//		}
//		return pageList;
//	}
	/**
	 * output the query result on the console. 
	 */
	public void showResult(){
		int i=0;
		while(this.scoreQueue.size() != 0){
			Score score = this.scoreQueue.poll();
			System.out.println(score.getScore() + "\t" + this.indexReader.getAbsolutePathOfDocument(score.getDocID()));
			PriorityQueue<Score>candidates = score.getMostRelatedDocs();
			int k=0;
			while(candidates.size() != 0){
				Score cand = candidates.poll();
				System.out.println("\t" + cand.getScore() + "\t" + this.indexReader.getAbsolutePathOfDocument(cand.getDocID()));
				k++;
				if(k==5)break;
			}
			i++;
			if(i==20)break;
		}
	}
	/**
	 * output the top K query result on the console. 
	 */
	public void showTopKResult(int k){}
	
}
