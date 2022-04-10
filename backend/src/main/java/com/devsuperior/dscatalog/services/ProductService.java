package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repository.CategoryRepository;
import com.devsuperior.dscatalog.repository.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public List<ProductDTO> findAll() {
		var products = this.productRepository.findAll();
		var productsDTO = products
				.stream()
				.map(product -> new ProductDTO(product))
				.collect(Collectors.toList());
		
		return productsDTO;
	}
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPageable(Pageable pageable) {
		var productsPage = this.productRepository.findAll(pageable);
		var productsPageDTO = productsPage.map(product -> new ProductDTO(product));
		
		return productsPageDTO;
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		var optionalProduct = this.productRepository.findById(id);
		var product = optionalProduct.orElseThrow(() ->
					new ResourceNotFoundException("Entity not found")
				);
		
		return new ProductDTO(product, product.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO productDTO) {
		var product = new Product();
		
		this.copyDtoToEntity(productDTO, product);
		
		product = this.productRepository.save(product);
		
		return new ProductDTO(product);
	}
	
	@Transactional
	public ProductDTO update(Long id, ProductDTO productDTO) {
		try {
			var product = this.productRepository.getById(id);
			
			this.copyDtoToEntity(productDTO, product);
			
			product = this.productRepository.save(product);
			
			return new ProductDTO(product);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found" + productDTO.getId());
		}
	}
	
	public void delete(Long id) {
		try {
			this.productRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(ProductDTO productDTO, Product product) {
		product.setName(productDTO.getName());
		product.setDescription(productDTO.getDescription());
		product.setPrice(productDTO.getPrice());
		product.setImgUrl(productDTO.getImgUrl());
		
		product.getCategories().clear();
		
		productDTO.getCategories().forEach(categoryDTO -> {
			var category = this.categoryRepository.getById(categoryDTO.getId());
			product.getCategories().add(category);
		});
	}
	
}
