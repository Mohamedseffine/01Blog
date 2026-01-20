import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { NotificationWebSocketClient } from '../utils/notification-websocket-client';
import { environment } from '@env/environment';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private connectedSubject = new BehaviorSubject<boolean>(false);
  public connected$ = this.connectedSubject.asObservable();

  private client: NotificationWebSocketClient | null = null;

  constructor() {}

  connect(token?: string, onMessage?: (data: any) => void) {
    if (!this.client) {
      this.client = new NotificationWebSocketClient(environment.wsUrl);
    }
    this.client.connect(token, (data) => {
      this.connectedSubject.next(true);
      if (onMessage) onMessage(data);
    }, () => {
      this.connectedSubject.next(false);
    });
  }

  // notify server that user has connected (server expects /app/users/connect)
  sendUserConnect(userId: number | string) {
    if (!this.client) return;
    this.client.send('/app/users/connect', String(userId));
  }

  disconnect() {
    if (this.client) {
      this.client.disconnect();
      this.connectedSubject.next(false);
    }
  }

  isConnected(): boolean {
    return this.connectedSubject.value;
  }
}
