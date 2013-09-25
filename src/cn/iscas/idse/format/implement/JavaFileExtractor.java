package cn.iscas.idse.format.implement;

import cn.iscas.idse.format.FileExtractor;

public class JavaFileExtractor implements FileExtractor {
	
	private String filePath = null;

	@Override
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		System.out.println("success");
		return null;
	}

	@Override
	public String getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}
}
