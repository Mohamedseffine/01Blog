/* NotificationWebSocketClient using SockJS + STOMP */
// lightweight wrapper to connect to backend STOMP endpoint
// uses stompjs and sockjs-client packages

import { Client, IMessage, Frame } from '@stomp/stompjs';
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import * as SockJS from 'sockjs-client';

type NotificationHandler = (data: any) => void;

export class NotificationWebSocketClient {
  private client: Client | null = null;
  private connected = false;

  constructor(private url: string) { }

  connect(
    token?: string,
    onMessage?: NotificationHandler,
    onError?: (err: any) => void,
    onConnect?: () => void
  ) {
    if (this.connected) return;

    this.client = new Client({
      webSocketFactory: () =>
        new SockJS(token ? `${this.url}?token=${encodeURIComponent(token)}` : this.url) as any,
      reconnectDelay: 5000,
      debug: (str: string) => console.debug('[STOMP]', str),
    });

    this.client.onConnect = (frame: Frame) => {
      this.connected = true;
      console.info('STOMP connected', frame && frame.headers);
      if (onConnect) {
        onConnect();
      }
      // subscribe to personal queue only (user-specific notifications)
      try {
        this.client?.subscribe('/user/queue/notifications', (msg: IMessage) => {
          if (msg.body && onMessage) onMessage(JSON.parse(msg.body));
        });
      } catch (e) {
        console.error('Subscribe error', e);
      }
    };

    this.client.onStompError = (frame: Frame) => {
      this.connected = false;
      console.error('STOMP error', frame);
      if (onError) onError(frame.headers);
    };

    this.client.onWebSocketError = (evt: any) => {
      console.error('WebSocket error', evt);
      this.connected = false;
      if (onError) onError(evt);
    };

    this.client.onWebSocketClose = (evt: any) => {
      console.info('WebSocket closed', evt);
      this.connected = false;
    };

    this.client.activate();
  }

  disconnect() {
    if (this.client) {
      try {
        this.client.deactivate();
      } finally {
        this.connected = false;
      }
    }
  }

  isConnected() {
    return this.connected;
  }

  // publish a simple message to the server (useful to notify presence)
  send(destination: string, body: any) {
    if (!this.client || !this.connected) return;
    try {
      this.client.publish({ destination, body: typeof body === 'string' ? body : JSON.stringify(body) });
    } catch (e) {
      console.error('Publish error', e);
    }
  }
}
