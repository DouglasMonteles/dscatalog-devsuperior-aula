package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.devsuperior.dscatalog.dtos.UserInsertDTO;
import com.devsuperior.dscatalog.repository.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void initialize(UserInsertValid constraintAnnotation) {}
	
	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {
		var list = new ArrayList<FieldMessage>();
		
		var user = this.userRepository.findByEmail(dto.getEmail());
		
		if (user != null) {
			list.add(new FieldMessage("email", "Email já cadastrado"));
		}
		
		list.forEach(e-> {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage())
				.addPropertyNode(e.getFieldName())
				.addConstraintViolation();
		});
		
		return list.isEmpty();
	}
	
}
