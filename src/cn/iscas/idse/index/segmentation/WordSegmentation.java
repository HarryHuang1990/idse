package cn.iscas.idse.index.segmentation;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import cn.iscas.idse.config.InstanceManager;
import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.format.FileExtractor;
import cn.iscas.idse.format.implement.PdfFileExtractor;
import ICTCLAS.CLibrary;


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

	private static CLibrary ICTCLAS50 = null;
	
//	private static WordSegmentation instance = null;
	
//	public static WordSegmentation getInstance(){
//		if(instance == null)
//			instance = new WordSegmentation();
//		return instance;
//	}
	
	public WordSegmentation(){};
	
	
	/**
	 * Initialize the ICTCLA for word segmentation.
	 */
	public void initialize(){
		int charset_type = 1;  // UTF-8 ---- 1   CBK --- 0
		ICTCLAS50 = CLibrary.Instance;
		int init_flag = ICTCLAS50.NLPIR_Init(SystemConfiguration.rootPath, charset_type, "0");
		String nativeBytes = null;
		
		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is "+nativeBytes);
			System.exit(0);
		}
	}
	
	/**
	 * segment the specific input String with the APIs provided by ICTCLAS
	 * @param docID
	 * @param text
	 */
	public synchronized String segmentString(String text)
	{	
		String nativeStr = null;
		if(text != null){
			//achieve result of segmentation
			nativeStr = ICTCLAS50.NLPIR_ParagraphProcess(text, 0);//分词处理
			//remove seperator
			nativeStr = nativeStr.replaceAll(SystemConfiguration.seperatorRegx, " ");
			//remove punctuation
			nativeStr = PunctuationFilter.removePunctuation(nativeStr);
		}
		return  nativeStr;
	}
	
	/**
	 * release the resource occupied by the ICTLAS
	 */
	public void exitICTCLAS(){
		ICTCLAS50.NLPIR_Exit();
		ICTCLAS50=null;
	}
	
	
	public static void main(String[] args) {
//		PdfFileExtractor doc = new PdfFileExtractor();
//		doc.setFilePath("F:\\lucene-test\\信息检索\\SIGMOD2013-Efficient ad-hoc search for personalized PageRank.pdf");
//		String text = doc.getContent();
		
		String text = "public File_Extractor gets_Files_Extracting(String fileSuffix)  中华人民共和国中央人民政府 我很爱你 ";
		
		WordSegmentation ws = new WordSegmentation();
		ws.initialize();
		//字符串分词   
		for(int i=1; i<2; i++){
			System.out.println(ws.segmentString(text));
		}
		ws.exitICTCLAS();
	}
}
