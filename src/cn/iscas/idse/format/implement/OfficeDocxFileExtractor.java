package cn.iscas.idse.format.implement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import cn.iscas.idse.format.FileExtractor;

public class OfficeDocxFileExtractor implements FileExtractor {
	
	private String filePath = null;
	private XWPFWordExtractor extractor = null;
	
	@Override
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		try {
			InputStream inputStream = new FileInputStream(this.filePath);
			extractor = new XWPFWordExtractor(new XWPFDocument(inputStream));
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	@Override
	public String getContent() {
		String content = null;
		if(this.extractor != null){
			content = this.extractor.getText();
		}
		return content;
	}

	@Override
	public String getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[]){
		OfficeDocxFileExtractor doc = new OfficeDocxFileExtractor();
		doc.setFilePath("D:\\My DBank\\研究生文档\\Important Date on SE.docx");
		System.out.println(doc.getContent());
	}
}
