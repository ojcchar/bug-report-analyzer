package org.seers.bugrepanalyzer;

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

		String[] args = { "https://issues.apache.org", outFolder, "MAHOUT" };
		MainJiraRetriever.main(args);

		FileUtils.deleteDirectory(directory);
	}

}
