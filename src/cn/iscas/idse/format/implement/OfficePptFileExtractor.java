package cn.iscas.idse.format.implement;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hslf.extractor.PowerPointExtractor;

import cn.iscas.idse.format.FileExtractor;

public class OfficePptFileExtractor  implements FileExtractor {
	
	private String filePath = null;
	private PowerPointExtractor extractor = null;

	@Override
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		try {
			FileInputStream inputStream = new FileInputStream(this.filePath);
			extractor = new PowerPointExtractor(inputStream);
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
		OfficePptFileExtractor op = new OfficePptFileExtractor();
		op.setFilePath("D:\\My DBank\\¹ú½±´ð±ç-ÈÎ¹ð³¬.ppt");
		System.out.println(op.getContent());
	}
}
