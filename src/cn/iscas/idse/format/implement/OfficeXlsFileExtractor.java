package cn.iscas.idse.format.implement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import cn.iscas.idse.format.FileExtractor;

public class OfficeXlsFileExtractor implements FileExtractor{
	
	private String filePath = null;
	private ExcelExtractor extractor = null;

	@Override
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		
		try {
			InputStream inputStream = new FileInputStream(this.filePath);
		    HSSFWorkbook workbook = new HSSFWorkbook(new POIFSFileSystem(inputStream));
			extractor = new ExcelExtractor(workbook);
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
				//filter formulas from the results
				this.extractor.setFormulasNotResults(true);
				//filter Sheet name from the results
			    this.extractor.setIncludeSheetNames(false);
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
		extractor.getMetadataTextExtractor();
		return null;
	}
	
	public static void main(String args[]){
		OfficeXlsFileExtractor doc = new OfficeXlsFileExtractor();
		doc.setFilePath("D:\\My DBank\\研究生文档\\2012届和2013届毕业生去向.xls");
		System.out.println(doc.getContent());
	}
	
}
