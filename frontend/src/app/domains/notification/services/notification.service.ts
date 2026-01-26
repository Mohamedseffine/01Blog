import { Injectable, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map, catchError, of } from 'rxjs';

import { environment } from '@env/environment';
import { Notification, NotificationResponse } from '../models/notification.model';
import { NotificationWebSocketClient } from '@core/utils/notification-websocket-client';
import { CurrentUser } from '@domains/auth/models/auth.model';
import { AuthService } from '@core/services/auth.service';
import { ErrorService } from '@core/services/error.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = `${environment.apiUrl}/notifications`;
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();
  private wsClient: NotificationWebSocketClient | null = null;
  private connectedUserId: number | null = null;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private errorService: ErrorService,
    private ngZone: NgZone
  ) { }

  getNotifications(page: number = 0, size: number = 20): Observable<NotificationResponse> {
    return this.http.get<any>(this.apiUrl, {
      params: { page: page.toString(), size: size.toString() }
    }).pipe(
      map((res) => {
        const data = res?.data ?? res;
        if (data?.content) {
          this.notificationsSubject.next(data.content);
        }
        return data as NotificationResponse;
      }),
      catchError((error) => {
        return of({
          content: [],
          number: 0,
          size: size,
          totalPages: 0,
          totalElements: 0
        } as NotificationResponse);
      })
    );
  }

  getUnreadNotifications(): Observable<Notification[]> {
    return this.http.get<any>(`${this.apiUrl}/unread`).pipe(
      map((res) => res?.data ?? res),
      catchError((error) => {
        return of([]);
      })
    );
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<any>(`${this.apiUrl}/${id}/read`, {}).pipe(
      map(() => void 0),
      catchError((error) => {
        this.errorService.addWarning('Could not mark notification as read');
        return of(void 0);
      })
    );
  }

  markAsUnread(id: number): Observable<void> {
    return this.http.put<any>(`${this.apiUrl}/${id}/unread`, {}).pipe(
      map(() => void 0),
      catchError((error) => {
        this.errorService.addWarning('Could not mark notification as unread');
        return of(void 0);
      })
    );
  }

  markAllAsRead(): Observable<void> {
    return this.http.put<any>(`${this.apiUrl}/read-all`, {}).pipe(
      map(() => void 0),
      catchError((error) => {
        this.errorService.addWarning('Could not mark all notifications as read');
        return of(void 0);
      })
    );
  }

  deleteNotification(id: number): Observable<void> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`).pipe(
      map(() => void 0),
      catchError((error) => {
        this.errorService.addWarning('Could not delete notification');
        return of(void 0);
      })
    );
  }

  /**
   * Called when WebSocket receives a new notification
   */
  onNotificationReceived(notification: Notification): void {
    this.ngZone.run(() => {
      const current = this.notificationsSubject.value;
      // Check if notification already exists to prevent duplicates
      const exists = current.some(n => n.id === notification.id);
      if (!exists) {
        this.notificationsSubject.next([notification, ...current]);
      }
    });
  }

  startRealtime(user: CurrentUser | null) {
    if (!user?.id) {
      this.stopRealtime();
      return;
    }
    if (this.wsClient?.isConnected() && this.connectedUserId === user.id) {
      return;
    }
    const token = this.authService.getToken() || undefined;
    this.wsClient = new NotificationWebSocketClient(`${environment.apiUrl}/ws`);
    this.connectedUserId = user.id;
    this.wsClient.connect(
      token,
      (msg) => this.onNotificationReceived(msg),
      () => {

        this.errorService.addWarning('Connection error. Notifications may be delayed.');
      },
      () => {
        this.wsClient?.send('/app/users/connect', user.id);
      }
    );
  }

  stopRealtime() {
    if (this.wsClient && this.connectedUserId) {
      this.wsClient.send('/app/users/disconnect', this.connectedUserId);
    }
    this.wsClient?.disconnect();
    this.wsClient = null;
    this.connectedUserId = null;
  }
}
