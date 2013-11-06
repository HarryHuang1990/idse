package cn.iscas.idse.search.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements a Query entity which contains all information 
 * about the query
 * @author Harry Huang
 *
 */
public class Query {
	
	/**
	 * query string for searching
	 */
	private String query = "";
	/**
	 * key words distribution in query string.
	 */
	private Map<String, List<Integer>> queryPosting = new HashMap<String, List<Integer>>();
	
	public Query(String query){
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Map<String, List<Integer>> getQueryPosting() {
		return queryPosting;
	}

	public void setQueryPosting(Map<String, List<Integer>> queryPosting) {
		this.queryPosting = queryPosting;
	}
	
	public String toString(){
		return this.queryPosting.keySet().toString();
	}
	
}
