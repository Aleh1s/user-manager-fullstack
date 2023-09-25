export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL
export const OAUTH2_REDIRECT_URL = import.meta.env.VITE_OAUTH2_REDIRECT_URL
export const GOOGLE_AUTH_URL = `${API_BASE_URL}/oauth2/authorize/google?redirect_uri=${OAUTH2_REDIRECT_URL}`
export const GITHUB_AUTH_URL = `${API_BASE_URL}/oauth2/authorize/github?redirect_uri=${OAUTH2_REDIRECT_URL}`
