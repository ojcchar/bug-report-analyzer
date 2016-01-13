package seers.bugrepanalyzer.json;

import com.google.gson.annotations.SerializedName;

public class JSONOriginalQuestion {

	@SerializedName("question_id")
	private String questionId;
	
	private String title;

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
}
