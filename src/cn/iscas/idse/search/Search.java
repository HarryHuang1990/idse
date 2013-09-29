package cn.iscas.idse.search;

/**
 * this is a main API for query.
 * receive the query information and execute searching.
 * @author Harry Huang
 *
 */
public class Search {
	
	
	private QueryParser queryParser;
	
	public Search(){
		this.queryParser = new QueryParser();
	}

	public void executeSearch(String query){
		
		/*
		 * parse the query
		 */
		this.queryParser.setQuery(query);
		this.queryParser.parse();
		
//		QueryResult queryResult
		
	}
	
	
}
