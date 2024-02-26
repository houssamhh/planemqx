package planemqx.commonPriorities;

import static planemqx.common.Log.COMM;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import planemqx.common.Log;

public class ServerServices {
	CloseableHttpClient httpClient;

	public ServerServices() {
		httpClient = HttpClientBuilder.create().build();
	}

	public void request(final String idClient, final String originalTopic) throws IOException {
		HttpGet request = new HttpGet(
				"http://localhost:8080/topic_rewrite/getRepublishingTopic/" + idClient + "/" + originalTopic);
		CloseableHttpResponse response = httpClient.execute(request);
		if (Log.ON) {
			COMM.info("{}", () -> response.getStatusLine().getStatusCode());
		}
		HttpEntity entity = response.getEntity();
		if (Log.ON) {
			if (entity != null) {
				String result = EntityUtils.toString(entity);
				COMM.info("{}", () -> result);
			}
		}
		response.close();
	}

	public HashMap<String, String> requestAllByOriginalTopic(final String originalTopic) throws IOException {
		HttpGet request = new HttpGet("http://localhost:8080/topic_rewrite/getAllByOriginalTopic/" + originalTopic);
		CloseableHttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		Gson gson = new GsonBuilder().create();
		if (entity != null) {
			return gson.fromJson(EntityUtils.toString(entity), HashMap.class);
		}
		response.close();
		return new HashMap<>();

	}

	public void close() throws IOException {
		httpClient.close();
	}

	public static void main(final String[] args) throws IOException {
		ServerServices serverServices = new ServerServices();
		serverServices.requestAllByOriginalTopic("smoke");
	}
}
