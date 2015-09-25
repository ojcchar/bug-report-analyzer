package seers.bugrepanalyzer.index;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public class AssDocumentAnalyzer extends StopwordAnalyzerBase {

	protected AssDocumentAnalyzer(CharArraySet stopwords) {
		super(stopwords);
	}

	public AssDocumentAnalyzer(File stopwords) throws IOException {
		this(loadStopwordSet(stopwords.toPath()));
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source = new LowerCaseTokenizer();

		TokenStream result = new PorterStemFilter(source);
		result = new StopFilter(result, stopwords);
		return new TokenStreamComponents(source, result);

	}

}
