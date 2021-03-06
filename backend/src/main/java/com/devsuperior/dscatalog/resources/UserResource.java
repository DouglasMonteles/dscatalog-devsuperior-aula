package com.devsuperior.dscatalog.resources;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dscatalog.dtos.UserDTO;
import com.devsuperior.dscatalog.dtos.UserInsertDTO;
import com.devsuperior.dscatalog.dtos.UserUpdateDTO;
import com.devsuperior.dscatalog.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

	@Autowired
	private UserService userService;
	
	@GetMapping
	public ResponseEntity<Page<UserDTO>> findAllPaged(Pageable pageable) {
		var usersDTO = this.userService.findAllPaged(pageable);
		return ResponseEntity.ok().body(usersDTO);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
		var userDTO = this.userService.findById(id);
		return ResponseEntity.ok().body(userDTO);
	}
	
	@PostMapping
	public ResponseEntity<UserDTO> insert(@RequestBody @Valid UserInsertDTO userInsertDTO) {
		var newUserDTO = this.userService.insert(userInsertDTO);
		
		var uri = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(newUserDTO.getId())
				.toUri();
		
		return ResponseEntity.created(uri).body(newUserDTO);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable @Valid Long id, 
			@RequestBody @Valid UserUpdateDTO userDTO) {
		var updatedUserDTO = this.userService.update(id, userDTO);
		return ResponseEntity.ok().body(updatedUserDTO);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		this.userService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
}
