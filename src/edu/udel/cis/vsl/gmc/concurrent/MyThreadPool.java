package edu.udel.cis.vsl.gmc.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * The threadPool That is used to manage the threads used in the algorithm.
 * 
 * @author yanyihao
 *
 */
public class MyThreadPool {
	/**
	 * The upper bound of active threads that are executing.
	 */
	private int parallelism = Runtime.getRuntime().availableProcessors();

	/**
	 * The number of threads that are waiting.
	 */
	private int waitingNum = 0;

	/**
	 * The max number of threads which includes all threads that are executing
	 * and waiting.
	 */
	private final int maxNumOfThread = 2 * Runtime.getRuntime().availableProcessors();

	private ForkJoinPool pool;

	public MyThreadPool(int parallelism) {
		this.parallelism = parallelism;
		pool = new ForkJoinPool(maxNumOfThread);
	}

	public MyThreadPool() {
		pool = new ForkJoinPool(parallelism);
	}

	/**
	 * Increase the counter for waiting threads.
	 */
	public synchronized void incrementWaiting() {
		waitingNum++;
	}

	/**
	 * Decrease the counter for waiting threads.
	 */
	public synchronized void decrementWaiting() {
		waitingNum--;
	}

	/**
	 * @return the number of threads that are currently executing (not include
	 *         waiting threads).
	 */
	public int getActiveNum() {
		return pool.getActiveThreadCount() - waitingNum;
	}
	
	/**
	 * @return the total number of threads (include threads that are waiting).
	 */
	public int getRunningNum() {
		return pool.getActiveThreadCount();
	}
	
	public void submit(ForkJoinTask<Integer> task) {
		pool.submit(task);
	}

	public boolean isQuiescent() {
		return pool.isQuiescent();
	}
	
	public int getMaxNumOfThread() {
		return maxNumOfThread;
	}

	public void shutdown() {
		pool.shutdown();
	}
}
