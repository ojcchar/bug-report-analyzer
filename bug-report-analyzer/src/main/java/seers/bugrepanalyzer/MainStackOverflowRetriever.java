package seers.bugrepanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;

import net.quux00.simplecsv.CsvWriter;
import net.quux00.simplecsv.CsvWriterBuilder;
import seers.bugrepanalyzer.json.JSONQuestion;
import seers.bugrepanalyzer.json.JSONSOWrapper;

public class MainStackOverflowRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainStackOverflowRetriever.class);
	private static final String APP_KEY = "4MBjj9CT2dZhUNwgoRf5hw((";
	/**
	 * A parameter sent to the API to exclude fields we don't need from the
	 * response
	 */
	private static final String FILTER_ID = "!)R7_YdkZ4Dy7w3*GPH6r3kkM";

	public static void main(String[] args) {

		String outDirPath;
		String[] tags;

		try {
			outDirPath = args[0];
			tags = args[1].split(",");
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("Wrong arguments");
			LOGGER.error("Arguments: output_folder tag1[,tag2[,tag3...]]");
			LOGGER.error("Example: /home/juan/Data/duplicates/SO jquery,mysql,angularjs");

			return;
		}

		try {
			File outDir = new File(outDirPath);
			FileUtils.forceMkdir(outDir);

			for (String tag : tags) {
				if (tag.isEmpty()) {
					continue;
				}

				File duplicatesOutput = new File(outDirPath + File.separator + tag + ".csv");
				try (CsvWriter csvWriter = new CsvWriterBuilder(new FileWriter(duplicatesOutput)).separator(';')
						.build()) {
					processTag(tag, csvWriter);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void processTag(String tag, CsvWriter csvWriter) throws IOException, InterruptedException {

		LOGGER.info("Tag: " + tag);

		boolean hasMore = true;
		Gson gson = new Gson();
		int page = 1;

		while (hasMore) {
			HttpRequest req = new NetHttpTransport().createRequestFactory()
					.buildGetRequest(new GenericUrl(String.format(
							"https://api.stackexchange.com/2.2/search/advanced?key=%s&page=%d&pagesize=100"
									+ "&order=desc&sort=relevance&closed=True&tagged=%s&site=stackoverflow&filter=%s",
							APP_KEY, page, tag, FILTER_ID)));
			HttpResponse res = req.execute();

			JSONSOWrapper wrapper = gson.fromJson(res.parseAsString(), JSONSOWrapper.class);

			List<JSONQuestion> questions = wrapper.getItems();

			for (JSONQuestion question : questions) {
				// We're only interested in duplicate questions
				if ("duplicate".equals(question.getClosedReason())
						|| "exact duplicate".equals(question.getClosedReason())) {
					csvWriter.writeNext(question.getCSVLine());
				}
			}

			hasMore = wrapper.getHasMore();
			page++;
			LOGGER.info("Quota remaining: " + wrapper.getQuotaRemaining());

			int backoff = wrapper.getBackoff();
			if (backoff == -1) {
				LOGGER.info("No backoff value for request");
			} else {
				LOGGER.info("Backing off for " + backoff + " seconds");
				int backoffMillis = backoff * 1000;
				Thread.sleep(backoffMillis);
			}
		}
	}

}
