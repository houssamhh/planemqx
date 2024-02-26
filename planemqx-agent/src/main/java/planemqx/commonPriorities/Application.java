package planemqx.commonPriorities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "applicationId", "applicationName", "applicationCategory", "priority", "processingRate",
		"processingDistribution", "subscribesTo" })
public class Application {
	@JsonProperty("applicationId")
	private String applicationId;
	@JsonProperty("applicationName")
	private String applicationName;
	@JsonProperty("applicationCategory")
	private String applicationCategory;
	@JsonProperty("priority")
	private int priority;
	@JsonProperty("processingRate")
	private int processingRate;
	@JsonProperty("processingDistribution")
	private String processingDistribution;
	@JsonProperty("subscribesTo")
	private List<String> subscribesTo = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Application() {
	}

	public Application(final String applicationId, final String applicationName, final String applicationCategory,
			final int priority, final int processingRate, final String processingDistribution,
			final List<String> subscribesTo) {
		super();
		this.applicationId = applicationId;
		this.applicationName = applicationName;
		this.applicationCategory = applicationCategory;
		this.priority = priority;
		this.processingRate = processingRate;
		this.processingDistribution = processingDistribution;
		this.subscribesTo = subscribesTo;
	}

	@JsonProperty("applicationId")
	public String getApplicationId() {
		return applicationId;
	}

	@JsonProperty("applicationId")
	public void setApplicationId(final String applicationId) {
		this.applicationId = applicationId;
	}

	@JsonProperty("applicationName")
	public String getApplicationName() {
		return applicationName;
	}

	@JsonProperty("applicationName")
	public void setApplicationName(final String applicationName) {
		this.applicationName = applicationName;
	}

	@JsonProperty("applicationCategory")
	public String getApplicationCategory() {
		return applicationCategory;
	}

	@JsonProperty("applicationCategory")
	public void setApplicationCategory(final String applicationCategory) {
		this.applicationCategory = applicationCategory;
	}

	@JsonProperty("priority")
	public int getPriority() {
		return priority;
	}

	@JsonProperty("priority")
	public void setPriority(final int priority) {
		this.priority = priority;
	}

	@JsonProperty("processingRate")
	public int getProcessingRate() {
		return processingRate;
	}

	@JsonProperty("processingRate")
	public void setProcessingRate(final int processingRate) {
		this.processingRate = processingRate;
	}

	@JsonProperty("processingDistribution")
	public String getProcessingDistribution() {
		return processingDistribution;
	}

	@JsonProperty("processingDistribution")
	public void setProcessingDistribution(final String processingDistribution) {
		this.processingDistribution = processingDistribution;
	}

	@JsonProperty("subscribesTo")
	public List<String> getSubscribesTo() {
		return subscribesTo;
	}

	@JsonProperty("subscribesTo")
	public void setSubscribesTo(final List<String> subscribesTo) {
		this.subscribesTo = subscribesTo;
	}

	@Override
	public String toString() {
		return "Application [applicationId=" + applicationId + ", applicationName=" + applicationName
				+ ", applicationCategory=" + applicationCategory + ", priority=" + priority + ", processingRate="
				+ processingRate + ", processingDistribution=" + processingDistribution + ", subscribesTo="
				+ subscribesTo + "]";
	}
}
