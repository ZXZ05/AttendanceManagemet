import { ref } from 'vue'
import { findEmployeeByNumber } from '@/api/employee'
import { getLoginUsername, getUserType, setLoginSession, getAuthToken } from '@/utils/auth'

const name = ref('')
const number = ref('')
const type = ref('')
const loaded = ref(false)

let loadPromise = null

export async function loadCurrentUser() {
  if (loaded.value) return { name: name.value, number: number.value, type: type.value }
  if (loadPromise) return loadPromise

  const loginNumber = getLoginUsername()
  if (!loginNumber) {
    loaded.value = true
    return { name: '', number: '', type: '' }
  }

  loadPromise = (async () => {
    try {
      const data = await findEmployeeByNumber(loginNumber)
      name.value = data?.name || ''
      number.value = data?.number || loginNumber
      type.value = data?.type ?? getUserType() ?? ''

      const token = getAuthToken()
      if (token) {
        setLoginSession(number.value, token, type.value)
      }

      loaded.value = true
      return { name: name.value, number: number.value, type: type.value }
    } catch (error) {
      loaded.value = true
      throw error
    } finally {
      loadPromise = null
    }
  })()

  return loadPromise
}

export function useUser() {
  return { name, number, type, loaded }
}
