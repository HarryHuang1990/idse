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
	 * ͣ�ôʱ�-����������ࣩӳ���
	 */
	public static Map<String, String> stopWordPluginMap = new HashMap<String, String>();
	/**
	 * Berkeley DB manager
	 */
	public static DBManager database = null;
	/**
	 * �ļ�������������
	 */
	public static Map<String, FileType> fileTypeBuff = new HashMap<String, FileType>();
	/**
	 * �ָ����б�
	 */
	public static String seperatorRegx = "[-_:]"; 
	
	/**
	 * ���������PDF�ı���С���ޣ�K��
	 */
	public static short maxSizeAllowed_PDF = 5000;
	
	/**
	 * ���������text�ı���С���ޣ�K��
	 */
	public static short maxSizeAllowed_TXT = 500;
	
	/**
	 * ����ļ����е��ļ�������150�����򲻶�����ļ����µ���Щ�ļ����з�������������ļ��У���Ҫ�����жϡ�
	 */
	public static int maxFileCountPreDirectory = 150;
	
	/**
	 * ��ЧKLֵ���ޣ�����klֵ�������޵�����document,������topic�������
	 */
	public static double klUpbound = 0.3;
	
	/**
	 * �ļ�֮����л��������ޣ������л�����D(a,b)�������޵�����document��������location�������
	 */
	public static int dMAX_GAMA = 1;
	
	/**
	 * �����ֵ÷ֶ��ܹ�ϵ�÷ֵĹ��ױ���
	 */
	public static double topicFactor = 0.4;
	public static double taskFactor = 0.4;
	public static double locationFactor = 0.2;
	
	/**
	 * ���ظ��û��Ľ����
	 */
	public static int topN = 20;
	/**
	 * ����ĵ����Ƽ�����
	 */
	public static int step = 2;
	/**
	 * �Ƽ����ĵ���
	 */
	public static int recommendedDocNumber = 5;
	
	/**
	 * ��������Ŀ��Ŀ¼�б�
	 */
	public static List<String> targetDirectories = new ArrayList<String>();
	public static String targetDirectoryValues = null;
	/**
	 * Ӧ�ó����Ŀ¼·����·���ɷ�б��/�����·��ĩβ�Է�б��/����
	 */
	public static String rootPath = "";
	
	/**
	 * �û���Ϊ��־�ļ�
	 */
	public static String userActivityLogFile = "F:/user_activity_log/log.csv";
	/**
	 * LDAĿ¼
	 */
	public static String LDAPath = "";
	/**
	 * ����LDA��ģ��docID�б��ļ� (�̶�����)
	 */
	public static String LDAdocIDListFileName = "00000000.map";
	/**
	 * ����LDA��ģ��doc�����ļ� (�̶�����)
	 */
	public static String LDADataFileName = "00000000";
	
	
	/**
	 * ����ʱ����-���ڽ綨��Ч���ļ���������Щ��֮���ٺ̵ܶ�ʱ���ھ͹رյ��ļ���
	 * ���ǲ���Ϊ���Ǿ��к�ǿ��Ŀ���ԡ�����˵�����������ҳ��ʱ�򣬶������Ǹ���Ȥ�ģ�
	 * ����˵��Ҫ��ҳ�����ǻ��ע�ܳ���ʱ�䣬���Ƕ���һЩ���õ�ҳ�����˵���ǿ���ɨ
	 * һ�۾͹ر��ˣ�ǰ��򿪵�ʱ����ܾͼ����֡������Ч���ļ���ʱ�������øò�����
	 * ���壨��λΪs��
	 */
	public static int validViewPeriod = 10; 
	/**
	 * taskʱ����-���ڶ���task֮���ʱ���ȣ�������������ļ�֮����ʱ�䳬��10���ӣ�
	 * ����Ϊ������һ���µ�task��
	 */
	public static int intervalTaskPeriod = 600;
	
	/**
	 * ����task�ϲ������ƶ���ֵ
	 */
	public static double taskSimilarityThreshold = 0.5f;
	
	
	public static TermLemmatizer lemmatizer = null;
	public static StopWordFilter stopWordFilter = null;
	public static WordSegmentation wordSegmentor = null;
	
	public static void init(){};
	
	
	static{
		System.out.println("ϵͳ���ڳ�ʼ��...");
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
		 * �����ļ����ͺ�׺�Ͳ����������
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
		 * ���ش�������Ŀ��Ŀ¼�б� 
		 */
		targetDirectoryValues = PropertiesManager.getKeyValue("target.directory");
		targetDirectorySplits = targetDirectoryValues.split(",");
		for(int i=0; i<targetDirectorySplits.length; i++){
			targetDirectories.add(targetDirectorySplits[i]);
			System.out.println(targetDirectorySplits[i]);
		}
		
		/*
		 *��ȡӦ�ó����Ŀ¼��ַ 
		 */
		rootPath = Converter.convertBackSlashToSlash(System. getProperty("user.dir"));
		rootPath += "/";
		
		/*
		 * ����LDAĿ¼��
		 */
		LDAPath = rootPath + "LDA/";//PropertiesManager.getKeyValue("LDA.dir");
		
		/*
		 * �ж�����Ŀ���Ƿ�ı�
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
		 * �����ļ���ʽ�����ļ���ʽ�б�
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
		
		
		// ���ؽ�ģ����
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
		
		
		// ��ʼ�� ͣ�ôʣ��ִʣ����ι鲢����
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
