/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_URL: string
  readonly VITE_KAKAO_CLIENT_ID: string
  readonly VITE_KAKAO_REDIRECT_URI: string
  readonly VITE_GRANT_KAKAO_CODE: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}