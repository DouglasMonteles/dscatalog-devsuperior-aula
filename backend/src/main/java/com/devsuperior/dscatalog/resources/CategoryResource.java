package com.devsuperior.dscatalog.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public ResponseEntity<List<CategoryDTO>> findAll() {
		var categoriesDTO = categoryService.findAll(); 
		return ResponseEntity.ok().body(categoriesDTO);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
		var categoryDTO = categoryService.findById(id);
		return ResponseEntity.ok().body(categoryDTO);
	}
	
	@PostMapping
	public ResponseEntity<Void> insert(@RequestBody CategoryDTO categoryDTO) {
		categoryDTO = categoryService.insert(categoryDTO);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(categoryDTO.getId())
				.toUri();
		
		return ResponseEntity.created(uri).build();
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> update(@PathVariable Long id, 
			@RequestBody CategoryDTO categoryDTO) {
		categoryDTO.setId(id);
		categoryDTO = categoryService.update(categoryDTO);
		
		return ResponseEntity.ok().build();
	}
	
}
