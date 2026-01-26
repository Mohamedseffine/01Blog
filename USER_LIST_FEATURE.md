# User List Feature Documentation

## Overview
A new user list page has been implemented with real-time search functionality. Users can discover other users on the platform and view their profiles.

## Features
- **Display All Users**: Shows all registered users in a paginated grid layout (12 users per page)
- **Real-time Search**: Debounced search (300ms) filtering by:
  - First name
  - Last name
  - Username
  - Email
- **User Cards**: Display user profile with:
  - Profile picture (with fallback)
  - Username (clickable link to profile)
  - Email address
  - Follower/Following counts
  - Bio preview
  - View Profile button
- **Pagination**: Previous/Next page navigation
- **Responsive Design**: Adapts to mobile and desktop screens
- **Error Handling**: Uses ErrorService for user-friendly error messages

## Files Created/Modified

### New Files
1. **[frontend/src/app/domains/user/components/user-list/user-list.component.ts](frontend/src/app/domains/user/components/user-list/user-list.component.ts)**
   - Standalone Angular component
   - Handles user search, pagination, and display
   - Uses RxJS for reactive search with debounce
   - 200+ lines

### Modified Files
1. **[frontend/src/app/domains/user/services/user.service.ts](frontend/src/app/domains/user/services/user.service.ts)**
   - Added `getAllUsers(page: number, size: number, search?: string)` method
   - Supports optional search parameter passed to backend API

2. **[frontend/src/app/domains/user/user.routes.ts](frontend/src/app/domains/user/user.routes.ts)**
   - Added route: `/users/list` → UserListComponent

3. **[frontend/src/app/layouts/main-layout/main-layout.component.ts](frontend/src/app/layouts/main-layout/main-layout.component.ts)**
   - Added "Users" link to main navigation
   - Visible only to authenticated users

## Usage
1. Navigate to `/users/list` or click "Users" in the main navigation (when authenticated)
2. Browse all users in the grid
3. Use the search bar to filter by name, username, or email
4. Click "View Profile" or username to go to user's profile
5. Use pagination controls to browse pages

## API Endpoint
**GET** `/api/v1/users`

Query Parameters:
- `page` (default: 0) - Page number (0-indexed)
- `size` (default: 20) - Results per page
- `search` (optional) - Search term to filter by firstname, lastname, username, or email

Response Format (Expected):
```json
{
  "data": {
    "content": [User...],
    "totalPages": 5,
    "totalElements": 50,
    "currentPage": 0
  }
}
```

## Design Features
- Bootstrap 5 styling with custom hover effects
- Responsive grid layout (1 column mobile, 2 columns tablet, 3+ columns desktop)
- Loading spinner during data fetch
- Empty state message when no results
- Fallback avatar for users without profile pictures
- Error messages routed through ErrorService (auto-dismissed)

## Technical Details
- **Component Type**: Standalone
- **Modules**: CommonModule, FormsModule, ReactiveFormsModule, RouterLink
- **State Management**: BehaviorSubject for pagination, FormControl for search
- **Search Debounce**: 300ms to prevent excessive API calls
- **Error Handling**: ErrorService.addWarning() for failed requests
- **Pagination**: Manual page navigation (no infinite scroll)

## Build Status
✅ Frontend builds successfully (7016ms)
✅ No TypeScript errors
✅ Component lazy-loaded in chunk 971.53fcddafddf077f9.js (7.88 kB)

## Next Steps (Optional)
1. Add sort/filter options (by followers, creation date)
2. Implement infinite scroll pagination
3. Add user action buttons (Follow, Unfollow, Block)
4. Add user list in admin dashboard
5. Implement "People You May Know" based on following graph
