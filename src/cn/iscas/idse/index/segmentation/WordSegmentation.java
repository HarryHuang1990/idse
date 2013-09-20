package cn.iscas.idse.index.segmentation;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import cn.iscas.idse.config.InstanceManager;
import cn.iscas.idse.format.FileExtractor;
import cn.iscas.idse.format.PdfFileExtractor;
import ICTCLAS.I3S.AC.ICTCLAS50;


/**
 * <p>
 * segment word (Chinese and English) with ICTCLAS which is a powerful open source suite for word segmentation and POS.
 * </p>
 * <p>
 * usage:
 * 	<ol>
 * 		<li>invoke method .getInstance() to achieve the instance.</li>
 * 		<li>invoke method .initialize() to initialize the word-segmentation object.</li>
 * 		<li>invoke method .segmentString(String text) to execute segmentation.</li>
 * 		<li>invoke method .exitICTCLAS() to release resource.</li>
 * 	</ol>
 * </p>
 * @author HarryHuang
 *
 */
public class WordSegmentation {

	private ICTCLAS50 ICTCLAS50 = null;
	
	private static WordSegmentation instance = null;
	
	public static WordSegmentation getInstance(){
		if(instance == null)
			instance = new WordSegmentation();
		return instance;
	}
	
	private WordSegmentation(){};
	
	/**
	 * destroy the instance and release its memory.
	 */
	public void destoryInstance(){
		instance = null;
	}
	
	/**
	 * Initialize the ICTCLA for word segmentation.
	 */
	public void initialize(){
		ICTCLAS50 = new ICTCLAS50();
		String argu = ".";
		//初始化
		try {
			if (ICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false)
			{
				System.err.println("Init Fail!");
				throw new Exception();
			}
			//设置词性标注集(0 计算所二级标注集，1 计算所一级标注集，2 北大二级标注集，3 北大一级标注集)
			ICTCLAS50.ICTCLAS_SetPOSmap(2);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * segment the specific input String with the APIs provided by ICTCLAS
	 * @param docID
	 * @param text
	 */
	public void segmentString(int docID, String text)
	{
		try {
			//achieve result of segmentation
			byte nativeBytes[] = ICTCLAS50.ICTCLAS_ParagraphProcess(text.getBytes("GB2312"), 0, 0);//分词处理
			String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			//remove punctuation
			nativeStr = PunctuationFilter.removePunctuation(nativeStr);
			//split the result-string and localize the term in text.
			int offset = -1;
			String currentTerm = "";
			StringTokenizer tokenizer = new StringTokenizer(nativeStr);
			while(tokenizer.hasMoreTokens()){
				currentTerm = tokenizer.nextToken().trim();
				//handle Camel Case style
				String[] words = CamelCase.splitCamelCase(currentTerm);
				if(words != null){
					for(String word : words){
						//localize and get the offset.
						offset = text.indexOf(word, ++offset);
						//lowercase the word
						word = word.toLowerCase();
						// handle Lemmatize
						word = ((TermLemmatizer)InstanceManager.getInstance(InstanceManager.CLASS_TERMLEMMATIZER)).adornText(word);
						if(word != null){
							// handle stop word
							if(!((StopWordFilter)InstanceManager.getInstance(InstanceManager.CLASS_STOPWORDFILTER)).isStopWord(word)){
								//add into index
								//TODO put the term info into the index.
								System.out.println(word + "\t" + offset);
							}
						}
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * release the resource occupied by the ICTLAS
	 */
	public void exitICTCLAS(){
		ICTCLAS50.ICTCLAS_Exit();
	}
	
	
	public static void main(String[] args) {
//		PdfFileExtractor doc = new PdfFileExtractor();
//		doc.setFilePath("F:\\lucene-test\\信息检索\\SIGMOD2013-Efficient ad-hoc search for personalized PageRank.pdf");
//		String text = doc.getContent();
		
		String text = "public File_Extractor gets_Files_Extracting(String fileSuffix){";
		
		WordSegmentation ws = WordSegmentation.getInstance();
		ws.initialize();
		//字符串分词   
		for(int i=1; i<2; i++){
			ws.segmentString(1, text);
			System.out.println(i+"/"+1000);
		}
		ws.exitICTCLAS();
	}
}
