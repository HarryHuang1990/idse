package cn.iscas.idse.format.implement;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.util.PDFTextStripper;

import cn.iscas.idse.config.SystemConfiguration;
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
		} catch (Exception e) {
			return;
		} catch (Error e) {
			return;
		}
	    
	}
	
	public static void main(String[]args) throws IOException{
		PdfFileExtractor doc = new PdfFileExtractor();
		doc.setFilePath("D:\\My DBank\\互联网实验室\\工作\\代码分析\\AST.pdf");
		System.out.println(doc.getContent());
	}

	@Override
	public String getContent() {
		String content = null;
		try {
			File file = new File(this.filePath);
			/*
			 * calc the size(M) of file. 
			 * file.length() returns the number of bytes of this file.
			 */
			double size = (file.length()*1.0/1024);
			
			if(size < SystemConfiguration.maxSizeAllowed_PDF){
				PDFTextStripper extractor = new PDFTextStripper();
				content = extractor.getText(this.parser.getPDDocument());
				content = this.wordRecovery(content);
			}
		} catch (Error e) {
			content = null;
		} catch (Exception e) {
			content = null;
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
		pdfContent = pdfContent.replaceAll("-[ ]*[\r]*[\n]*", "");
		return pdfContent;
	}
}
