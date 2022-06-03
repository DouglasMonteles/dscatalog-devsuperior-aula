package com.devsuperior.dscatalog.utils;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

@Component
public class TokenUtil {

	@Value("${security.oauth2.client.client-id}")
	private String clientId;
	
	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;
	
	public String obtainAccessToken(MockMvc mock, String username, String password) throws Exception {
		var params = new LinkedMultiValueMap<String, String>();
		
		params.add("grant_type", "password");
		params.add("client_id", clientId);
		params.add("username", username);
		params.add("password", password);
		
		var result = mock.perform(post("/oauth/token")
				.params(params)
				.with(httpBasic(clientId, clientSecret))
				.accept("application/json;chatset=UTF-8"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));
		
		var resultString = result.andReturn().getResponse().getContentAsString();
		
		var jsonParse = new JacksonJsonParser();
		
		return jsonParse.parseMap(resultString).get("access_token").toString();
	}
	
}
