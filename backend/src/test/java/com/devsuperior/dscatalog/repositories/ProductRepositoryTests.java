package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factory.ProductFactory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository productRepository;
	
	private Long exintingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		this.exintingId = 1L;
		this.nonExistingId = 1000L;
		this.countTotalProducts = 25L;
	}
	
	@Test
	public void findByIdShouldReturnOptionalProductNotEmptyWhenIdExists() {
		var optionalProduct = this.productRepository.findById(exintingId);
		var product = optionalProduct.get();
		
		Assertions.assertTrue(optionalProduct.isPresent());
		Assertions.assertEquals(exintingId, product.getId());
	}
	
	@Test
	public void findByIdShouldReturnOptionalProductEmptyWhenIdDoesNotExists() {
		var optionalProduct = this.productRepository.findById(nonExistingId);
		
		Assertions.assertFalse(optionalProduct.isPresent());
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		var product = ProductFactory.createProduct();
		product.setId(null);
		
		product = this.productRepository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		this.productRepository.deleteById(exintingId);
		
		Optional<Product> result = this.productRepository.findById(exintingId);
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			this.productRepository.deleteById(nonExistingId);
		});
	}
	
}
