import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of, throwError } from 'rxjs';

import { environment } from '@env/environment';
import { User, UserProfile, UpdateProfileDto } from '../models/user.model';
import { ErrorService } from '@core/services/error.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient, private errorService: ErrorService) { }

  private emptyPage(size: number) {
    return {
      content: [],
      number: 0,
      size,
      totalElements: 0,
      totalPages: 0,
      first: true,
      last: true
    };
  }

  private handleError<T>(message: string, fallback?: T) {
    return (error: any) => {
      this.errorService.addError(message);
      if (fallback !== undefined) {
        return of(fallback as T);
      }
      return throwError(() => error);
    };
  }

  getUserById(id: number): Observable<UserProfile> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<UserProfile>('Unable to load user profile.'))
    );
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<any>(`${this.apiUrl}/current`).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<User>('Unable to load current user.'))
    );
  }

  updateProfile(dto: UpdateProfileDto): Observable<User> {
    const formData = new FormData();
    if (dto.username) formData.append('username', dto.username);
    if (dto.bio) formData.append('bio', dto.bio);
    if (dto.profilePicture) formData.append('profilePicture', dto.profilePicture);
    return this.http.put<any>(`${this.apiUrl}/current`, formData).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<User>('Unable to update profile.'))
    );
  }

  getProfilePicture(userId: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/${userId}/profile-picture`, { responseType: 'blob' }).pipe(
      map((blob) => URL.createObjectURL(blob)),
      catchError((error) => {
        if (error?.status === 404) {
          // No profile image set; silently return empty string
          return of('');
        }
        this.errorService.addError('Unable to load profile picture.');
        return throwError(() => error);
      })
    );
  }

  followUser(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${userId}/follow`, {}).pipe(
      catchError(this.handleError<void>('Unable to follow user.'))
    );
  }

  unfollowUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${userId}/follow`).pipe(
      catchError(this.handleError<void>('Unable to unfollow user.'))
    );
  }

  getFollowers(userId: number, page: number = 0, size: number = 20): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${userId}/followers`, {
      params: { page: page.toString(), size: size.toString() }
    }).pipe(
      catchError(this.handleError<any>('Unable to load followers.', this.emptyPage(size)))
    );
  }

  getFollowing(userId: number, page: number = 0, size: number = 20): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${userId}/following`, {
      params: { page: page.toString(), size: size.toString() }
    }).pipe(
      catchError(this.handleError<any>('Unable to load following list.', this.emptyPage(size)))
    );
  }

  getAllUsers(page: number = 0, size: number = 20, search?: string): Observable<any> {
    const params: any = {
      page: page.toString(),
      size: size.toString()
    };
    if (search && search.trim()) {
      params.search = search.trim();
    }
    return this.http.get<any>(`${this.apiUrl}`, { params }).pipe(
      catchError(this.handleError<any>('Unable to load users.', this.emptyPage(size)))
    );
  }
}
