package org.seers.bugrepanalyzer.json;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JSONIssueFields {

	@SerializedName("issuetype")
	private JSONIssueField issueType;
	private List<JSONIssueField> fixVersions;
	private JSONIssueField resolution;
	@SerializedName("resolutiondate")
	private Date resolutionDate;
	private Date created;
	private JSONIssueField priority;
	private List<JSONIssueField> versions;
	private Date updated;
	private JSONIssueField status;
	private List<JSONIssueField> components;
	private String description;
	private String summary;
	private JSONIssueField creator;
	private JSONIssueField reporter;

	public JSONIssueField getIssueType() {
		return issueType;
	}

	public List<JSONIssueField> getFixVersions() {
		return fixVersions;
	}

	public JSONIssueField getResolution() {
		return resolution;
	}

	public Date getResolutionDate() {
		return resolutionDate;
	}

	public Date getCreated() {
		return created;
	}

	public JSONIssueField getPriority() {
		return priority;
	}

	public List<JSONIssueField> getVersions() {
		return versions;
	}

	public Date getUpdated() {
		return updated;
	}

	public JSONIssueField getStatus() {
		return status;
	}

	public List<JSONIssueField> getComponents() {
		return components;
	}

	public String getDescription() {
		return description;
	}

	public String getSummary() {
		return summary;
	}

	public JSONIssueField getCreator() {
		return creator;
	}

	public JSONIssueField getReporter() {
		return reporter;
	}

	@Override
	public String toString() {
		return "JSONIssueFields [issueType=" + issueType + ", fixVersions=" + fixVersions + ", resolution=" + resolution
				+ ", resolutionDate=" + resolutionDate + ", created=" + created + ", priority=" + priority
				+ ", versions=" + versions + ", updated=" + updated + ", status=" + status + ", components="
				+ components + ", description=" + description + ", summary=" + summary + ", creator=" + creator
				+ ", reporter=" + reporter + "]";
	}

}
