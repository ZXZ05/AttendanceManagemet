import { get, post } from './client'

export function getDepartmentList() {
  return get('/department/list')
}

export function createDepartment(payload) {
  return post('/department/insert', payload)
}

export function updateDepartment(payload) {
  return post('/department/update', payload)
}

export function deleteDepartmentById(id) {
  return post('/department/deleteById', { id })
}

export function createPosition(payload) {
  return post('/position/insert', payload)
}

export function updatePosition(payload) {
  return post('/position/update', payload)
}

export function deletePositionById(id) {
  return post('/position/deleteById', { id })
}

