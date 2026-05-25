import { get, post, unwrapResult } from './client'

export function getNotificationList(params = {}) {
  return get('/notification/list', { params }).then(unwrapResult)
}

export function getUnreadNotificationCount() {
  return get('/notification/unreadCount').then(unwrapResult)
}

export function markNotificationRead(id) {
  return post('/notification/read', { id }).then(unwrapResult)
}

export function markAllNotificationsRead() {
  return post('/notification/readAll', {}).then(unwrapResult)
}

