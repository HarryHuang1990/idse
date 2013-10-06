package cn.iscas.idse.search.entity;
/**
 * score unit for each document, which consists of 
 * document id and score value under the given query.
 * @author Administrator
 *
 */
public class Score{

	private int docID;
	private float score = 0f;
	
	public Score(int docID, float score){
		this.docID = docID;
		this.score = score;
	}

	public int getDocID() {
		return docID;
	}

	public void setDocID(int docID) {
		this.docID = docID;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
}
