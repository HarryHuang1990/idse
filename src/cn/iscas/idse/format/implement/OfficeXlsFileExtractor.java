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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getContent() {
		String content = null;
		if(this.extractor != null){
			//filter formulas from the results
			this.extractor.setFormulasNotResults(true);
			//filter Sheet name from the results
		    this.extractor.setIncludeSheetNames(false);
		    content = this.extractor.getText();
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
		doc.setFilePath("D:\\My DBank\\�о����ĵ�\\2012���2013���ҵ��ȥ��.xls");
		System.out.println(doc.getContent());
	}
	
}
