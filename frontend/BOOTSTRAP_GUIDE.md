# Bootstrap Integration Guide

## Overview

This Angular frontend uses **Bootstrap 5** for styling and UI components. All CSS is integrated and globally available throughout the application.

## Installation

Bootstrap and ng-bootstrap are already installed:
- `bootstrap` - CSS framework
- `ng-bootstrap` - Angular-specific components

```bash
npm install bootstrap ng-bootstrap @ng-bootstrap/ng-bootstrap
```

## Configuration

Bootstrap CSS is automatically loaded in `angular.json`:
```json
"styles": [
  "node_modules/bootstrap/dist/css/bootstrap.min.css",
  "src/styles.scss"
]
```

## Common Bootstrap Classes

### Layout & Grid

```html
<!-- Container -->
<div class="container">
  <div class="row">
    <div class="col-md-6">Half width on medium screens</div>
    <div class="col-md-6">Half width on medium screens</div>
  </div>
</div>

<!-- Responsive -->
<div class="container-lg">
  <div class="row g-4"><!-- gap-4 -->
    <div class="col-sm-6 col-lg-4">Responsive column</div>
  </div>
</div>
```

### Typography

```html
<h1>Heading 1</h1>
<h2>Heading 2</h2>
<p class="text-muted">Muted text</p>
<p class="text-truncate">Truncated text</p>
<p class="fw-bold">Bold text</p>
<p class="fs-6">Small text</p>
```

### Buttons

```html
<!-- Primary Button -->
<button class="btn btn-primary">Primary</button>

<!-- Secondary Button -->
<button class="btn btn-secondary">Secondary</button>

<!-- Outlined Button -->
<button class="btn btn-outline-primary">Outlined</button>

<!-- Sizes -->
<button class="btn btn-lg btn-primary">Large</button>
<button class="btn btn-sm btn-primary">Small</button>

<!-- States -->
<button class="btn btn-primary disabled">Disabled</button>
```

### Cards

```html
<div class="card">
  <img src="..." class="card-img-top" alt="...">
  <div class="card-body">
    <h5 class="card-title">Card Title</h5>
    <p class="card-text">Card content goes here.</p>
    <a href="#" class="btn btn-primary">Action</a>
  </div>
  <div class="card-footer text-muted">Card footer</div>
</div>
```

### Forms

```html
<form>
  <div class="mb-3">
    <label for="email" class="form-label">Email</label>
    <input type="email" class="form-control" id="email">
  </div>

  <div class="mb-3">
    <label for="password" class="form-label">Password</label>
    <input type="password" class="form-control" id="password">
  </div>

  <div class="form-check">
    <input class="form-check-input" type="checkbox" id="remember">
    <label class="form-check-label" for="remember">
      Remember me
    </label>
  </div>

  <button type="submit" class="btn btn-primary">Submit</button>
</form>
```

### Alerts

```html
<div class="alert alert-success">Success message</div>
<div class="alert alert-danger">Error message</div>
<div class="alert alert-warning">Warning message</div>
<div class="alert alert-info">Info message</div>
```

### Badges

```html
<span class="badge bg-primary">Primary</span>
<span class="badge bg-success">Success</span>
<span class="badge bg-danger">Danger</span>
<span class="badge bg-warning text-dark">Warning</span>
```

### Navbar

```html
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="#">Moblogging</a>
    
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
      <span class="navbar-toggler-icon"></span>
    </button>
    
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item">
          <a class="nav-link" href="/posts">Posts</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="/notifications">Notifications</a>
        </li>
      </ul>
    </div>
  </div>
</nav>
```

### List Group

```html
<div class="list-group">
  <a href="#" class="list-group-item list-group-item-action active">Active item</a>
  <a href="#" class="list-group-item list-group-item-action">Item</a>
  <a href="#" class="list-group-item list-group-item-action">Item</a>
</div>
```

### Pagination

```html
<nav aria-label="Page navigation">
  <ul class="pagination">
    <li class="page-item disabled"><a class="page-link">Previous</a></li>
    <li class="page-item active"><a class="page-link" href="#">1</a></li>
    <li class="page-item"><a class="page-link" href="#">2</a></li>
    <li class="page-item"><a class="page-link" href="#">Next</a></li>
  </ul>
</nav>
```

## Utility Classes

### Spacing
```html
<!-- Margin -->
<div class="m-3">Margin all sides</div>
<div class="mt-2">Margin top</div>
<div class="mb-4">Margin bottom</div>

<!-- Padding -->
<div class="p-3">Padding all sides</div>
<div class="px-2">Padding horizontal</div>
```

### Display & Flex
```html
<!-- Flex Layout -->
<div class="d-flex justify-content-between">
  <div>Left</div>
  <div>Right</div>
</div>

<div class="d-flex align-items-center gap-3">
  <div>Item 1</div>
  <div>Item 2</div>
</div>

<!-- Grid -->
<div class="d-grid gap-2">
  <button class="btn btn-primary">Full width button</button>
</div>
```

### Text Alignment
```html
<p class="text-start">Left aligned</p>
<p class="text-center">Center aligned</p>
<p class="text-end">Right aligned</p>
<p class="text-justify">Justified</p>
```

### Colors
```html
<p class="text-primary">Primary text</p>
<p class="text-success">Success text</p>
<p class="text-danger">Danger text</p>
<p class="text-warning">Warning text</p>
<p class="text-info">Info text</p>
<p class="text-muted">Muted text</p>

<!-- Background -->
<div class="bg-primary text-white p-3">Primary background</div>
<div class="bg-light p-3">Light background</div>
```

### Shadows
```html
<div class="shadow">Box shadow</div>
<div class="shadow-sm">Small shadow</div>
<div class="shadow-lg">Large shadow</div>
```

### Borders
```html
<div class="border">Border all sides</div>
<div class="border-top">Top border only</div>
<div class="border-primary">Colored border</div>
<div class="rounded">Rounded corners</div>
<div class="rounded-circle">Circle</div>
```

## Custom Colors

Bootstrap colors are customized in `styles.scss`:

```scss
:root {
  --bs-primary: #6366f1;      // Indigo
  --bs-secondary: #8b5cf6;    // Purple
  --bs-success: #10b981;      // Green
  --bs-danger: #ef4444;       // Red
  --bs-warning: #f59e0b;      // Yellow
  --bs-info: #3b82f6;         // Blue
}
```

## Component Examples

### Post Card with Bootstrap

```typescript
@Component({
  selector: 'app-post-card',
  template: `
    <div class="card h-100 shadow-sm">
      <img src="{{ post.image }}" class="card-img-top" alt="{{ post.title }}">
      <div class="card-body">
        <h5 class="card-title">{{ post.title }}</h5>
        <p class="card-text text-muted">{{ post.description }}</p>
        <a href="/posts/{{ post.id }}" class="btn btn-sm btn-primary">Read More</a>
      </div>
      <div class="card-footer bg-light">
        <small class="text-muted">{{ post.createdAt | date }}</small>
      </div>
    </div>
  `
})
export class PostCardComponent {
  @Input() post: any;
}
```

### Login Form with Bootstrap

```typescript
@Component({
  selector: 'app-login',
  template: `
    <div class="row justify-content-center">
      <div class="col-md-5">
        <div class="card shadow-lg">
          <div class="card-header bg-primary text-white">
            <h3 class="mb-0">Login</h3>
          </div>
          <div class="card-body">
            <form (ngSubmit)="login()">
              <div class="mb-3">
                <label class="form-label">Email</label>
                <input [(ngModel)]="email" type="email" class="form-control">
              </div>
              <div class="mb-3">
                <label class="form-label">Password</label>
                <input [(ngModel)]="password" type="password" class="form-control">
              </div>
              <button type="submit" class="btn btn-primary w-100">Sign In</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  `
})
```

### Notification List with Bootstrap

```typescript
@Component({
  selector: 'app-notifications',
  template: `
    <div>
      <h2 class="mb-4">Notifications</h2>
      <div class="list-group">
        <div *ngFor="let notif of notifications" 
             class="list-group-item list-group-item-action">
          <div class="d-flex justify-content-between">
            <div>
              <h6 class="mb-1">{{ notif.title }}</h6>
              <p class="text-muted small">{{ notif.message }}</p>
            </div>
            <span class="badge" [ngClass]="'bg-' + notif.type">
              {{ notif.type }}
            </span>
          </div>
        </div>
      </div>
    </div>
  `
})
```

## Responsive Design

Bootstrap provides responsive utilities:

```html
<!-- Show/hide on different breakpoints -->
<div class="d-none d-md-block">Visible on medium+ screens</div>
<div class="d-md-none">Visible on small screens</div>

<!-- Responsive text size -->
<h1 class="display-4 display-md-5">Responsive heading</h1>

<!-- Responsive columns -->
<div class="row">
  <div class="col-12 col-md-6 col-lg-4">Responsive column</div>
</div>
```

## Breakpoints

- **xs**: < 576px (default)
- **sm**: ≥ 576px
- **md**: ≥ 768px
- **lg**: ≥ 992px
- **xl**: ≥ 1200px
- **xxl**: ≥ 1400px

## Best Practices

1. **Use utility classes** for quick styling instead of custom CSS
2. **Leverage the grid system** for responsive layouts
3. **Keep custom styles minimal** - use Bootstrap for most styling
4. **Use semantic colors** - primary, success, danger, warning, info
5. **Mobile-first approach** - start with mobile view and add larger breakpoints
6. **Consistency** - stick to Bootstrap's spacing scale (multiples of 0.25rem)

## Resources

- [Bootstrap Documentation](https://getbootstrap.com/docs/5.0/)
- [Bootstrap Icons](https://icons.getbootstrap.com/)
- [Bootstrap Color Utilities](https://getbootstrap.com/docs/5.0/utilities/colors/)
- [Bootstrap Grid System](https://getbootstrap.com/docs/5.0/layout/grid/)

## Icons

You can add Bootstrap Icons CDN to `index.html`:

```html
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
```

Then use in templates:
```html
<i class="bi bi-heart"></i>
<i class="bi bi-star-fill"></i>
<i class="bi bi-search"></i>
```
