package planemqx.commonPriorities;

public class BrokerRule {

    private String name;
    private String sql;
    private Boolean enable;
    private String topic;
    private String description;

    @Override
    public String toString() {
       return "{\n" +
                "  \"name\": \""+name +"\",\n" +
                "  \"sql\": \""+sql+"\",\n" +
                "  \"actions\": [\n" +
                "    \"webhook:my_webhook\",\n" +
                "    {\n" +
                "      \"args\": {\n" +
                "        \"payload\": \"${payload}\",\n" +
                "        \"topic\": \""+topic+"\"\n" +
                "      },\n" +
                "      \"function\": \"republish\"\n" +
                "    },\n" +
                "  ],\n" +
                "  \"enable\": "+enable+",\n" +
                "  \"description\": \""+description+"\",\n" +
                "  \"metadata\": {}\n" +
                "}";
    }

    public BrokerRule(final String name, final String sql, final Boolean enable, final String topic,
    		final String description) {
        this.name = name;
        this.sql = sql;
        this.enable = enable;
        this.topic = topic;
        this.description = description;
    }
}
