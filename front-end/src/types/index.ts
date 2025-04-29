export interface ShortenedUrl {
  id: string;
  originalUrl: string;
  shortenUrl: string;
  customPath?: string;
  createdAt: string;
}

export interface UserCredentials {
  email: string;
  password: string;
}

export interface User {
  id: string;
  email: string;
  name?: string;
  createdAt: string;
} 