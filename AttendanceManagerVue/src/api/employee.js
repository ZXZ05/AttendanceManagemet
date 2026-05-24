import { get, post, unwrapResult } from './client'

export function getEmployeeList() {
  return get('/employee/list')
}

export function findEmployeeByNumber(number) {
  return post('/employee/findByNumber', { number })
}

export function findEmployeeByNameAndDepartment(payload) {
  return post('/employee/findByNameAndDepartment', payload)
}

export function createEmployee(payload) {
  return post('/employee/insert', payload)
}

export function updateEmployee(payload) {
  return post('/employee/update', payload)
}

export function updatePassword(payload) {
  return post('/employee/updatePassword', payload)
}

export function deleteEmployeeById(id) {
  return post('/employee/deleteById', { id })
}

export function getEmployeeTypeList() {
  return get('/employeeType/list')
}

export function findPositionsByDepartmentID(departmentID) {
  return post('/position/findByDepartmentID', { departmentID })
}

export function getEducationStats() {
  return get('/employee/getEducation').then(unwrapResult)
}

export function getAgeStats() {
  return get('/employee/getAge').then(unwrapResult)
}

export function getNewEmployeeStats() {
  return get('/employee/getNew').then(unwrapResult)
}
