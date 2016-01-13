package seers.bugrepanalyzer.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JSONSOWrapper {

	private List<JSONQuestion> items;

	@SerializedName("has_more")
	private boolean hasMore;

	private int backoff = -1;

	@SerializedName("quota_max")
	private int quotaMax;

	@SerializedName("quota_remaining")
	private int quotaRemaining;

	public List<JSONQuestion> getItems() {
		return items;
	}

	public void setItems(List<JSONQuestion> items) {
		this.items = items;
	}

	public boolean getHasMore() {
		return hasMore;
	}

	public int getBackoff() {
		return backoff;
	}

	public void setBackoff(int backoff) {
		this.backoff = backoff;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public int getQuotaMax() {
		return quotaMax;
	}

	public void setQuotaMax(int quotaMax) {
		this.quotaMax = quotaMax;
	}

	public int getQuotaRemaining() {
		return quotaRemaining;
	}

	public void setQuotaRemaining(int quotaRemaining) {
		this.quotaRemaining = quotaRemaining;
	}
}
