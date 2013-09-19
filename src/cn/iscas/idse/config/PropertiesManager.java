package cn.iscas.idse.config;

import java.util.Properties;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 配置文件操作类，用于读、写、更新配置文件
 * @author Harry Huang
 *
 */
public class PropertiesManager {
	private static final String APP_PATH = "/configure.properties", CHAR_SET = "UTF-8";
	private static Properties pApp = new Properties();

	private static boolean flag = false;

	public static String getApp(String sKey) {
		if(!flag) loadAll();
		return pApp.getProperty(sKey);
	}

	public static void loadAll() {
		flag = true;
		load(pApp, APP_PATH);
	}

	private static boolean load(Properties p, String sPath) {
		InputStream is = null;
		InputStreamReader isr = null;

		assert(!(sPath == null || sPath.isEmpty()));

		is = PropertiesManager.class.getResourceAsStream(sPath);
		
		try {
			isr = new InputStreamReader(is, CHAR_SET);
/*The load method just overwrites existing key-value pairs, 
so clear is necessary for refreshing.
*/
			p.clear();
			p.load(isr);
		} catch(Exception e) {
			p.clear();
			e.printStackTrace();
			return false;
		} finally {
//The InputStream used by InputStreamReader will be closed too.
			if(isr != null)
				try {
					isr.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
		}
		return true;
	}
}