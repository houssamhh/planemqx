package planemqx.agent;

public class MessageToRepublish {
	private String topic;
	private String payload;

	public MessageToRepublish(final String topic, final String payload) {
		this.topic = topic;
		this.payload = payload;
	}

	public String getTopic() {
		return topic;
	}

	public String getPayload() {
		return payload;
	}

}
