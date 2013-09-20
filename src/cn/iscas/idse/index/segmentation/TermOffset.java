package cn.iscas.idse.index.segmentation;

/**
 * this is unit structure for a (term, offset) binary tuple using to store the segmentation result.
 * @author Harry Huang
 *
 */
public class TermOffset {
	private String term = "";
	private int offset = 0;
	
	public TermOffset(String term, int offset){
		this.term = term;
		this.offset = offset;
	}
	
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
}
