package br.com.meetime.hubspot.controller;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.meetime.hubspot.model.Token;
import br.com.meetime.hubspot.repository.TokenRepository;
import br.com.meetime.hubspot.service.TokenService;



@RestController
@RequestMapping("/contato")
public class ContratoController {

	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private TokenService tokenService;
	
    @PostMapping("/salvar")
    public ResponseEntity<Map<String, Object>> salvarContato(@RequestBody Map<String, String> contato) throws JsonMappingException, JsonProcessingException {
        
    	
    	Optional<Token> token = tokenRepository.findByaccessToken(contato.get("token"));
    	Token tokenRenovado = new Token();
    	
    	if (token.isPresent()) {
    		if(!isTokenValido(token.get())) {
    			tokenRenovado = renovarToken(token.get());
        	}else {
        		tokenRenovado = token.get();
        	}
    	}
    	
    		RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + tokenRenovado.getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, String> properties = new HashMap<>();

            properties.put("firstname", contato.get("nome"));
            properties.put("lastname", contato.get("sobrenome"));
            properties.put("email", contato.get("email"));

            requestBody.put("properties", properties);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange("https://api.hubapi.com/crm/v3/objects/contacts", HttpMethod.POST, requestEntity, String.class);
                if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                	Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("success", true);
                    responseBody.put("message", "Contato cadastrado com sucesso!");
                    responseBody.put("token", tokenRenovado.getAccessToken());
                    
                    
                    Map<String, Object> webhookData = processarWebhook(requestBody); // Processa o webhook
                    String webhookDataString = URLEncoder.encode(new ObjectMapper().writeValueAsString(webhookData), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20"); // Substitui "+" por "%20" para evitar problemas na URL
                    responseBody.put("webhookData", webhookDataString);
                    
                    return ResponseEntity.ok(responseBody);
                } else {
                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("success", false);
                    responseBody.put("message", "Erro ao cadastrar o contato.");
                    return ResponseEntity.status(response.getStatusCode()).body(responseBody);
                }
            } catch (Exception e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Erro ao se comunicar com o HubSpot.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
    }
    
    public Boolean isTokenValido(Token token) {
        if (token.getCodigo() == null) {
            return false; // Token não encontrado
        }

        Integer expiresIn = token.getExpiresIn(); // Tempo de expiração em segundos
        LocalDate dataSalvamento = token.getDataSalvamento();
        LocalTime horaSalvamento = token.getHoraSalvamento();

        // Criar um LocalDateTime a partir da data e hora de salvamento
        LocalDateTime dataHoraSalvamento = LocalDateTime.of(dataSalvamento, horaSalvamento);

        // Somar o tempo de expiração ao momento do salvamento
        LocalDateTime dataHoraExpiracao = dataHoraSalvamento.plusSeconds(expiresIn);

        // Obter o horário atual
        LocalDateTime agora = LocalDateTime.now();

        // Comparar se o token ainda é válido
        return agora.isBefore(dataHoraExpiracao);
    }
    
    public Token renovarToken(Token token) throws JsonMappingException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        // Criando o corpo da requisição como um Map (irá virar JSON)
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("client_id", token.getClientId());
        requestBody.add("client_secret", token.getClientSecret());
        requestBody.add("redirect_uri", "https://hubspot-production-e626.up.railway.app/contato/salvar");
        requestBody.add("refresh_token", token.getRefreshToken());

     // Configurando os cabeçalhos para o tipo de mídia correto
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Criando a entidade HTTP com o corpo e os cabeçalhos
        //HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

     // Criando a entidade HTTP corretamente com tipo explícito
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);
        
            ResponseEntity<String> response = restTemplate.exchange("https://api.hubapi.com/oauth/v1/token", HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Converter a resposta JSON para extrair os tokens
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                String novoAccessToken = jsonNode.get("access_token").asText();
                String novoRefreshToken = jsonNode.get("refresh_token").asText();

                token.setAccessToken(novoAccessToken);
                token.setRefreshToken(novoRefreshToken);
                token.setDataSalvamento(LocalDate.now());
                token.setHoraSalvamento(LocalTime.now());
                // Salvar o token no banco de dados
                tokenService.salvar(token);
                
            } else {
            	return new Token();
            }
            return token;
    }
	
    @PostMapping("/webhook")
    public Map<String, Object> processarWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Webhook recebido: " + payload);
        return payload;
    }
    
    
    
}
