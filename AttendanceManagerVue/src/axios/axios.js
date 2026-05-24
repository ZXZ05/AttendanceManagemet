import axios from 'axios'
import { clearLoginSession, getAuthToken } from '@/utils/auth'

axios.defaults.baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:9331'

axios.interceptors.request.use(
  (config) => {
    const token = getAuthToken()
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

axios.interceptors.response.use(
  response => response,
  (error) => {
    const status = error?.response?.status
    if (status === 401) {
      clearLoginSession()
    }
    return Promise.reject(error)
  }
)

export default axios
