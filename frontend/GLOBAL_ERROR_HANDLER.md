# Global Error Handler - Documentation

## Overview

A custom Angular `ErrorHandler` has been implemented to centralize error management and silence known non-critical errors, reducing console noise while maintaining proper error logging.

## Features

### ✅ Automatic Error Handling
- Intercepts all Angular runtime errors
- Classifies errors by severity
- Routes errors based on type (critical/warning/transient)
- Silences known, non-critical errors

### ✅ Error Classification
Errors are categorized into three levels:

**Critical Errors** (Shown to User)
- 401 Unauthorized / 403 Forbidden
- 500+ Server errors
- Authentication/Authorization failures

**Warning Errors** (Shown as Warning)
- 404 Not Found
- 409 Conflict
- Validation errors
- Non-critical failures

**Transient Errors** (Debug Only)
- Network timeouts
- Connection errors
- Temporary failures

### ✅ Silent Error Handling
Known non-critical errors are silently handled:
- WebSocket connection errors
- STOMP protocol errors
- Resume Observer loop limit exceeded
- Browser extension errors
- DOM mutation errors
- Minor Angular warnings

### ✅ Logging Levels
- **Error**: Critical issues shown to user + logged
- **Warn**: Important issues shown as warning + logged
- **Debug**: Network/transient errors (dev only)

## Implementation

### GlobalErrorHandler Service
**File**: `/app/core/services/global-error-handler.ts`

```typescript
@Injectable({ providedIn: 'root' })
export class GlobalErrorHandler implements ErrorHandler {
  handleError(error: Error | any): void {
    // Classifies error severity
    // Decides to show to user, log, or silently handle
  }
}
```

### Bootstrap Integration
**File**: `/main.ts`

```typescript
bootstrapApplication(AppComponent, {
  providers: [
    // ... other providers
    { provide: ErrorHandler, useClass: GlobalErrorHandler }
  ]
})
```

## Silenced Error Patterns

### WebSocket/STOMP Errors
```
- WebSocket
- STOMP
- subscribe error
- Publish error
- Connection lost
- Disconnected
```

### Network Errors
```
- Network request failed
- Failed to fetch
- timeout
```

### Browser/Extension Errors
```
- top.GLOBALS
- chrome-extension
- moz-extension
```

### DOM/Observer Errors
```
- ResizeObserver loop limit exceeded
- deadCode
- document is not defined
```

### Angular Warnings
```
- common not found
- Warning
```

## Usage

### Error Flow

```
Application Error
       ↓
GlobalErrorHandler.handleError() called
       ↓
Error classified (critical/warning/transient)
       ↓
Check if should be silenced
       ↓
If critical  → ErrorService.addError() + console.error()
If warning   → ErrorService.addWarning() + console.warn()
If transient → console.debug() only
If silenced  → Silent handling (debug mode only)
```

### Console Outputs

**Error (Critical)**
```
[CRITICAL ERROR] Error message
```

**Warning**
```
[WARNING] Warning message
```

**Debug (Development Only)**
```
[SILENCED ERROR] Error details
[TRANSIENT ERROR] Transient error message
[HTTP GET ERROR] /api/endpoint { ... }
```

## Removed Explicit console.error Calls

The following `console.error()` calls have been removed:

1. **main.ts**: Bootstrap error logging
2. **notification-websocket-client.ts**: 
   - Subscribe errors
   - STOMP errors
   - WebSocket errors
   - Publish errors
3. **notification.service.ts**:
   - Notification fetch errors
   - Mark as read/unread errors
   - Delete notification errors
   - WebSocket connection errors
4. **error.interceptor.ts**:
   - Server error logging
5. **http-client.service.ts**:
   - HTTP error logging (changed to console.debug)

## Development Mode

In development (localhost/127.0.0.1), silenced errors are logged as `console.debug()` for debugging:

```typescript
console.debug('[SILENCED ERROR]', message, error);
```

In production, these are completely silent.

## Detection Logic

### Production Detection
```typescript
private isProduction(): boolean {
  return (
    typeof window !== 'undefined' &&
    window.location.hostname !== 'localhost' &&
    window.location.hostname !== '127.0.0.1'
  );
}
```

## Error Message Extraction

The handler intelligently extracts error messages from various error formats:

```typescript
if (error instanceof Error)
  return error.message;
if (typeof error === 'string')
  return error;
if (error?.message)
  return error.message;
if (error?.error?.message)
  return error.error.message;
if (error?.statusText)
  return error.statusText;
```

## Benefits

✅ **Reduced Console Noise**
- Only critical errors logged
- Known non-critical errors silenced
- Cleaner development console

✅ **Better User Experience**
- Users see only relevant errors
- Errors properly categorized
- Clear, actionable messages

✅ **Developer Experience**
- Easy to debug critical issues
- Development mode shows all errors
- Clear logging conventions

✅ **Centralized Control**
- Single place to manage error behavior
- Easy to add/remove silenced patterns
- Consistent error handling

## Customization

### Adding New Silenced Error Pattern

Edit `/app/core/services/global-error-handler.ts`:

```typescript
private shouldSilenceError(error: Error | any, message: string): boolean {
  const knownSilentErrors: string[] = [
    // ... existing patterns
    'your-new-pattern',  // Add here
  ];
  
  const errorStr = `${message}${error?.stack || ''}`.toLowerCase();
  return knownSilentErrors.some(pattern =>
    errorStr.includes(pattern.toLowerCase())
  );
}
```

### Changing Error Classification

Edit `classifyError()` method to change which errors are critical/warning/transient:

```typescript
private classifyError(error: Error | any): 'critical' | 'warning' | 'transient' {
  // Add conditions for your custom error types
}
```

## Testing

### Test in Development
1. Open browser DevTools
2. Navigate to app
3. Silenced errors show as `[SILENCED ERROR]` in debug logs
4. Critical errors shown as alerts to user

### Test in Production
1. Deploy to production URL
2. Silenced errors are completely silent
3. Critical errors shown to user
4. Warning errors shown as warnings

## Best Practices

1. **Don't Suppress Critical Errors**
   - Only silence truly non-critical patterns
   - Errors that affect UX should not be silenced

2. **Document Silenced Patterns**
   - Keep list updated
   - Explain why each is silenced

3. **Review Periodically**
   - Check if silenced patterns are still needed
   - Remove patterns no longer relevant

4. **Use Error Service for Application Errors**
   - GlobalErrorHandler for framework/runtime errors
   - ErrorService for application-level errors

## Troubleshooting

### Error Not Being Silenced
1. Check exact error message text
2. Ensure pattern matches (case-insensitive)
3. Verify `shouldSilenceError()` is being called

### Important Errors Being Silenced
1. Review `shouldSilenceError()` patterns
2. Check `classifyError()` classification
3. May need to remove pattern or adjust logic

### Need More Debug Info
1. Change to development URL (localhost)
2. Silenced errors will show in console.debug()
3. Check Network tab for HTTP errors

## Related Files

- Global Error Handler: [/app/core/services/global-error-handler.ts](./src/app/core/services/global-error-handler.ts)
- Main Bootstrap: [/main.ts](./src/main.ts)
- Error Service: [/app/core/services/error.service.ts](./src/app/core/services/error.service.ts)
- Error Interceptor: [/app/core/interceptors/error.interceptor.ts](./src/app/core/interceptors/error.interceptor.ts)

## Summary

The Global Error Handler provides:
- ✅ Automatic error interception and classification
- ✅ Smart silence of non-critical errors
- ✅ Reduced console noise
- ✅ Better error UX
- ✅ Centralized error management
- ✅ Development/production aware logging

All error handling now flows through this single, configurable system.
