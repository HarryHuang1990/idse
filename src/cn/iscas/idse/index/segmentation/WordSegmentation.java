package cn.iscas.idse.index.segmentation;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import cn.iscas.idse.config.InstanceManager;
import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.format.FileExtractor;
import cn.iscas.idse.format.implement.PdfFileExtractor;
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

	private static ICTCLAS50 ICTCLAS50 = null;
	
//	private static WordSegmentation instance = null;
	
//	public static WordSegmentation getInstance(){
//		if(instance == null)
//			instance = new WordSegmentation();
//		return instance;
//	}
	
	public WordSegmentation(){};
	
	/**
	 * destroy the instance and release its memory.
	 */
//	public void destoryInstance(){
//		instance = null;
//	}
	
	/**
	 * Initialize the ICTCLA for word segmentation.
	 */
	public void initialize(){
//		if(ICTCLAS50 == null){
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
//		}
	}
	
	/**
	 * segment the specific input String with the APIs provided by ICTCLAS
	 * @param docID
	 * @param text
	 */
	public synchronized String segmentString(String text)
	{
		String nativeStr = null;
		try {
			if(text != null){
				//achieve result of segmentation
				byte nativeBytes[] = ICTCLAS50.ICTCLAS_ParagraphProcess(text.getBytes("GB2312"), 0, 0);//分词处理
				nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
				//remove seperator
				nativeStr = nativeStr.replaceAll(SystemConfiguration.seperatorRegx, " ");
				//remove punctuation
				nativeStr = PunctuationFilter.removePunctuation(nativeStr);
			}
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		return  nativeStr;
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
		
		WordSegmentation ws = new WordSegmentation();
		ws.initialize();
		//字符串分词   
		for(int i=1; i<2; i++){
			ws.segmentString(text);
			System.out.println(i+"/"+1000);
		}
		ws.exitICTCLAS();
	}
}
