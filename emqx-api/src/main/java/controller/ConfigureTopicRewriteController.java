package controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import common.DatabaseHelper;

@RestController
@RequestMapping(path = "/topic_rewrite")
public class ConfigureTopicRewriteController {

    @PostMapping(path = "/add")
    public ResponseEntity<?> addData(@RequestBody Map<String, Object> params) {
        String topic = (String) params.get("topic");
        String subTopic = (String) params.get("rewrited_to");
        String clientid = (String) params.get("clientid");
        if (topic == null || topic.isBlank() || subTopic == null || subTopic.isBlank() || clientid == null
                || clientid.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (DatabaseHelper.insert(topic, subTopic, clientid) != 1) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /*
        @author Nirmine
     */
    @GetMapping(path = "/getRepublishingTopic/{idClient}/{originalTopic}")
    public String getRewriteTopic(@PathVariable String idClient, @PathVariable String originalTopic) {
        return DatabaseHelper.getById(idClient, originalTopic);
    }

    @GetMapping(path = "/getAllByOriginalTopic/{originalTopic}")
    public HashMap<String, String> getAllByOriginalTopic(@PathVariable String originalTopic) {
        return DatabaseHelper.getAllByOriginalTopic(originalTopic);
    }
}
