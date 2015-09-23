package org.seers.bugrepanalyzer.stats;

import java.util.List;

public class IndexStats {

	private List<AssTermStats> termStats;
	private int numDoc;
	private int numTerms;
	private int numTermsThreshold;
	private int threshold;
	private long totalDf;

	public List<AssTermStats> getTermStats() {
		return termStats;
	}

	public void setTermStats(List<AssTermStats> termStats) {
		this.termStats = termStats;
	}

	public int getNumDoc() {
		return numDoc;
	}

	public void setNumDoc(int numDoc) {
		this.numDoc = numDoc;
	}

	public int getNumTerms() {
		return numTerms;
	}

	public void setNumTerms(int numTerms) {
		this.numTerms = numTerms;
	}

	public int getNumTermsThreshold() {
		return numTermsThreshold;
	}

	public void setNumTermsThreshold(int numTermsThreshold) {
		this.numTermsThreshold = numTermsThreshold;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	@Override
	public String toString() {
		return "IndexStats [numDoc=" + numDoc + ", numTerms=" + numTerms + ", numTermsThreshold=" + numTermsThreshold
				+ ", threshold=" + threshold + "]";
	}

	public long getTotalDf() {
		return totalDf;
	}

	public void setTotalDf(long totalDf) {
		this.totalDf = totalDf;
	}

}
