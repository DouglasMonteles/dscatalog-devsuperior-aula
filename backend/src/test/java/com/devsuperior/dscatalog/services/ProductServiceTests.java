package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factory.CategoryFactory;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService productService;
	
	@Mock
	private ProductRepository productRepository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private Product product;
	private PageImpl<Product> page;
	private Category category;
	
	@BeforeEach
	void setUp() throws Exception {
		this.existingId = 1L;
		this.nonExistingId = 1000L;
		this.dependentId = 4L;
		this.product = ProductFactory.createProduct();
		this.category = CategoryFactory.createCategory();
		this.page = new PageImpl<Product>(List.of(product));
		
		Mockito.when(this.productRepository.findAll((Pageable) ArgumentMatchers.any()))
			.thenReturn(page);
		
		Mockito.when(this.productRepository.save(ArgumentMatchers.any()))
			.thenReturn(product);
		
		Mockito.when(this.productRepository.getById(existingId))
			.thenReturn(product);
		
		Mockito.when(this.productRepository.getById(nonExistingId))
			.thenThrow(EntityNotFoundException.class);
		
		Mockito.when(this.productRepository.findById(existingId))
			.thenReturn(Optional.of(product));
		
		Mockito.when(this.productRepository.findById(nonExistingId))
			.thenReturn(Optional.empty());
		
		Mockito.when(this.categoryRepository.getById(existingId))
			.thenReturn(category);
		
		Mockito.when(this.categoryRepository.getById(nonExistingId))
			.thenThrow(EntityNotFoundException.class);
		
		Mockito.doNothing()
			.when(this.productRepository).deleteById(existingId);
		
		Mockito.doThrow(EmptyResultDataAccessException.class)
			.when(this.productRepository).deleteById(nonExistingId);
		
		Mockito.doThrow(DataIntegrityViolationException.class)
			.when(this.productRepository).deleteById(dependentId);

	}
	
	@Test
	public void findAllPagedShoudReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = this.productService.findAllPageable(pageable);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(this.productRepository)
			.findAll(pageable);
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		var product = this.productService.findById(existingId);
		
		Assertions.assertNotNull(product);
		
		Mockito.verify(this.productRepository, Mockito.times(1))
			.findById(existingId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			this.productService.findById(nonExistingId);
		});
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		var productDTO = ProductFactory.createProductDTO();
		var result = this.productService.update(this.existingId, productDTO);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(this.productRepository, Mockito.times(1))
			.save(this.product);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			var productDTO = new ProductDTO(this.product);
			this.productService.update(this.nonExistingId, productDTO);
		});
		
		this.product.setId(nonExistingId);

		Mockito.verify(this.productRepository, Mockito.times(0))
			.save(this.product);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			this.productService.delete(dependentId);
		});
		
		Mockito.verify(this.productRepository, Mockito.times(1))
			.deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			this.productService.delete(nonExistingId);
		});
		
		Mockito.verify(this.productRepository, Mockito.times(1))
			.deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {			
			this.productService.delete(existingId);
		});
		
		Mockito.verify(this.productRepository, Mockito.times(1))
			.deleteById(existingId);
	}
	
}
