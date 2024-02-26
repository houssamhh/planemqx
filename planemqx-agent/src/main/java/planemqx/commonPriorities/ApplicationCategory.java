package planemqx.commonPriorities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "categoryId", "categoryName" })

public class ApplicationCategory {
	@JsonProperty("categoryId")
	private String categoryId;
	@JsonProperty("categoryName")
	private String categoryName;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public ApplicationCategory() {
	}

	public ApplicationCategory(final String categoryId, final String categoryName) {
		super();
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}

	@JsonProperty("categoryId")
	public String getCategoryId() {
		return categoryId;
	}

	@JsonProperty("categoryId")
	public void setCategoryId(final String categoryId) {
		this.categoryId = categoryId;
	}

	@JsonProperty("categoryName")
	public String getCategoryName() {
		return categoryName;
	}

	@JsonProperty("categoryName")
	public void setCategoryName(final String categoryName) {
		this.categoryName = categoryName;
	}
}
