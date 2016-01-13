package seers.bugrepanalyzer.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JSONClosedDetails {
	
	@SerializedName("original_questions")
	private List<JSONOriginalQuestion> originalQuestions;
	
	private String reason;

	public List<JSONOriginalQuestion> getOriginalQuestions() {
		return originalQuestions;
	}

	public void setOriginalQuestions(List<JSONOriginalQuestion> originalQuestions) {
		this.originalQuestions = originalQuestions;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
