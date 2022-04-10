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

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repository.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		var categories = categoryRepository.findAll();
		var categoriesDTO = categories
				.stream()
				.map(category -> new CategoryDTO(category))
				.collect(Collectors.toList());
		
		return categoriesDTO;
	}
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPageable(Pageable pageable) {
		var pageCategory = categoryRepository.findAll(pageable);
		var pageCategoryDTO = pageCategory
				.map(category -> new CategoryDTO(category));
		
		return pageCategoryDTO; 
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		var optionalCategory = categoryRepository.findById(id);
		var category = optionalCategory.orElseThrow(() -> 
			new EntityNotFoundException("Entity not found"));
		
		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO categoryDTO) {
		var category = new Category();
		
		category.setId(null);
		category.setName(categoryDTO.getName());
		
		category = categoryRepository.save(category);
		
		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
		try {
			var category = categoryRepository.getById(id);
			
			category.setName(categoryDTO.getName());
			
			categoryRepository.save(category);
			
			return new CategoryDTO(category);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + categoryDTO.getId());
		}
	}

	public void delete(Long id) {
		try {
			categoryRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
}
