package org.seers.bugrepanalyzer.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JSONIssueFields {

	public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

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

	public List<String> getCSVLine() {
		List<String> line = new ArrayList<>();
		line.add(getFieldName(issueType));
		line.add(getListFields(fixVersions));
		line.add(getFieldName(resolution));
		line.add(getDateFormat(resolutionDate));
		line.add(getDateFormat(created));
		line.add(getFieldName(priority));
		line.add(getDateFormat(updated));
		line.add(getFieldName(status));
		line.add(getListFields(components));
		line.add(getFieldName(description));
		line.add(getFieldName(summary));
		line.add(getFieldName(creator));
		line.add(getFieldName(reporter));
		return line;
	}

	private String getListFields(List<JSONIssueField> fields) {

		if (fields == null) {
			return "NA";
		}

		StringBuffer fixVer = new StringBuffer();
		for (JSONIssueField field : fields) {
			fixVer.append(field.getName());
			fixVer.append(",");
		}
		if (fixVer.length() > 0) {
			fixVer.delete(fixVer.length() - 1, fixVer.length());
		}

		String string = fixVer.toString();
		return escapeNewLines(string);
	}

	private String getFieldName(String field) {
		if (field == null) {
			return "NA";
		}
		return escapeNewLines(field);
	}

	private String getDateFormat(Date date) {
		if (date == null) {
			return "NA";
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
		return dateFormat.format(date);
	}

	private String getFieldName(JSONIssueField field) {
		if (field == null) {
			return "NA";
		}
		String name = field.getName();
		return escapeNewLines(name);
	}

	private String escapeNewLines(String name) {
		return name.replace("\n\r", "\\n\\r").replace("\r\n", "\\r\\n").replace("\r", "\\r").replace("\n", "\\n");
	}

}
