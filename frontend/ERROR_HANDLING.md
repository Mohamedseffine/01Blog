# Frontend Error Handling Guide

This document outlines the comprehensive error handling improvements made to the frontend application.

## Overview

The frontend now has a centralized, type-safe error handling system with:
- **ErrorService**: Centralized error state management
- **Error Alert Component**: User-friendly error display with auto-dismiss
- **Enhanced Error Interceptor**: HTTP error handling with token refresh
- **Validation Utilities**: Input validation functions
- **HttpClient Wrapper**: Request/response logging

## Architecture

### 1. ErrorService (`/app/core/services/error.service.ts`)

Centralized error management service using RxJS BehaviorSubject.

**Features:**
- Type-safe `AppError` interface with error type (error/warning/info/success)
- Auto-dismiss functionality (configurable duration)
- HTTP status code to user-friendly message mapping
- Error queue management with `dismissError()` and `clearErrors()`
- Reactive error stream via `errors$` observable

**Usage:**
```typescript
import { ErrorService } from '@core/services/error.service';

constructor(private errorService: ErrorService) {}

// Add error
this.errorService.addError('Something went wrong!');

// Add warning
this.errorService.addWarning('Please check your input');

// Add success
this.errorService.addSuccess('Operation completed!');

// Add info
this.errorService.addInfo('Processing your request...');

// Get user-friendly message for HTTP status
const message = ErrorService.getErrorMessage(404);
```

**AppError Interface:**
```typescript
interface AppError {
  message: string;
  type: 'error' | 'warning' | 'info' | 'success';
  code?: number;           // Optional HTTP status code
  timestamp: Date;
  dismissible?: boolean;   // Default: true
  duration?: number;       // Auto-dismiss in milliseconds (0 = persistent)
}
```

### 2. Error Alert Component (`/app/core/components/error-alert/error-alert.component.ts`)

Standalone component that displays errors as toasts with animations.

**Features:**
- Fixed position (top-right) with responsive design
- Bootstrap alert styling with color-coded messages
- Icon indicators (❌ error, ⚠️ warning, ✅ success, ℹ️ info)
- Auto-dismiss based on error duration
- Manual dismiss with close button
- Smooth fade-in/out animations

**Usage:**
Add to app root component:
```typescript
import { ErrorAlertComponent } from '@core/components/error-alert/error-alert.component';

@Component({
  standalone: true,
  imports: [ErrorAlertComponent],
  template: `
    <app-error-alert></app-error-alert>
    <router-outlet></router-outlet>
  `
})
```

### 3. Enhanced Error Interceptor (`/core/interceptors/error.interceptor.ts`)

Global HTTP error handler with automatic token refresh.

**Features:**
- Automatic JWT token refresh on 401 responses
- Status-specific error messages:
  - 400: Validation errors
  - 401: Session expired (with redirect to login)
  - 403: Permission denied
  - 404: Resource not found
  - 409: Conflict (e.g., duplicate data)
  - 422: Unprocessable entity (validation)
  - 500+: Server error
  - 0: Network/connection error

**Error Mapping:**
```typescript
// HTTP Status → User-Friendly Message
0     → "Unable to connect to server..."
400   → "Please check your input and try again"
401   → "Your session has expired..."
403   → "You do not have permission..."
404   → "Resource not found"
409   → Error from backend or "This action conflicts..."
422   → "Please check your input and try again"
500+  → "Server error. Please try again later"
```

### 4. Validation Utilities (`/app/shared/utils/validators.ts`)

Comprehensive validation functions for form inputs.

**Available Validators:**

```typescript
// Email validation
validateEmail(email): ValidationResult

// Password (8+ chars, uppercase, lowercase, number)
validatePassword(password): ValidationResult

// Password matching
validatePasswordMatch(password, confirmPassword): ValidationResult

// Username (3-20 chars, alphanumeric + underscore)
validateUsername(username): ValidationResult

// Text field (min/max length)
validateText(text, minLength, maxLength, fieldName): ValidationResult

// Past date validation
validatePastDate(dateString): ValidationResult

// URL validation
validateUrl(url): ValidationResult

// Phone number validation
validatePhoneNumber(phone): ValidationResult

// File size validation
validateFileSize(file, maxSizeInMB): ValidationResult

// Image file validation
validateImageFile(file, maxSizeInMB): ValidationResult

// Combine multiple validations
combineValidations(...validations): ValidationResult
```

**Example Usage:**
```typescript
import * as Validators from '@shared/utils/validators';

// Validate email
const result = Validators.validateEmail('user@example.com');
if (!result.isValid) {
  this.errorService.addError(result.errors[0]);
}

// Validate password
const pwdResult = Validators.validatePassword(password);
if (!pwdResult.isValid) {
  pwdResult.errors.forEach(err => this.errorService.addError(err));
}

// Combine multiple validations
const combined = Validators.combineValidations(
  Validators.validateUsername(username),
  Validators.validateEmail(email),
  Validators.validatePassword(password)
);
```

### 5. HttpClient Wrapper (`/app/core/services/http-client.service.ts`)

Optional HTTP wrapper for consistent logging and error handling.

**Features:**
- Request/response logging to console
- Error logging with full details
- Methods: `get<T>`, `post<T>`, `put<T>`, `patch<T>`, `delete<T>`

**Usage:**
```typescript
import { HttpClientService } from '@core/services/http-client.service';

constructor(private http: HttpClientService) {}

this.http.get<User>('/api/users/1').subscribe(
  user => console.log(user)
  // Errors handled by ErrorInterceptor and ErrorService
);
```

## Integration with Services

### Notification Service Example
```typescript
markAsUnread(id: number): Observable<void> {
  return this.http.put<any>(`${this.apiUrl}/${id}/unread`, {}).pipe(
    map(() => void 0),
    catchError((error) => {
      this.errorService.addWarning('Could not mark notification as unread');
      return of(void 0);
    })
  );
}
```

## Best Practices

### 1. Use ErrorService for User-Facing Messages
```typescript
// ❌ Bad: Uses console
console.error('Failed to load data');

// ✅ Good: Uses ErrorService
this.errorService.addError('Failed to load data. Please try again.');
```

### 2. Validate Input Early
```typescript
onSubmit(form: any) {
  const emailValidation = validateEmail(form.email);
  if (!emailValidation.isValid) {
    this.errorService.addError(emailValidation.errors[0]);
    return;
  }
  // Proceed with submission
}
```

### 3. Handle Errors in Services
```typescript
getNotifications(): Observable<Notification[]> {
  return this.http.get<Notification[]>(this.apiUrl).pipe(
    catchError((error) => {
      this.errorService.addError('Failed to load notifications');
      return of([]);
    })
  );
}
```

### 4. Different Error Types for Context
```typescript
// Error - something went wrong
this.errorService.addError('Could not save changes');

// Warning - user should be aware
this.errorService.addWarning('Connection lost. Retrying...');

// Success - positive feedback
this.errorService.addSuccess('Changes saved successfully!');

// Info - informational message
this.errorService.addInfo('Loading data...');
```

### 5. Auto-Dismiss Configuration
```typescript
// Error with 5-second auto-dismiss
this.errorService.addError('Temporary issue', 5000);

// Persistent error (user must dismiss)
this.errorService.addError('Critical error', 0);

// Success with 3-second auto-dismiss (default)
this.errorService.addSuccess('Saved!');
```

## Error Flow

```
User Action
    ↓
Service Method
    ↓
HTTP Request (via HttpClient)
    ↓
[Success] → Service handles response
    ↓
    Error Interceptor (catches error)
    ↓
    Error mapped to user-friendly message
    ↓
    ErrorService.addError()
    ↓
    Error Alert Component displays message
    ↓
    Auto-dismiss or manual close
```

## Testing Error Handling

### Manual Testing

1. **Test Offline**: Disable network to trigger status 0 error
2. **Test 401**: Login, clear token, make API call
3. **Test 403**: Access admin endpoint as regular user
4. **Test 404**: Request non-existent resource
5. **Test Validation**: Submit invalid form data

### Component Testing Example

```typescript
it('should display error message', () => {
  const errorMessage = 'Test error';
  component.errorService.addError(errorMessage);
  
  fixture.detectChanges();
  
  const alertEl = fixture.debugElement.query(By.css('.alert-danger'));
  expect(alertEl.nativeElement.textContent).toContain(errorMessage);
});
```

## Migration Guide

### From console.error() to ErrorService

```typescript
// Before
catch((error) => {
  console.error('Failed to save:', error);
  return of(null);
})

// After
catch((error) => {
  this.errorService.addError('Failed to save. Please try again.');
  return of(null);
})
```

### From Generic Alerts to Typed Errors

```typescript
// Before
alert('An error occurred');

// After
this.errorService.addError('An error occurred. Please refresh and try again.');
```

## Related Files

- Error Service: [/app/core/services/error.service.ts](../../app/core/services/error.service.ts)
- Error Alert Component: [/app/core/components/error-alert/error-alert.component.ts](../../app/core/components/error-alert/error-alert.component.ts)
- Error Interceptor: [/app/core/interceptors/error.interceptor.ts](../../app/core/interceptors/error.interceptor.ts)
- Validators: [/app/shared/utils/validators.ts](../../app/shared/utils/validators.ts)
- HttpClient Service: [/app/core/services/http-client.service.ts](../../app/core/services/http-client.service.ts)

## Summary

The improved error handling system provides:
- ✅ Centralized error management
- ✅ User-friendly error messages
- ✅ Type-safe error handling
- ✅ Automatic HTTP error handling
- ✅ Input validation utilities
- ✅ Request/response logging
- ✅ Consistent error UI
- ✅ Auto-dismiss functionality

All components work together to provide a seamless error experience for users while helping developers track issues through console logging.
