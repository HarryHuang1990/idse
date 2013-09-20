package cn.iscas.idse.index.segmentation;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

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
 * 		<li>invoke method .segmentString(String sInput) to execute segmentation.</li>
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
	 * @param sInput
	 */
	public void segmentString(String sInput)
	{
		try {
			//achieve result of segmentation
			byte nativeBytes[] = ICTCLAS50.ICTCLAS_ParagraphProcess(sInput.getBytes("GB2312"), 0, 0);//分词处理
			String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			//remove punctuation
			nativeStr = PunctuationFilter.removePunctuation(nativeStr);
			//split the result-string and localize the term in sInput.
			int offset = -1;
			String currenctTerm = "";
			StringTokenizer tokenizer = new StringTokenizer(nativeStr);
			while(tokenizer.hasMoreTokens()){
				
				currenctTerm = tokenizer.nextToken();
				offset = sInput.indexOf(currenctTerm, ++offset);
				if(offset > 100)break;
				
				System.out.println(currenctTerm.toLowerCase() + "\t" + offset);
			}
//			System.out.println("未导入用户词典的分词结果： " + nativeStr);//打印结果
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
		PdfFileExtractor doc = new PdfFileExtractor();
		doc.setFilePath("F:\\lucene-test\\信息检索\\SIGMOD2013-Efficient ad-hoc search for personalized PageRank.pdf");
		String sInput = doc.getContent();
		
//		String sInput = "public File_Extractor get_File_Extractor(String fileSuffix){";
		
		WordSegmentation ws = WordSegmentation.getInstance();
		ws.initialize();
		//字符串分词   
		for(int i=1; i<2; i++){
			ws.segmentString(sInput);
			System.out.println(i+"/"+1000);
		}
		ws.exitICTCLAS();
	}
}
