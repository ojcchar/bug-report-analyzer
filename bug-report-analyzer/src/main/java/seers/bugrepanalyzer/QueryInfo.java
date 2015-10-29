package seers.bugrepanalyzer;

import java.util.Comparator;
import java.util.Date;

public class QueryInfo {

	private String key;
	private Date created;
	private String summary;
	private String description;

	public QueryInfo(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public Date getCreated() {
		return created;
	}

	public String getSummary() {
		return summary;
	}

	public String getDescription() {
		return description;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueryInfo other = (QueryInfo) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	public static Comparator<QueryInfo> createdDateComparator = new Comparator<QueryInfo>() {

		@Override
		public int compare(QueryInfo o1, QueryInfo o2) {
			return o1.getCreated().compareTo(o2.getCreated());
		}

	};

	@Override
	public String toString() {
		return key;
	}

}
