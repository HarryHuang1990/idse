package cn.iscas.idse.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import cn.iscas.idse.index.segmentation.StopWordFilter;
import cn.iscas.idse.index.segmentation.TermLemmatizer;
import cn.iscas.idse.index.segmentation.WordSegmentation;
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
	 * 有效KL值上限，凡是kl值大于上限的两个document,不建立topic语义关联
	 */
	public static double klUpbound = 0.3;
	
	/**
	 * 文件之间的切换距离上限，凡是切换距离D(a,b)大于上限的两个document，不建立location语义关联
	 */
	public static int dMAX_GAMA = 1;
	
	/**
	 * 各部分得分对总关系得分的贡献比例
	 */
	public static double topicFactor = 0.4;
	public static double taskFactor = 0.4;
	public static double locationFactor = 0.2;
	
	/**
	 * 返回给用户的结果数
	 */
	public static int topN = 20;
	/**
	 * 相关文档的推荐步长
	 */
	public static int step = 2;
	/**
	 * 推荐的文档数
	 */
	public static int recommendedDocNumber = 5;
	
	/**
	 * 待索引的目标目录列表
	 */
	public static List<String> targetDirectories = new ArrayList<String>();
	public static String targetDirectoryValues = null;
	/**
	 * 应用程序根目录路径。路径由反斜杠/间隔，路径末尾以反斜杠/结束
	 */
	public static String rootPath = "";
	
	/**
	 * 用户行为日志文件
	 */
	public static String userActivityLogFile = "F:/user_activity_log/log.csv";
	/**
	 * LDA目录
	 */
	public static String LDAPath = "";
	/**
	 * 用于LDA建模的docID列表文件 (固定名称)
	 */
	public static String LDAdocIDListFileName = "00000000.map";
	/**
	 * 用于LDA建模的doc数据文件 (固定名称)
	 */
	public static String LDADataFileName = "00000000";
	
	
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
	public static double taskSimilarityThreshold = 0.5f;
	
	
	public static TermLemmatizer lemmatizer = null;
	public static StopWordFilter stopWordFilter = null;
	public static WordSegmentation wordSegmentor = null;
	
	public static void init(){};
	
	
	static{
		System.out.println("系统正在初始化...");
		String formatSuffixValue = "";
		String formatSuffixSplits[] = null;
		String classValue = "";
		String classSplits[] = null;
		String formatClassValue = "";
		String formatClassSplits[] = null;
		
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
		 * 加载LDA目录的
		 */
		LDAPath = rootPath + "LDA/";//PropertiesManager.getKeyValue("LDA.dir");
		
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
		
		
		// 加载建模参数
		maxSizeAllowed_PDF = Short.parseShort(PropertiesManager.getKeyValue("maxSizeAllowed_PDF"));				
		maxSizeAllowed_TXT = Short.parseShort(PropertiesManager.getKeyValue("maxSizeAllowed_TXT"));
		maxFileCountPreDirectory = Integer.parseInt(PropertiesManager.getKeyValue("maxFileCountPreDirectory"));
		validViewPeriod = Integer.parseInt(PropertiesManager.getKeyValue("validViewPeriod"));
		intervalTaskPeriod = Integer.parseInt(PropertiesManager.getKeyValue("intervalTaskPeriod"));
		taskSimilarityThreshold = Double.parseDouble(PropertiesManager.getKeyValue("taskSimilarityThreshold"));
		klUpbound = Double.parseDouble(PropertiesManager.getKeyValue("klUpbound"));
		dMAX_GAMA = Integer.parseInt(PropertiesManager.getKeyValue("dMAX_GAMA"));
		topicFactor = Double.parseDouble(PropertiesManager.getKeyValue("topicFactor"));
		taskFactor = Double.parseDouble(PropertiesManager.getKeyValue("taskFactor"));
		locationFactor = Double.parseDouble(PropertiesManager.getKeyValue("locationFactor"));
		topN = Integer.parseInt(PropertiesManager.getKeyValue("topN"));
		step = Integer.parseInt(PropertiesManager.getKeyValue("step"));
		recommendedDocNumber = Integer.parseInt(PropertiesManager.getKeyValue("recommendedDocNumber"));
		userActivityLogFile = PropertiesManager.getKeyValue("userActivityLogFile");
		
		
		// 初始化 停用词，分词，词形归并对象
		lemmatizer = new TermLemmatizer(); 
		stopWordFilter = new StopWordFilter();
		wordSegmentor = new WordSegmentation();
	}
	
	public static JSONObject getParamsInJSON(){
		JSONObject obj = new JSONObject();
		obj.accumulate("target_directory", targetDirectoryValues);
		obj.accumulate("pdf_size", maxSizeAllowed_PDF);
		obj.accumulate("txt_size", maxSizeAllowed_TXT);
		obj.accumulate("directory_size", maxFileCountPreDirectory);
		obj.accumulate("duration", validViewPeriod);
		obj.accumulate("interval", intervalTaskPeriod);
		obj.accumulate("task_similarity", taskSimilarityThreshold);
		obj.accumulate("kl", klUpbound);
		obj.accumulate("transfer_length", dMAX_GAMA);
		obj.accumulate("topic_factor", topicFactor);
		obj.accumulate("task_factor", taskFactor);
		obj.accumulate("location_factor", locationFactor);
		obj.accumulate("result_number", topN);
		obj.accumulate("recommend_step", step);
		obj.accumulate("recommend_size", recommendedDocNumber);
		obj.accumulate("log_file", userActivityLogFile);
		return obj;
	}
	
	public static void updateParams(
			String targetDirectoryValues,
			short maxSizeAllowed_PDF,
			short maxSizeAllowed_TXT,
			int maxFileCountPreDirectory,
			int validViewPeriod,
			int intervalTaskPeriod,
			Double taskSimilarityThreshold,
			double klUpbound,
			int dMAX_GAMA,
			double topicFactor,
			double taskFactor,
			double locationFactor,
			int topN,
			int step,
			int recommendedDocNumber,
			String userActivityLogFile
			){
		SystemConfiguration.targetDirectoryValues = targetDirectoryValues;
		String[]targetDirectorySplits = SystemConfiguration.targetDirectoryValues.split(",");
		SystemConfiguration.targetDirectories.clear();
		for(int i=0; i<targetDirectorySplits.length; i++){
			SystemConfiguration.targetDirectories.add(targetDirectorySplits[i]);
			System.out.println(targetDirectorySplits[i]);
		}
		
		SystemConfiguration.maxSizeAllowed_PDF = maxSizeAllowed_PDF;
		SystemConfiguration.maxSizeAllowed_TXT = maxSizeAllowed_TXT;
		SystemConfiguration.maxFileCountPreDirectory = maxFileCountPreDirectory;
		SystemConfiguration.validViewPeriod = validViewPeriod;
		SystemConfiguration.intervalTaskPeriod = intervalTaskPeriod;
		SystemConfiguration.taskSimilarityThreshold = taskSimilarityThreshold;
		SystemConfiguration.klUpbound = klUpbound;
		SystemConfiguration.dMAX_GAMA = dMAX_GAMA;
		SystemConfiguration.topicFactor = topicFactor;
		SystemConfiguration.taskFactor = taskFactor;
		SystemConfiguration.locationFactor = locationFactor;
		SystemConfiguration.topN = topN;
		SystemConfiguration.step = step;
		SystemConfiguration.recommendedDocNumber = recommendedDocNumber;
		SystemConfiguration.userActivityLogFile = userActivityLogFile;
	}
	
	public static void main(String args[]){
		String s = "dfsf\\dfs\\sdfs\\";
		
		System.out.println(s.replaceAll("\\\\", "/"));
	}
}
