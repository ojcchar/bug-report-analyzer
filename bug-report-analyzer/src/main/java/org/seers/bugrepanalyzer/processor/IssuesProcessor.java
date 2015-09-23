package org.seers.bugrepanalyzer.processor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.seers.bugrepanalyzer.json.JSONIssue;
import org.seers.bugrepanalyzer.json.JSONIssues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.quux00.simplecsv.CsvWriter;

public class IssuesProcessor {

	private final static String JIRA_PATH_SEARCH = "/jira/rest/api/2/search?";
	private final static Logger LOGGER = LoggerFactory.getLogger(IssuesProcessor.class);

	private String domain;
	private String project;
	private int currentIssue;
	private int numResults;
	private File outDir;
	private int totalResults;
	private CsvWriter csvw;

	public IssuesProcessor(String domain, String project, int i, int numResults, int totalResults, File outDir,
			CsvWriter csvw) {
		super();
		this.domain = domain;
		this.project = project;
		this.currentIssue = i;
		this.numResults = numResults;
		this.outDir = outDir;
		this.totalResults = totalResults;
		this.csvw = csvw;
	}

	public File downloadIssues() throws Exception {
		LOGGER.debug("Downloading issues [" + currentIssue + ", " + (currentIssue + numResults) + "]");

		String jql = HttpJiraUtils.getJql(project, currentIssue, numResults);
		String urlJira = domain + JIRA_PATH_SEARCH + jql;

		String response = HttpJiraUtils.getStringResponse(urlJira);

		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(response).getAsJsonObject();

		validateResponse(json);

		// pretty printing
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(json);

		// write original json
		File outFile = writeResponse(prettyJson, project, currentIssue, outDir);

		// parse necessary fields
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
		JSONIssues issuesContent = gson.fromJson(prettyJson, JSONIssues.class);

		writeJsonIssues(issuesContent);

		return outFile;

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

	private File writeResponse(String response, String project, int i, File outDir) throws IOException {
		File file = new File(outDir + File.separator + project + "-" + i + ".json");
		FileUtils.writeStringToFile(file, response);
		return file;
	}
}
