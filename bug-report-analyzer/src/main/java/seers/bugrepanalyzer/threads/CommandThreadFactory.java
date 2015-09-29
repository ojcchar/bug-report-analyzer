package seers.bugrepanalyzer.threads;

import java.util.concurrent.ThreadFactory;

public class CommandThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {

		if (r instanceof CommandLatchRunnable) {
			return new Thread(r, ((CommandLatchRunnable) r).getName());
		}

		return new Thread(r);
	}
}
