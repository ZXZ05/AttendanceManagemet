import { get, post } from './client'

export function getFixedAssetList() {
  return get('/fixedasset/list')
}

export function getFixedAssetTypeList() {
  return get('/fixedassetType/list')
}

export function createFixedAsset(payload) {
  return post('/fixedasset/insert', payload)
}

export function paySalary(payload) {
  return post('/salary/payOff', payload)
}

export function exportCheckExcel(params) {
  return get('/check/exportExcel', { params, responseType: 'blob' })
}

export function findSalaryByNumberAndMonth(payload) {
  return post('/salary/findByNumberAndMonth', payload)
}

export { getCheckList } from './attendance'
