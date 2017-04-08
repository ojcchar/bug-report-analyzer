package seers.bugrepanalyzer.json;

public class JSONIssueField {

	private int id = -1;
	private String name = "";
	
	

	public JSONIssueField(String name) {
		super();
		this.name = name;
	}

	public JSONIssueField(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

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
