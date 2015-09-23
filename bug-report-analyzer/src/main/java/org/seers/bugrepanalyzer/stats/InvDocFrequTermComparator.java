package org.seers.bugrepanalyzer.stats;

import java.util.Comparator;

public class InvDocFrequTermComparator implements Comparator<AssTermStats> {

	public int compare(AssTermStats o1, AssTermStats o2) {
		return Double.compare(o1.getInvDocFreq(), o2.getInvDocFreq()) * -1;
	}
}
