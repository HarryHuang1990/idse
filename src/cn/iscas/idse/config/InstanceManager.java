package cn.iscas.idse.config;

import cn.iscas.idse.index.segmentation.StopWordFilter;
import cn.iscas.idse.index.segmentation.TermLemmatizer;
import cn.iscas.idse.index.segmentation.WordSegmentation;

/**
 * <p>
 * manage the Singleton Instances
 * </p>
 * <p>
 * 	usage:
 * </p>
 * <p>
 * 		<ol>
 * 			<li>invoke .getInstance(classNo) to get the instance of specific Class.</li>
 * 			<li>invoke .destroyInstance(classNo) to release the memory space occupied by the instance of specific Class.</li>
 * 		</ol>
 * </p>
 * @author Harry Huang
 *
 */
public class InstanceManager {
	
	/**
	 * No. of Class TermLemmatizer
	 */
	public static final short CLASS_TERMLEMMATIZER = 1;
	/**
	 * No. of Class StopWordFilter
	 */
	public static final short CLASS_STOPWORDFILTER = 2;
	/**
	 * No. of Class WordSegmentation
	 */
	public static final short CLASS_WORDSEGMENTATION = 3;
	
	/**
	 * get instance of the Singleton for the specific class.
	 * @param classNo
	 * @return
	 */
	public static Object getInstance(short classNo){
		Object instance = null;
		switch(classNo){
		case InstanceManager.CLASS_TERMLEMMATIZER:
			instance = SystemConfiguration.lemmatizer;
			break;
		case InstanceManager.CLASS_STOPWORDFILTER:
			instance = SystemConfiguration.stopWordFilter;
			break;
		case InstanceManager.CLASS_WORDSEGMENTATION:
			instance = SystemConfiguration.wordSegmentor;
			break;
		}
		return instance;
	}
	
	/**
	 * destroy the instance of specific class to release memory.
	 * @param classNo
	 */
//	public static void destroyInstance(short classNo){
//		switch(classNo){
//		case InstanceManager.CLASS_TERMLEMMATIZER:
//			TermLemmatizer.getInstance().destoryInstance();
//			break;
//		case InstanceManager.CLASS_STOPWORDFILTER:
//			StopWordFilter.getInstance().destoryInstance();
//			break;
//		case InstanceManager.CLASS_WORDSEGMENTATION:
//			WordSegmentation.getInstance().destoryInstance();
//			break;
//		}
//	}
}
