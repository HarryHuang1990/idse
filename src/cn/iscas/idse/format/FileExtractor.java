package cn.iscas.idse.format;

/**
 * An interface for file extracting, which is used to extract text from various formats of file.
 * The implementation of each format can be constructed by the class FileExtractorFactory
 */
public interface FileExtractor {
	/**
	 * set the path of file that needs to parse.
	 * @param filePath
	 * @return
	 */
	public void setFilePath(String filePath);
	/**
	 * get the content of text from the file of specific format.
	 * @return
	 */
	public String getContent();
	/**
	 * obtain the content of text from the file of specific format.
	 * @return
	 */
	public String getMetaData();
}
