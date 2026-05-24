import { get, post } from './client'

export function getAttendanceTimeRange() {
  return get('/enum/getAttendanceTime')
}

export function checkOn(payload) {
  return post('/check/checkOn', payload)
}

export function checkOff(payload) {
  return post('/check/checkOff', payload)
}

export function getCheckOnStatus(payload) {
  return post('/check/getCheckOn', payload)
}

export function getCheckOffStatus(payload) {
  return post('/check/getCheckOff', payload)
}

export function getCheckInfo(payload) {
  return post('/check/getCheckInfo', payload)
}

export function findCheckByNumber(payload) {
  return post('/check/findByNumber', payload)
}

export function getCheckList(payload) {
  return post('/check/getCheckList', payload)
}

export function exportCheckExcel(params) {
  return get('/check/exportExcel', { params, responseType: 'blob' })
}

