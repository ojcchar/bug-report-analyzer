package seers.bugrepanalyzer.stats;

public class AssTermStats {

	private String termTxt;
	private int docFreq;
	private double invDocFreq;
	private double entropy;

	public String getTermTxt() {
		return termTxt;
	}

	public void setTermTxt(String termTxt) {
		this.termTxt = termTxt;
	}

	public int getDocFreq() {
		return docFreq;
	}

	public void setDocFreq(int docFreq) {
		this.docFreq = docFreq;
	}

	public double getInvDocFreq() {
		return invDocFreq;
	}

	public void setInvDocFreq(double invDocFreq) {
		this.invDocFreq = invDocFreq;
	}

	@Override
	public String toString() {
		return "AssTermStats [termTxt=" + termTxt + ", docFreq=" + docFreq
				+ ", invDocFreq=" + invDocFreq + "]";
	}

	public double getEntropy() {
		return entropy;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

}
