package org.seers.bugrepanalyzer.json;

import java.util.List;

public class JSONIssues {

	private int startAt;
	private int maxResults;
	private int total;
	private List<JSONIssue> issues;

	public int getStartAt() {
		return startAt;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public int getTotal() {
		return total;
	}

	public List<JSONIssue> getIssues() {
		return issues;
	}

}
