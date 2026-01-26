/**
 * Common validation utilities for frontend form and data validation
 * Provides reusable validation functions for email, password, username, etc.
 */

export interface ValidationResult {
  isValid: boolean;
  errors: string[];
}

/**
 * Email validation
 */
export function validateEmail(email: string): ValidationResult {
  const errors: string[] = [];
  
  if (!email) {
    errors.push('Email is required');
    return { isValid: false, errors };
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    errors.push('Invalid email format');
  }
  
  return { isValid: errors.length === 0, errors };
}

/**
 * Password validation
 * Requirements: at least 8 characters, 1 uppercase, 1 lowercase, 1 number
 */
export function validatePassword(password: string): ValidationResult {
  const errors: string[] = [];
  
  if (!password) {
    errors.push('Password is required');
    return { isValid: false, errors };
  }
  
  if (password.length < 8) {
    errors.push('Password must be at least 8 characters');
  }
  
  if (!/[A-Z]/.test(password)) {
    errors.push('Password must contain at least one uppercase letter');
  }
  
  if (!/[a-z]/.test(password)) {
    errors.push('Password must contain at least one lowercase letter');
  }
  
  if (!/[0-9]/.test(password)) {
    errors.push('Password must contain at least one number');
  }
  
  return { isValid: errors.length === 0, errors };
}

/**
 * Password match validation
 */
export function validatePasswordMatch(password: string, confirmPassword: string): ValidationResult {
  const errors: string[] = [];
  
  if (password !== confirmPassword) {
    errors.push('Passwords do not match');
  }
  
  return { isValid: errors.length === 0, errors };
}

/**
 * Username validation
 * Requirements: 3-20 characters, alphanumeric and underscore only
 */
export function validateUsername(username: string): ValidationResult {
  const errors: string[] = [];
  
  if (!username) {
    errors.push('Username is required');
    return { isValid: false, errors };
  }
  
  if (username.length < 3) {
    errors.push('Username must be at least 3 characters');
  }
  
  if (username.length > 20) {
    errors.push('Username must be at most 20 characters');
  }
  
  if (!/^[a-zA-Z0-9_]+$/.test(username)) {
    errors.push('Username can only contain letters, numbers, and underscores');
  }
  
  return { isValid: errors.length === 0, errors };
}

/**
 * Text field validation (general)
 */
export function validateText(text: string, minLength: number = 1, maxLength: number = 500, fieldName: string = 'Text'): ValidationResult {
  const errors: string[] = [];
  
  if (!text || text.trim().length === 0) {
    errors.push(`${fieldName} is required`);
    return { isValid: false, errors };
  }
  
  if (text.length < minLength) {
    errors.push(`${fieldName} must be at least ${minLength} character${minLength !== 1 ? 's' : ''}`);
  }
  
  if (text.length > maxLength) {
    errors.push(`${fieldName} must be at most ${maxLength} character${maxLength !== 1 ? 's' : ''}`);
  }
  
  return { isValid: errors.length === 0, errors };
}

/**
 * Date validation (check if date is in the past)
 */
export function validatePastDate(dateString: string): ValidationResult {
  const errors: string[] = [];
  
  if (!dateString) {
    errors.push('Date is required');
    return { isValid: false, errors };
  }
  
  const selectedDate = new Date(dateString);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  if (selectedDate > today) {
    errors.push('Date cannot be in the future');
  }
  
  return { isValid: errors.length === 0, errors };
}

/**
 * URL validation
 */
export function validateUrl(url: string): ValidationResult {
  const errors: string[] = [];
  
  if (!url) {
    errors.push('URL is required');
    return { isValid: false, errors };
  }
  
  try {
    new URL(url);
  } catch (e) {
    errors.push('Invalid URL format');
  }
  
  return { isValid: errors.length === 0, errors };
}

/**
 * Phone number validation (basic international format)
 */
export function validatePhoneNumber(phone: string): ValidationResult {
  const errors: string[] = [];
  
  if (!phone) {
    errors.push('Phone number is required');
    return { isValid: false, errors };
  }
  
  // Simple regex for phone numbers: +1-123-456-7890 or similar variations
  const phoneRegex = /^[\d\s\-\+\(\)]+$/;
  if (!phoneRegex.test(phone)) {
    errors.push('Invalid phone number format');
  }
  
  // Remove non-digits and check length (should be 10+ digits)
  const digitsOnly = phone.replace(/\D/g, '');
  if (digitsOnly.length < 10) {
    errors.push('Phone number must contain at least 10 digits');
  }
  
  return { isValid: errors.length === 0, errors };
}

/**
 * File size validation
 */
export function validateFileSize(file: File, maxSizeInMB: number): ValidationResult {
  const errors: string[] = [];
  
  if (!file) {
    errors.push('File is required');
    return { isValid: false, errors };
  }
  
  const maxSizeInBytes = maxSizeInMB * 1024 * 1024;
  if (file.size > maxSizeInBytes) {
    errors.push(`File size must be less than ${maxSizeInMB}MB`);
  }
  
  return { isValid: errors.length === 0, errors };
}

/**
 * Image file validation
 */
export function validateImageFile(file: File, maxSizeInMB: number = 5): ValidationResult {
  const errors: string[] = [];
  
  const sizeValidation = validateFileSize(file, maxSizeInMB);
  if (!sizeValidation.isValid) {
    errors.push(...sizeValidation.errors);
  }
  
  const allowedMimeTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!allowedMimeTypes.includes(file.type)) {
    errors.push('File must be a valid image (JPEG, PNG, GIF, or WebP)');
  }
  
  return { isValid: errors.length === 0, errors };
}

/**
 * Combine multiple validation results
 */
export function combineValidations(...validations: ValidationResult[]): ValidationResult {
  const allErrors = validations.flatMap(v => v.errors);
  return {
    isValid: allErrors.length === 0,
    errors: allErrors
  };
}
