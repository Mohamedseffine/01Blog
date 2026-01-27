# Moblogging

Full-stack blogging platform with posts, comments, reactions, follows, notifications, reports, and admin tooling.

## Stack
- **Frontend:** Angular 21, RxJS, Bootstrap, standalone components, global error handling & success interceptor.
- **Backend:** Spring Boot 3, Spring Security (JWT), JPA/Hibernate, PostgreSQL, Bucket4j rate limiting.
- **Real-time:** WebSocket/STOMP notifications.

## Quick Start
1) **Backend**
   ```bash
   cd backend/moblogging
    ADMIN_SEED_ENABLED=true \
    ADMIN_USERNAME=admin \
    ADMIN_EMAIL=admin@example.com \
    ADMIN_PASSWORD='Mosdef@123' \
    ADMIN_FIRST_NAME=Admin \
    ADMIN_LAST_NAME=User \
    ADMIN_GENDER=PREFER_NOT_TO_SAY \
    ADMIN_PROFILE_TYPE=PUBLIC \
    ./mvnw spring-boot:run
   ```
   - Needs PostgreSQL; defaults in `application-dev.properties`:
     ```
     spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
     spring.datasource.username=postgres
     spring.datasource.password=theWiseEnlil@
     files.uploadDirectory=uploads
     ```
   - Seed admin via env vars (`ADMIN_USERNAME`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`, …).

2) **Frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```
   - Runs at `http://localhost:4200`, expects backend at `http://localhost:8080` (`environment.ts`).

## Core Features
- Authentication with JWT, refresh, and rate limits on auth endpoints.
- Posts with media, visibility (public/private/close friends), subjects, edit/delete.
- Comments with nesting, edit/delete by owners; reactions on posts and comments.
- Follow/unfollow, user profiles with post lists, profile editing and uploads.
- Reporting of posts/comments/users; admins resolve/delete; resolved/deleted content clears related reports.
- Notifications (real-time via WebSocket), saved posts, bans (blocked at login, refresh, and request time).
- Admin dashboard for users, posts, comments, reports, bans, hide/unhide, delete.

## Important Behaviors
- **Rate limiting:** IP + endpoint buckets (login, register, refresh/logout, default). 429 returned on excess.
- **Bans:** Banned users cannot log in, refresh tokens, or access endpoints (JWT filter returns 403).
- **Reports cleanup:** Resolving a report removes it; deleting a post/comment also removes related reports.
- **Error handling:** Global error handler/interceptors; success interceptor toasts for non-GET successes.
- **Debounced actions:** Critical buttons (forms, reactions, reports, admin actions) are debounced to prevent double submissions.

## Configuration Details
- **Backend env/properties:** DB (`spring.datasource.*`), `files.uploadDirectory`, JWT (`jwt.secret`, `jwt.expiration`, `jwt.refresh-expiration`), admin seed (`ADMIN_USERNAME`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`, names/gender/profile-type).
- **Rate limits (per IP):** `/auth/login` 5/min, `/auth/register` 3/min, `/auth/refresh` + `/auth/logout` 10/min, default 100/min (Bucket4j).
- **Media uploads:** Multipart on; 50MB/file, 60MB/request, max 20 parts, stored under `uploads/` by default.
- **WebSocket:** STOMP over `/ws`; notifications start after login with the access token.
- **Roles & bans:** Admin vs user. Banned users are blocked on login, refresh, and every JWT-auth’d request (403).

## Usage Notes
- **Auth flow:** Login issues access + refresh; refresh rotates tokens and verifies stored hash/expiry/revocation.
- **Reports:** Users report posts/comments/users; admins resolve (removes report) or delete content (also removes related reports) from the reports panel.
- **Visibility & moderation:** Posts can be public/private/close-friends; hide/unhide via admin; owners edit/delete their own posts/comments; admins can delete but not edit others’ comments.
- **Friction control:** Debounced mutation buttons to avoid double submissions; global success/error toasts keep UX responsive.

## Running Tests / Lint
- Backend build: `cd backend/moblogging && ./mvnw -DskipTests package`
- Frontend lint: `cd frontend && npm run lint` (may require adjusting existing lint issues).

## Project Structure (high level)
- `frontend/src/app/domains/*` — feature areas (auth, post, comment, user, admin, report, react, notification).
- `frontend/src/app/core` — global services (auth, error handling, interceptors, websocket, etc.).
- `frontend/src/app/shared` — shared utilities and directives (`debounce-click`).
- `backend/moblogging/src/main/java/com/zone01oujda/moblogging/*` — domain packages for auth, post, comment, report, admin, security, etc.
- `backend/moblogging/src/main/resources` — Spring configs, including `application-dev.properties`.

## Common Commands
- Start backend: `./mvnw spring-boot:run`
- Start frontend: `npm start`
- Build backend: `./mvnw -DskipTests package`
- Build frontend: `npm run build`

## Deployment Notes
- Configure database credentials and `files.uploadDirectory` for persistent storage.
- Set `jwt.secret`, `jwt.expiration`, `jwt.refresh-expiration` appropriately.
- Serve frontend with your preferred host (e.g., `ng build --configuration production` and static hosting).
