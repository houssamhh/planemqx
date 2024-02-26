package controller;

import static common.Log.GEN;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import common.DatabaseHelper;
import common.Log;
import common.ServerConfigurationHelper;

@RestController
@RequestMapping(path = "/authorization")
public class AuthorizationController {

	private class Response {

		private String result;

		@SuppressWarnings("unused")
		public String getResult() {
			return result;
		}

		public Response setResult(String result) {
			this.result = result;
			return this;
		}

	}

	@PostMapping(path = "/auth")
	public ResponseEntity<Response> handleAuthorization(@RequestBody Map<String, Object> params) {

		Map<String, String> database = DatabaseHelper.select();

		String action = (String) params.get("action");

		if (!action.equals("subscribe")) {
			return ResponseEntity.status(200).body(new Response().setResult("allow"));
		}

		String clientId = (String) params.get("clientid");
		String topic = (String) params.get("topic");
		String subTopic;
		if ((subTopic = database.get(clientId + topic)) == null) {
			return ResponseEntity.status(200).body(new Response().setResult("allow"));
		}
		String url = ServerConfigurationHelper.BORKER_URL + "/clients/" + clientId + "/subscribe";
		String body = "{ \"topic\": \"" + subTopic + "\", \"qos\": 1 }";

		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(ServerConfigurationHelper.LOGIN, ServerConfigurationHelper.PASSWORD);
		headers.setContentType(MediaType.APPLICATION_JSON);
		List<MediaType> l = new ArrayList<MediaType>();
		l.add(MediaType.APPLICATION_JSON);
		headers.setAccept(l);
		ResponseEntity<String> response = new RestTemplate().postForEntity(url, new HttpEntity<Object>(body, headers),
				String.class);

		if (response.getStatusCodeValue() != 200) {
			if (Log.ON) {
				GEN.info("{}", () -> response.getStatusCodeValue());
			}
			return ResponseEntity.status(200).body(new Response().setResult("allow"));
		}
		return ResponseEntity.status(200).body(new Response().setResult("deny"));
	}

}
