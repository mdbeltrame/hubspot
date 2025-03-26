package br.com.meetime.hubspot.controller;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WebhookController {
	
	 private final AtomicReference<Map<String, Object>> lastWebhookData = new AtomicReference<>();

	    @PostMapping(value = "/webhook", consumes = "application/json")
	    public ResponseEntity<?> processarWebhook(@RequestBody Map<String, Object> payload) {
	        System.out.println("Webhook recebido: " + payload);
	        
	        // Salva os dados do webhook para serem acessados depois
	        lastWebhookData.set(payload);

	        return ResponseEntity.ok(payload);
	    }

	    @GetMapping("/webhook-data")
	    public ResponseEntity<Map<String, Object>> getWebhookData() {
	        Map<String, Object> data = lastWebhookData.get();
	        if (data == null) {
	            return ResponseEntity.noContent().build(); // Nenhum dado disponível ainda
	        }
	        return ResponseEntity.ok(data);
	    }
	
}
