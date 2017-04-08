package seers.bugrepanalyzer.processor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.quux00.simplecsv.CsvWriter;
import seers.appcore.threads.processor.ThreadException;
import seers.appcore.threads.processor.ThreadProcessor;
import seers.appcore.utils.ExceptionUtils;
import seers.bugrepanalyzer.json.JSONIssue;
import seers.bugrepanalyzer.json.JSONIssueFields;
import seers.bugrepanalyzer.json.JSONIssueLink;
import seers.bugrepanalyzer.json.JSONIssues;

public class DuplicateProcessor implements ThreadProcessor {

	private final static Logger LOGGER = LoggerFactory.getLogger(IssuesRetriever.class);

	private String project;
	private int currentIssue;
	private File inDir;
	private int numResults;
	private CsvWriter csvw;

	public DuplicateProcessor(String project, int i, int numResults, int numIssues, File inDir, CsvWriter csvw) {
		this.project = project;
		this.currentIssue = i;
		this.numResults = numResults;
		this.inDir = inDir;
		this.csvw = csvw;
	}

	@Override
	public void processJob() throws ThreadException {

		try {
			LOGGER.debug("Reading file [" + currentIssue + ", " + (currentIssue + numResults) + "]");
			File inFile = getOutFile(project, currentIssue, inDir);
			String contentFile = FileUtils.readFileToString(inFile);

			// parse necessary fields
			Gson gson = new GsonBuilder().setDateFormat(JSONIssueFields.DATE_PATTERN).create();
			JSONIssues issues = gson.fromJson(contentFile, JSONIssues.class);

			writeIssues(issues);
		} catch (Exception e) {
			ThreadException e2 = new ThreadException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		}

	}

	private void writeIssues(JSONIssues issues) {

		for (JSONIssue issue : issues.getIssues()) {
			List<JSONIssueLink> issueLinks = issue.getFields().getIssueLinks();
			for (JSONIssueLink link : issueLinks) {
				JSONIssue outwardIssue = link.getOutwardIssue();
				if (outwardIssue == null) {
					// System.out.println(issue.getKey());
					continue;
				}
				List<String> nextLine = Arrays.asList(new String[] { String.valueOf(issue.getId()), issue.getKey(),
						String.valueOf(outwardIssue.getId()), outwardIssue.getKey(),
						String.valueOf(link.getType().getId()), link.getType().getName() });
				csvw.writeNext(nextLine);
			}
		}

	}

	@Override
	public String getName() {
		return project + " [" + this.currentIssue + ", " + (currentIssue + numResults) + "]";
	}

	private File getOutFile(String project, int i, File outDir) {
		return new File(outDir + File.separator + project + "-" + i + ".json");
	}

}
