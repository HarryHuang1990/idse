package cn.iscas.idse.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.iscas.idse.config.SystemConfiguration;

/**
 * This is just a class for some specific stop word generation.
 * @author Harry Huang
 *
 */
public class stopWord {
	
	public static void stopWordTitle(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(SystemConfiguration.rootPath+"resource/stop_word_title.txt")));
			List<String> stopwords = new ArrayList<String>();
			String line;
			while((line = reader.readLine()) != null){
				stopwords.add(line.split(":")[0].trim());
			}
			reader.close();
			System.out.println(stopwords.toString());
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(SystemConfiguration.rootPath+"resource/stop_word_title.txt")));
			for(String stopword : stopwords){
				writer.write(stopword+"\n");
			}
			writer.flush();
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		stopWordTitle();
	};
}
