import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:9331'

axios.interceptors.request.use(
  config => config,
  error => Promise.reject(error)
)

axios.interceptors.response.use(
  response => response,
  error => Promise.reject(error)
)

export default axios
