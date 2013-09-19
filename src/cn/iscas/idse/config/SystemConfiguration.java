package cn.iscas.idse.config;

import java.util.HashMap;
import java.util.Map;

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
	 * 应用程序根目录路径。路径由反斜杠/间隔，路径末尾以反斜杠/结束
	 */
	public static String rootPath = "";
	
	static{
		String formatSuffixValue = "";
		String formatSuffixSplits[] = null;
		String formatClassValue = "";
		String formatClassSplits[] = null;
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
		 *获取应用程序根目录地址 
		 */
		rootPath = System. getProperty("user.dir");
		while(rootPath.indexOf("\\" ) != -1){
			rootPath = rootPath.replace( "\\", "/" );
	    }
		rootPath += "/";
	}
}
