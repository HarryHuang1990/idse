package cn.iscas.idse.index.segmentation;

import java.io.UnsupportedEncodingException;

import cn.iscas.idse.format.FileExtractor;
import cn.iscas.idse.format.PdfFileExtractor;
import ICTCLAS.I3S.AC.ICTCLAS50;


/**
 * segment word (Chinese and English) with ICTCLAS which is a powerful open source suite for word segmentation and POS.
 * @author Administrator
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
		//��ʼ��
		try {
			if (ICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false)
			{
				System.err.println("Init Fail!");
				throw new Exception();
			}
			//���ô��Ա�ע��(0 ������������ע����1 ������һ����ע����2 ���������ע����3 ����һ����ע��)
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
			byte nativeBytes[] = ICTCLAS50.ICTCLAS_ParagraphProcess(sInput.getBytes("GB2312"), 0, 0);//�ִʴ���
			String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			System.out.println("δ�����û��ʵ�ķִʽ���� " + nativeStr);//��ӡ���
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * release the resource occupied by the ICTLAS
	 */
	public void exitICTCLAS(){
		ICTCLAS50.ICTCLAS_Exit();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		PdfFileExtractor doc = new PdfFileExtractor();
//		doc.setFilePath("F:\\lucene-test\\��Ϣ����\\��Ч���Ի�PageRank�㷨����.pdf");
//		String sInput = doc.getContent();
		
		String sInput = "public FileExtractor getFileExtractor(String fileSuffix){";
		
		WordSegmentation ws = WordSegmentation.getInstance();
		ws.initialize();
		//�ַ����ִ�   
		for(int i=1; i<1000; i++){
			ws.segmentString(sInput);
			System.out.println(i+"/"+1000);
		}
		ws.exitICTCLAS();
	}
}
