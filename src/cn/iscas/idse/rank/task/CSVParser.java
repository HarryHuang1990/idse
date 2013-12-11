package cn.iscas.idse.rank.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.iscas.idse.config.SystemConfiguration;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * parser the csv format get the necessary data from the data set.
 * 
 * @author Harry Huang
 *
 */
public class CSVParser {
	
	private Set<List<Integer>> taskGroups = new HashSet<List<Integer>>();
	private List<List<Log>> tasks = new ArrayList<List<Log>>();
	private List<Log>logs = new ArrayList<Log>();
	private Log lastLog = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 * execute the csv parser on a given file.
	 * @param csvFile
	 */
	public void execute(String csvFile, String outputFile){
		this.execute(csvFile);
		this.outputTasks(outputFile + "_merge");
		
	}
	
	public List<List<Log>> execute(String csvFile){
		try {
			this.lastLog = null;
			File file = new File(csvFile);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			int lineNo = 0;
			System.out.println("clustering...");
			while((line = reader.readLine()) != null){
				try {
					lineNo++;
					if(lineNo == 1)continue;
					String[]splits = this.getAttributesValues(line);
					Log log = new Log();
					log.setFileName(splits[0]);
					log.setStart(this.dateFormat.parse(splits[1].replaceAll("-", "/")));
					log.setEnd(this.dateFormat.parse(splits[2].replaceAll("-", "/")));
					log.setDuration(this.getDuration(splits[3]));
					log.setDomain(splits[4]);
					
					if(this.logs.size() == 0)
						this.logs.add(log);
					else if(this.logs.get(this.logs.size() - 1).getFileName().equals(log.getFileName())){
						this.logs.get(this.logs.size() - 1).intergateLog(log);
					}
					else
						this.logs.add(log);
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			reader.close();
			this.filterLog();
			this.intergateTheSimilarityTask();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.tasks;
	}
	
	/**
	 * cluster the log record and get the task.
	 * step1 : split by VALID_VIEW_PERIOD (default 10s)
	 */
	private void filterLog(){
		System.out.println("filtering...");
		List<Log>taskList = new ArrayList<Log>();
		for(Log log : this.logs){
			if(log.getDuration() >= SystemConfiguration.validViewPeriod && !log.getFileName().startsWith("http")){
				taskList.add(log);
			}
			else{
				if(taskList.size() != 0){
					this.addSplitsAndAddRawTask(taskList);
					taskList = new ArrayList<Log>();
				}
			}
		}
		if(taskList.size() != 0){
			this.addSplitsAndAddRawTask(taskList);
			taskList = new ArrayList<Log>();
		}
	}
	
	/**
	 * cluster the log record and get the task.
	 * step2 : split by INTERVAL_TASK_PERIOD (default 60s)
	 */
	private void addSplitsAndAddRawTask(List<Log> rawTask){
		List<Log>task = new ArrayList<Log>();
		for(int i=0; i < rawTask.size()-1; i++){
			task.add(rawTask.get(i));
			if(this.getTimeInterval(rawTask.get(i), rawTask.get(i+1)) >= SystemConfiguration.intervalTaskPeriod){
				if(task.size() != 0){
					this.tasks.add(task);
					task = new ArrayList<Log>();
				}
			}
		}
		task.add(rawTask.get(rawTask.size()-1));
		this.tasks.add(task);
	}
	
	/**
	 * calculate the interval between the two adjacent record. 
	 * @param formerLog
	 * @param laterLog
	 * @return
	 */
	private int getTimeInterval(Log formerLog, Log laterLog){
		int interval = (int)((laterLog.getStart().getTime() - formerLog.getEnd().getTime()) / 1000);
		return interval;
	}
	
	/**
	 * intergrate the similar task 
	 */
	private void intergateTheSimilarityTask(){
		if(this.tasks.size() > 1){
			List<Log> last = this.tasks.get(0);
			List<List<Log>> newTasks = new ArrayList<List<Log>>();
			for(int i=1; i<this.tasks.size(); i++){
				if(this.taskSimilarity(last, this.tasks.get(i), last.size()<this.tasks.get(i).size()) 
						> SystemConfiguration.taskSimilarityThreshold){
					// merge
					last.addAll(this.tasks.get(i));
				}
				else{
					// only put the task whose size is larger than 1.
					if(last.size() > 1)
						newTasks.add(last);
					last = this.tasks.get(i);
				}
			}
			if(last.size() > 1)
				newTasks.add(last);
			this.tasks = newTasks;
		}
	}
	
	/**
	 * <p>
	 * calculates the similarity between the given two tasks
	 * </p>
	 * <p>
	 * 	SimilarityMeasure = number of common files in the two tasks / Total number of files in the smaller tasks.
	 * </p>
	 * 
	 * @param task1
	 * @param task2
	 * @param if the task1 is the smaller task
	 * @return the similarity between the given two tasks.
	 */
	private float taskSimilarity(List<Log> task1, List<Log> task2, boolean formerIsSmaller){
		int smallerSize = formerIsSmaller ? task1.size() : task2.size();
		int common = 0;
		List<Log> larger = task1, smaller = task2;
		if(formerIsSmaller){
			larger = task2;
			smaller = task1;
		}
		
		for(Log log : smaller){
			for(Log largerLog : larger){
				if(log.getFileName().equals(largerLog.getFileName())){
					common++;
				}
			}
		}
		
		return 1.0f * common / smallerSize;
	}

	
	public static String[] getAttributesValues(String logLine){
		String[]splits = logLine.split("\",\"");
		for(int i=0; i<splits.length; i++){
			if(splits[i].startsWith("\"")){
				splits[i] = splits[i].substring(1);
			}
			if(splits[i].endsWith("\"")){
				splits[i] = splits[i].substring(0, splits[i].length() - 1);
			}
			System.out.println(splits[i]);
		}
		
		return splits;
	}
	
	/**
	 * convert the duration string to the seconds
	 * "0:01:01" ->  61s
	 * @param duration
	 * @return
	 */
	public static int getDuration(String duration){
		int sec = 0;
		String[]splits = duration.split(":");
		sec = 3600 * Integer.parseInt(splits[0]) + 60 * Integer.parseInt(splits[1]) + Integer.parseInt(splits[2]);
		return sec;
	}
	
	
	/**
	 * output the tasks onto disk
	 * @param outputFile
	 */
	private void outputTasks(String outputFile){
		System.out.println("outputing...");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			
			for(List<Log> task : this.tasks){
				for(Log log : task)
					writer.write(log.toString() + "\n");
				writer.write("\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		CSVParser parser = new CSVParser();
//		parser.execute("F:\\user_activity_log\\ManicTimeData_doc.csv", "F:\\user_activity_log\\tasks_v2");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			System.out.println(dateFormat.parse("2012-9-2 02:03:03".replaceAll("-", "/")).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
