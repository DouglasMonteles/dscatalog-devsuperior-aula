package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repository.CategoryRepository;
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
	public CategoryDTO update(CategoryDTO categoryDTO) {
		try {
			var category = categoryRepository.getById(categoryDTO.getId());
			
			category.setName(categoryDTO.getName());
			
			categoryRepository.save(category);
			
			return new CategoryDTO(category);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + categoryDTO.getId());
		}
	}
	
}
