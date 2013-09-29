package cn.iscas.idse.format.implement;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.iscas.idse.config.SystemConfiguration;
import cn.iscas.idse.format.FileExtractor;

public class TextFileExtractor implements FileExtractor {
	
	private String filePath = null;

	@Override
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public String getContent() {
		String content = null;
		try {
			File file = new File(this.filePath);
			/*
			 * calc the size(M) of file. 
			 * file.length() returns the number of bytes of this file.
			 */
			double size = (file.length()*1.0/1024);
			
			if(size < SystemConfiguration.maxSizeAllowed_TXT){
				byte[] tempbytes = new byte[100];
		        int byteread = 0;
		        InputStream in = new FileInputStream(this.filePath);
		        ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
		        while ((byteread = in.read(tempbytes)) != -1) {
		        	out.write(tempbytes, 0, byteread);
		        }
		        content = out.toString();
			}
			
		} catch (Exception e) {
			content = null;
		} catch (Error e) {
			content = null;
		}
		
		return content;
	}

	@Override
	public String getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

}
