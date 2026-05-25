import { post, unwrapResult } from './client'

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return post('/file/avatar/upload', formData).then(unwrapResult)
}

