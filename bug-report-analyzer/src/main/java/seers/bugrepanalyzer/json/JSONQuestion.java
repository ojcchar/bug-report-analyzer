package seers.bugrepanalyzer.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JSONQuestion {

	@SerializedName("closed_details")
	private JSONClosedDetails closedDetails;

	private List<String> tags;

	@SerializedName("closed_reason")
	private String closedReason;

	private String link;

	@SerializedName("question_id")
	private String questionId;

	private String title;

	public JSONClosedDetails getClosedDetails() {
		return closedDetails;
	}

	public void setClosedDetails(JSONClosedDetails closedDetails) {
		this.closedDetails = closedDetails;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getClosedReason() {
		return closedReason;
	}

	public void setClosedReason(String closedReason) {
		this.closedReason = closedReason;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getCSVLine() {
		List<String> csvLine = new ArrayList<String>();

		csvLine.add(getQuestionId());
		csvLine.add(getTitle());
		csvLine.add(getLink());
		csvLine.add(String.join(",", getTags()));

		List<JSONOriginalQuestion> originalQuestions = getClosedDetails().getOriginalQuestions();
		List<String> originalLinks = new ArrayList<>();
		for (JSONOriginalQuestion originalQuestion : originalQuestions) {
			originalLinks.add("http://stackoverflow.com/q/" + originalQuestion.getQuestionId());
		}
		csvLine.add(String.join(",", originalLinks));

		csvLine.add(getClosedReason());

		return csvLine;
	}
}
