package planemqx.common;

import static planemqx.common.Log.COMM;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class APIConsumerHelper {

	private APIConsumerHelper() {
	}

	public static String request(final String url, final String body, final String login, final String password,
			final RequestType requestType) throws IOException {
		if (url == null || url.isBlank() || requestType == null) {
			throw new IllegalArgumentException();
		}
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(url);
		Builder builder = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		if (login != null && password != null) {
			builder.header(HttpHeaders.AUTHORIZATION, "Basic " + getCredentials(login, password));
		}
		Response response = null;
		switch (requestType) {
		case GET:
			response = builder.get();
			break;
		case POST:
			response = builder.post(Entity.entity(body, MediaType.APPLICATION_JSON));
			break;
		case PUT:
			response = builder.put(Entity.entity(body, MediaType.APPLICATION_JSON));
			break;
		case DELETE:
			response = builder.delete();
			break;
		default:
			if (Log.ON) {
				COMM.error("{}", () -> "Error processing the request (requestType=" + requestType + ")");
			}
			throw new IOException("Error processing the request (requestType=" + requestType + ")");
		}
		int responseCode = response.getStatus();
		if ((responseCode < 200 && responseCode > 299)) {
			if (Log.ON) {
				COMM.error("{}", () -> "Error processing the request" + responseCode);
			}
			client.close();
			throw new IOException("Error processing the request" + responseCode);
		}
		String content = response.readEntity(String.class);
		client.close();
		if (Log.ON)
			COMM.info("{}", () -> "request success " + responseCode + ": " + content);
		return content;
	}

	public static String request(final String url, final Map<String, Object> body, final String login,
			final String password, final RequestType requestType) throws IOException {
		return request(url, JSONHelper.parseToString(body), login, password, requestType);
	}

	private static String getCredentials(final String login, final String password) {
		return Base64.getEncoder().encodeToString((login + ":" + password).getBytes());
	}
}
