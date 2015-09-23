package org.seers.bugrepanalyzer.threads;

import java.util.concurrent.CountDownLatch;

import org.seers.bugrepanalyzer.processor.IssuesProcessor;

public class CommandLatchRunnable implements Runnable {

	private CountDownLatch cntDwnLatch;
	private IssuesProcessor proc;

	public CommandLatchRunnable(IssuesProcessor proc, CountDownLatch cntDwnLatch) {
		this.cntDwnLatch = cntDwnLatch;
		this.proc = proc;
	}

	@Override
	public void run() {

		try {
			proc.downloadIssues();
		} catch (Exception e) {
			e.printStackTrace();
		}
		cntDwnLatch.countDown();

	}

}
