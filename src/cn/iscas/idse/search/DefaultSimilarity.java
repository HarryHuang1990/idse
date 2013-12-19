package cn.iscas.idse.search;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.search.entity.Document;
import cn.iscas.idse.search.entity.Query;
import cn.iscas.idse.search.entity.Score;

/**
 * An implementation of the classic VSM based on the {@link TFIDFSimilarity}
 * 
 * @author Harry Huang
 *
 */
public class DefaultSimilarity extends TFIDFSimilarity {
	
	/**
	 * <term, df> maps for title and content
	 * dfMap[0] : the map for title;
	 * dfMap[1] : the map for content;
	 */
	private Map<String, Integer>[] dfMap;
	private int documentNumber = 0;
	private IndexReader indexReader = new IndexReader();
	
	public DefaultSimilarity(Map<String, Integer>[] dfMap, int documentNumber){
		this.dfMap = dfMap;
		this.documentNumber = documentNumber;
	}

	/**
	 * <p>
	 * set <term, df> maps for title and content
	 * </p>
	 * <ol>
	 * <li>
	 * dfMap[0] : the map for title;
	 * </li>
	 * <li>
	 * dfMap[1] : the map for content;
	 * </li>
	 * </ol>
	 * @param dfMap
	 */
	public void setDfMap(Map<String, Integer>[] dfMap) {
		this.dfMap = dfMap;
	}
	
	public void setDocumentNumber(int documentNumber) {
		this.documentNumber = documentNumber;
	}

	/**
	 * calculate the hits rate of the query keywords 
	 * in a field (title or content) of a given document.
	 * @param hits - the number of query keywords in the specific field
	 * @param totalQueryWords - the number of query keywords
	 * @return
	 */
	private float coord(int hits, int totalQueryWords){
		return 1.0f * hits / totalQueryWords ;
	}
	
	/**
	 * calculate the similarity score between given query and document
	 * @param query
	 * @param document
	 * @return
	 */
	public Score score(Query query, Document document){
		Score score = null;
		double hitsRatioTitle = this.coord(document.getTitlePostings().size(), query.getQueryPosting().size());
		double hitsRatioContent = this.coord(document.getContentPostings().size(), query.getQueryPosting().size());
		if(hitsRatioTitle<0.5 && hitsRatioContent<0.5){
			hitsRatioTitle = 0;
			hitsRatioContent = 0;
		}
		if(hitsRatioTitle + hitsRatioContent != 0){
			/*
			 * get the tuning factors according to the hits of different fields of document.
			 */
			double factors[] = this.getTuningFactor(hitsRatioTitle, hitsRatioContent);
			/*
			 * calculate the similarity score on different fields.
			 */
			double scoreTitle = this.fieldSimilarity(query, document, true);
			double scoreContent = this.fieldSimilarity(query, document, false);
			
//			score = new Score(
//					document.getDocID(), 
//					(factors[0] * scoreTitle + factors[1] * scoreContent));
			score = new Score(
					document.getDocID(), 
					(hitsRatioTitle * scoreTitle + hitsRatioContent * scoreContent));
		}
		else
			score = new Score(document.getDocID(), 	0);
		
		return score;
	}
	
	/**
	 * calculate the similarity score between given query and the certain field (title, content) of document.
	 * @param query
	 * @param document
	 * @param isTitle	-  indicates the object of calculation : title or content?
	 * @return
	 */
	private double fieldSimilarity(Query query, Document document, boolean isTitle){
		double similarity = this.queryNormal(query, isTitle) * this.vectorsProduct(query, document, isTitle)
				* this.documentNormal(document, isTitle) * this.documentBoost(document);
		return similarity;
	}
	
	/**
	 * calculate the product of query vector and document vector
	 * @param queryVector
	 * @param documentVector
	 * @return
	 */
	private double vectorsProduct(Query query, Document document, boolean isTitle){
		double result = 0.0;
		Map<String, List<Integer>> queryVector = query.getQueryPosting();
		Map<String, List<Integer>> documentVector = isTitle ? document.getTitlePostings() : document.getContentPostings();
		
		for(Entry<String, List<Integer>>entry : documentVector.entrySet()){
			//vector space product = Q * D
			result += this.IDF(entry.getKey(), isTitle) * this.TFIDF(entry.getKey(), entry.getValue().size(), isTitle);
		}
		return result;
	}
	
	/**
	 * calculate the tf-idf value of the given term
	 * @param term
	 * @param tf
	 * @param isTitle
	 * @return
	 */
	private double TFIDF(String term, int tf, boolean isTitle){
		return this.TF(tf) * this.IDF(term, isTitle);
	}
	
	/**
	 * tf(t in d) correlates to the term's frequency
	 * @param tf
	 * @return
	 */
	private double TF(int tf){
		return (1 + Math.log10(tf));
	}
	
	/**
	 * calculate idf
	 * @param term
	 * @param isTitle
	 * @return
	 */
	private double IDF(String term, boolean isTitle){
		double idf = 1.0;
		if(isTitle)
			idf = (1 + Math.log10(this.documentNumber * 1.0f / this.dfMap[0].get(term)));
		else
			idf = (1 + Math.log10(this.documentNumber * 1.0f / this.dfMap[1].get(term)));
		return idf;
	}
	
	/**
	 * normal the given query
	 * @param query
	 * @return
	 */
	private double queryNormal(Query query, boolean isTitle){
		double sumOfSquare = 0.0;
		Map<String, List<Integer>> queryVector = query.getQueryPosting();
		
		for(Entry<String, List<Integer>> entry : queryVector.entrySet()){
			if(isTitle && !this.dfMap[0].containsKey(entry.getKey()))
				continue;
			if(!isTitle && !this.dfMap[1].containsKey(entry.getKey()))
				continue;
			
			sumOfSquare += Math.pow(this.IDF(entry.getKey(), isTitle), 2);
		}
		return sumOfSquare == 0 ? 0 : 1.0 / Math.sqrt(sumOfSquare);
//		return 1.0f;
	}

	/**
	 * normal the given document field.
	 * @param document
	 * @param isTitle
	 * @return
	 */
	private double documentNormal(Document document, boolean isTitle){
		int hitTotalCount = 0;
		int docLength = indexReader.getDocContentLength(document.getDocID());
		double sumOfSquare = 0f;
		
		Map<String, List<Integer>> documentVector = isTitle ? document.getTitlePostings() : document.getContentPostings();
		for(Entry<String, List<Integer>> entry : documentVector.entrySet()){
			sumOfSquare += Math.pow(this.TFIDF(entry.getKey(), entry.getValue().size(), isTitle), 2);
			hitTotalCount += entry.getValue().size();
		}
		return sumOfSquare == 0 ? 0 : 1.0f / (Math.sqrt(sumOfSquare + (docLength - hitTotalCount)));
	}
	
	/**
	 * return the boost score of the given document, 
	 * which represents as the importance of the document
	 * @param document
	 * @return
	 */
	private double documentBoost(Document document){
		return 1.0;
	}
	
	/**
	 * return the hits of keywords of query in 
	 * title and content of given document.
	 * @param query
	 * @param document
	 * @return
	 */
	private int[] getHits(Query query, Document document){
		return new int[]{document.getTitlePostings().size(), document.getContentPostings().size()};
	}
	
	/**
	 * return the tuning factors for score integration between title-part and content-part 
	 * @param coordTitle
	 * @param coordContent
	 * @return float[0] - factor for title-part; float[1] - factor for content-part.
	 */
	private double[] getTuningFactor(double coordTitle, double coordContent){
		double[] factors = new double[2];
//		float sumOfSquare = (float) Math.sqrt(Math.pow(coordTitle, 2) + Math.pow(coordContent, 2));
		double sumOfSquare = coordTitle + coordContent;
		factors[0] = coordTitle / sumOfSquare;
		factors[1] = coordContent / sumOfSquare;
		return factors;
	}
}
