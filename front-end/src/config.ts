// URL Configuration
export const BASE_PROTOCOL = "http://";
export const BASE_URL = "localhost";
export const BASE_PORT = "8080";
export const API_URL = `http://localhost:8081/api`;

// Authentication endpoints
export const OAUTH_ENDPOINTS = {
  KAKAO: `${API_URL}/oauth2/authorization/kakao`,
  GOOGLE: `${API_URL}/oauth2/authorization/google`
};

// API endpoints
export const API_ENDPOINTS = {
  SHORTEN_URL: `${API_URL}/free/link`,
  CHECK_AVAILABILITY: (path: string) => `${API_URL}/urls/check-availability/${path}`
};
