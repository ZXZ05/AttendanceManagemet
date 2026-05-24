export const USER_TYPE = {
  EMPLOYEE: '2',
  ADMIN: '3'
}

const USERNAME_KEY = 'username'
const TOKEN_KEY = 'auth_token'
const USER_TYPE_KEY = 'user_type'

export function setLoginSession(username, token, userType) {
  sessionStorage.setItem(USERNAME_KEY, username)
  sessionStorage.setItem(TOKEN_KEY, token)
  sessionStorage.setItem(USER_TYPE_KEY, userType == null ? '' : String(userType))
}

export function clearLoginSession() {
  sessionStorage.removeItem(USERNAME_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(USER_TYPE_KEY)
}

export function getLoginUsername() {
  return sessionStorage.getItem(USERNAME_KEY)
}

export function getAuthToken() {
  return sessionStorage.getItem(TOKEN_KEY)
}

export function getUserType() {
  return sessionStorage.getItem(USER_TYPE_KEY)
}

export function isAuthenticated() {
  return !!getLoginUsername() && !!getAuthToken()
}

export function isAdmin() {
  return getUserType() === USER_TYPE.ADMIN
}
