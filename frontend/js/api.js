// Centralized API Wrapper for EduNest V2
// Defaults to relative /api in production (or custom remote backend) or localhost:8080 in local development
const API_BASE = (function() {
  if (typeof window !== 'undefined' && window.EDUNEST_API_BASE) {
    return window.EDUNEST_API_BASE;
  }
  const stored = typeof localStorage !== 'undefined' ? localStorage.getItem('edunest_api_url') : null;
  if (stored) return stored;
  if (typeof window !== 'undefined' && (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')) {
    return 'http://localhost:8080/api';
  }
  return '/api';
})();

async function apiFetch(endpoint, options = {}) {
  const token = localStorage.getItem('edunest_token');
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...(options.headers || {})
  };

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers
  });

  if (response.status === 401) {
    localStorage.clear();
    window.location.href = '/login.html';
    throw new Error('Session expired. Please log in again.');
  }

  if (response.status === 204) {
    return null;
  }

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(data.error || data.message || `Request failed (${response.status})`);
  }

  return data;
}

// Convenient helper supporting both: apiRequest(url, method, body) and apiRequest(url, options)
async function apiRequest(endpoint, methodOrOptions = 'GET', body = null) {
  if (typeof methodOrOptions === 'object' && methodOrOptions !== null) {
    return apiFetch(endpoint, methodOrOptions);
  }
  const method = (typeof methodOrOptions === 'string' ? methodOrOptions : 'GET').toUpperCase();
  const options = { method };
  if (body !== null && body !== undefined) {
    options.body = typeof body === 'string' ? body : JSON.stringify(body);
  }
  return apiFetch(endpoint, options);
}

// Ensure global accessibility across all scripts
window.apiFetch = apiFetch;
window.apiRequest = apiRequest;
