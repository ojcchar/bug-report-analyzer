package seers.bugrepanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import seers.bugrepanalyzer.processor.HttpJiraUtils;

public class MainSourceForgeRetriever {

	private static final String SF_DOMAIN = "http://sourceforge.net/rest/p/";
	static List<String> projects = Arrays.asList("adempiere", "atunes", "winmerge");
	static String outputFolder = "C:/Users/ojcch/Documents/Projects/Discourse_query_reformulation/data/query_quality_data/bugs2";

	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
	static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) throws Exception {

		// for each system
		for (String project : projects) {

			try {
				// get # of bugs
				int numBugs = getNumberOfBugs(project);

				System.out.println(project + ": " + numBugs);

				processIssues(project, numBugs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static File processIssues(String project, int numIssues) throws Exception {

		// out file
		File file = new File(outputFolder + File.separator + project + "_Queries_info.txt");
		try (CsvWriter csvw = new CsvWriterBuilder(new FileWriter(file)).separator(';').build()) {

			// get the issues
			int numResults = 200;
			double numPages = Math.ceil((double) numIssues / numResults);
			List<SFIssuesRetriever> procs = new ArrayList<>();
			System.out.println("num pages: " + numPages);
			for (int page = 0; page < numPages; page++) {
				SFIssuesRetriever proc = new SFIssuesRetriever(project, page, numResults, numIssues, csvw);
				procs.add(proc);
			}

			ThreadCommandExecutor executor = new ThreadCommandExecutor();
			executor.setCorePoolSize(2);

			try {
				// run the threads
				CountDownLatch cntDwnLatch = new CountDownLatch(procs.size());
				for (SFIssuesRetriever proc : procs) {
					executor.executeCommRunnable(new CommandLatchRunnable(proc, cntDwnLatch));

				}
				cntDwnLatch.await();
			} finally {
				executor.shutdown();
			}

		}

		return file;
	}

	private static int getNumberOfBugs(String system) throws IOException {

		String urlJira = SF_DOMAIN + system + "/bugs";

		String response = HttpJiraUtils.getStringResponse(urlJira);
		JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();

		return jsonObject.get("count").getAsInt();
	}

	public static class SFIssuesRetriever implements ThreadProcessor {

		private String project;
		private int numResults;
		private int totalResults;
		private CsvWriter csvw;
		private int page;
		Gson gson = new Gson();

		public SFIssuesRetriever(String project, int page, int numResults, int totalResults, CsvWriter csvw) {
			super();
			this.project = project;
			this.page = page;
			this.numResults = numResults;
			this.totalResults = totalResults;
			this.csvw = csvw;
		}

		@Override
		public String getName() {
			return project + " - page: " + page;
		}

		@Override
		public void processJob() throws ThreadException {

			try {
				// download issues
				String content = downloadIssues();

				System.out.println(project + " - page: " + page);

				// parse them
				Map<String, Object> issuesContent = gson.fromJson(content, Map.class);

				// write them
				writeIssues((ArrayList<Map<String, Object>>) issuesContent.get("tickets"));
			} catch (Exception e) {
				e.printStackTrace();
				ThreadException e2 = new ThreadException(e.getMessage());
				ExceptionUtils.addStackTrace(e, e2);
				throw e2;
			}

		}

		private void writeIssues(ArrayList<Map<String, Object>> listOfBugs) throws Exception {

			for (Map<String, Object> bug : listOfBugs) {
				Double tickNumber = (Double) bug.get("ticket_num");
				int bugId = tickNumber.intValue();
				String issueContent = downloadIssue(bugId);
				Map<String, Object> parsedIssue = gson.fromJson(issueContent, Map.class);

				JSONIssue jsonIssue = getIssue((Map<String, Object>) parsedIssue.get("ticket"), bugId);

				// System.out.println(jsonIssue);
				List<String> nextLine = jsonIssue.getCSVLine();
				csvw.writeNext(nextLine);
			}

		}

		private JSONIssue getIssue(Map<String, Object> parsedIssue, int bugId) throws ParseException {

			String key = String.valueOf(bugId);

			JSONIssueField issueType = null;
			List<JSONIssueField> fixVersions = null;
			JSONIssueField resolution = null;
			Date resolutionDate = getDate(parsedIssue, "mod_date");
			Date created = getDate(parsedIssue, "created_date");
			Date updated = resolutionDate;
			JSONIssueField status = new JSONIssueField((String) parsedIssue.get("status"));
			String description = (String) parsedIssue.get("description");
			String summary = (String) parsedIssue.get("summary");
			JSONIssueField creator = new JSONIssueField((String) parsedIssue.get("reported_by"));
			JSONIssueField reporter = new JSONIssueField((String) parsedIssue.get("reported_by"));

			JSONIssueFields fields = new JSONIssueFields(issueType, fixVersions, resolution, resolutionDate, created,
					null, null, updated, status, null, description, summary, creator, reporter, null);

			JSONIssue issue = new JSONIssue(bugId, key, fields);

			return issue;
		}

		private Date getDate(Map<String, Object> parsedIssue, String key) throws ParseException {
			try {
				return formatter.parse((String) parsedIssue.get(key));
			} catch (ParseException e) {
				return formatter2.parse((String) parsedIssue.get(key));
			}
		}

		private String downloadIssue(int bugId) throws IOException {
			String urlJira = SF_DOMAIN + project + "/bugs/" + bugId;

			return getContent(urlJira);
		}

		private String getContent(String urlJira) throws IOException {
			String response = HttpJiraUtils.getStringResponse(urlJira);
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(response).getAsJsonObject();

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String content = gson.toJson(json);
			return content;
		}

		private String downloadIssues() throws IOException {

			String urlJira = SF_DOMAIN + project + "/bugs?limit=" + numResults + "&page=" + page;

			return getContent(urlJira);
		}

	}

}
