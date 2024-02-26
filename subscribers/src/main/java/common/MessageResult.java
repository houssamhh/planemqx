package common;

public class MessageResult {

	public long timestamp;
	public String topic;
	public long latency;
	
	public MessageResult(long timestamp, String topic, long latency) {
		this.timestamp = timestamp;
		this.topic = topic;
		this.latency = latency;
	}
}
