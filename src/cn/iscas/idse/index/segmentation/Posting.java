package cn.iscas.idse.index.segmentation;

import java.util.HashSet;
import java.util.Set;

/**
 * Posting structure for index, which is formulated as (docID, term_frequency, [offset1, offset2, offset3, ...]) for each term X
 * in the specific document named as DocID.
 * @author Administrator
 *
 */
public class Posting {
	private int docID;
	private int termFrequency=0;
	/**
	 * offsets of the term in the document docID.
	 */
	private Set<Integer> offsets = new HashSet<Integer>();
	public int getDocID() {
		return docID;
	}
	
	public void setDocID(int docID) {
		this.docID = docID;
	}
	public int getTermFrequency() {
		return termFrequency;
	}
	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}
	public Set<Integer> getOffsets() {
		return offsets;
	}
	/**
	 * add a new offset of the term to the offset Set, then term frequency increases by 1 
	 * @param newOffset
	 */
	public void addOffsets(int newOffset) {
		this.offsets.add(newOffset);
		this.termFrequency++;
	}
}
