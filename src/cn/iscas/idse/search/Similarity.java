package cn.iscas.idse.search;

/**
 * <p>
 * This is just abstract API for query, we should extends this class to 
 * implement a specific retrieval model, just like 
 * <a href="http://en.wikipedia.org/wiki/Vector_Space_Model">Vector Space Model</a>, 
 * <a href="http://en.wikipedia.org/wiki/Okapi_BM25">Okapi BM25 model</a>, etc.
 * </p> 
 * <p>
 * In this project, both models mentioned above are optimized and implemented.
 * </p>
 * @author Harry Huang
 *
 */
public abstract class Similarity {
	
	/**
	 * Constructor
	 */
	public Similarity(){}
	
	public void coord(){}
	
	
}
