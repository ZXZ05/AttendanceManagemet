import axios from '@/axios/axios'

export function get(url, config) {
  return axios.get(url, config).then((res) => res.data)
}

export function post(url, data, config) {
  return axios.post(url, data, config).then((res) => res.data)
}

