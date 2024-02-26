package planemqx.commonPriorities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "topicId", "topicName", "publishers", "subscribers" })
public class Topic {
	@JsonProperty("topicId")
	private String topicId;
	@JsonProperty("topicName")
	private String topicName;
	@JsonProperty("publishers")
	private List<String> publishers = null;
	@JsonProperty("subscribers")
	private List<String> subscribers = null;

	/**
	 * No args constructor for use in serialization
	 */
	public Topic() {
	}

	public Topic(final String topicId, final String topicName, final List<String> publishers,
			final List<String> subscribers) {
		super();
		this.topicId = topicId;
		this.topicName = topicName;
		this.publishers = publishers;
		this.subscribers = subscribers;
	}

	@JsonProperty("topicId")
	public String getTopicId() {
		return topicId;
	}

	@JsonProperty("topicId")
	public void setTopicId(final String topicId) {
		this.topicId = topicId;
	}

	@JsonProperty("topicName")
	public String getTopicName() {
		return topicName;
	}

	@JsonProperty("topicName")
	public void setTopicName(final String topicName) {
		this.topicName = topicName;
	}

	@JsonProperty("publishers")
	public List<String> getPublishers() {
		return publishers;
	}

	@JsonProperty("publishers")
	public void setPublishers(final List<String> publishers) {
		this.publishers = publishers;
	}

	@JsonProperty("subscribers")
	public List<String> getSubscribers() {
		return subscribers;
	}

	@JsonProperty("subscribers")
	public void setSubscribers(final List<String> subscribers) {
		this.subscribers = subscribers;
	}
}
