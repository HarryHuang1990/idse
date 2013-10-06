package cn.iscas.idse.search;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		/*
		 * get the tuning factors according to the hits of different fields of document.
		 */
		float factors[] = this.getTuningFactor(
				this.coord(document.getTitlePostings().size(), query.getQueryPosting().size()), 
				this.coord(document.getContentPostings().size(), query.getQueryPosting().size()));
		/*
		 * calculate the similarity score on different fields.
		 */
		float scoreTitle = this.fieldSimilarity(query, document, true);
		float scoreContent = this.fieldSimilarity(query, document, false);
		
		Score score = new Score(
				document.getDocID(), 
				factors[0] * scoreTitle + factors[1] * scoreContent);
		
		return score;
	}
	
	/**
	 * calculate the similarity score between given query and the certain field (title, content) of document.
	 * @param query
	 * @param document
	 * @param isTitle	-  indicates the object of calculation : title or content?
	 * @return
	 */
	private float fieldSimilarity(Query query, Document document, boolean isTitle){
		float similarity = this.queryNormal(query) * this.vectorsProduct(query, document, isTitle)
				* this.documentNormal(document, isTitle) * this.documentBoost(document);
		return similarity;
	}
	
	/**
	 * calculate the product of query vector and document vector
	 * @param queryVector
	 * @param documentVector
	 * @return
	 */
	private float vectorsProduct(Query query, Document document, boolean isTitle){
		float result = 0.0f;
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
	private float TFIDF(String term, int tf, boolean isTitle){
		return this.TF(tf) * this.IDF(term, isTitle);
	}
	
	/**
	 * tf(t in d) correlates to the term's frequency
	 * @param tf
	 * @return
	 */
	private float TF(int tf){
		return (float) (1 + Math.log10(tf));
	}
	
	/**
	 * calculate idf
	 * @param term
	 * @param isTitle
	 * @return
	 */
	private float IDF(String term, boolean isTitle){
		float idf = 1.0f;
		if(isTitle)
			idf = (float) (1 + Math.log10(this.documentNumber * 1.0f / this.dfMap[0].get(term)));
		else
			idf = (float) (1 + Math.log10(this.documentNumber * 1.0f / this.dfMap[1].get(term)));
		return idf;
	}
	
	/**
	 * normal the given query
	 * @param query
	 * @return
	 */
	private float queryNormal(Query query){
		return 1.0f;
	}

	/**
	 * normal the given document field.
	 * @param document
	 * @param isTitle
	 * @return
	 */
	private float documentNormal(Document document, boolean isTitle){
		
		float sumOfSquare = 0f;
		
		Map<String, List<Integer>> documentVector = isTitle ? document.getTitlePostings() : document.getContentPostings();
		
		for(Entry<String, List<Integer>> entry : documentVector.entrySet()){
			sumOfSquare += Math.pow(this.TFIDF(entry.getKey(), entry.getValue().size(), isTitle), 2);
		}
		
		return sumOfSquare == 0 ? 0 : 1.0f / (float) Math.sqrt(sumOfSquare);
	}
	
	/**
	 * return the boost score of the given document, 
	 * which represents as the importance of the document
	 * @param document
	 * @return
	 */
	private float documentBoost(Document document){
		return 1.0f;
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
	private float[] getTuningFactor(float coordTitle, float coordContent){
		float[] factors = new float[2];
		float sumOfSquare = (float) Math.sqrt(Math.pow(coordTitle, 2) + Math.pow(coordContent, 2));
		factors[0] = coordTitle / sumOfSquare;
		factors[1] = coordContent / sumOfSquare;
		return factors;
	}
}
