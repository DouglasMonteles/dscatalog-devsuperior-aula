package com.devsuperior.dscatalog.components;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.devsuperior.dscatalog.repositories.UserRepository;

@Component
public class JwtTokenEnhancer implements TokenEnhancer {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, 
			OAuth2Authentication authentication) {
		var user = this.userRepository.findByEmail(authentication.getName());
		
		if (user == null) {
			throw new UsernameNotFoundException("User not found. Username: " + authentication.getName());
		}
		
		var tokenAddionalInformation = new HashMap<String, Object>();
		
		tokenAddionalInformation.put("userFirstName", user.getFirstName());
		tokenAddionalInformation.put("userLastName", user.getLastName());
		tokenAddionalInformation.put("userId", user.getId());
		
		var token = (DefaultOAuth2AccessToken) accessToken;
		
		token.setAdditionalInformation(tokenAddionalInformation);
		
		return accessToken;
	}
	
}
