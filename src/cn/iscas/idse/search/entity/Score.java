package cn.iscas.idse.search.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * score unit for each document, which consists of 
 * document id and score value under the given query.
 * @author Administrator
 *
 */
public class Score{

	private int docID;
	private double score = 0;
	private List<Integer> mostRelatedDocs = new ArrayList<Integer>();
	
	public Score(int docID, double score){
		this.docID = docID;
		this.score = score;
	}

	public int getDocID() {
		return docID;
	}

	public void setDocID(int docID) {
		this.docID = docID;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public List<Integer> getMostRelatedDocs() {
		return mostRelatedDocs;
	}

	public void setMostRelatedDocs(List<Integer> mostRelatedDocs) {
		this.mostRelatedDocs = mostRelatedDocs;
	}
}
