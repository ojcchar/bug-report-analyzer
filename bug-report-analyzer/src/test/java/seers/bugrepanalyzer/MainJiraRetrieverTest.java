package seers.bugrepanalyzer;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainJiraRetrieverTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testMain() throws IOException {
		String outFolder = "test_data/jira_issues";
		File directory = new File(outFolder);
		FileUtils.forceMkdir(directory);

		String[] args = { "https://issues.apache.org", outFolder, "MAHOUT", "mahout-0.8", "CK" };
		MainJiraRetriever.main(args);

		assertTrue(new File("test_data/jira_issues/json_data/mahout-0.8_Queries_info.txt").exists());
		FileUtils.deleteDirectory(directory);
	}

}
