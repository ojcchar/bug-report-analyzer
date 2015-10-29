package seers.bugrepanalyzer.processor;

import java.io.File;

public interface IssuesProcessor {
	File processIssues() throws Exception;

	String getName();
}
