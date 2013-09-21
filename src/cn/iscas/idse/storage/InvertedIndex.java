package cn.iscas.idse.storage;

import java.util.HashSet;
import java.util.Set;


import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import static com.sleepycat.persist.model.Relationship.*;
import static com.sleepycat.persist.model.DeleteAction.*;
/**
 * <p>
 * 	An Entity representing a inverted index for a specific unique term.
 * </p>
 * <p>structure:</p> 
 * <ol>
 * 	<li>target id (primary key, unique)</li>
 * 	<li>target path (e.g., c:/, /home)</li>
 * </ol>
 * @author Harry Huang
 *
 */
@Entity
public class InvertedIndex {
	@PrimaryKey
	private String term = null;
	
	/**
	 * posting list
	 */
	private Set<Posting> postings = new HashSet<Posting>();

	public InvertedIndex(){}
	
	public InvertedIndex(String term){
		this.term = term;
	}
	
	public InvertedIndex(String term, Set<Posting> postings){
		this.term = term;
		this.postings = postings;
	}
	
	/**
	 * get DF.
	 * @return
	 */
	public int getDocumentFrequence(){
		return this.postings.size();
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Set<Posting> getPostings() {
		return postings;
	}

	public void setPostings(Set<Posting> postings) {
		this.postings = postings;
	}
	
	
	
}
