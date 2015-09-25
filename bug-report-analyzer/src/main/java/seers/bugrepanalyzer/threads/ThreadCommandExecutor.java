package seers.bugrepanalyzer.threads;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadCommandExecutor {

	private static final int DEF_CORE_POOL_SIZE = 15;
	private static final int DEF_KEEPAL_TIME = 20;

	private int corePoolSize = DEF_CORE_POOL_SIZE;
	private int maximumPoolSize = corePoolSize;
	private long keepAliveTime = DEF_KEEPAL_TIME;
	private BlockingQueue<Runnable> workQueue;
	private ThreadFactory threadFactory;
	private RejectedExecutionHandler handler;

	private ThreadPoolExecutor executor;

	private static ThreadCommandExecutor thrdCommExecutor;

	private ThreadCommandExecutor() {
		initThreadExecutor();
	}

	public static synchronized ThreadCommandExecutor getInstance() {
		if (thrdCommExecutor == null) {
			thrdCommExecutor = new ThreadCommandExecutor();
		}
		return thrdCommExecutor;
	}

	public void setCorePoolSize(int size) {
		executor.setCorePoolSize(size);
		executor.setMaximumPoolSize(size);
	}

	private void initThreadExecutor() {
		// handler = new ThreadPoolExecutor.AbortPolicy();
		// handler = new ThreadPoolExecutor.DiscardOldestPolicy();
		// handler = new ThreadPoolExecutor.DiscardPolicy();
		handler = new ThreadPoolExecutor.CallerRunsPolicy();
		workQueue = new ArrayBlockingQueue<Runnable>(maximumPoolSize);
		threadFactory = new CommandThreadFactory();

		executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES, workQueue,
				threadFactory, handler);
	}

	public void exeucuteCommRunnable(CommandLatchRunnable comm) {
		executor.execute(comm);
	}

	public long getTaskcount() {
		return executor.getTaskCount();
	}

	public void shutdown() {
		executor.shutdown();
	}

	public int getCorePoolSize() {
		return executor.getCorePoolSize();
	}

}
