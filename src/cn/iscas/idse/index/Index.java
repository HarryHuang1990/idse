package cn.iscas.idse.index;

import org.apache.log4j.Logger;

import cn.iscas.idse.config.InstanceManager;
import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.index.segmentation.WordSegmentation;
import cn.iscas.idse.rank.topic.InputDataForLDA;

/**
 * the main interface of indexing
 * @author Harry Huang
 *
 */
public class Index {
	
	private static final Logger log = Logger.getLogger(Index.class);
	
	/**
	 * execute index generation from here
	 */
	public void createIndex(){
		WordSegmentation wordSegmentor = (WordSegmentation)InstanceManager.getInstance(InstanceManager.CLASS_WORDSEGMENTATION);
		wordSegmentor.initialize();
		for(int i=0; i<SystemConfiguration.targetDirectories.size(); i++){
			String targetPath = SystemConfiguration.targetDirectories.get(i);
			log.info("正在创建索引" + targetPath);
			if(i==0){
				IndexWriter indexWriter = new IndexWriter(wordSegmentor);
				indexWriter.executeIndexing(targetPath);
			}
			else{
				IndexUpdater indexUpdater = new IndexUpdater(wordSegmentor);
				indexUpdater.executeIndexUpdate(targetPath);
			}
		}
		wordSegmentor.exitICTCLAS();
//		wordSegmentor.destoryInstance();
		
		//execute the LDA input data.
		InputDataForLDA LDADataBuilder = new InputDataForLDA(100000);
		LDADataBuilder.executeFormat();
		LDADataBuilder.saveWordListBuffer();
	}
	/**
	 * execute index updating from here
	 */
	public void updateIndex(){
		log.info("开始更新索引...");
		WordSegmentation wordSegmentor = (WordSegmentation)InstanceManager.getInstance(InstanceManager.CLASS_WORDSEGMENTATION);
		wordSegmentor.initialize();
		IndexUpdater indexUpdater = new IndexUpdater(wordSegmentor);
		indexUpdater.execute();
		wordSegmentor.exitICTCLAS();
//		wordSegmentor.destoryInstance();
		
		//execute the LDA input data.
		InputDataForLDA LDADataBuilder = new InputDataForLDA(100000);
		LDADataBuilder.executeFormat();
		LDADataBuilder.saveWordListBuffer();
	}
	
	public static void main(String args[]){
		Index index = new Index();
		index.createIndex();
	}
	
}
