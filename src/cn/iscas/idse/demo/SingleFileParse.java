package cn.iscas.idse.demo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;

import cn.iscas.idse.config.InstanceManager;
import cn.iscas.idse.format.FileExtractor;
import cn.iscas.idse.format.FileExtractorFactory;
import cn.iscas.idse.format.implement.PdfFileExtractor;
import cn.iscas.idse.index.segmentation.CamelCase;
import cn.iscas.idse.index.segmentation.StopWordFilter;
import cn.iscas.idse.index.segmentation.TermLemmatizer;
import cn.iscas.idse.index.segmentation.WordSegmentation;

/*
 * demo of the index procedure of a single file.
 */
public class SingleFileParse {
	
	public static void main(String[]args) throws IOException{
		for(int i=0; i<3; i++){
			long start = System.currentTimeMillis();
			/*
			 * D:\\My DBank\\工具&工具包\\morphadorner-2009-04-30\\data\\gate\\ANNIE\\resources\\tokeniser\\chinesetokeniser\\bothlexu8.txt
			 */
			indexFile(new File("D:\\My DBank\\工具&工具包\\morphadorner-2009-04-30\\data\\gate\\ANNIE\\resources\\tokeniser\\chinesetokeniser\\tradlexu8.txt"), ".txt");
			long end = System.currentTimeMillis();
			System.out.println("总时间:"+((end-start)*1.0/1000) + "s");
		}
	}
	
	public static void indexFile(File file, String suffix){
		WordSegmentation ws = (WordSegmentation)InstanceManager.getInstance(InstanceManager.CLASS_WORDSEGMENTATION);
		ws.initialize();
		/*
		 * index title.
		 */
		String title = file.getName();
		int typeIndexStart = title.lastIndexOf(".");
		if(typeIndexStart != -1){
			title = title.substring(0, typeIndexStart);
		}
		String segmentResult = ws.segmentString(title);
		if(segmentResult != null)
			moreProcedure(true, title, segmentResult);
		
		/*
		 * index content.
		 */
		//get the content extractor.
		FileExtractor extractor = FileExtractorFactory.getInstance().getFileExtractor(suffix);
		
		System.err.println(file.getAbsolutePath());
		if(extractor != null){
			System.err.println("parsable!");
			long start = System.currentTimeMillis();
			extractor.setFilePath(file.getAbsolutePath());
			String content = extractor.getContent();
			long end = System.currentTimeMillis();
			System.out.println("抽取文本时间:"+((end-start)*1.0/1000) + "s");
//			System.out.println(content);
			segmentResult = ws.segmentString(content);
			if(segmentResult != null)
				moreProcedure(false, content, segmentResult);
		}
		
		ws.exitICTCLAS();
//		ws.destoryInstance();
	}
	
	/**
	 * subsequent steps to handle text after segmentation.
	 * @param docID		document id
	 * @param isTitle	the text to handle is title or not
	 * @param beforeSeg		the original text before segmentation.
	 * @param afterSeg		the text after segmentation and it is without punctuation.
	 */
	public static void moreProcedure(boolean isTitle, String beforeSeg, String afterSeg){
		//split the result-string and localize the term in text.
		int offset = -1;
		String currentTerm = "";
		StringTokenizer tokenizer = new StringTokenizer(afterSeg);
		while(tokenizer.hasMoreTokens()){
			currentTerm = tokenizer.nextToken().trim();
			//handle Camel Case style
			String[] words = CamelCase.splitCamelCase(currentTerm);
			if(words != null){
				for(String word : words){
					//localize and get the offset.
					int nextIndex = beforeSeg.indexOf(word, ++offset);
					if(nextIndex != -1){
						offset = nextIndex;
						//lowercase the word
						word = word.toLowerCase();
						// handle Lemmatize
						word = ((TermLemmatizer)InstanceManager.getInstance(InstanceManager.CLASS_TERMLEMMATIZER)).adornText(word);
						if(word != null){
							// handle stop word
							if(!((StopWordFilter)InstanceManager.getInstance(InstanceManager.CLASS_STOPWORDFILTER)).isStopWord(word)){
								//add into index
								//put the term info into the index.
//								System.out.println(word + "\t" + offset);
							}
						}
					}
					else
						offset += word.length();

				}
			}
		}
	}
	
}
