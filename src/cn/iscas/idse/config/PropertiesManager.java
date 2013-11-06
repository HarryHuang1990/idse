package cn.iscas.idse.config;

import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * �����ļ������࣬���ڶ���д�����������ļ�
 * @author Harry Huang
 *
 */
public class PropertiesManager {
	
    /**
     * �����ļ���·��
     */
    static String profilepath = System.getProperty("user.dir") + "/src/configure.properties";
    /**
    * ���þ�̬����
    */
    private static Properties props = new Properties();
    static {
        try {
            props.load(new FileInputStream(profilepath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {        
            System.exit(-1);
        }
    }

    /**
    * ��ȡ�����ļ�����Ӧ����ֵ
    * @param key
    * @return String
    */
    public static String getKeyValue(String key) {
        return props.getProperty(key);
    }

    /**
    * ��������key��ȡ������ֵvalue
    * @param filePath �����ļ�·��
    * @param key ����
    */ 
    public static String readValue(String filePath, String key) {
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            String value = props.getProperty(key);
            System.out.println(key +"����ֵ�ǣ�"+ value);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
    * ���£�����룩һ��properties��Ϣ(���������ֵ)
    * ����������Ѿ����ڣ����¸�������ֵ��
    * ��������������ڣ�����һ�Լ�ֵ��
    * @param keyname ����
    * @param keyvalue ��ֵ
    */ 
    public static void writeProperties(String keyname,String keyvalue) {        
        try {
            // ���� Hashtable �ķ��� put��ʹ�� getProperty �����ṩ�����ԡ�
            // ǿ��Ҫ��Ϊ���Եļ���ֵʹ���ַ���������ֵ�� Hashtable ���� put �Ľ����
            OutputStream fos = new FileOutputStream(profilepath);
            props.setProperty(keyname, keyvalue);
            // ���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��
            // ���� Properties ���е������б�����Ԫ�ضԣ�д�������
            props.store(fos, "Update '" + keyname + "' value");
        } catch (IOException e) {
            System.err.println("�����ļ����´���");
        }
    }

    /**
    * ����properties�ļ��ļ�ֵ��
    * ����������Ѿ����ڣ����¸�������ֵ��
    * ��������������ڣ�����һ�Լ�ֵ��
    * @param keyname ����
    * @param keyvalue ��ֵ
    */ 
    public static void updateProperties(String keyname,String keyvalue) {
        try {
            // ���� Hashtable �ķ��� put��ʹ�� getProperty �����ṩ�����ԡ�
            // ǿ��Ҫ��Ϊ���Եļ���ֵʹ���ַ���������ֵ�� Hashtable ���� put �Ľ����
            OutputStream fos = new FileOutputStream(profilepath);          
            props.setProperty(keyname, keyvalue);
            // ���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��
            // ���� Properties ���е������б�����Ԫ�ضԣ�д�������
            props.store(fos, "Update '" + keyname + "' value");
        } catch (IOException e) {
            System.err.println("�����ļ����´���");
        }
    }

	
	public static void main(String args[]){
//		writeProperties("posting_title_id", "12345");
		System.out.println(getKeyValue("posting_title_id"));
	}
}