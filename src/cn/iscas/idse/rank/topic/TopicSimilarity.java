package cn.iscas.idse.rank.topic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.storage.entity.TopicRelation;

import jgibblda.LDA;

/**
 * implements the calculation of similarity between 
 * cluster and cluster , file and file.
 * @author Harry Huang
 *
 */
public class TopicSimilarity {

	/**
	 * the topic distribution ( or talent topic space vector )list
	 */
	private double[][] topicDistributions = null;
	/**
	 * topic relation graph. that is, this is the result of the topic mining.
	 */
	private Map<Integer, TopicRelation> topicRelationGraph = new HashMap<Integer, TopicRelation>();
	/**
	 * ordered id - documentID map.
	 * value : documentID is the signal for the document in the index store.
	 * key : ordered id is the index in the LDA data file.
	 */
	private Map<Integer, Integer>docIDOrderedMap = new HashMap<Integer, Integer>();
	/**
	 * KL value distribution
	 * key : value interval, the map is as fellow: 
	 * 			0 - [0,1)
	 * 			1 - [1,2)
	 * 			2 - [2,3)
	 * 			3 - [3,4)
	 * 			4 - [4,5)
	 * 			...
	 * value : frequency
	 */
	public Map<Integer, Integer> kldis = new HashMap<Integer, Integer>();
	private String docIDOrderedMapFile = "";
	private String ldaDataFile = "";
	
	/**
	 * 
	 * @param docIDIndexMapFile		this is only a filename rather than filepath
	 * @param ldaDataFile			this is only a filename rather than filepath
	 */
	public TopicSimilarity(String docIDIndexMapFile, String ldaDataFile){
		this.docIDOrderedMapFile = docIDIndexMapFile;
		this.ldaDataFile = ldaDataFile;
	}
	
	public Map<Integer, TopicRelation> getTopicRelationGraph() {
		return topicRelationGraph;
	}

	/**
	 * calculate the similarity of given two files according to the topic distribution THETA
	 * @param fileA
	 * @param fileB
	 */
	public void run(){
		this.loadDocIndexMap();
		// read the topic distribution
		this.getTopicDistribution();
//		this.getTopicDistribution("model-final.theta");
		// calculate the similarity of every two document topic and generate the topic relation graph
		this.generateTopicRelationGraph();
	}
	
	private void generateTopicRelationGraph(){
		for(int i=0; i<=10; i++)
			kldis.put(i, 0);
		for(int i=0; i<this.topicDistributions.length-1; i++)
			for(int j=i+1; j<this.topicDistributions.length; j++)
			{
				double js = this.getDistanceJS(this.topicDistributions[i], this.topicDistributions[j]);
				// build the relation between the two document if the js < kl_upbound
				if(js < SystemConfiguration.klUpbound){
					this.addTopicRelationGraph(this.docIDOrderedMap.get(i), this.docIDOrderedMap.get(j), js);
				}
//				System.out.println("(" + this.docIDOrderedMap.get(i) + ", " + this.docIDOrderedMap.get(j) + ")=" + js);
//				this.addKLDis(js);
			}
//		System.out.println(this.kldis.toString());
	}
	
	/**
	 * put the relation between docID1 and docID2 into the relation graph
	 * @param docID1
	 * @param docID2
	 * @param klDistance
	 */
	public void addTopicRelationGraph(int docID1, int docID2, double klDistance){
		if(this.topicRelationGraph.containsKey(docID1)){
			this.topicRelationGraph.get(docID1).putNewRelation(docID2, klDistance);
		}
		else{
			TopicRelation topicRelation = new TopicRelation(docID1);
			topicRelation.putNewRelation(docID2, klDistance);
			this.topicRelationGraph.put(docID1, topicRelation);
		}
		
		if(this.topicRelationGraph.containsKey(docID2)){
			this.topicRelationGraph.get(docID2).putNewRelation(docID1, klDistance);
		}
		else{
			TopicRelation topicRelation = new TopicRelation(docID2);
			topicRelation.putNewRelation(docID1, klDistance);
			this.topicRelationGraph.put(docID2, topicRelation);
		}
	}
	
	/**
	 * KL value distribution operation.
	 * @param value
	 */
	public void addKLDis(double value){
		int key = (int)(value * 10);
		kldis.put(key, kldis.get(key) + 1);
	}
	/**
	 * calculate the Jensen-Shannon distance between two given distribution
	 * @param x
	 * @param y
	 * @return
	 */
	public double getDistanceJS(double[]x, double[]y){
		double[]m = this.getM(x, y);
		double js = (this.getDistanceKL(x, m) + this.getDistanceKL(y, m)) / 2;
		return js;
	}
	/**
	 * calculate the Kullback¨CLeibler divergence £¨KL£©between two given distribution
	 * @param x
	 * @param y
	 * @return
	 */
	private double getDistanceKL(double[]x, double[]y){
		double kl = 0;
		for(int i=0; i<x.length; i++)
			kl += x[i] * Math.log(x[i] / y[i]);
		return kl;
	}
	/**
	 * calculate the M vector, which is the average vector of X, Y 
	 * @param x
	 * @param y
	 * @return
	 */
	private double[] getM(double[] x, double[] y){
		double[] m = new double[x.length];
		for(int i=0; i<x.length; i++)
			m[i] = (x[i] + y[i]) / 2;
		return m;
	} 
	/**
	 * load the index-docID map file.
	 * @param docIDIndexMapFile
	 */
	private void loadDocIndexMap(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(SystemConfiguration.LDAPath, this.docIDOrderedMapFile)));
			String line = "";
			int lineNo = 0;
			while((line = reader.readLine()) != null){
				this.docIDOrderedMap.put(lineNo, Integer.parseInt(line));
				lineNo++;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * get the topic distribution directly from the LDA model
	 * @param LdaDataFile use the file name rather than file path.
	 */
	private void getTopicDistribution(){
		LDA lda = new LDA();
		this.topicDistributions = lda.getTopicDistribuiton(this.ldaDataFile);
	}
	
	/**
	 * get the topic distribution from a file
	 */
	private void getTopicDistribution(String topicDistributionFile){
		try{
			this.topicDistributions = new double[this.docIDOrderedMap.size()][];
			BufferedReader reader = new BufferedReader(new FileReader(new File(SystemConfiguration.LDAPath, topicDistributionFile)));
			String line = "";
			int lineNo = 0;
			while((line = reader.readLine()) != null){
				String[] splits = line.split(" ");
				this.topicDistributions[lineNo] = new double[splits.length];
				for(int i=0; i<splits.length; i++)
					this.topicDistributions[lineNo][i] = Double.parseDouble(splits[i]);
				lineNo++;
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public double[] getVector(String line){
		double[]vector;
		String[]splits = line.split(" ");
		vector = new double[splits.length];
		for(int i=0; i<splits.length; i++){
			vector[i] = Double.parseDouble(splits[i]);
		}
		return vector;
	}
	
	public double getScore(double js){
		return 1 / (Math.pow(Math.E, js*3));
	}
	
	public static void main(String args[]){
//		LDA lda = new LDA();
//		
//		Map<String, Integer> map = new HashMap<String, Integer>();
//		TopicCluster cluster = new TopicCluster();
//		for(int i=0; i<=7; i++){
//			cluster.addTopicFile(lda.getTopicFile("0000000" + i));
//		}
//		System.out.println(cluster.toString());

		
		TopicSimilarity ts = new TopicSimilarity("00000000.map", "00000000");
		
//		String str1 = "6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 0.01602803738317757 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 0.03071428571428571 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 0.10681575433911882 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 0.010687583444592792 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 0.8317823765020027 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 0.003344459279038718 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6 6.675567423230975E-6"; 
//		String str2 = "6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 0.013365139949109416 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 0.03181297709923664 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 0.0935178117048346 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 0.02736005089058524 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 0.8333396946564885 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6 6.3613231552162854E-6"; 
//		String str3 = "1.2578616352201258E-6 3.786163522012578E-4 1.2578616352201258E-6 1.2578616352201258E-6 0.0026427672955974843 1.2578616352201258E-6 1.2578616352201258E-6 0.0036490566037735853 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 5.044025157232704E-4 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 0.013460377358490566 1.2578616352201258E-6 1.2578616352201258E-6 6.30188679245283E-4 1.2578616352201258E-6 0.0738377358490566 1.2578616352201258E-6 0.0011333333333333334 1.2578616352201258E-6 1.2578616352201258E-6 0.008177358490566038 0.007925786163522013 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 0.016227672955974843 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 0.021384905660377356 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 0.7120767295597484 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 1.2578616352201258E-6 0.014969811320754717 8.817610062893082E-4 1.2578616352201258E-6 1.270440251572327E-4 1.2578616352201258E-6 0.09723396226415094 0.024655345911949685 1.2578616352201258E-6 ";
//		
//		double[]v1 = ts.getVector(str1);
//		double[]v2 = ts.getVector(str2);
//		double[]v3 = ts.getVector(str3);
////		System.out.println(ts.getDistanceJS(v1, v2));
////		System.out.println(ts.getDistanceJS(v1, v3));
		System.out.println(ts.getScore(0.0000112));
		System.out.println(ts.getScore(0.1000112));
		System.out.println(ts.getScore(0.2000112));
		System.out.println(ts.getScore(0.3000112));
		System.out.println(ts.getScore(0.4000112));
		System.out.println(ts.getScore(0.5000112));
		System.out.println(ts.getScore(0.6000112));
		System.out.println(ts.getScore(0.7000112));
		
		ts.run();
		
		
	}
	
}
