import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="post-list">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="mb-0">Posts</h1>
        <a href="/posts/create" class="btn btn-primary">
          <i class="bi bi-plus-circle"></i> Create Post
        </a>
      </div>

      <div class="row g-4">
        <div class="col-md-6 col-lg-4">
          <div class="card h-100 shadow-sm">
            <img src="https://via.placeholder.com/300x200" class="card-img-top" alt="Post">
            <div class="card-body">
              <h5 class="card-title">Sample Post Title</h5>
              <p class="card-text text-muted text-truncate-multiline">
                This is a sample post description. It shows how posts will be displayed in the list view.
              </p>
              <div class="d-flex justify-content-between align-items-center">
                <small class="text-muted">Jan 19, 2026</small>
                <a href="#" class="btn btn-sm btn-outline-primary">Read More</a>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-6 col-lg-4">
          <div class="card h-100 shadow-sm">
            <img src="https://via.placeholder.com/300x200" class="card-img-top" alt="Post">
            <div class="card-body">
              <h5 class="card-title">Another Sample Post</h5>
              <p class="card-text text-muted text-truncate-multiline">
                This demonstrates how multiple posts will be displayed in a responsive grid layout.
              </p>
              <div class="d-flex justify-content-between align-items-center">
                <small class="text-muted">Jan 18, 2026</small>
                <a href="#" class="btn btn-sm btn-outline-primary">Read More</a>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-6 col-lg-4">
          <div class="card h-100 shadow-sm">
            <img src="https://via.placeholder.com/300x200" class="card-img-top" alt="Post">
            <div class="card-body">
              <h5 class="card-title">Third Sample Post</h5>
              <p class="card-text text-muted text-truncate-multiline">
                The layout is fully responsive and will adapt to different screen sizes.
              </p>
              <div class="d-flex justify-content-between align-items-center">
                <small class="text-muted">Jan 17, 2026</small>
                <a href="#" class="btn btn-sm btn-outline-primary">Read More</a>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <nav aria-label="Page navigation" class="mt-5">
        <ul class="pagination justify-content-center">
          <li class="page-item disabled">
            <a class="page-link" href="#" tabindex="-1">Previous</a>
          </li>
          <li class="page-item active"><a class="page-link" href="#">1</a></li>
          <li class="page-item"><a class="page-link" href="#">2</a></li>
          <li class="page-item"><a class="page-link" href="#">3</a></li>
          <li class="page-item">
            <a class="page-link" href="#">Next</a>
          </li>
        </ul>
      </nav>
    </div>
  `,
})
export class PostListComponent { }
