package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService productService;
	
	private Long existingId;
	private Long nonExistingId;
	private PageImpl<ProductDTO> page;
	private ProductDTO productDTO;
	
	@BeforeEach
	void setUp() throws Exception {
		this.existingId = 1L;
		this.nonExistingId = 2L;
		this.productDTO = ProductFactory.createProductDTO();
		this.page = new PageImpl<>(List.of(productDTO));
		
		Mockito.when(this.productService.findAllPageable(ArgumentMatchers.any()))
			.thenReturn(page);
		
		Mockito.when(this.productService.findById(existingId))
			.thenReturn(productDTO);
		
		Mockito.when(this.productService.findById(nonExistingId))
			.thenThrow(ResourceNotFoundException.class);
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		var result = this.mockMvc
				.perform(get("/products")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
		var result = this.mockMvc
				.perform(get("/products/{id}", this.existingId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void findByIdShouldReturnStatusNotFoundWhenIdDoesNotExists() throws Exception {
		var result = this.mockMvc
				.perform(get("/products/{id}", this.nonExistingId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
}
