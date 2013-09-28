package cn.iscas.idse.demo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;

public class FileReadDemo {
	public static void main(String[]args) throws IOException{
//		readByte("D:\\My DBank\\����&���߰�\\morphadorner-2009-04-30\\data\\gate\\ANNIE\\resources\\tokeniser\\chinesetokeniser\\bothlexu8.txt");
		readBytes("D:\\My DBank\\����&���߰�\\morphadorner-2009-04-30\\data\\gate\\ANNIE\\resources\\tokeniser\\chinesetokeniser\\bothlexu8.txt");
//		readBuff("D:\\My DBank\\����&���߰�\\morphadorner-2009-04-30\\data\\gate\\ANNIE\\resources\\tokeniser\\chinesetokeniser\\bothlexu8.txt");
//		readFileByRandomAccess("D:\\My DBank\\����&���߰�\\morphadorner-2009-04-30\\data\\gate\\ANNIE\\resources\\tokeniser\\chinesetokeniser\\bothlexu8.txt");
		
	}
	
	public static void readByte(String fileName){
		long start = System.currentTimeMillis();
        File file = new File(fileName);
        InputStream in = null;
        try {
            System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
            // һ�ζ�һ���ֽ�
            in = new FileInputStream(file);
            int tempbyte;
            ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());  
            while ((tempbyte = in.read()) != -1) {
                out.write(tempbyte);
            }
            System.out.println(out.toString());;
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
		long end = System.currentTimeMillis();
		System.out.println("��ʱ��:"+((end-start)*1.0/1000) + "s");
	}
	
	public static void readBytes(String fileName){
		long start = System.currentTimeMillis();
		File file = new File(fileName);
        InputStream in = null;
		try {
            System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�");
            // һ�ζ�����ֽ�
            byte[] tempbytes = new byte[100];
            int byteread = 0;
            in = new FileInputStream(fileName);
            String content = "";
            ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());  
            // �������ֽڵ��ֽ������У�bytereadΪһ�ζ�����ֽ���
            while ((byteread = in.read(tempbytes)) != -1) {
                out.write(tempbytes, 0, byteread); 
//                content += new String(tempbytes);
            }
            System.out.println(out.toString());;
            
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
		long end = System.currentTimeMillis();
		System.out.println("��ʱ��:"+((end-start)*1.0/1000) + "s");
	}
	
	public static void readBuff(String fileName){
		long start = System.currentTimeMillis();
		File file = new File(fileName);
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String content = "";
			String line;
			while((line = reader.readLine())!=null){
//				 System.out.println(line);
				 content += line;
			}
			System.out.println(content);
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("��ʱ��:"+((end-start)*1.0/1000) + "s");
	}
	
	public static void readFileByRandomAccess(String fileName) {
		long start = System.currentTimeMillis();
        RandomAccessFile randomFile = null;
        try {
            System.out.println("�����ȡһ���ļ����ݣ�");
            // ��һ����������ļ�������ֻ����ʽ
            randomFile = new RandomAccessFile(fileName, "r");
            // �ļ����ȣ��ֽ���
            long fileLength = randomFile.length();
            // ���ļ�����ʼλ��
            int beginIndex = (fileLength > 4) ? 4 : 0;
            // �����ļ��Ŀ�ʼλ���Ƶ�beginIndexλ�á�
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            // һ�ζ�10���ֽڣ�����ļ����ݲ���10���ֽڣ����ʣ�µ��ֽڡ�
            // ��һ�ζ�ȡ���ֽ�������byteread
            String content = "";
            while ((byteread = randomFile.read(bytes)) != -1) {
//                System.out.write(bytes, 0, byteread);
                content += new String(bytes);
            }
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                }
            }
        }
        long end = System.currentTimeMillis();
		System.out.println("��ʱ��:"+((end-start)*1.0/1000) + "s");
    }
	
	/**
     * ��ʾ�������л�ʣ���ֽ���
     */
    private static void showAvailableBytes(InputStream in) {
        try {
            System.out.println("��ǰ�ֽ��������е��ֽ���Ϊ:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ���ַ�Ϊ��λ��ȡ�ļ��������ڶ��ı������ֵ����͵��ļ�
     */
    public static void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        try {
            System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
            // һ�ζ�һ���ַ�
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // ����windows�£�\r\n�������ַ���һ��ʱ����ʾһ�����С�
                // ������������ַ��ֿ���ʾʱ���ỻ�����С�
                // ��ˣ����ε�\r����������\n�����򣬽������ܶ���С�
                if (((char) tempchar) != '\r') {
                    System.out.print((char) tempchar);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�");
            // һ�ζ�����ַ�
            char[] tempchars = new char[30];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            // �������ַ����ַ������У�charreadΪһ�ζ�ȡ�ַ���
            while ((charread = reader.read(tempchars)) != -1) {
                // ͬ�����ε�\r����ʾ
                if ((charread == tempchars.length)
                        && (tempchars[tempchars.length - 1] != '\r')) {
                    System.out.print(tempchars);
                } else {
                    for (int i = 0; i < charread; i++) {
                        if (tempchars[i] == '\r') {
                            continue;
                        } else {
                            System.out.print(tempchars[i]);
                        }
                    }
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    
    /**
     * A����׷���ļ���ʹ��RandomAccessFile
     */
    public static void appendMethodA(String fileName, String content) {
        try {
            // ��һ����������ļ���������д��ʽ
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // �ļ����ȣ��ֽ���
            long fileLength = randomFile.length();
            //��д�ļ�ָ���Ƶ��ļ�β��
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * B����׷���ļ���ʹ��FileWriter
     */
    public static void appendMethodB(String fileName, String content) {
        try {
            //��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
