import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

/**
 * WebSocket Service for managing real-time notifications
 * Integrates with NotificationWebSocketClient for STOMP communication
 */
@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private connectedSubject = new BehaviorSubject<boolean>(false);
  public connected$ = this.connectedSubject.asObservable();

  constructor() { }

  setConnected(connected: boolean): void {
    this.connectedSubject.next(connected);
  }

  isConnected(): boolean {
    return this.connectedSubject.value;
  }
}
