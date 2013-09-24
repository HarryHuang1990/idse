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
	
	@SecondaryKey(relate=MANY_TO_ONE, relatedEntity=Term.class, onRelatedEntityDelete=CASCADE)
	private String term;
	
	/**
	 * offsets of the term in the document docID.
	 */
	private Set<Integer> offsets = new HashSet<Integer>();
	
	/**
	 * for binding
	 */
	private PostingContent(){}
	
	public PostingContent(int docID, String term){
		this.docID = docID;
		this.term = term;
	}
	
	public PostingContent(int docID, String term, Set<Integer> offsets){
		this.docID = docID;
		this.term = term;
		this.offsets = offsets;
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

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Set<Integer> getOffsets() {
		return offsets;
	}

	public void setOffsets(Set<Integer> offsets) {
		this.offsets = offsets;
	}
}
