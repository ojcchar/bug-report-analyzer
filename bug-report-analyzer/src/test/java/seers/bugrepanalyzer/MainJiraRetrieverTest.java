package seers.bugrepanalyzer;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class MainJiraRetrieverTest {
	
	public static void main(String[] args) throws IOException {

//		String outFolder = "C:\\Users\\ojcch\\Documents\\Tmp\\hibernate";
//		String[] args2 = { "https://hibernate.atlassian.net", outFolder, "HHH", "hibernate", "CK" };
//		MainJiraRetriever.main(args2);
		

		String outFolder = "C:/Users/ojcch/Documents/Projects/Discourse_query_reformulation/data/query_quality_data/bugs3";
		String[] args2 = {  "https://issues.apache.org/jira", outFolder, "NUTCH,BOOKKEEPER,DERBY,MAHOUT,OPENJPA,TIKA", "apache-nutch,bookkeeper-4.1.0,derby-10.9.1.0,mahout,openjpa-2.0.1,tika-1.3", "CK" };
		MainJiraRetriever.main(args2);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

//	@Test
	public void testMain() throws IOException {
		String outFolder = "test_data/jira_issues";
		File directory = new File(outFolder);
		FileUtils.forceMkdir(directory);

		String[] args = { "https://issues.apache.org/jira", outFolder, "MAHOUT", "mahout-0.8", "CK" };
		MainJiraRetriever.main(args);

		assertTrue(new File("test_data/jira_issues/json_data/mahout-0.8_Queries_info.txt").exists());
		FileUtils.deleteDirectory(directory);
	}

}
