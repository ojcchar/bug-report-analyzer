package seers.bugrepanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.quux00.simplecsv.CsvWriter;
import net.quux00.simplecsv.CsvWriterBuilder;
import seers.appcore.threads.CommandLatchRunnable;
import seers.appcore.threads.ThreadCommandExecutor;
import seers.bugrepanalyzer.processor.HttpJiraUtils;
import seers.bugrepanalyzer.processor.IssuesRetriever;

public class MainJiraRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainJiraRetriever.class);
	private static String domain;
	private static String outputFolder;
	private static boolean checkFiles;

	public static void main(String[] args) throws IOException {

		String[] projects = null;
		String[] outFiles = null;
		try {
			domain = args[0];
			outputFolder = args[1];
			projects = args[2].split(",");
			outFiles = args[3].split(",");
			checkFiles = "CK".equalsIgnoreCase(args[4]);
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.info("Wrong arguments");
			LOGGER.info(
					"Arguments [jira_domain] [output_folder] [projects] [output_files] [check_files_already_downloaded? (CK/NCK) ]");
			LOGGER.info(
					"Example https://issues.apache.org c:/out MAHOUT,ZOOKEEPER mahout-0.8,zookeeper-3.4.5;zookeeper-3.4.6 CK");
			return;
		}

		try {

			if (outFiles.length != projects.length) {
				throw new Exception("Out files and projects do not match");
			}
			for (int i = 0; i < projects.length; i++) {
				String project = projects[i].trim();

				if (project.isEmpty()) {
					continue;
				}

				processProject(project, outFiles[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void processProject(String project, String outFiles) throws Exception {

		LOGGER.info("Project: " + project);

		// get the total # of issues
		int numIssues = HttpJiraUtils.getNumIssues(project, domain);

		LOGGER.info("Total # of issues: " + numIssues);

		// create out folder
		File outDir = new File(outputFolder + File.separator + "json_data");
		FileUtils.forceMkdir(outDir);

		String[] outFilesSplit = outFiles.split(";");

		for (String outFile : outFilesSplit) {

			// download the issues
			File issuesFile = processIssues(project, numIssues, outDir, null, outFile);

			LOGGER.info("Issues processed in " + issuesFile);
		}

	}

	private static File processIssues(String project, int numIssues, File outDir, File indexDir, String outFile)
			throws Exception {

		// out file
		File file = new File(outDir + File.separator + outFile + "_Queries_info.txt");
		try (CsvWriter csvw = new CsvWriterBuilder(new FileWriter(file)).separator(';').build()) {

			// get the issues
			int numResults = 100;
			List<IssuesRetriever> procs = new ArrayList<>();
			for (int i = 0; i < numIssues; i += numResults) {
				IssuesRetriever proc = new IssuesRetriever(domain, project, i, numResults, numIssues, outDir, csvw,
						checkFiles);
				procs.add(proc);
			}

			ThreadCommandExecutor executor = new ThreadCommandExecutor();

			try {
				// run the threads
				CountDownLatch cntDwnLatch = new CountDownLatch(procs.size());
				for (IssuesRetriever proc : procs) {
					executor.exeucuteCommRunnable(new CommandLatchRunnable(proc, cntDwnLatch));

				}
				cntDwnLatch.await();
			} finally {
				executor.shutdown();
			}

		}

		return file;
	}

}
