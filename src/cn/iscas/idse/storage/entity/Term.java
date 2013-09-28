package cn.iscas.idse.storage.entity;

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
 * 	<li>term</li>
 * 	<li>title posting list</li>
 * 	<li>content posting list</li>
 * </ol>
 * @author Harry Huang
 *
 */
@Entity
public class Term {
	@PrimaryKey
	private String term = null;
	
	@SecondaryKey(relate=ONE_TO_MANY, relatedEntity=PostingTitle.class, onRelatedEntityDelete=NULLIFY)
	private Set<Integer> postingTitle = new HashSet<Integer>();
	
	@SecondaryKey(relate=ONE_TO_MANY, relatedEntity=PostingContent.class, onRelatedEntityDelete=NULLIFY)
	private Set<Integer> postingContent = new HashSet<Integer>();

	public Term(){}
	
	public Term(String term){
		this.term = term;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Set<Integer> getPostingTitle() {
		return postingTitle;
	}

	public void setPostingTitle(Set<Integer> postingTitle) {
		this.postingTitle = postingTitle;
	}

	public Set<Integer> getPostingContent() {
		return postingContent;
	}

	public void setPostingConteng(Set<Integer> postingConteng) {
		this.postingContent = postingConteng;
	}
	/**
	 * get DF. for title
	 * @return
	 */
	public int getDocumentFrequenceForTitle() {
		return this.postingTitle.size();
	}
	/**
	 * get DF. for content
	 * @return
	 */
	public int getDocumentFrequenceForContent() {
		return this.postingContent.size();
	}
}
