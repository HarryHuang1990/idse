package cn.iscas.idse.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.iscas.idse.storage.DBManager;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;

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
	 * ��������Ŀ��Ŀ¼�б�
	 */
	public static List<String> targetDirectories = new ArrayList<String>();
	/**
	 * Ӧ�ó����Ŀ¼·����·���ɷ�б��/�����·��ĩβ�Է�б��/����
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
		 * �����ļ����ͺ�׺�Ͳ����������
		 */
		formatSuffixValue = PropertiesManager.getApp("format.suffix");
		formatSuffixSplits = formatSuffixValue.split(",");
		formatClassValue = PropertiesManager.getApp("format.classpath");
		formatClassSplits = formatClassValue.split(",");
		for(int i=0; i<formatSuffixSplits.length; i++)
			formatPluginMap.put(formatSuffixSplits[i], formatClassSplits[i]);
		
		/*
		 * ���ش�������Ŀ��Ŀ¼�б� 
		 */
		targetDirectoryValues = PropertiesManager.getApp("target.directory");
		targetDirectorySplits = targetDirectoryValues.split(",");
		for(int i=0; i<targetDirectorySplits.length; i++)
			targetDirectories.add(targetDirectorySplits[i]);
		
		/*
		 *��ȡӦ�ó����Ŀ¼��ַ 
		 */
		rootPath = System. getProperty("user.dir");
		while(rootPath.indexOf("\\" ) != -1){
			rootPath = rootPath.replace( "\\", "/" );
	    }
		rootPath += "/";
		
		/*
		 * �ж�����Ŀ���Ƿ�ı�
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
