import { get, post } from './client'

export function getCustomerList() {
  return get('/customer/list')
}

export function findCustomerByApplyNumber(applyNumber) {
  return post('/customer/findByApplyNumber', { applyNumber })
}

export function findCustomerByNameAndType(payload) {
  return post('/customer/findByNameAndType', payload)
}

export function createCustomer(payload) {
  return post('/customer/insert', payload)
}

export function updateCustomer(payload) {
  return post('/customer/update', payload)
}

export function deleteCustomerById(id) {
  return post('/customer/deleteById', { id })
}

