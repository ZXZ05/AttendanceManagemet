import { post } from './client'

export function login(payload) {
  return post('/login/login', payload)
}

export function register(payload) {
  return post('/login/register', payload)
}

