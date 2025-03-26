package br.com.meetime.hubspot.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.meetime.hubspot.model.Token;
import br.com.meetime.hubspot.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AutorizacaoController {

	@Autowired
	private TokenService tokenService;
	
	private String CLIENT_ID;
	private String CLIENT_SECRET;
    
    @PostMapping("/authorize")
    public ResponseEntity<String> generateAuthUrl(HttpServletResponse response,@RequestBody Map<String, String> autorizacao) throws IOException {
    	
    	this.CLIENT_ID = autorizacao.get("clientId");
    	this.CLIENT_SECRET = autorizacao.get("clientSecret");
    	
    	String authUrl = "https://app.hubspot.com/oauth/authorize" +
                "?client_id="+CLIENT_ID+
                "&scope=crm.objects.contacts.write%20crm.objects.contacts.read%20oauth"+
    			"&redirect_uri=https://hubspot-production-e626.up.railway.app/auth/callback";
        
    	return ResponseEntity.ok(authUrl);
    }
    
    @GetMapping("/callback")
    private RedirectView getTokenFromCode(@RequestParam("code") String code) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", CLIENT_ID);
        requestBody.add("client_secret", CLIENT_SECRET);
        requestBody.add("redirect_uri", "https://hubspot-production-e626.up.railway.app/auth/callback");
        requestBody.add("code", code);

        // Configura o cabeçalho da requisição
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Criando a entidade HTTP com os parâmetros e cabeçalhos
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Envia a requisição e recebe a resposta
            ResponseEntity<String> response = restTemplate.exchange("https://api.hubapi.com/oauth/v1/token", HttpMethod.POST, requestEntity, String.class);
            
         // Processa o JSON
            String jsonResponse = response.getBody();
            
                // Usando Jackson para ler o JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                
                // Extrair os valores necessários do JSON
                String accessToken = jsonNode.get("access_token").asText();
                String refreshToken = jsonNode.get("refresh_token").asText();
                Integer expiresIn = jsonNode.get("expires_in").asInt();
                String tokenType = jsonNode.get("token_type").asText();
                
            	Token token = new Token();
                token.setAccessToken(accessToken);
                token.setRefreshToken(refreshToken);
                token.setExpiresIn(expiresIn);
                token.setTokenType(tokenType);
                token.setClientId(CLIENT_ID);
                token.setClientSecret(CLIENT_SECRET);
                token.setDataSalvamento(LocalDate.now());
                token.setHoraSalvamento(LocalTime.now());
                
                // Salvar o token no banco de dados
                tokenService.salvar(token);
                
                // Redireciona com uma mensagem de sucesso e com o token para conseguir cadastrar novos contatos na mesma tela
                return new RedirectView("/TokenSalvo.html?success=Token%20Gerado%20Com%20Sucesso&token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8));
            } catch (Exception e) {
                // Retorna erro caso algo dê errado
                return new RedirectView("/Dashboard.html?error=Erro%20ao%20trocar%20c%C3%B3digo%20pelo%20token.");
            }
    }
    
}
