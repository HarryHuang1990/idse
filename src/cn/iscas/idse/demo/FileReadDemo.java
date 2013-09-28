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
//		readByte("D:\\My DBank\\工具&工具包\\morphadorner-2009-04-30\\data\\gate\\ANNIE\\resources\\tokeniser\\chinesetokeniser\\bothlexu8.txt");
		readBytes("D:\\My DBank\\工具&工具包\\morphadorner-2009-04-30\\data\\gate\\ANNIE\\resources\\tokeniser\\chinesetokeniser\\bothlexu8.txt");
//		readBuff("D:\\My DBank\\工具&工具包\\morphadorner-2009-04-30\\data\\gate\\ANNIE\\resources\\tokeniser\\chinesetokeniser\\bothlexu8.txt");
//		readFileByRandomAccess("D:\\My DBank\\工具&工具包\\morphadorner-2009-04-30\\data\\gate\\ANNIE\\resources\\tokeniser\\chinesetokeniser\\bothlexu8.txt");
		
	}
	
	public static void readByte(String fileName){
		long start = System.currentTimeMillis();
        File file = new File(fileName);
        InputStream in = null;
        try {
            System.out.println("以字节为单位读取文件内容，一次读一个字节：");
            // 一次读一个字节
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
		System.out.println("总时间:"+((end-start)*1.0/1000) + "s");
	}
	
	public static void readBytes(String fileName){
		long start = System.currentTimeMillis();
		File file = new File(fileName);
        InputStream in = null;
		try {
            System.out.println("以字节为单位读取文件内容，一次读多个字节：");
            // 一次读多个字节
            byte[] tempbytes = new byte[100];
            int byteread = 0;
            in = new FileInputStream(fileName);
            String content = "";
            ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());  
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
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
		System.out.println("总时间:"+((end-start)*1.0/1000) + "s");
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
		System.out.println("总时间:"+((end-start)*1.0/1000) + "s");
	}
	
	public static void readFileByRandomAccess(String fileName) {
		long start = System.currentTimeMillis();
        RandomAccessFile randomFile = null;
        try {
            System.out.println("随机读取一段文件内容：");
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(fileName, "r");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 读文件的起始位置
            int beginIndex = (fileLength > 4) ? 4 : 0;
            // 将读文件的开始位置移到beginIndex位置。
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
            // 将一次读取的字节数赋给byteread
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
		System.out.println("总时间:"+((end-start)*1.0/1000) + "s");
    }
	
	/**
     * 显示输入流中还剩的字节数
     */
    private static void showAvailableBytes(InputStream in) {
        try {
            System.out.println("当前字节输入流中的字节数为:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    public static void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        try {
            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r') {
                    System.out.print((char) tempchar);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("以字符为单位读取文件内容，一次读多个字节：");
            // 一次读多个字符
            char[] tempchars = new char[30];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            // 读入多个字符到字符数组中，charread为一次读取字符数
            while ((charread = reader.read(tempchars)) != -1) {
                // 同样屏蔽掉\r不显示
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
     * A方法追加文件：使用RandomAccessFile
     */
    public static void appendMethodA(String fileName, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * B方法追加文件：使用FileWriter
     */
    public static void appendMethodB(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
