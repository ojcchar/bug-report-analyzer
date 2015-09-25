package seers.bugrepanalyzer.stats;

import java.util.Comparator;

public class DocFrequTermComparator implements Comparator<AssTermStats> {

	public int compare(AssTermStats o1, AssTermStats o2) {
		int docFreq1 = o1.getDocFreq();
		int docFreq2 = o2.getDocFreq();
		return ((docFreq1 < docFreq2) ? -1 : ((docFreq1 == docFreq2) ? 0 : 1)) * -1;
	}
}
