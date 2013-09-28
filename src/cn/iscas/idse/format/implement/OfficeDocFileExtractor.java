package cn.iscas.idse.format.implement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import cn.iscas.idse.format.FileExtractor;

public class OfficeDocFileExtractor implements FileExtractor {
	
	private String filePath = null;
	private WordExtractor extractor = null;

	@Override
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(this.filePath);
			extractor = (WordExtractor) ExtractorFactory.createExtractor(inputStream);
			inputStream.close();
		} catch (Exception e) {
			return;
		} catch (Error e) {
			return;
		}
	}
	
	@Override
	public String getContent() {
		String content = null;
		try{
			content = this.extractor.getTextFromPieces();
		}
		catch(Exception e){
			content = null;
		}
		catch(Error e){
			content = null;
		}
		return content;
	}

	@Override
	public String getMetaData() {
		this.extractor.getTextFromPieces();
		return null;
	}

	public static void main(String args[]){
		OfficeDocFileExtractor doc = new OfficeDocFileExtractor();
		doc.setFilePath("D:\\My DBank\\研究生文档\\中文论文格式模板.doc");
		System.out.println(doc.getContent());
	}

}
