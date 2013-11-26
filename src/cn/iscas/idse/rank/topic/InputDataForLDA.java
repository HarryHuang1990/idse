package cn.iscas.idse.rank.topic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import cn.iscas.idse.config.InstanceManager;
import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.format.FileExtractor;
import cn.iscas.idse.format.FileExtractorFactory;
import cn.iscas.idse.index.IndexReader;
import cn.iscas.idse.index.segmentation.CamelCase;
import cn.iscas.idse.index.segmentation.StopWordFilter;
import cn.iscas.idse.index.segmentation.TermLemmatizer;
import cn.iscas.idse.index.segmentation.WordSegmentation;
import cn.iscas.idse.storage.entity.Document;

import com.sleepycat.persist.EntityCursor;

/**
 * Implements the input data generation of LDA.
 * Both data for training/estimating the model and new data (i.e., previously unseen data) have the same format as follows:
 * 
 * [M]
 * [document1]
 * [document2]
 * ...
 * [documentM]
 *
 * in which the first line is the total number for documents [M]. Each line after that is one document. [documenti] is the ith document of the dataset that consists of a list of Ni words/terms.
 *
 *
 * [documenti] = [wordi1] [wordi2] ... [wordiNi]
 *
 * in which all [wordij] (i=1..M, j=1..Ni) are text strings and they are separated by the blank character.
 * 
 * @author Harry Huang
 *
 */
public class InputDataForLDA {
	
	private static final Logger log = Logger.getLogger(InputDataForLDA.class);
	
	/**
	 * directory to save data 
	 */
	private String dir = SystemConfiguration.LDAPath;
	/**
	 * name of data file
	 */
	private static String dataFileNoStr = "0000000.txt";
	/**
	 * number of data file
	 */
	public static int dataFileNo = 0;
	/**
	 * wordLists to save in the file of No. {@link dataFileNo} 
	 */
	private List<String> wordListBuffer = new ArrayList<String>();
	/**
	 * ordered id - documentID map.
	 * documentID is the signal for the document in the index store.
	 * ordered id is the index in the list.
	 */
	private List<Integer> idIndexMap = new ArrayList<Integer>(); 
	/**
	 * the threshold of wordListBuffer. the max count of wordList in a single data file.  
	 * a wordList is corresponding to a document.
	 */
	private int bufferUpBound = 10;
	
	private WordSegmentation wordSegmentor = null;
	
	public InputDataForLDA(){
		this.wordSegmentor = (WordSegmentation) InstanceManager.getInstance(InstanceManager.CLASS_WORDSEGMENTATION);
		this.wordSegmentor.initialize();
	}
	
	public InputDataForLDA(int bufferUpBound){
		this();
		this.bufferUpBound = bufferUpBound;
	}
	
	/**
	 * put a new wordList into the wordListBuffer. 
	 * write the wordListBuffer into a new data file name of {@link dataFileNo} 
	 * if the size of wordListBuffer is larger than given threshold.  
	 * @param wordList
	 */
	private void saveData(String wordList, int documentID){
		if("".equals(wordList.trim())) return;
		if(this.wordListBuffer.size() < bufferUpBound){
			this.wordListBuffer.add(wordList);
			this.idIndexMap.add(documentID);
		}
		else{
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(this.getFilename())));
				System.out.println("buffer list size = " + this.wordListBuffer.size());
				writer.write(this.wordListBuffer.size() + "\n");
				for(String list : this.wordListBuffer)
					writer.write(list + "\n");
				writer.flush();
				writer.close();
				this.wordListBuffer.clear();
				this.wordListBuffer.add(wordList);

				// writer <LDA data index, document ID> map on the disk
				writer = new BufferedWriter(new FileWriter(new File(this.getFilename() + ".map")));
				System.out.println("<index, docID> index map size = " + this.idIndexMap.size());
				for(int docID : this.idIndexMap)
					writer.write(docID + "\n");
				writer.flush();
				writer.close();
				this.idIndexMap.clear();
				this.idIndexMap.add(documentID);
				
				// data file number increased by 1
				InputDataForLDA.dataFileNo ++ ;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * write the wordLists into a new data file name of {@link dataFileNo} 
	 */
	public void saveWordListBuffer(){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(this.getFilename())));
			System.out.println("buffer list size = " + this.wordListBuffer.size());
			writer.write(this.wordListBuffer.size() + "\n");
			for(String list : this.wordListBuffer)
				writer.write(list + "\n");
			writer.flush();
			writer.close();
			this.wordListBuffer.clear();
			
			// writer <LDA data index, document ID> map on the disk
			writer = new BufferedWriter(new FileWriter(new File(this.getFilename() + ".map")));
			System.out.println("<index, docID> index map size = " + this.idIndexMap.size());
			for(int docID : this.idIndexMap)
				writer.write(docID + "\n");
			writer.flush();
			writer.close();
			this.idIndexMap.clear();
			
			// data file number increased by 1
			InputDataForLDA.dataFileNo ++ ;
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			this.wordSegmentor.exitICTCLAS();
//			this.wordSegmentor.destoryInstance();
		}
	}
	
	/**
	 * <p>
	 * get the file name according to the file no.
	 * </p>
	 * <p>
	 * file name = "0" x N + file no.
	 * </p>
	 * <p>
	 * the file name consists of 8 bits, e.g., 00000001, 00000002
	 * </p>
	 * @return
	 */
	private String getFilename(){
		String filename = "" + InputDataForLDA.dataFileNo;
		int lengthOfName = filename.length();
		for(int i=0; i < (8 - lengthOfName); i++)
			filename = "0" + filename;
		return dir + filename;
	}
	
	/**
	 * transfer the content of document to LDA input data format
	 * @param file
	 */
	public boolean generatorInputData(File file, int documentID){
		// get file suffix
		int suffixIndex = file.getName().lastIndexOf(".");
		String suffix = "";
		if(suffixIndex != -1)
			suffix = file.getName().substring(suffixIndex, file.getName().length()).toLowerCase();
		else
			suffix = ".";
		// get the content extractor.
		FileExtractor extractor = FileExtractorFactory.getInstance().getFileExtractor(suffix);
		System.err.println(file.getAbsolutePath());
		if(extractor != null){
			System.err.println("parsable!");
			extractor.setFilePath(file.getAbsolutePath());
			String content = extractor.getContent();
			String segmentResult = this.wordSegmentor.segmentString(content);
			if(segmentResult != null)
				this.saveData(this.transferToWordList(segmentResult), documentID);
			return true;
		}
		return false;
	}
	
	/**
	 * transfer the segmentation result of given document 
	 * to the specific word list.
	 * @param segmentResult
	 * @return
	 */
	private String transferToWordList(String segmentResult){
		List<String> wordList = new ArrayList<String>();
		String currentTerm = "";
		StringTokenizer tokenizer = new StringTokenizer(segmentResult);
		while(tokenizer.hasMoreTokens()){
			currentTerm = tokenizer.nextToken().trim();
			//handle Camel Case style
			String[] words = CamelCase.splitCamelCase(currentTerm);
			if(words != null){
				for(String word : words){
					//lowercase the word
					word = word.toLowerCase();
					// handle Lemmatize
					word = ((TermLemmatizer)InstanceManager.getInstance(InstanceManager.CLASS_TERMLEMMATIZER)).adornText(word);
					if(word != null){
						// handle stop word
						if(!((StopWordFilter)InstanceManager.getInstance(InstanceManager.CLASS_STOPWORDFILTER)).isStopWord(word)){
							wordList.add(word);
						}
					}
				}
			}
		}
		
		String wordListStr = wordList.toString();
		wordListStr = wordListStr.substring(1, wordListStr.length() - 1);	//remove '[' and ']'
		wordListStr = wordListStr.replaceAll(",", "");
		return wordListStr;
	}
	
	/**
	 * integrate the LDA format of all document file into a input file.
	 */
	public void executeFormat(){
		IndexReader indexReader = new IndexReader();
		
		// initialize the segmentor
		this.wordSegmentor.initialize();
		// get target directories
		System.out.println("formating...");
		long start = System.currentTimeMillis();
		
		EntityCursor<Document> cursor = indexReader.getDocumentsCursor();
		int total_size = indexReader.getNumberDocuments();
		int point = 0;
		for(Document doc : cursor){
			point ++;
			this.generatorInputData(new File(indexReader.getAbsolutePathOfDocument(doc.getDocID())), doc.getDocID());
			System.out.println("=======���ȣ�" + point + " / " + total_size + " ========");
		}
		cursor.close();
		
		long end = System.currentTimeMillis();
		System.out.println("formating done. time = " + ((end - start) * 1.0 / 1000 / 60) + " min");
		// destroy the segmentor
		this.wordSegmentor.exitICTCLAS();
//		this.wordSegmentor.destoryInstance();
	}

	public static void main(String args[]){
		InputDataForLDA d = new InputDataForLDA(100000);
		String a[] = new String[]{
//				"D:\\My DBank\\����\\��Ϣ����\\2006-SIGIR- LDA-based document models for ad-hoc retrieval.pdf",
//				"D:\\My DBank\\����\\������\\2012-MSR-Inferring Semantically Related Words from Software Context.pdf",
//				"D:\\My DBank\\����\\���ν���-���ݵ�.pdf",
//				"D:\\My DBank\\���岿\\�ܱ�\\�ʸ���-201309020908�ܱ�.docx",
//				"D:\\My DBank\\�������-�ι�.ppt",
//				"D:\\My DBank\\������ʵ����\\ʵ���������ļ�\\����3-����֪ʶ��Ȩ��֤��.doc"
				"D:\\My DBank\\������ʵ����\\����\\�ı�����\\��ƽ�����ݷ���\\A probabilistic interpretation of SVMs with an application to unbalanced classification.PDF",
				"D:\\My DBank\\������ʵ����\\����\\�ı�����\\��ƽ�����ݷ���\\Adaptive weighted learning for unbalanced multicategory classification.pdf",
				"D:\\My DBank\\������ʵ����\\����\\�ı�����\\��ƽ�����ݷ���\\Posterior probability support vector machines for unbalanced data.PDF",
				"D:\\My DBank\\������ʵ����\\����\\�ı�����\\��ƽ�����ݷ���\\Support Vector Machines for Multi-class Signal Classification with Unbalanced Samples.pdf",
				"D:\\My DBank\\������ʵ����\\����\\�ı�����\\��ƽ�����ݷ���\\SVM�ڷ�ƽ�����ݼ��е�Ӧ��.pdf",
				"D:\\My DBank\\������ʵ����\\����\\�ı�����\\��ƽ�����ݷ���\\��ƽ����������о�����.pdf",
				"D:\\My DBank\\������ʵ����\\����\\�ı�����\\��ƽ�����ݷ���\\��ƽ��֧����������ƽ�ⷽ��.pdf",
				"D:\\My DBank\\������ʵ����\\����\\�ı�����\\��ƽ�����ݷ���\\�����ƽ�����ݵ�����SVMѧϰ�㷨.pdf",
				"D:\\My DBank\\������ʵ����\\����\\�ı�����\\��ƽ�����ݷ���\\����_SVM�Ĳ�ƽ�������ھ��о�.pdf"
		};
		
		
		d.executeFormat();
		d.saveWordListBuffer();
		
	}
	
}
