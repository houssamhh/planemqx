package planemqx.commonPriorities;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "brokerId",
    "brokerName",
    "bufferSize",
    "processingRate",
    "topics"
})
public class MappingBroker {
    @JsonProperty("brokerId")
    private String brokerId;
    @JsonProperty("brokerName")
    private String brokerName;
    @JsonProperty("bufferSize")
    private int bufferSize;
    @JsonProperty("processingRate")
    private int processingRate;
    @JsonProperty("topics")
    private List<String> topics = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MappingBroker() {
    }

    public MappingBroker(final String brokerId, final String brokerName, final int bufferSize, final int processingRate, final List<String> topics) {
        super();
        this.brokerId = brokerId;
        this.brokerName = brokerName;
        this.bufferSize = bufferSize;
        this.processingRate = processingRate;
        this.topics = topics;
    }

    @JsonProperty("brokerId")
    public String getBrokerId() {
        return brokerId;
    }

    @JsonProperty("brokerId")
    public void setBrokerId(final String brokerId) {
        this.brokerId = brokerId;
    }

    @JsonProperty("brokerName")
    public String getBrokerName() {
        return brokerName;
    }

    @JsonProperty("brokerName")
    public void setBrokerName(final String brokerName) {
        this.brokerName = brokerName;
    }

    @JsonProperty("bufferSize")
    public int getBufferSize() {
        return bufferSize;
    }

    @JsonProperty("bufferSize")
    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @JsonProperty("processingRate")
    public int getProcessingRate() {
        return processingRate;
    }

    @JsonProperty("processingRate")
    public void setProcessingRate(final int processingRate) {
        this.processingRate = processingRate;
    }

    @JsonProperty("topics")
    public List<String> getTopics() {
        return topics;
    }

    @JsonProperty("topics")
    public void setTopics(final List<String> topics) {
        this.topics = topics;
    }
}
