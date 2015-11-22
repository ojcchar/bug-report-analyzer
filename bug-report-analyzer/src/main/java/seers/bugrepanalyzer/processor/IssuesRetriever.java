package seers.bugrepanalyzer.processor;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.quux00.simplecsv.CsvWriter;
import seers.appcore.threads.processor.ThreadException;
import seers.appcore.threads.processor.ThreadProcessor;
import seers.appcore.utils.ExceptionUtils;
import seers.bugrepanalyzer.json.JSONIssue;
import seers.bugrepanalyzer.json.JSONIssueFields;
import seers.bugrepanalyzer.json.JSONIssues;

public class IssuesRetriever implements ThreadProcessor {

	private final static String JIRA_PATH_SEARCH = "/jira/rest/api/2/search?";
	private final static Logger LOGGER = LoggerFactory.getLogger(IssuesRetriever.class);

	private String domain;
	private String project;
	private int currentIssue;
	private int numResults;
	private File outDir;
	private int totalResults;
	private CsvWriter csvw;
	private boolean checkFiles;

	public IssuesRetriever(String domain, String project, int i, int numResults, int totalResults, File outDir,
			CsvWriter csvw, boolean checkFiles) throws IOException {
		super();
		this.domain = domain;
		this.project = project;
		this.currentIssue = i;
		this.numResults = numResults;
		this.outDir = outDir;
		this.totalResults = totalResults;
		this.csvw = csvw;
		this.checkFiles = checkFiles;
	}

	@Override
	public void processJob() throws ThreadException {
		try {
			File outFile = getOutFile(project, currentIssue, outDir);
			String contentFile = null;

			if (checkFiles && outFile.exists() && outFile.isFile()) {
				LOGGER.debug("Reading file [" + currentIssue + ", " + (currentIssue + numResults) + "]");
				contentFile = FileUtils.readFileToString(outFile);
			} else {
				LOGGER.debug("Downloading issues [" + currentIssue + ", " + (currentIssue + numResults) + "]");
				contentFile = downLoadIssues();

				// write original json
				FileUtils.writeStringToFile(outFile, contentFile);
			}

			// parse necessary fields
			Gson gson = new GsonBuilder().setDateFormat(JSONIssueFields.DATE_PATTERN).create();
			JSONIssues issuesContent = gson.fromJson(contentFile, JSONIssues.class);

			writeJsonIssues(issuesContent);
		} catch (Exception e) {
			ThreadException e2 = new ThreadException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		}

	}

	private String downLoadIssues() throws UnsupportedEncodingException, IOException {
		String contentFile;
		String jql = HttpJiraUtils.getJql(project, currentIssue, numResults);
		String urlJira = domain + JIRA_PATH_SEARCH + jql;

		String response = HttpJiraUtils.getStringResponse(urlJira);

		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(response).getAsJsonObject();

		validateResponse(json);

		// pretty printing
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		contentFile = gson.toJson(json);
		return contentFile;
	}

	private void writeJsonIssues(JSONIssues issuesContent) {
		List<JSONIssue> issues = issuesContent.getIssues();
		for (JSONIssue jsonIssue : issues) {
			// System.out.println(jsonIssue);
			List<String> nextLine = jsonIssue.getCSVLine();
			csvw.writeNext(nextLine);
		}
	}

	private void validateResponse(JsonObject jsonObject) {

		int total = jsonObject.get("total").getAsInt();
		int startAt = jsonObject.get("startAt").getAsInt();
		int maxResults = jsonObject.get("maxResults").getAsInt();

		if (startAt != currentIssue || maxResults != numResults || total != totalResults) {
			throw new RuntimeException("The issues numbers do not match");
		}

	}

	private File getOutFile(String project, int i, File outDir) {
		return new File(outDir + File.separator + project + "-" + i + ".json");
	}

	@Override
	public String getName() {
		return "project [" + this.currentIssue + ", " + (currentIssue + numResults) + "]";
	}
}
