// Minimal browser polyfills for libraries that expect Node-like globals
// Expose `global` for libs that reference it (e.g. some websocket/sockjs helpers).
(window as any).global = window;

// Provide a minimal `process.env` to satisfy packages checking environment.
try {
  if (!(window as any).process) {
    (window as any).process = { env: { NODE_ENV: 'development' } };
  } else if (!(window as any).process.env) {
    (window as any).process.env = { NODE_ENV: 'development' };
  }
} catch (e) {
  // ignore
}

// Ensure crypto is accessible through global.crypto for older libs
if (!(window as any).crypto && (window as any).msCrypto) {
  (window as any).crypto = (window as any).msCrypto;
}
