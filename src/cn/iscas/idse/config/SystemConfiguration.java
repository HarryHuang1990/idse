package cn.iscas.idse.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.iscas.idse.storage.DBManager;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;

/**
 * 管理系统的配置信息管理：
 * 解析不同文件格式的插件信息，以<后缀名，完整类名>形式存在
 * @author HarryHuang
 *
 */
public class SystemConfiguration {
	
	/**
	 * 文件格式-插件（处理类）映射表
	 */
	public static Map<String, String> formatPluginMap = new HashMap<String, String>();
	/**
	 * 待索引的目标目录列表
	 */
	public static List<String> targetDirectories = new ArrayList<String>();
	/**
	 * 应用程序根目录路径。路径由反斜杠/间隔，路径末尾以反斜杠/结束
	 */
	public static String rootPath = "";
	
	static{
		String formatSuffixValue = "";
		String formatSuffixSplits[] = null;
		String formatClassValue = "";
		String formatClassSplits[] = null;
		String targetDirectoryValues = "";
		String targetDirectorySplits[] = null;
		/*
		 * 加载文件类型后缀和插件（类名）
		 */
		formatSuffixValue = PropertiesManager.getApp("format.suffix");
		formatSuffixSplits = formatSuffixValue.split(",");
		formatClassValue = PropertiesManager.getApp("format.classpath");
		formatClassSplits = formatClassValue.split(",");
		for(int i=0; i<formatSuffixSplits.length; i++)
			formatPluginMap.put(formatSuffixSplits[i], formatClassSplits[i]);
		
		/*
		 * 加载待索引的目标目录列表 
		 */
		targetDirectoryValues = PropertiesManager.getApp("target.directory");
		targetDirectorySplits = targetDirectoryValues.split(",");
		for(int i=0; i<targetDirectorySplits.length; i++)
			targetDirectories.add(targetDirectorySplits[i]);
		
		/*
		 *获取应用程序根目录地址 
		 */
		rootPath = System. getProperty("user.dir");
		while(rootPath.indexOf("\\" ) != -1){
			rootPath = rootPath.replace( "\\", "/" );
	    }
		rootPath += "/";
		
		/*
		 * 判断索引目标是否改变
		 */
		DBManager database = new DBManager();
		TargetDirectoryAccessor accessor = new TargetDirectoryAccessor(database.getIndexStore());
		for(String targetPath : targetDirectories){
			if(!accessor.getSecondaryTargetPath().contains(targetPath)){
				//put the target path into the Berkeley DB
				TargetDirectory targetAccessor = new TargetDirectory(targetPath);
				accessor.getPrimaryTargetID().putNoReturn(targetAccessor);
			}
		}
		database.close();
	}
}
