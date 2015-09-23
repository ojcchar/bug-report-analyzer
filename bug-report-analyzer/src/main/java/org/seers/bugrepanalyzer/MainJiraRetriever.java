package org.seers.bugrepanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.seers.bugrepanalyzer.index.AssDocumentIndexer;
import org.seers.bugrepanalyzer.processor.HttpJiraUtils;
import org.seers.bugrepanalyzer.processor.IssuesProcessor;
import org.seers.bugrepanalyzer.stats.AssDocumentReporter;
import org.seers.bugrepanalyzer.stats.AssTermStats;
import org.seers.bugrepanalyzer.stats.IndexStats;
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
	private static final File stopWordsFile = new File("stopwords-en-java-html.txt");

	private static String domain;
	private static String outputFolder;
	private static boolean checkFiles;

	public static void main(String[] args) throws IOException {

		String[] projects = null;
		try {
			domain = args[0];
			outputFolder = args[1];
			projects = args[2].split(",");
			checkFiles = "CK".equalsIgnoreCase(args[3]);
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.info("Wrong arguments");
			LOGGER.info("Arguments [jira_domain] [output_folder] [projects] [check_files? (CK) ]");
			return;
		}

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

			// create index folder
			File indexDir = new File(outputFolder + File.separator + "index");
			FileUtils.forceMkdir(indexDir);

			// download the issues
			File issuesFile = processIssues(project, numIssues, outDir, indexDir);

			LOGGER.info("Issues processed in " + issuesFile);

			readIssues(project, indexDir);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readIssues(String project, File indexDir) throws IOException {
		AssDocumentReporter rep = new AssDocumentReporter(indexDir);

		IndexStats indexStats = rep.getIndexStats(0);

		System.out.println(indexStats);

		List<AssTermStats> termStats = rep.getTopTermsByDocFreq(indexStats);
		for (AssTermStats assTermStats : termStats) {
			System.out.println(assTermStats);
		}

		rep.close();
	}

	private static File processIssues(String project, int numIssues, File outDir, File indexDir) throws Exception {

		// out file
		File file = new File(outDir + File.separator + "issues-" + project + ".csv");
		FileWriter fw = new FileWriter(file);
		CsvWriter csvw = new CsvWriterBuilder(fw).separator(';').build();

		AssDocumentIndexer indexer = new AssDocumentIndexer(stopWordsFile, indexDir);

		// get the issues
		int numResults = 100;
		List<IssuesProcessor> procs = new ArrayList<>();
		for (int i = 0; i < numIssues; i += numResults) {
			IssuesProcessor proc = new IssuesProcessor(domain, project, i, numResults, numIssues, outDir, csvw,
					checkFiles, indexer);
			procs.add(proc);
		}

		// run the threads
		CountDownLatch cntDwnLatch = new CountDownLatch(procs.size());
		for (IssuesProcessor proc : procs) {
			ThreadCommandExecutor.getInstance().exeucuteCommRunnable(new CommandLatchRunnable(proc, cntDwnLatch));

		}
		cntDwnLatch.await();

		csvw.close();
		indexer.close();

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
