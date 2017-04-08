package seers.bugrepanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.Gson;

import net.quux00.simplecsv.CsvWriter;
import net.quux00.simplecsv.CsvWriterBuilder;
import seers.appcore.threads.CommandLatchRunnable;
import seers.appcore.threads.ThreadCommandExecutor;
import seers.appcore.threads.processor.ThreadException;
import seers.appcore.threads.processor.ThreadProcessor;
import seers.appcore.utils.ExceptionUtils;
import seers.bugrepanalyzer.json.JSONIssue;
import seers.bugrepanalyzer.json.JSONIssueField;
import seers.bugrepanalyzer.json.JSONIssueFields;

public class MainHMTLBugzillaRetriever {
	private static String domain = "https://bugs.eclipse.org/bugs/";

	static String outputFolder = "C:/Users/ojcch/Documents/Projects/Discourse_query_reformulation/data/query_quality_data/bugs2";

	public static void main(String[] args) throws Exception {

		String project = "eclipse-2.0";
		int numBugs = 389227;

		System.out.println(project + ": " + numBugs);

		processIssues(project, numBugs);
	}

	private static File processIssues(String project, int numIssues) throws Exception {

		// out file
		File file = new File(outputFolder + File.separator + project + "_Queries_info.txt");
		try (CsvWriter csvw = new CsvWriterBuilder(new FileWriter(file)).separator(';').build()) {

			// get the issues
			int numResults = 200;
			List<BZIssuesRetriever> procs = new ArrayList<>();
			for (int currentBug = 1; currentBug <= numIssues; currentBug += numResults) {
				BZIssuesRetriever proc = new BZIssuesRetriever(project, currentBug, numResults, numIssues, csvw);
				procs.add(proc);
			}

			ThreadCommandExecutor executor = new ThreadCommandExecutor();
			executor.setCorePoolSize(50);

			try {
				// run the threads
				CountDownLatch cntDwnLatch = new CountDownLatch(procs.size());
				for (BZIssuesRetriever proc : procs) {
					executor.executeCommRunnable(new CommandLatchRunnable(proc, cntDwnLatch));

				}
				cntDwnLatch.await();
			} finally {
				executor.shutdown();
			}

		}

		return file;
	}

	public static class BZIssuesRetriever implements ThreadProcessor {

		private String project;
		private int numResults;
		private int totalResults;
		private CsvWriter csvw;
		private int currentBug;
		Gson gson = new Gson();

		public BZIssuesRetriever(String project, int currentBug, int numResults, int totalResults, CsvWriter csvw) {
			super();
			this.project = project;
			this.currentBug = currentBug;
			this.numResults = numResults;
			this.totalResults = totalResults;
			this.csvw = csvw;
		}

		@Override
		public String getName() {
			return project + " - currentBug: " + currentBug;
		}

		@Override
		public void processJob() throws ThreadException {
			
			System.out.println("Processing "+currentBug);

			for (int bugId = currentBug; bugId < currentBug + numResults; bugId++) {
				try {
					JSONIssue jsonIssue = parseIssue(bugId);
					
					if (jsonIssue==null) {
						continue;
					}

					// System.out.println(jsonIssue);
					List<String> nextLine = jsonIssue.getCSVLine();
					csvw.writeNext(nextLine);
				} catch (Exception e) {
					System.err.println("Error for bug: "+bugId);
					e.printStackTrace();
					ThreadException e2 = new ThreadException(e.getMessage());
					ExceptionUtils.addStackTrace(e, e2);
					throw e2;
				}

			}

		}

		private JSONIssue parseIssue(int bugId) throws IOException {
			String url = domain + "show_bug.cgi?id=" + bugId;
			Document doc = Jsoup.connect(url).get();
			
			Element elem = doc.getElementById("error_msg");
			
			if (elem!= null) {
				
				return null;
				
			}

			String summary = doc.getElementById("short_desc_nonedit_display").text();
			String description = doc.getElementById("c0").getElementsByTag("pre").get(0).text();

			String key = String.valueOf(bugId);

			JSONIssueField issueType = null;
			List<JSONIssueField> fixVersions = null;
			JSONIssueField resolution = null;
			Date resolutionDate = null;
			Date created = null;
			Date updated = resolutionDate;
			JSONIssueField status = null;
			JSONIssueField creator = null;
			JSONIssueField reporter = null;

			JSONIssueFields fields = new JSONIssueFields(issueType, fixVersions, resolution, resolutionDate, created,
					null, null, updated, status, null, description, summary, creator, reporter, null);

			JSONIssue issue = new JSONIssue(bugId, key, fields);

			return issue;
		}

	}

}
