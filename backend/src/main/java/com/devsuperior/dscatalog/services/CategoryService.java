package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repository.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {
	
	private final String MSG_NOT_FOUND = "Entity not found"; 
	
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
			new EntityNotFoundException(MSG_NOT_FOUND));
		
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
	
}
