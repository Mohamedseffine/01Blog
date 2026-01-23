import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '@env/environment';
import { User, UserProfile, UpdateProfileDto } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) { }

  getUserById(id: number): Observable<UserProfile> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map((res) => res?.data ?? res)
    );
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<any>(`${this.apiUrl}/current`).pipe(
      map((res) => res?.data ?? res)
    );
  }

  updateProfile(id: number, dto: UpdateProfileDto): Observable<User> {
    const formData = new FormData();
    if (dto.username) formData.append('username', dto.username);
    if (dto.bio) formData.append('bio', dto.bio);
    if (dto.profilePicture) formData.append('profilePicture', dto.profilePicture);
    return this.http.put<any>(`${this.apiUrl}/${id}`, formData).pipe(
      map((res) => res?.data ?? res)
    );
  }

  getProfilePicture(userId: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/${userId}/profile-picture`, { responseType: 'blob' }).pipe(
      map((blob) => URL.createObjectURL(blob))
    );
  }

  followUser(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${userId}/follow`, {});
  }

  unfollowUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${userId}/follow`);
  }

  getFollowers(userId: number, page: number = 0, size: number = 20): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${userId}/followers`, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  getFollowing(userId: number, page: number = 0, size: number = 20): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${userId}/following`, {
      params: { page: page.toString(), size: size.toString() }
    });
  }
}
