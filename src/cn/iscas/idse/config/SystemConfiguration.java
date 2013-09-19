package cn.iscas.idse.config;

import java.util.HashMap;
import java.util.Map;

/**
 * ����ϵͳ��������Ϣ����
 * ������ͬ�ļ���ʽ�Ĳ����Ϣ����<��׺������������>��ʽ����
 * @author HarryHuang
 *
 */
public class SystemConfiguration {
	
	/**
	 * �ļ���ʽ-����������ࣩӳ���
	 */
	public static Map<String, String> formatPluginMap = new HashMap<String, String>();
	/**
	 * Ӧ�ó����Ŀ¼·����·���ɷ�б��/�����·��ĩβ�Է�б��/����
	 */
	public static String rootPath = "";
	
	static{
		String formatSuffixValue = "";
		String formatSuffixSplits[] = null;
		String formatClassValue = "";
		String formatClassSplits[] = null;
		/*
		 * �����ļ����ͺ�׺�Ͳ����������
		 */
		formatSuffixValue = PropertiesManager.getApp("format.suffix");
		formatSuffixSplits = formatSuffixValue.split(",");
		formatClassValue = PropertiesManager.getApp("format.classpath");
		formatClassSplits = formatClassValue.split(",");
		for(int i=0; i<formatSuffixSplits.length; i++)
			formatPluginMap.put(formatSuffixSplits[i], formatClassSplits[i]);
		/*
		 *��ȡӦ�ó����Ŀ¼��ַ 
		 */
		rootPath = System. getProperty("user.dir");
		while(rootPath.indexOf("\\" ) != -1){
			rootPath = rootPath.replace( "\\", "/" );
	    }
		rootPath += "/";
	}
}
