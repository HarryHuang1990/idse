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
 * 	<li>posting list</li>
 * </ol>
 * @author Harry Huang
 *
 */
@Entity
public class Term {
	@PrimaryKey
	private String term = null;
	
	private int documentFrequence = 0;

	public Term(){}
	
	public Term(String term){
		this.term = term;
	}
	
	/**
	 * get DF.
	 * @return
	 */
	public int getDocumentFrequence(){
		return documentFrequence;
	}
	
	public void setDocumentFrequence(int documentFrequence){
		this.documentFrequence = documentFrequence;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
