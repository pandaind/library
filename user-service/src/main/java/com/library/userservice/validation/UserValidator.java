package com.library.userservice.validation;

import com.library.userservice.exception.InvalidUserDataException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Component
public class UserValidator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{1,14}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_]{3,50}$"
    );
    
    public void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new InvalidUserDataException("email", "cannot be empty");
        }
        
        if (email.length() > 100) {
            throw new InvalidUserDataException("email", "must not exceed 100 characters");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidUserDataException("email", "must be a valid email address");
        }
    }
    
    public void validateUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new InvalidUserDataException("username", "cannot be empty");
        }
        
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new InvalidUserDataException("username", 
                "must be 3-50 characters and contain only letters, numbers, and underscores");
        }
    }
    
    public void validatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            if (phone.length() > 20) {
                throw new InvalidUserDataException("phone", "must not exceed 20 characters");
            }
            
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new InvalidUserDataException("phone", 
                    "must be a valid international phone number format");
            }
        }
    }
    
    public void validateName(String name, String fieldName) {
        if (!StringUtils.hasText(name)) {
            throw new InvalidUserDataException(fieldName, "cannot be empty");
        }
        
        if (name.length() > 50) {
            throw new InvalidUserDataException(fieldName, "must not exceed 50 characters");
        }
        
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            throw new InvalidUserDataException(fieldName, "can only contain letters and spaces");
        }
    }
}