package br.com.meetime.hubspot.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WebhookController {
	
	private final AtomicReference<List<Map<String, Object>>> lastWebhookData = new AtomicReference<>();

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processarWebhook(@RequestBody List<Map<String, Object>> payload) {
        System.out.println("Webhook recebido: " + payload);
        lastWebhookData.set(payload);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/webhook-data")
    public ResponseEntity<?> getWebhookData() {
        List<Map<String, Object>> data = lastWebhookData.get();
        if (data == null || data.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(data);
    }
	
}
