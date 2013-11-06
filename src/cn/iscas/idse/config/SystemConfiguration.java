package cn.iscas.idse.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.iscas.idse.storage.DBManager;
import cn.iscas.idse.storage.entity.Category;
import cn.iscas.idse.storage.entity.FileType;
import cn.iscas.idse.storage.entity.TargetDirectory;
import cn.iscas.idse.storage.entity.accessor.AccessorFactory;
import cn.iscas.idse.storage.entity.accessor.CategoryAccessor;
import cn.iscas.idse.storage.entity.accessor.FileTypeAccessor;
import cn.iscas.idse.storage.entity.accessor.TargetDirectoryAccessor;
import cn.iscas.idse.utilities.Converter;

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
	 * 停用词表-插件（处理类）映射表
	 */
	public static Map<String, String> stopWordPluginMap = new HashMap<String, String>();
	/**
	 * Berkeley DB manager
	 */
	public static DBManager database = null;
	/**
	 * 文件类型索引缓存
	 */
	public static Map<String, FileType> fileTypeBuff = new HashMap<String, FileType>();
	/**
	 * 分隔符列表
	 */
	public static String seperatorRegx = "[-_:]"; 
	
	/**
	 * 允许分析的PDF文本大小上限（K）
	 */
	public static short maxSizeAllowed_PDF = 5000;
	
	/**
	 * 允许分析的text文本大小上限（K）
	 */
	public static short maxSizeAllowed_TXT = 500;
	
	/**
	 * 如果文件夹中的文件数超过150个，则不对这个文件夹下的这些文件进行分析。如果有子文件夹，需要继续判断。
	 */
	public static int maxFileCountPreDirectory = 150;
	
	/**
	 * 待索引的目标目录列表
	 */
	public static List<String> targetDirectories = new ArrayList<String>();
	/**
	 * 应用程序根目录路径。路径由反斜杠/间隔，路径末尾以反斜杠/结束
	 */
	public static String rootPath = "";
	
	
	
	/**
	 * 过滤时间间隔-用于界定有效打开文件，对于那些打开之后再很短的时间内就关闭的文件，
	 * 我们不认为他们具有很强的目的性。比如说我们在浏览网页的时候，对于我们感兴趣的，
	 * 或者说重要的页面我们会关注很长的时间，但是对于一些无用的页面的俩说我们可能扫
	 * 一眼就关闭了，前后打开的时间可能就几秒种。这个有效的文件打开时间下限用该参数来
	 * 定义（单位为s）
	 */
	public static int validViewPeriod = 10; 
	/**
	 * task时间间隔-用于度量task之间的时间跨度，如果相邻两个文件之间间隔时间超过10分钟，
	 * 则认为启动了一个新的task。
	 */
	public static int intervalTaskPeriod = 600;
	
	/**
	 * 用于task合并的相似度阈值
	 */
	public static float taskSimilarityThreshold = 0.5f;
	
	
	static{
		String formatSuffixValue = "";
		String formatSuffixSplits[] = null;
		String classValue = "";
		String classSplits[] = null;
		String formatClassValue = "";
		String formatClassSplits[] = null;
		
		String targetDirectoryValues = "";
		String targetDirectorySplits[] = null;
		
		String formatCategory = "";
		String formatCategorySplits[] = null;
		
		/*
		 * 加载文件类型后缀和插件（类名）
		 */
		formatSuffixValue = PropertiesManager.getKeyValue("format.suffix");
		formatSuffixSplits = formatSuffixValue.split(",");
		
		classValue = PropertiesManager.getKeyValue("format.classpath");
		classSplits = classValue.split(",");
		
		formatClassValue = PropertiesManager.getKeyValue("format.suffix_classpath_map");
		formatClassSplits = formatClassValue.split(",");
		
		for(int i=0; i<formatSuffixSplits.length; i++)
			formatPluginMap.put(formatSuffixSplits[i], classSplits[Integer.parseInt(formatClassSplits[i]) - 1]);
		
		/*
		 * 加载待索引的目标目录列表 
		 */
		targetDirectoryValues = PropertiesManager.getKeyValue("target.directory");
		targetDirectorySplits = targetDirectoryValues.split(",");
		for(int i=0; i<targetDirectorySplits.length; i++){
			targetDirectories.add(targetDirectorySplits[i]);
			System.out.println(targetDirectorySplits[i]);
		}
		
		/*
		 *获取应用程序根目录地址 
		 */
		rootPath = Converter.convertBackSlashToSlash(System. getProperty("user.dir"));
		rootPath += "/";
		
		/*
		 * 判断索引目标是否改变
		 */
		database = new DBManager();
//		TargetDirectoryAccessor accessor = new TargetDirectoryAccessor(database.getIndexStore());
//		for(String targetPath : targetDirectories){
//			if(!accessor.getSecondaryTargetPath().contains(targetPath)){
//				//put the target path into the Berkeley DB
//				TargetDirectory targetAccessor = new TargetDirectory(targetPath);
//				accessor.getPrimaryTargetID().putNoReturn(targetAccessor);
//			}
//		}
		
		/*
		 * 加载文件格式类别和文件格式列表
		 */
		formatCategory = PropertiesManager.getKeyValue("format.category");
		formatCategorySplits = formatCategory.split(",");
		
		String type = "";
		String typeSplits[] = null;
		CategoryAccessor categoryAccessor = AccessorFactory.getCategoryAccessor(database.getIndexStore());
		FileTypeAccessor fileTypeAccessor = AccessorFactory.getFileTypeAccessor(database.getIndexStore());
		
		for(int i=0; i<formatCategorySplits.length; i++){
			if(!categoryAccessor.getPrimaryCategoryID().contains((byte)(i+1))){
				categoryAccessor.getPrimaryCategoryID().putNoReturn(new Category((byte)(i+1), formatCategorySplits[i]));
			}
			type = PropertiesManager.getKeyValue("format.category." + formatCategorySplits[i]);
			typeSplits = type.split(",");
			for(int j=0; j<typeSplits.length; j++){
				fileTypeBuff.put(typeSplits[j], new FileType(typeSplits[j], (byte)(i+1)));
			}
		}
		
		
	}
}
