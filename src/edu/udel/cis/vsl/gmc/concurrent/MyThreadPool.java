package edu.udel.cis.vsl.gmc.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class MyThreadPool {
	private int parallelism = Runtime.getRuntime().availableProcessors();
	
	private int waitingNum = 0;
	
	private final int maxNumOfThread = 2 * Runtime.getRuntime().availableProcessors();
	
	private ForkJoinPool pool;
	
	public MyThreadPool(int parallelism){
		this.parallelism = parallelism;
		pool = new ForkJoinPool(maxNumOfThread);
	}
	
	public MyThreadPool(){
		pool = new ForkJoinPool(parallelism);
	}
	
	public synchronized void incrementWaiting(){
		waitingNum++;
	}
	
	public synchronized void decrementWaiting(){
		waitingNum--;
	}
	
	public int getActiveNum(){
		return pool.getActiveThreadCount() - waitingNum;
	}
	
	public int getRunningNum(){
		return pool.getActiveThreadCount();
	}
	
	public void submit(ForkJoinTask<Integer> task){
		pool.submit(task);
	}
	
	public boolean isQuiescent(){
		return pool.isQuiescent();
	}
	
	public int getMaxNumOfThread(){
		return maxNumOfThread;
	}
	
	public void shutdown(){
		pool.shutdown();
	}
}
