package cn.iscas.idse.rank.topic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.rank.MatrixWriter;

/**
 * Statistics of distribution of topics 
 * @author Harry Huang
 *
 */
public class TopicDistribution {

	/**
	 * topic-doc map. the index of list represents the topic id.
	 * the value for each entity in the list is the document id list.
	 */
	private Map<Integer, List<Integer>> topicDocMap = new HashMap<Integer, List<Integer>>(); 
	/**
	 * ordered id - documentID map.
	 * documentID is the signal for the document in the index store.
	 * ordered id is the index in the LDA data file.
	 */
	private Map<Integer, Integer>idIndexMap = new HashMap<Integer, Integer>();
	
	public TopicDistribution(String docIDIndexMapFile){
		//load the index-id map file.
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(docIDIndexMapFile)));
			String line = "";
			int lineNo = 0;
			while((line = reader.readLine()) != null){
				lineNo++;
				this.idIndexMap.put(lineNo, Integer.parseInt(line));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * statistics of doc-topic distribution file. 
	 * map the document to the topic having the highest probability.
	 * @param docTopicDistrFile
	 * @return topicID-docIDs map
	 */
	public Map<Integer, List<Integer>> execute(String docTopicDistrFile){
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(docTopicDistrFile)));
			String line = "";
			int lineNO = 0;
			while((line = reader.readLine()) != null){
				lineNO++;
				this.addToDistribution(this.getTopicIndex(line.split(" ")), this.getDocumentID(lineNO));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return this.topicDocMap;
	}
	
	/**
	 * return the documentID of the position index representing.
	 * @param posIndex the index of documentID in LDA data file.
	 * @return
	 */
	private int getDocumentID(int posIndex){
		return this.idIndexMap.get(posIndex);
	}
	
	/**
	 * return the index of highest probability
	 * @return
	 */
	private int getTopicIndex(String probabilities[]){
		int topicIndex = 0;
		double max = 0.0;
		for(int i=1; i<=probabilities.length; i++){
			double prob = Double.parseDouble(probabilities[i-1]);
			if(prob > max){
				max = prob;
				topicIndex = i;
			}
		}
		return topicIndex;
	}
	
	/**
	 * add the document ID into the specific
	 * @param docID
	 */
	private void addToDistribution(int topicIndex, int docID){
		if(topicDocMap.containsKey(topicIndex)){
			this.topicDocMap.get(topicIndex).add(docID);
		}
		else{
			List<Integer>docList = new ArrayList<Integer>();
			docList.add(docID);
			this.topicDocMap.put(topicIndex, docList);
		}
	}
	
	/**
	 * output the document topic distribution into the given file.
	 * @param outputFile
	 */
	public void outputDistribution(String outputFile){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			IndexReader indexReader = new IndexReader();
			for(int i=1; i <= this.topicDocMap.size(); i++){
				writer.write("topic " + (i-1) + "\n");
				System.out.println("topic " + (i-1) + "\n");
				List<Integer> docIDs = this.topicDocMap.get(i);
				if(docIDs != null)
					for(int docID : docIDs){
						writer.write(indexReader.getAbsolutePathOfDocument(docID) + "\n");
					}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		MatrixWriter writer = new MatrixWriter();
		TopicDistribution td = new TopicDistribution("F:/JGibbLDA/models/casestudy/00000000.txt");
//		writer.writeTopicRelationMatrix(td.execute("F:/JGibbLDA/models/casestudy/model-final.theta"));
//		td.outputDistribution("F:/JGibbLDA/models/casestudy/00000000_topic_distribution");
	}
}
