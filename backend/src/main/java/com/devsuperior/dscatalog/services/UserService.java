package com.devsuperior.dscatalog.services;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.UserDTO;
import com.devsuperior.dscatalog.dtos.UserInsertDTO;
import com.devsuperior.dscatalog.dtos.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {
	
	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		var users = this.userRepository.findAll(pageable);
		var usersDTO = users.map(user -> new UserDTO(user));
		
		return usersDTO;
	}
	
	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		var optionalUser = this.userRepository.findById(id);
		var user = optionalUser.orElseThrow(() -> new ResourceNotFoundException("User not found. Id: " + id));
	
		return new UserDTO(user);
	}
	
	@Transactional
	public UserDTO insert(UserInsertDTO userInsertDTO) {
		var user = new User();
		this.copyToEntity(userInsertDTO, user);
		user.setPassword(this.passwordEncoder.encode(userInsertDTO.getPassword()));
		
		user = this.userRepository.save(user);
		
		return new UserDTO(user);
	}
	
	@Transactional
	public UserDTO update(Long id, UserUpdateDTO userDTO) {
		try {
			var user = this.userRepository.getById(id);
			this.copyToEntity(userDTO, user);
			
			user = this.userRepository.save(user);
			
			return new UserDTO(user);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found. Id: " + id);
		}
	}
	
	public void delete(Long id) {
		try {
			this.userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found. Id: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = this.userRepository.findByEmail(username);
		
		if (user == null) {
			logger.error("User not found. User: " + username);
			throw new UsernameNotFoundException("Email not found");
		}
		
		logger.info("User found. User: " + username);
		return user;
	}

	private void copyToEntity(UserDTO userDTO, User user) {
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		
		user.getRoles().clear();
		
		userDTO.getRoles().forEach(roleDTO -> {
			var role = this.roleRepository.getById(roleDTO.getId());
			user.getRoles().add(role);
		});
	}
	
}
