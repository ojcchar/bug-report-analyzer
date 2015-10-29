package seers.bugrepanalyzer.json;

import com.google.gson.annotations.SerializedName;

public class JSONIssueLink {

	private JSONIssueField type;
	@SerializedName("outwardIssue")
	private JSONIssue outwardIssue;

	public JSONIssueField getType() {
		return type;
	}

	public JSONIssue getOutwardIssue() {
		return outwardIssue;
	}

	public void setType(JSONIssueField type) {
		this.type = type;
	}

	public void setOutwardIssue(JSONIssue outwardIssue) {
		this.outwardIssue = outwardIssue;
	}

}
