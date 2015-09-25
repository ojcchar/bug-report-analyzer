package seers.bugrepanalyzer.json;

import java.util.ArrayList;
import java.util.List;

public class JSONIssue {

	private int id;
	private String key;
	private JSONIssueFields fields;

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "JSONIssue [id=" + id + ", key=" + getKey() + ", fields=" + getFields() + "]";
	}

	public List<String> getCSVLine() {
		List<String> line = new ArrayList<>();
		line.add(String.valueOf(id));
		line.add(getKey());
		line.addAll(getFields().getCSVLine());
		return line;
	}

	public String getKey() {
		return key;
	}

	public JSONIssueFields getFields() {
		return fields;
	}

}
