# Moblogging Frontend - Angular DDD Architecture

## Overview

This is an Angular 17 frontend application built following **Domain-Driven Design (DDD)** principles. The architecture is organized by business domains rather than technical layers, promoting better code organization, scalability, and maintainability.

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── domains/              # Business domains (DDD)
│   │   │   ├── auth/             # Authentication domain
│   │   │   │   ├── models/       # Data models (LoginRequest, AuthResponse, etc.)
│   │   │   │   ├── services/     # Domain services (AuthService)
│   │   │   │   ├── components/   # UI components (LoginComponent, RegisterComponent)
│   │   │   │   ├── guards/       # Route guards (authGuard)
│   │   │   │   └── auth.routes.ts # Domain routing
│   │   │   │
│   │   │   ├── post/             # Post domain
│   │   │   │   ├── models/       # Post, CreatePostDto, PostListResponse
│   │   │   │   ├── services/     # PostService
│   │   │   │   ├── components/   # PostListComponent, PostCreateComponent, etc.
│   │   │   │   └── post.routes.ts
│   │   │   │
│   │   │   ├── comment/          # Comment domain
│   │   │   ├── user/             # User domain
│   │   │   ├── notification/     # Notification domain (WebSocket ready)
│   │   │   ├── admin/            # Admin domain
│   │   │   ├── react/            # Reaction domain
│   │   │   └── report/           # Report domain
│   │   │
│   │   ├── shared/               # Shared utilities and components
│   │   │   ├── models/           # Shared data models
│   │   │   ├── services/         # Shared services
│   │   │   ├── components/       # Reusable UI components
│   │   │   ├── pipes/            # Custom pipes
│   │   │   └── directives/       # Custom directives
│   │   │
│   │   ├── core/                 # Core application services
│   │   │   ├── services/         # WebSocketService, etc.
│   │   │   └── interceptors/     # HTTP interceptors (auth, error handling)
│   │   │
│   │   ├── layouts/              # Application layouts
│   │   │   └── main-layout/      # Main layout wrapper
│   │   │
│   │   ├── app.component.ts      # Root component
│   │   └── app.routes.ts         # Main routing configuration
│   │
│   ├── environments/             # Environment configurations
│   │   ├── environment.ts        # Development environment
│   │   └── environment.prod.ts   # Production environment
│   │
│   ├── styles/                   # Global styles
│   ├── index.html               # HTML entry point
│   └── main.ts                  # Angular bootstrap file
│
├── angular.json                 # Angular CLI config
├── tsconfig.json               # TypeScript config
├── tsconfig.app.json           # App TypeScript config
├── tsconfig.spec.json          # Test TypeScript config
├── package.json                # NPM dependencies
└── README.md                   # This file
```

## Domain-Driven Design (DDD) Explained

In this architecture, each **domain** represents a distinct business capability:

### Key Principles:

1. **Isolation**: Each domain is self-contained with models, services, and components
2. **Cohesion**: Domain logic is grouped together
3. **Loose Coupling**: Domains communicate through well-defined interfaces
4. **Scalability**: Easy to add new domains without affecting existing code

### Domain Structure (Example: Post Domain):

```
domains/post/
├── models/
│   └── post.model.ts           # Data models: Post, CreatePostDto, PostListResponse
├── services/
│   └── post.service.ts         # API calls and business logic
├── components/
│   ├── post-list/              # Display list of posts
│   ├── post-create/            # Create new post
│   ├── post-detail/            # View single post
│   └── post-edit/              # Edit existing post
└── post.routes.ts              # Domain routing configuration
```

## Setup Instructions

### Prerequisites
- Node.js 18+ and npm
- Angular CLI 17+

### Installation

1. **Install dependencies**:
   ```bash
   cd frontend
   npm install
   ```

2. **Start development server**:
   ```bash
   npm start
   ```
   The app will be available at `http://localhost:4200`

3. **Build for production**:
   ```bash
   npm run build:prod
   ```

4. **Run tests**:
   ```bash
   npm test
   ```

## Architecture Patterns

### 1. Smart (Container) vs Dumb (Presentational) Components

**Smart Components** (located in domain):
- Connect to services
- Handle business logic
- Pass data to presentational components

```typescript
// Example: post-list.component.ts
@Component({ ... })
export class PostListComponent implements OnInit {
  constructor(private postService: PostService) {}
  
  ngOnInit() {
    this.postService.getPosts().subscribe(posts => {
      this.displayPosts(posts);
    });
  }
}
```

**Dumb Components** (in shared):
- Receive data via @Input
- Emit events via @Output
- No direct service dependencies

### 2. Service Layer

Each domain has a service that:
- Makes HTTP requests to the backend API
- Manages domain-specific state
- Provides methods for component interactions

```typescript
@Injectable({ providedIn: 'root' })
export class PostService {
  constructor(private http: HttpClient) {}
  
  getPosts(page = 0, size = 10): Observable<PostListResponse> {
    // Implementation
  }
}
```

### 3. Routing Architecture

Routes are defined per domain and lazy-loaded:

```typescript
// app.routes.ts
export const routes: Routes = [
  {
    path: 'posts',
    loadChildren: () => import('./domains/post/post.routes')
      .then(m => m.POST_ROUTES)
  },
  {
    path: 'auth',
    loadChildren: () => import('./domains/auth/auth.routes')
      .then(m => m.AUTH_ROUTES)
  }
];
```

### 4. HTTP Interceptors

**Auth Interceptor**: Attaches JWT tokens to all requests
```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = authService.getToken();
  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
  return next(req);
};
```

**Error Interceptor**: Handles global error scenarios (401, 403, 500)

### 5. Authentication & Guards

Route guards protect authenticated routes:
```typescript
{
  path: 'dashboard',
  canActivate: [authGuard],
  component: DashboardComponent
}
```

## WebSocket Integration

The application includes real-time notification support via WebSocket:

```typescript
// In your component
constructor(
  private notificationService: NotificationService,
  private wsService: WebSocketService
) {}

ngOnInit() {
  // Listen for real-time notifications
  this.notificationService.notifications$.subscribe(notification => {
    console.log('New notification:', notification);
  });
}
```

Use the JavaScript client from the backend:
```html
<script src="/path/to/websocket-client.js"></script>
<script>
  const wsClient = new NotificationWebSocketClient(userId);
  wsClient.on('onNotification', (notification) => {
    // Handle notification
  });
  wsClient.connect();
</script>
```

## Environment Configuration

Configure different API endpoints for dev/prod:

```typescript
// environment.ts (development)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  wsUrl: 'ws://localhost:8080/ws'
};

// environment.prod.ts (production)
export const environment = {
  production: true,
  apiUrl: 'https://api.moblogging.com',
  wsUrl: 'wss://api.moblogging.com/ws'
};
```

## API Integration

All API calls go through domain services:

```typescript
// Post Service
getPosts(page: number, size: number): Observable<PostListResponse> {
  return this.http.get<PostListResponse>(
    `${this.apiUrl}`,
    { params: { page, size } }
  );
}

// Component
posts$ = this.postService.getPosts(0, 10);
```

## Best Practices

1. **Keep Components Focused**: One responsibility per component
2. **Use Observable Streams**: Leverage RxJS for data flow
3. **Unsubscribe Properly**: Use `takeUntil` or async pipe
4. **Type Safety**: Strict TypeScript typing for all code
5. **Error Handling**: Implement consistent error handling via interceptors
6. **Naming Conventions**:
   - Components: `*.component.ts`
   - Services: `*.service.ts`
   - Models: `*.model.ts`
   - Routes: `*.routes.ts`

## Path Aliases

Use TypeScript path aliases for cleaner imports:

```typescript
// Instead of:
import { PostService } from '../../../post/services/post.service';

// Use:
import { PostService } from '@domains/post/services/post.service';
```

Available aliases:
- `@app/*` - App root
- `@domains/*` - Domain modules
- `@shared/*` - Shared utilities
- `@core/*` - Core services
- `@layouts/*` - Layout components
- `@env/*` - Environment configs

## Development Workflow

### Creating a New Feature

1. **Create domain structure**:
   ```bash
   mkdir -p src/app/domains/myfeature/{models,services,components}
   ```

2. **Define models**:
   ```typescript
   // models/myfeature.model.ts
   export interface MyFeature {
     id: number;
     // properties...
   }
   ```

3. **Create service**:
   ```typescript
   // services/myfeature.service.ts
   @Injectable({ providedIn: 'root' })
   export class MyFeatureService {
     // implementation...
   }
   ```

4. **Create components**:
   ```typescript
   // components/myfeature-list/myfeature-list.component.ts
   @Component({ ... })
   export class MyFeatureListComponent {
     // implementation...
   }
   ```

5. **Add routing**:
   ```typescript
   // myfeature.routes.ts
   export const MYFEATURE_ROUTES: Routes = [
     { path: '', component: MyFeatureListComponent }
   ];
   ```

## Testing

The project is configured with Jasmine/Karma for testing:

```typescript
// Example test
describe('PostService', () => {
  let service: PostService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PostService);
  });

  it('should fetch posts', () => {
    // test implementation
  });
});
```

## Performance Optimization

1. **Lazy Loading**: Routes are lazy-loaded per domain
2. **OnPush Change Detection**: Use for better performance
3. **RxJS Operators**: Debounce, throttle for search/filters
4. **Tree Shaking**: Unused code is removed in production build

## Browser Support

| Browser | Support |
|---------|---------|
| Chrome  | Latest  |
| Firefox | Latest  |
| Safari  | Latest  |
| Edge    | Latest  |

## Troubleshooting

### Module Not Found Errors
- Check path aliases in `tsconfig.json`
- Verify import paths use correct casing

### Service Injection Errors
- Ensure service is decorated with `@Injectable({ providedIn: 'root' })`
- Check that service is imported in component

### WebSocket Connection Issues
- Verify backend WebSocket endpoint is running
- Check browser console for error messages
- Ensure backend CORS allows frontend origin

## Resources

- [Angular Documentation](https://angular.io/docs)
- [TypeScript Documentation](https://www.typescriptlang.org/docs)
- [RxJS Guide](https://rxjs.dev/guide/overview)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)

## Contributing

1. Follow the DDD structure
2. Keep domains isolated
3. Use TypeScript strict mode
4. Add tests for new features
5. Keep components focused and reusable

## License

MIT License - See LICENSE file for details
