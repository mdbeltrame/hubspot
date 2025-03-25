package br.com.meetime.hubspot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.meetime.hubspot.model.Token;
import br.com.meetime.hubspot.repository.TokenRepository;

@Service
public class TokenService {

	@Autowired
    private TokenRepository tokenRepository;

    public void salvar(Token token) {
    	tokenRepository.saveAndFlush(token);
    }
	
}
