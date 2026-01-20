// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.

export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  // SockJS requires http(s) URL (not ws://). Use http for dev SockJS endpoint.
  wsUrl: 'http://localhost:8080/ws',
  version: '1.0.0'
};
