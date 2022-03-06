package com.esra.ideas.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.esra.ideas.models.Idea;

@Component
public class IdeaValidator {
	
	public boolean supports(Class<?> clazz) {
		return Idea.class.equals(clazz);
	}
	
	public void validate(Object target, Errors errors) {
		Idea idea =(Idea) target;
		if(idea.getDescription().equals("")) 
			errors.rejectValue("description", "Blank", "Idea must be Present");
	
	}

}
