const LOGIN_COOKIE_KEY = 'LOGIN'
const USERNAME_KEY = 'username'

function hasLoginCookie() {
  return document.cookie
    .split(';')
    .map((item) => item.trim())
    .some((item) => item.startsWith(`${LOGIN_COOKIE_KEY}=`))
}

export function setLoginSession(username) {
  document.cookie = `${LOGIN_COOKIE_KEY}=true; path=/`
  sessionStorage.setItem(USERNAME_KEY, username)
}

export function clearLoginSession() {
  document.cookie = `${LOGIN_COOKIE_KEY}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`
  sessionStorage.removeItem(USERNAME_KEY)
}

export function getLoginUsername() {
  return sessionStorage.getItem(USERNAME_KEY)
}

export function isAuthenticated() {
  return hasLoginCookie() && !!getLoginUsername()
}

