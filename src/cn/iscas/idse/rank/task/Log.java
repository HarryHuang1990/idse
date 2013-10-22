package cn.iscas.idse.rank.task;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * represents the a log struct of user activity
 * @author dell
 *
 */
public class Log {
	/**
	 * file name include the path partly if it's a documents 
	 * if it is a webpage, the file name is the url of the target page.
	 */
	private String fileName;
	
	private Date start;
	private Date end;
	private int duration;
	private String domain;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public void intergateLog(Log log){
		this.setEnd(log.getEnd());
		this.setDuration(this.getDuration() + log.getDuration());
	}
	
	public String toString(){
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateformat.format(this.start) + "\t" + dateformat.format(this.end) + "\t" + this.duration + "\t" + this.fileName;
	}
	
}
