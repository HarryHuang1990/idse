package cn.iscas.idse.storage.entity;

import static com.sleepycat.persist.model.DeleteAction.CASCADE;
import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * Posting structure for content index, which is formulated as (docID, term_frequency, [offset1, offset2, offset3, ...]) for each term X in content
 * in the specific document named as DocID.
 * @author Harry Huang
 *
 */
@Entity
public class PostingContent {
	
	/**
	 * postingID auto-increase.
	 */
	@PrimaryKey(sequence="PostingContentID")
	private int postingID;
	
	@SecondaryKey(relate=MANY_TO_ONE, relatedEntity=Document.class, onRelatedEntityDelete=CASCADE)
	private int docID;
	
	/**
	 * offsets of the term in the document docID.
	 */
	private Set<Integer> offsets = new HashSet<Integer>();
	
	/**
	 * for binding
	 */
	private PostingContent(){}
	
	public PostingContent(int docID){
		this.docID = docID;
	}
	
	public PostingContent(int docID, Set<Integer> offsets){
		this.docID = docID;
		this.offsets = offsets;
	}
	
	public PostingContent(int postingID, int docID, Set<Integer> offsets){
		this.postingID = postingID;
		this.docID = docID;
		this.offsets = offsets;
	}
	
	public PostingContent(int postingID, int docID){
		this.postingID = postingID;
		this.docID = docID;
	}
	
	/**
	 * get TF.
	 * @return
	 */
	public int getTermFrequency(){
		return this.offsets.size();
	}

	public int getPostingID() {
		return postingID;
	}

	public void setPostingID(int postingID) {
		this.postingID = postingID;
	}

	public int getDocID() {
		return docID;
	}

	public void setDocID(int docID) {
		this.docID = docID;
	}

	public Set<Integer> getOffsets() {
		return offsets;
	}

	public void setOffsets(Set<Integer> offsets) {
		this.offsets = offsets;
	}
	
	@Override
	public String toString(){
		return "{" + postingID + ", " + docID + "," + offsets.toString() + "}";
	}
}
