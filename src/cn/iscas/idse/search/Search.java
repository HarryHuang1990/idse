package cn.iscas.idse.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.search.entity.Document;
import cn.iscas.idse.search.entity.Query;
import cn.iscas.idse.storage.entity.PostingContent;
import cn.iscas.idse.storage.entity.PostingTitle;
import cn.iscas.idse.storage.entity.Term;

/**
 * this is a main API for query.
 * receive the query information and execute searching.
 * @author Harry Huang
 *
 */
public class Search {
	
	
	private QueryParser queryParser;
	private IndexReader indexReader;
	
	public Search(){
		this.queryParser = new QueryParser();
		this.indexReader = new IndexReader();
	}

	public void executeSearch(String query){
		
		/*
		 * parse the query
		 */
		this.queryParser.setQuery(query);
		this.queryParser.parse();
		System.out.println(this.queryParser.getQueryEntity().toString());
		
		QueryResult queryResult = this.getQueryResult(this.queryParser.getQueryEntity());
		if(queryResult != null)
			queryResult.showResult();
	}
	
	/**
	 * read the index, extract the relevant documents, calculate the similarity between each 
	 * document and the given query. Order the document by the similarity score.
	 * @param queryEntity
	 * @return
	 */
	public QueryResult getQueryResult(Query queryEntity){
		/*
		 *  candidate documents map <documentId, document object>
		 */
		Map<Integer, Document> documents = new HashMap<Integer, Document>();
		/*
		 * df map
		 */
		Map<String, Integer>[] dfMap = new Map[2];
		dfMap[0] = new HashMap<String, Integer>();
		dfMap[1] = new HashMap<String, Integer>();
		/*
		 * document number
		 */
		int documentNumber = this.indexReader.getNumberDocuments();
		
		
		for(String term : queryEntity.getQueryPosting().keySet()){
			// extract the inverted index from database.
			Term termEntity = this.indexReader.getTermByTerm(term);
			if(termEntity == null)continue;
			//df
			dfMap[0].put(term, termEntity.getDocumentFrequenceForTitle());
			dfMap[1].put(term, termEntity.getDocumentFrequenceForContent());
			// get title posting list
			for(int postingID : termEntity.getPostingTitle()){
				PostingTitle pt = this.indexReader.getPostingTitleByPostingID(postingID);
				if(documents.containsKey(pt.getDocID())){
					documents.get(pt.getDocID()).getTitlePostings().put(term, pt.getOffsets());
				}
				else{
					Document doc = new Document(pt.getDocID());
					doc.getTitlePostings().put(term, pt.getOffsets());
					documents.put(pt.getDocID(), doc);
				}
			}
			// get content posting list
			for(int postingID : termEntity.getPostingContent()){
				PostingContent pc = this.indexReader.getPostingContentByPostingID(postingID);
				if(documents.containsKey(pc.getDocID())){
					documents.get(pc.getDocID()).getContentPostings().put(term, pc.getOffsets());
				}
				else{
					Document doc = new Document(pc.getDocID());
					doc.getContentPostings().put(term, pc.getOffsets());
					documents.put(pc.getDocID(), doc);
				}
			}
		}
		
		// calculate similarity
		QueryResult queryResult = null;
		if(documents.size() != 0){
			queryResult = new QueryResult(documents.size());
			DefaultSimilarity similarity = new DefaultSimilarity(dfMap, documentNumber);
			for(Entry<Integer, Document>entry : documents.entrySet()){
				queryResult.put(similarity.score(queryEntity, entry.getValue()));
			}
		}
		
		return queryResult;
	}
	
	public static void main(String[]args){
		Search search = new Search();
		while(true){
			Scanner str = new Scanner(System.in);
			search.executeSearch(str.nextLine());
		}
		
	}
}
