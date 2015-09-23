package org.seers.bugrepanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.seers.bugrepanalyzer.processor.HttpJiraUtils;
import org.seers.bugrepanalyzer.processor.IssuesProcessor;
import org.seers.bugrepanalyzer.threads.CommandLatchRunnable;
import org.seers.bugrepanalyzer.threads.ThreadCommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.quux00.simplecsv.CsvWriter;
import net.quux00.simplecsv.CsvWriterBuilder;

public class MainJiraRetriever {

	private static final String JIRA_PATH_SEARCH = "/jira/rest/api/2/search?";
	private static final Logger LOGGER = LoggerFactory.getLogger(MainJiraRetriever.class);

	private static String domain;
	private static String outputFolder;

	public static void main(String[] args) throws IOException {

		domain = args[0];
		outputFolder = args[1];
		String[] projects = args[2].split(",");

		for (String project : projects) {
			project = project.trim();

			processProject(project);
		}

	}

	private static void processProject(String project) {
		try {

			LOGGER.info("Project: " + project);

			// get the total # of issues
			int numIssues = getNumIssues(project);

			LOGGER.info("Total # of issues: " + numIssues);

			// create out folder
			File outDir = new File(outputFolder + File.separator + "json_data");
			FileUtils.forceMkdir(outDir);

			// download the issues
			File issuesFile = downloadIssues(project, numIssues, outDir);

			LOGGER.info("Issues processed in " + issuesFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static File downloadIssues(String project, int numIssues, File outDir) throws Exception {

		File file = new File(outDir + File.separator + "issues-" + project + ".csv");
		FileWriter fw = new FileWriter(file);
		CsvWriter csvw = new CsvWriterBuilder(fw).quoteChar(CsvWriter.NO_QUOTE_CHARACTER).separator(';').build();

		// get the issues
		int numResults = 100;
		List<IssuesProcessor> procs = new ArrayList<>();
		for (int i = 0; i < numIssues; i += numResults) {
			IssuesProcessor proc = new IssuesProcessor(domain, project, i, numResults, numIssues, outDir, csvw);
			procs.add(proc);
		}

		// run the threads
		CountDownLatch cntDwnLatch = new CountDownLatch(procs.size());
		for (IssuesProcessor proc : procs) {
			ThreadCommandExecutor.getInstance().exeucuteCommRunnable(new CommandLatchRunnable(proc, cntDwnLatch));

		}
		cntDwnLatch.await();

		csvw.close();

		return file;
	}

	private static int getNumIssues(String project) throws IOException {

		String jql = HttpJiraUtils.getJql(project, 0, 0);
		String urlJira = domain + JIRA_PATH_SEARCH + jql;

		String response = HttpJiraUtils.getStringResponse(urlJira);
		JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();

		return jsonObject.get("total").getAsInt();
	}

}
