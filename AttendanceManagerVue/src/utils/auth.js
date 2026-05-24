export const USER_ROLE = {
  EMPLOYEE: '4',
  SUPERVISOR: '2',
  HR: '5',
  FINANCE: '6',
  SYS_ADMIN: '3',
  LEGACY_EMPLOYEE: '1'
}

// Keep compatibility with existing imports.
export const USER_TYPE = {
  EMPLOYEE: USER_ROLE.EMPLOYEE,
  ADMIN: USER_ROLE.SYS_ADMIN
}

export const PERMISSION = {
  ADMIN_ENTRY: 'admin.entry',
  ADMIN_DASHBOARD: 'admin.dashboard',
  EMPLOYEE_MANAGE: 'employee.manage',
  ORG_MANAGE: 'org.manage',
  FINANCE_MANAGE: 'finance.manage',
  STAT_VIEW: 'stat.view',
  EXCEL_IMPORT: 'excel.import',
  ANNOUNCEMENT_MANAGE: 'announcement.manage',
  SYSTEM_ADMIN: 'system.admin'
}

const USERNAME_KEY = 'username'
const TOKEN_KEY = 'auth_token'
const USER_TYPE_KEY = 'user_type'

const ROLE_LABEL_MAP = {
  [USER_ROLE.EMPLOYEE]: '普通员工',
  [USER_ROLE.SUPERVISOR]: '部门主管',
  [USER_ROLE.HR]: '人事',
  [USER_ROLE.FINANCE]: '财务',
  [USER_ROLE.SYS_ADMIN]: '系统管理员',
  [USER_ROLE.LEGACY_EMPLOYEE]: '普通员工'
}

const ROLE_PERMISSION_MAP = {
  [USER_ROLE.EMPLOYEE]: [],
  [USER_ROLE.LEGACY_EMPLOYEE]: [],
  [USER_ROLE.SUPERVISOR]: [
    PERMISSION.ADMIN_ENTRY,
    PERMISSION.ADMIN_DASHBOARD,
    PERMISSION.STAT_VIEW,
    PERMISSION.ANNOUNCEMENT_MANAGE
  ],
  [USER_ROLE.HR]: [
    PERMISSION.ADMIN_ENTRY,
    PERMISSION.ADMIN_DASHBOARD,
    PERMISSION.EMPLOYEE_MANAGE,
    PERMISSION.ORG_MANAGE,
    PERMISSION.STAT_VIEW,
    PERMISSION.EXCEL_IMPORT,
    PERMISSION.ANNOUNCEMENT_MANAGE
  ],
  [USER_ROLE.FINANCE]: [
    PERMISSION.ADMIN_ENTRY,
    PERMISSION.ADMIN_DASHBOARD,
    PERMISSION.FINANCE_MANAGE,
    PERMISSION.STAT_VIEW,
    PERMISSION.ANNOUNCEMENT_MANAGE
  ],
  [USER_ROLE.SYS_ADMIN]: Object.values(PERMISSION)
}

export function setLoginSession(username, token, userType) {
  sessionStorage.setItem(USERNAME_KEY, username)
  sessionStorage.setItem(TOKEN_KEY, token)
  sessionStorage.setItem(USER_TYPE_KEY, userType == null ? '' : String(userType))
}

export function clearLoginSession() {
  sessionStorage.removeItem(USERNAME_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(USER_TYPE_KEY)
}

export function getLoginUsername() {
  return sessionStorage.getItem(USERNAME_KEY)
}

export function getAuthToken() {
  return sessionStorage.getItem(TOKEN_KEY)
}

export function getUserType() {
  return sessionStorage.getItem(USER_TYPE_KEY)
}

export function getRoleLabel(userType = getUserType()) {
  return ROLE_LABEL_MAP[String(userType)] || '普通员工'
}

export function getRolePermissions(userType = getUserType()) {
  return ROLE_PERMISSION_MAP[String(userType)] || []
}

export function hasPermission(permission, userType = getUserType()) {
  if (!permission) return true
  return getRolePermissions(userType).includes(permission)
}

export function hasAnyPermission(permissions = [], userType = getUserType()) {
  return permissions.some((item) => hasPermission(item, userType))
}

export function hasAllPermissions(permissions = [], userType = getUserType()) {
  return permissions.every((item) => hasPermission(item, userType))
}

export function canAccessAdminPortal(userType = getUserType()) {
  return hasPermission(PERMISSION.ADMIN_ENTRY, userType)
}

export function isAuthenticated() {
  return !!getLoginUsername() && !!getAuthToken()
}

// Keep compatibility with existing checks that mean "system administrator".
export function isAdmin(userType = getUserType()) {
  return hasPermission(PERMISSION.SYSTEM_ADMIN, userType)
}
