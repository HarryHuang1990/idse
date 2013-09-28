package cn.iscas.idse.index;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	
	//�̳߳ض���
	private ThreadPoolExecutor pool = null;
	private int size;
	
	public ThreadPool(int size){
		this.size = size;
		//��ʼ���̳߳�
		pool = new ThreadPoolExecutor(
				size, 
				size,   
                0L, TimeUnit.SECONDS,   
                new ArrayBlockingQueue<Runnable>(2*size),
                new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	//������߳���
	public synchronized int getActiveThreadCount(){
		return pool.getActiveCount();
	}
	
	//��ɵ�������
	public synchronized long getCompletedTaskCount(){
		return pool.getCompletedTaskCount();
	}
	
	//�ڻ�������еȴ���������
	public synchronized int getWorkQueueSize(){
		return pool.getQueue().size();
	}
	
	//�ж��̳߳��Ƿ�Ϊ�գ�û�м�����̼߳���ʾΪ�գ�
	public synchronized boolean isEmpty(){
		return pool.getActiveCount() == 0;
	}
	
	//��������Ƿ�����
	public synchronized boolean isWorkQueueFull(){
		return pool.getQueue().size() >= 2*size;
	}
	
	//ִ������
	public void execute(Runnable task){
		pool.execute(task);
	}
	
	public void stop(){
		pool.shutdown();
	}
	
}
