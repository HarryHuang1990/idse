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
		this.scoreQueue = new PriorityQueue<Score>(this.resultCount, new Comparator<Score>(){
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
	
	
	/**
	 * return the K document with the highest score ordered by score descently.
	 * @param K
	 * @return
	 */
	public List<Document> getTopK(int K){
		List<Document> topKList = new ArrayList<Document>();
		Iterator<Score> iterator = this.scoreQueue.iterator();
		for(int i=0; iterator.hasNext() && i < K; i++){
			topKList.add(this.indexReader.getDocumentByDocID(iterator.next().getDocID()));
		}
		return topKList;
	}
	
	/**
	 * return the result list showed on the page N. (N is the page No.)
	 * @param pageNo	-	page No.
	 * @param pageCount		-	result number displayed on each page.
	 * @return
	 */
	public List<Document> getPageN(int pageNo, int pageCount){
		List<Document> pageList = new ArrayList<Document>();
		Iterator<Score> iterator = this.scoreQueue.iterator();
		int pageStart = 1 + (pageNo - 1) * pageCount;
		int pageEnd = pageNo * pageCount;
		for(int i=1; iterator.hasNext() && (i >= pageStart && i <= pageEnd); i++){
			pageList.add(this.indexReader.getDocumentByDocID(iterator.next().getDocID()));
		}
		return pageList;
	}
	/**
	 * output the query result on the console. 
	 */
	public void showResult(){
		while(this.scoreQueue.size() != 0){
			Score score = this.scoreQueue.poll();
			System.out.println(score.getScore() + "\t" + this.indexReader.getAbsolutePathOfDocument(score.getDocID()));
		}
	}
	/**
	 * output the top K query result on the console. 
	 */
	public void showTopKResult(int k){}
	
}
