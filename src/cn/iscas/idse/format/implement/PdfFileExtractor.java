package cn.iscas.idse.format.implement;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.util.PDFTextStripper;

import cn.iscas.idse.format.FileExtractor;


public class PdfFileExtractor implements FileExtractor {
	
	private String filePath = null;
	private PDFParser parser = null;

	@Override
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(filePath);
		    this.parser = new  PDFParser(inputStream); 
		    this.parser.parse();
		    inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	    
	}
	
	public static void main(String[]args) throws IOException{
		PdfFileExtractor doc = new PdfFileExtractor();
		doc.setFilePath("F:\\lucene-test\\ÐÅÏ¢¼ìË÷\\SIGMOD2013-Efficient ad-hoc search for personalized PageRank.pdf");
		System.out.println(doc.getContent());
	}

	@Override
	public String getContent() {
		String content = null;
		try {
			PDFTextStripper extractor = new PDFTextStripper();
			content = extractor.getText(this.parser.getPDDocument());
			content = this.wordRecovery(content);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return content;
	}

	@Override
	public String getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * the word at the end of the line usually is split into two parts which are connected by '-' and line break.
	 * this method is aimed to recovery the word to normal.
	 * @return
	 */
	private String wordRecovery(String pdfContent){
		pdfContent = pdfContent.replaceAll("-\r\n", "");
		return pdfContent;
	}
}
