package cn.iscas.idse.format.implement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.iscas.idse.format.FileExtractor;

public class OfficeXlsxFileExtractor implements FileExtractor{
	
	private String filePath = null;
	private XSSFExcelExtractor extractor = null;
	
	@Override
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(this.filePath);
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			this.extractor = new XSSFExcelExtractor(workbook);
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
		return null;
	}
	
	public static void main(String args[]){
		OfficeXlsxFileExtractor doc = new OfficeXlsxFileExtractor();
		doc.setFilePath("D:\\My DBank\\文献\\操作系统\\desktop search\\desktopSearch学术论文统计.xlsx");
		System.out.println(doc.getContent());
		
		String s = "";
		
		
	}
	
}
