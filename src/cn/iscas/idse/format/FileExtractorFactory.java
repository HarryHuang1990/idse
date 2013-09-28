package cn.iscas.idse.format;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import cn.iscas.idse.config.SystemConfiguration;

/**
 * create the file extracting object for specific format of file, which is the implementation of the interface FileExtractor 
 * @author Harry Huang
 *
 */
public class FileExtractorFactory {
	
	private static FileExtractorFactory instance = null;
	
	public static FileExtractorFactory getInstance(){
		if(instance == null)
			instance = new FileExtractorFactory();
		return instance;
	}
	
	public FileExtractor getFileExtractor(String fileSuffix){
//		System.out.println(SystemConfiguration.formatPluginMap);
		FileExtractor fileExtractor = null;
        try {
        	//plugin, 使用绝对地址加载class
//        	Class c = null;
//            String root_path = System. getProperty("user.dir");
//            while(root_path.indexOf("\\" ) != -1){
//              root_path = root_path.replace( "\\", "/" );
//            }
//            System. out.println("项目根目录地址：" + "file://"   +  root_path  +   "/plugin/cn/iscas/idse/format/" );
//            URL[] us  =  { new URL("file://"    +  root_path  +  "/plugin/cn/iscas/idse/format/" )};
//            ClassLoader loader  =   new  URLClassLoader(us);
//            c = loader.loadClass(SystemConfiguration.formatPluginMap.get(".xml"));
//            FileExtractor fileExtractor = (FileExtractor)c.newInstance();
//            fileExtractor.getContent();

        	//non-plugin
        	if(SystemConfiguration.formatPluginMap.containsKey(fileSuffix)){
        		Class c = Class. forName(SystemConfiguration.formatPluginMap.get(fileSuffix));
    			fileExtractor = (FileExtractor)c.newInstance();
        	}
        	
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
        
        return fileExtractor;
		
	}
	
}
