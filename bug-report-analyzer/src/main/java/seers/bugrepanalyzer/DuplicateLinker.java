package seers.bugrepanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.quux00.simplecsv.CsvWriter;
import net.quux00.simplecsv.CsvWriterBuilder;
import seers.appcore.threads.CommandLatchRunnable;
import seers.appcore.threads.ThreadCommandExecutor;
import seers.bugrepanalyzer.processor.DuplicateProcessor;
import seers.bugrepanalyzer.processor.HttpJiraUtils;

public class DuplicateLinker {

	private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateLinker.class);
	private static String inFolder;
	private static String domain;

	public static void main(String[] args) throws IOException {

		String[] projects = null;
		String[] outFiles = null;
		try {
			domain = args[0];
			inFolder = args[1];
			projects = args[2].split(",");
			outFiles = args[3].split(",");
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("Wrong arguments");
			LOGGER.info("Arguments: [jira_domain] [in_folder] [projects] [output_files]");
			LOGGER.info(
					"Example https://issues.apache.org /home/juan/Data/duplicates MAHOUT,ZOOKEEPER,BOOKKEEPER,DERBY,OPENJPA,PIG,SOLR,TIKA,LUCENE mahout-0.8,zookeeper-3.4.5,bookkeeper-4.4.0,derby-10.0.2.2,openjpa-2.4.1,pig-0.15.1,solr-5.5,tika-1.12,lucene-5.5");
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
		File inDir = new File(inFolder + File.separator + "json_data");

		String[] outFilesSplit = outFiles.split(";");

		for (String outFile : outFilesSplit) {

			// download the issues
			File issuesFile = processIssues(project, numIssues, inDir, null, outFile);

			LOGGER.info("Issues processed in " + issuesFile);
		}

	}

	private static File processIssues(String project, int numIssues, File inDir, File indexDir, String outFile)
			throws Exception {

		// out file
		File file = new File(inDir + File.separator + outFile + "_Duplicates.txt");
		try (CsvWriter csvw = new CsvWriterBuilder(new FileWriter(file)).separator(';').build()) {

			// get the issues
			int numResults = 100;
			List<DuplicateProcessor> procs = new ArrayList<>();
			for (int i = 0; i < numIssues; i += numResults) {
				DuplicateProcessor proc = new DuplicateProcessor(project, i, numResults, numIssues, inDir, csvw);
				procs.add(proc);
			}

			ThreadCommandExecutor executor = new ThreadCommandExecutor();

			try {
				// run the threads
				CountDownLatch cntDwnLatch = new CountDownLatch(procs.size());
				for (DuplicateProcessor proc : procs) {
					executor.executeCommRunnable(new CommandLatchRunnable(proc, cntDwnLatch));

				}
				cntDwnLatch.await();
			} finally {
				executor.shutdown();
			}

		}

		return file;
	}

}
