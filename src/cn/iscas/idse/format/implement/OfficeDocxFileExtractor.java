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
			if(this.extractor != null){
				content = this.extractor.getText();
			}
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
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[]){
		OfficeDocxFileExtractor doc = new OfficeDocxFileExtractor();
		doc.setFilePath("D:\\My DBank\\博客\\20130817用PersonalRank实现基于图的推荐算法\\~$ersonalRank实现基于图的推荐算法.docx");
		System.out.println(doc.getContent());
	}
}
