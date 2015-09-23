package org.seers.bugrepanalyzer.json;

public class JSONIssueField {

	private int id = -1;
	private String name = "";

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return " [id=" + id + ", name=" + name + "]";
	}

}
