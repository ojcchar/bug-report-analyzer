package seers.bugrepanalyzer.index;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import seers.bugrepanalyzer.json.JSONIssue;

public class AssDocumentIndexer {

	private Analyzer analyzer;
	private IndexWriter writer;

	public AssDocumentIndexer(File stopWordsFile, File idxDir) throws IOException {
		analyzer = new AssDocumentAnalyzer(stopWordsFile);

		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		conf.setOpenMode(OpenMode.CREATE_OR_APPEND);

		Directory dir = new SimpleFSDirectory(idxDir.toPath());

		writer = new IndexWriter(dir, conf);
	}

	/**
	 * Creates or updates the index of the collection in the specified directory
	 * 
	 * @param issues
	 *            the document collection
	 * @param idxDir
	 *            the output directory
	 * @param STOP_WORDS_FILE
	 *            the stop words file
	 * @param similarity
	 * @throws IOException
	 */
	public void indexDocuments(List<JSONIssue> issues) throws IOException {

		for (JSONIssue assDocFile : issues) {
			Document doc = getLuceneDocument(assDocFile);
			writer.addDocument(doc);
		}

	}

	public void close() throws IOException {
		writer.close();
	}

	private Document getLuceneDocument(JSONIssue doc) {
		Document luceneDoc = new Document();

		FieldType type = new FieldType();
		type.setStored(true);

		luceneDoc.add(new Field("id", String.valueOf(doc.getId()), type));

		// -----------------------------------

		type = new FieldType();
		type.setStored(true);

		String key = doc.getFields().getSummary();
		luceneDoc.add(new Field("key", key, type));

		// -----------------------------------

		type = new FieldType();
		type.setStoreTermVectors(true);
		type.setTokenized(true);
		type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		type.setStoreTermVectorPositions(true);

		String title = doc.getFields().getSummary();
		luceneDoc.add(new Field("summary", title, type));

		// -----------------------------------

		type = new FieldType();
		type.setStoreTermVectors(true);
		type.setTokenized(true);
		type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		type.setStoreTermVectorPositions(true);

		String descrip = doc.getFields().getSummary();
		luceneDoc.add(new Field("description", descrip, type));

		return luceneDoc;

	}
}
