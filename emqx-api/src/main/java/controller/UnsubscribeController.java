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
@RequestMapping(path = "/unsubscribe")
public class UnsubscribeController {

	@PostMapping(path = "/app")
	public void handleUnsubscription(@RequestBody Map<String, Object> params) {
		Map<String, String> database = DatabaseHelper.select();
		String client_id = (String) params.get("clientid");
		String topic = (String) params.get("topic");
		String rewrited_topic;
		if ((rewrited_topic = database.get(client_id + topic)) == null) {
			return;
		}
		String url = ServerConfigurationHelper.BORKER_URL + "/clients/" + client_id + "/unsubscribe";
		String body = "{ \"topic\": \"" + rewrited_topic + "\" }";
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(ServerConfigurationHelper.LOGIN, ServerConfigurationHelper.PASSWORD);
		headers.setContentType(MediaType.APPLICATION_JSON);
		List<MediaType> l = new ArrayList<MediaType>();
		l.add(MediaType.APPLICATION_JSON);
		headers.setAccept(l);
		ResponseEntity<String> response = new RestTemplate().postForEntity(url, new HttpEntity<Object>(body, headers),
				String.class);
		if (response.getStatusCodeValue() != 204) {
			if (Log.ON) {
				GEN.info("{}", () -> response.getStatusCodeValue());
			}
		}
		if (Log.ON) {
			GEN.info("{}", () -> "Unsubscribe [" + client_id + ", " + topic + ", " + System.currentTimeMillis() + "]");
		}
	}
}
