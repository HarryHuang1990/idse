package cn.iscas.idse.index;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	
	//线程池对象
	private ThreadPoolExecutor pool = null;
	private int size;
	
	public ThreadPool(int size){
		this.size = size;
		//初始化线程池
		pool = new ThreadPoolExecutor(
				size, 
				size,   
                0L, TimeUnit.SECONDS,   
                new ArrayBlockingQueue<Runnable>(2*size),
                new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	//激活的线程数
	public synchronized int getActiveThreadCount(){
		return pool.getActiveCount();
	}
	
	//完成的任务数
	public synchronized long getCompletedTaskCount(){
		return pool.getCompletedTaskCount();
	}
	
	//在缓冲队列中等待的任务数
	public synchronized int getWorkQueueSize(){
		return pool.getQueue().size();
	}
	
	//判断线程池是否为空（没有激活的线程即表示为空）
	public synchronized boolean isEmpty(){
		return pool.getActiveCount() == 0;
	}
	
	//缓冲队列是否已满
	public synchronized boolean isWorkQueueFull(){
		return pool.getQueue().size() >= 2*size;
	}
	
	//执行任务
	public void execute(Runnable task){
		pool.execute(task);
	}
	
	public void stop(){
		pool.shutdown();
	}
	
}
