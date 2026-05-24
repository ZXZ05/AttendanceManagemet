import { get, post } from './client'

export function getNotice() {
  return get('/meeting/getNotice')
}

export function getAllNotice() {
  return get('/meeting/getAllNotice')
}

export function getAllMeeting() {
  return get('/meeting/getAllMeeting')
}

export function createMeetingOrNotice(payload) {
  return post('/meeting/insert', payload)
}

export function deleteMeetingOrNoticeById(id) {
  return post('/meeting/deleteById', { id })
}

export function getRoomList() {
  return get('/fixedasset/getRoomList')
}

