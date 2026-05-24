import { get, post } from './client'

export function getTaskList(payload) {
  return post('/task/list', payload)
}

export function findTaskByApplyID(payload) {
  return post('/task/findByApplyID', payload)
}

export function approveTask(payload) {
  return post('/task/approval', payload)
}

export function getLeaveList(payload) {
  return post('/leave/list', payload)
}

export function createLeave(payload) {
  return post('/leave/insert', payload)
}

export function getLeaveTypeList() {
  return get('/leaveType/list')
}

export function findFixedAssetByEmployeeNumber(payload) {
  return post('/fixedasset/findByEmployeeNumber', payload)
}

export function createFixedAsset(payload) {
  return post('/fixedasset/insert', payload)
}

export function getFixedAssetList() {
  return get('/fixedasset/list')
}

export function getFixedAssetTypeList() {
  return get('/fixedassetType/list')
}

