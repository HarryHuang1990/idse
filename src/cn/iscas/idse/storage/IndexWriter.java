package cn.iscas.idse.storage;

import cn.iscas.idse.index.segmentation.TermOffset;

/**
 * write the index into the Berkeley DB
 * @author Harry Huang
 *
 */
public class IndexWriter {
	
	/**
	 * put a new <term, offset> tuple into the index. 
	 * Once size of the index in memory is larger than a given threshold, 
	 * the index is written into the Berkeley DB. 
	 * @param termOffset
	 * @return
	 */
	public void writeTermOffSetIntoIndex(int docID, String term, int offset){
		//TODO mamager the index in memory
	}
	
}
