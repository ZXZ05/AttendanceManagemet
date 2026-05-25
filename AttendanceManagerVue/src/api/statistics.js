export { getEducationStats, getAgeStats, getNewEmployeeStats } from './employee'
export { getCheckRepairStats } from './attendance'
import { post, unwrapResult } from './client'

export function getStatisticsOverview(payload = {}) {
  return post('/statistics/overview', payload).then(unwrapResult)
}
