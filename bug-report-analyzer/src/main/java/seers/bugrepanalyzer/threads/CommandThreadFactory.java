package seers.bugrepanalyzer.threads;

import java.util.concurrent.ThreadFactory;

public class CommandThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r);
	}
}
