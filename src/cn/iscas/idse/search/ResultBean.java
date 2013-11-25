package cn.iscas.idse.search;

import java.util.ArrayList;
import java.util.List;

/**
 * the result bean returned to the UI
 * @author Harry Huang
 *
 */
public class ResultBean {
	private int docID;
	private String file;
	private String directory;
	private int recommendSize;
	private List<ResultBean> recommends = new ArrayList<ResultBean>();
	public int getDocID() {
		return docID;
	}
	public void setDocID(int docID) {
		this.docID = docID;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public int getRecommendSize() {
		return recommendSize;
	}
	public void setRecommendSize(int recommendSize) {
		this.recommendSize = recommendSize;
	}
	public List<ResultBean> getRecommends() {
		return recommends;
	}
	public void setRecommends(List<ResultBean> recommends) {
		this.recommends = recommends;
	}
	
	
}
