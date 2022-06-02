package com.devsuperior.dscatalog.services;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductRepository productRepository;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	private Long categoryId;
	private String name;
	
	@BeforeEach
	public void setUp() throws Exception {
		this.existingId = 1L;
		this.nonExistingId = 1000L;
		this.countTotalProducts = 25L;
		this.categoryId = 1L;
		this.name = "pc gamer";
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0Size10() {
		var pageRequest = PageRequest.of(0, 10);
		var result = this.productService.findAllPageable(categoryId, name, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(0, result.getNumber());
		Assertions.assertEquals(10, result.getSize());
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	@Test
	public void findAllPageShouldReturnEmptyPageWhenPageDoesNotExist() {
		var pageRequest = PageRequest.of(50, 10);
		var result = this.productService.findAllPageable(categoryId, name, pageRequest);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findAllPageShouldReturnShortedPageWhenSortByName() {
		var pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		var result = this.productService.findAllPageable(categoryId, name, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() throws Exception {
		this.productService.delete(existingId);
		
		Assertions.assertEquals((countTotalProducts - 1), this.productRepository.count());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			this.productService.delete(nonExistingId);
		});
	}
	
}
