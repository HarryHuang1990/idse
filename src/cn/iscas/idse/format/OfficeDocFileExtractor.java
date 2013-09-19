package cn.iscas.idse.format;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OpenXML4JException e) {
			e.printStackTrace();
		} catch (XmlException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getContent() {
		String content = null;
		content = this.extractor.getTextFromPieces();
//			POIFSFileSystem fileSystem = new POIFSFileSystem(fis);
//			// Firstly, get an extractor for the Workbook
//			POIOLE2TextExtractor oleTextExtractor = ExtractorFactory.createExtractor(fileSystem);
//			// Then a List of extractors for any embedded Word objects embedded into it.
//			POITextExtractor[] embeddedExtractors;
//			embeddedExtractors = ExtractorFactory.getEmbededDocsTextExtractors(oleTextExtractor);
//			for (POITextExtractor textExtractor : embeddedExtractors) {
//				  // A Word Document
//				 if (textExtractor instanceof WordExtractor) {
//				      WordExtractor wordExtractor = (WordExtractor) textExtractor;
//				      String[] paragraphText = wordExtractor.getParagraphText();
//				      for (String paragraph : paragraphText) {
//				         System.out.println(paragraph);
//				      }
//				      // Display the document's header and footer text
//				      System.out.println("Footer text: " + wordExtractor.getFooterText());
//				      System.out.println("Header text: " + wordExtractor.getHeaderText());
//				   }
//			}

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
