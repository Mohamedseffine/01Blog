import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '@env/environment';
import { User, UserProfile, UpdateProfileDto } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) { }

  getUserById(id: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/${id}`);
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/current`);
  }

  updateProfile(id: number, dto: UpdateProfileDto): Observable<User> {
    const formData = new FormData();
    if (dto.username) formData.append('username', dto.username);
    if (dto.bio) formData.append('bio', dto.bio);
    if (dto.profilePicture) formData.append('profilePicture', dto.profilePicture);
    return this.http.put<User>(`${this.apiUrl}/${id}`, formData);
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
