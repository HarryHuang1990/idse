package cn.iscas.idse.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.search.entity.Document;
import cn.iscas.idse.search.entity.Query;
import cn.iscas.idse.search.entity.Score;
import cn.iscas.idse.storage.entity.PageRankGraph;
import cn.iscas.idse.storage.entity.PostingContent;
import cn.iscas.idse.storage.entity.PostingTitle;
import cn.iscas.idse.storage.entity.Term;
import cn.iscas.idse.utilities.Sort;

/**
 * this is a main API for query.
 * receive the query information and execute searching.
 * @author Harry Huang
 *
 */
public class Search {
	
	private static final Logger log = Logger.getLogger(Search.class);
	
	private QueryParser queryParser;
	private IndexReader indexReader;
	
	public Search(){
		this.queryParser = new QueryParser();
		this.indexReader = new IndexReader();
	}

	public QueryResult executeSearch(String query){
		
		/*
		 * parse the query
		 */
		this.queryParser.setQuery(query);
		this.queryParser.parse();
		System.out.println(this.queryParser.getQueryEntity().toString());
		
		QueryResult queryResult = this.getQueryResult(this.queryParser.getQueryEntity());
		queryResult = this.getFinalScoreOfTopN(queryResult);
		
//		if(queryResult != null)
//			queryResult.showResult();
		
		return queryResult;
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
		dfMap[0] = new HashMap<String, Integer>();	// for title
		dfMap[1] = new HashMap<String, Integer>();	// for content
		/*
		 * document number
		 */
		int documentNumber = this.indexReader.getNumberDocuments();
		
		
		for(String term : queryEntity.getQueryPosting().keySet()){
			// extract the inverted index from database.
			Term termEntity = this.indexReader.getTermByTerm(term);
			if(termEntity == null)continue;
			//df
			int df_title = termEntity.getDocumentFrequenceForTitle();
			int df_content = termEntity.getDocumentFrequenceForContent();
			if(df_title != 0)
				dfMap[0].put(term, termEntity.getDocumentFrequenceForTitle());
			if(df_content != 0)
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
//				queryResult.put(this.getFinalScore(similarity.score(queryEntity, entry.getValue()), PR));
				Score score = similarity.score(queryEntity, entry.getValue());
				if(score.getScore() != 0)
					queryResult.put(score);
			}
		}
		return queryResult;
	}
	
	/**
	 * sort the topN relevent result with pageRank score (view as secondary sort) . and return the new sort.
	 * topN(default 20) is set in the {@link cn.iscas.idse.config.SystemConfiguration}.
	 * @param releventResult
	 * @return
	 */
	public QueryResult getFinalScoreOfTopN(QueryResult releventResult){
		if(releventResult != null){
			List<Score> releventList = releventResult.getTopK(SystemConfiguration.topN);
			releventResult.clear();
			List<List<Score>> secondarySort = new ArrayList<List<Score>>();
			secondarySort.add(new ArrayList<Score>()); // >0.8 , 0
			secondarySort.add(new ArrayList<Score>()); // >0.6 , 1
			secondarySort.add(new ArrayList<Score>()); // >0.4 , 2
			secondarySort.add(new ArrayList<Score>()); // >0.2 , 3
			secondarySort.add(new ArrayList<Score>()); // <=0.2, 4
			for(Score score : releventList){
				PageRankGraph pageRankGraph = this.indexReader.getPageRankGraphByID(score.getDocID());
				if(pageRankGraph != null){
					double cosin = score.getScore();
					score.setScore(cosin * pageRankGraph.getPageRankScore());
					// get top5 most related documents
					score.setMostRelatedDocs(pageRankGraph.getRecommendedDocs());
					if(cosin < 0.2){
						secondarySort.get(4).add(score);
					}
					else if(cosin < 0.4){
						secondarySort.get(3).add(score);
					}
					else if(cosin < 0.6){
						secondarySort.get(2).add(score);
					}
					else if(cosin < 0.8){
						secondarySort.get(1).add(score);
					}
					else{
						secondarySort.get(0).add(score);
					}	
				}
			}
			// secondary sort;
			int rank = 0;
			for(List<Score> secList : secondarySort){
				Sort.sortDoubleList(secList);
				for(Score score : secList){
					score.setScore(1.0/(++rank));
					releventResult.put(score);
				}
			}
		}
		return releventResult;
	}
	

	
	
//	/**
//	 * ### Combine the cosin score with the pageRank score if the pageRank mode is chosen.
//	 * @param cosinScore	the tf-idf score
//	 * @param PR			PR = true, combine the tf-idf score and pageRank; PR=false, return directly without pageRank.
//	 * @return
//	 */
//	public Score getFinalScore(Score cosinScore, boolean PR){
//		Score score = cosinScore;
//		if(PR){
//			PageRankGraph pageRankGraph = this.indexReader.getPageRankGraphByID(score.getDocID());
//			if(pageRankGraph == null)
//				System.out.println(score.getDocID());
//			score.setScore(score.getScore() * pageRankGraph.getPageRankScore());
//		}
//		return score;
//	}
	
	public static void main(String[]args){
		Search search = new Search();
		while(true){
			Scanner str = new Scanner(System.in);
			String keyword = str.nextLine();
			search.executeSearch(keyword);
		}
		
	}
}
