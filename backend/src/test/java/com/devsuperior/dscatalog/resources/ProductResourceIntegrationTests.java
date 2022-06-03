package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIntegrationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	private String username;
	private String password;
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	public void setUp() throws Exception {
		this.username = "maria@gmail.com";
		this.password = "123456";
		this.existingId = 1L;
		this.nonExistingId = 1000L;
		this.countTotalProducts = 25L;
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenShortByName() throws Exception {
		var result = this.mockMvc
				.perform(get("/products?page=0&size=12&sort=name,asc")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}
	
	@Test
	public void updateShouldReturnStatusOkWhenIdExists() throws Exception {
		var accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		var productDTO = ProductFactory.createProductDTO();
		var jsonBody = objectMapper.writeValueAsString(productDTO);
		
		var expectedName = productDTO.getName();
		var expectedDescription = productDTO.getDescription();
		
		var result = this.mockMvc
				.perform(put("/products/{id}", this.existingId)
						.header("Authorization", "Bearer " + accessToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.description").value(expectedDescription));
	}
	
	@Test
	public void updateShouldReturnStatusNotFoundWhenIdDoesNotExist() throws Exception {
		var accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

		var productDTO = ProductFactory.createProductDTO();
		var jsonBody = objectMapper.writeValueAsString(productDTO);
		
		var result = this.mockMvc
				.perform(put("/products/{id}", this.nonExistingId)
						.header("Authorization", "Bearer " + accessToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
}
