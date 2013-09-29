package cn.iscas.idse.search.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a Document Entity for similarity calculation.
 * the entity contains the postings corresponding to the keywords 
 * both in title and content.
 * @author Harry Huang
 *
 */
public class Document {
	
	/**
	 * document ID
	 */
	private int docID;
	/**
	 * title posting map <term, offset>
	 */
	private Map<String, List<Integer>> titlePostings = new HashMap<String, List<Integer>>();
	/**
	 * content posting map <term, offset>
	 */
	private Map<String, List<Integer>> contentPostings = new HashMap<String, List<Integer>>();
	
	public Document(int docID){
		this.docID = docID;
	}
	
	public int getDocID() {
		return docID;
	}
	public void setDocID(int docID) {
		this.docID = docID;
	}
	public Map<String, List<Integer>> getTitlePostings() {
		return titlePostings;
	}
	public void setTitlePostings(Map<String, List<Integer>> titlePostings) {
		this.titlePostings = titlePostings;
	}
	public Map<String, List<Integer>> getContentPostings() {
		return contentPostings;
	}
	public void setContentPostings(Map<String, List<Integer>> contentPostings) {
		this.contentPostings = contentPostings;
	}

}
