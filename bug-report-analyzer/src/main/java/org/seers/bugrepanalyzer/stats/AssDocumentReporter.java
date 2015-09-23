package org.seers.bugrepanalyzer.stats;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class AssDocumentReporter {

	private IndexReader reader;
	private DecimalFormat df = new DecimalFormat("0.00000");

	public AssDocumentReporter(File idxDir) throws IOException {
		reader = DirectoryReader.open(FSDirectory.open(idxDir.toPath()));
	}

	public List<AssTermStats> getTopTermsByDocFreq(int threshold) throws IOException {
		IndexStats indexStats = getIndexStats(threshold);
		return getTopTermsByDocFreq(indexStats);
	}

	public List<AssTermStats> getTopTermsByDocFreq(IndexStats indexStats) {
		sortTermStatsByDocFreq(indexStats);
		return indexStats.getTermStats().subList(0, indexStats.getNumTermsThreshold());
	}

	private void sortTermStatsByDocFreq(IndexStats indexStats) {
		sortTermStatsByDocFreq(indexStats.getTermStats());
	}

	public IndexStats getIndexStats(int threshold) throws IOException {
		IndexStats idxSt = new IndexStats();
		idxSt.setNumDoc(reader.numDocs());

		setIndexDocFrequency(idxSt, threshold);

		return idxSt;
	}

	public void close() throws IOException {
		reader.close();
	}

	private void setIndexDocFrequency(IndexStats idxSt, int threshold) throws IOException {

		String field = "summary";
		Fields fields = MultiFields.getFields(reader);
		if (fields == null) {
			throw new RuntimeException("field " + field + " not found");
		}

		List<AssTermStats> termStats = new ArrayList<AssTermStats>();
		int numTerms = 0;
		int numTermsThreshold = 0;

		Terms terms = fields.terms(field);
		if (terms == null) {
			throw new RuntimeException("No terms found");
		}

		TermsEnum termsEnum = terms.iterator();
		BytesRef term;

		long totalDocFreq = 0;
		while ((term = termsEnum.next()) != null) {
			numTerms++;

			int docFreq = termsEnum.docFreq();
			totalDocFreq = +docFreq;

			if (docFreq >= threshold) {
				numTermsThreshold++;
			}

			AssTermStats termSt = new AssTermStats();
			termSt.setDocFreq(docFreq);
			termSt.setTermTxt(term.utf8ToString());
			termSt.setInvDocFreq(Math.log((double) reader.numDocs() / (double) docFreq));

			termStats.add(termSt);
		}

		idxSt.setTotalDf(totalDocFreq);
		idxSt.setTermStats(termStats);
		idxSt.setNumTerms(numTerms);
		idxSt.setThreshold(threshold);
		idxSt.setNumTermsThreshold(numTermsThreshold);
	}

	public void sortTermStatsByDocFreq(List<AssTermStats> termStats) {
		Collections.sort(termStats, new DocFrequTermComparator());
	}

	public void sortTermStatsByInvDocFreq(List<AssTermStats> termStats) {
		Collections.sort(termStats, new InvDocFrequTermComparator());
	}

	public void sortTermStatsByInvDocFreq(IndexStats indexStats) {
		sortTermStatsByInvDocFreq(indexStats.getTermStats());
	}

	public String getTermsStatsFormattedIDF(List<AssTermStats> termStats, int top) {

		if (top < 0) {
			throw new RuntimeException("The top " + top + " cannot be returned");
		}

		StringBuffer buff = new StringBuffer();

		for (int i = 0; i < top && i < termStats.size(); i++) {
			AssTermStats tStat = termStats.get(i);

			buff.append(tStat.getTermTxt());
			buff.append("\t");
			buff.append(df.format(tStat.getInvDocFreq()));
			buff.append("\n");
		}

		return buff.toString();
	}

	public void calculateDfEntropy(List<AssTermStats> assTermStats, long totalDf) {
		for (AssTermStats tStats : assTermStats) {
			double probDf = tStats.getDocFreq() / (double) totalDf;
			tStats.setEntropy(-probDf * Math.log(probDf));
		}

	}

	public String getTermsStatsFormattedEntropy(List<AssTermStats> termStats, int topIdfResults) {

		if (topIdfResults < 0) {
			throw new RuntimeException("The top " + topIdfResults + " cannot be returned");
		}

		StringBuffer buff = new StringBuffer();

		for (int i = 0; i < topIdfResults && i < termStats.size(); i++) {
			AssTermStats tStat = termStats.get(i);

			buff.append(tStat.getTermTxt());
			buff.append("\t");
			buff.append(df.format(tStat.getEntropy()));
			buff.append("\n");
		}

		return buff.toString();
	}
}
