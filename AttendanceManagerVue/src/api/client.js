import axios from '@/axios/axios'

export function get(url, config) {
  return axios.get(url, config).then((res) => res.data)
}

export function post(url, data, config) {
  return axios.post(url, data, config).then((res) => res.data)
}

export function unwrapResult(response) {
  if (response && typeof response === 'object' && Object.prototype.hasOwnProperty.call(response, 'code')) {
    if (response.code === 200) {
      return response.data ?? response
    }
    throw new Error(response.message || '请求失败')
  }
  return response
}
