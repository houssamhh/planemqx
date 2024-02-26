package planemqx.commonPriorities;

import static planemqx.common.Log.GEN;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import planemqx.common.Configuration;
import planemqx.common.Log;

public class APIServices {
	private CloseableHttpClient httpClient;

	public APIServices() {
		CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(Configuration.ADMIN_LOGIN, Configuration.ADMIN_PASSWORD));
		httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
	}

	public void close() throws IOException {
		this.httpClient.close();
	}

	public void addRule(final String originalTopic, final String republishingTopic) throws IOException {
		// add request parameter, form parameters
		HttpPost request = new HttpPost("http://10.0.0.252:18083/api/v5/users");	//for mininet
		BrokerRule brokerRule = new BrokerRule("republish", "SELECT * FROM \\\"" + originalTopic + "\\\"", true,
				republishingTopic, " ");
		StringEntity params = new StringEntity(brokerRule.toString());
		request.addHeader("content-type", "application/json");
		request.setEntity(params);
		HttpResponse response = httpClient.execute(request);
		if (Log.ON) {
			// do not use a lambda expression because of exceptions
			GEN.info(EntityUtils.toString(response.getEntity()));
		}

	}

	public void addAgentUser() throws IOException {
		HttpPost request = new HttpPost("http://10.0.0.252:18083/api/v5/users");	//for mininet
		String reques = "{\n" + "  \"username\": \"agent\",\n" + "  \"password\": \"agent\",\n"
				+ "  \"description\": \"planiot-agent\"\n" + "}";
		StringEntity params = new StringEntity(reques);
		request.addHeader("content-type", "application/json");
		request.setEntity(params);
		if (Log.ON) {
			GEN.info("{}", () -> request);
		}
		HttpResponse response = httpClient.execute(request);
		if (Log.ON) {
			GEN.info(EntityUtils.toString(response.getEntity()));
		}
	}

//	void getRules() throws IOException {
//		HttpGet request = new HttpGet("http://localhost:18083/api/v5/rules");
//		CloseableHttpResponse response = httpClient.execute(request);
//		if (Log.ON) {
//			GEN.info("{}", () -> response.getStatusLine().getStatusCode());
//		}
//		HttpEntity entity = response.getEntity();
//		if (entity != null) {
//			String result = EntityUtils.toString(entity);
//			if (Log.ON) {
//				GEN.info("{}", () -> result);
//			}
//		}
//		response.close();
//	}

	void closeClient() throws IOException {
		httpClient.close();
	}

	public static void main(final String[] args) throws IOException {
		APIServices apiServices = new APIServices();
		apiServices.addAgentUser();
	}
}
