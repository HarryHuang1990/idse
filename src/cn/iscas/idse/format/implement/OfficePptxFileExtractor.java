package cn.iscas.idse.format.implement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

import cn.iscas.idse.format.FileExtractor;

public class OfficePptxFileExtractor  implements FileExtractor {
	
	private String filePath = null;
	private XSLFPowerPointExtractor extractor = null;
	
	@Override
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(this.filePath);
			XMLSlideShow slideShow = new XMLSlideShow(inputStream);
			this.extractor = new XSLFPowerPointExtractor(slideShow);
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
		return null;
	}
	
	public static void main(String args[]){
		OfficePptxFileExtractor op = new OfficePptxFileExtractor();
		op.setFilePath("D:\\My DBank\\»¥ÁªÍøÊµÑéÊÒ\\Àý»á\\»Ê¸¦Ñî-Searching Chnnected API Subgraph via Text Phrase.pptx");
		System.out.println(op.getContent());
	}
	
}
