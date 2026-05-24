import { get, post, unwrapResult } from './client'

function upload(path, file) {
  const formData = new FormData()
  formData.append('file', file)
  return post(path, formData).then(unwrapResult)
}

export function importDepartmentExcel(file) {
  return upload('/excelImport/department', file)
}

export function importPositionExcel(file) {
  return upload('/excelImport/position', file)
}

export function importEmployeeExcel(file) {
  return upload('/excelImport/employee', file)
}

export function importCustomerExcel(file) {
  return upload('/excelImport/customer', file)
}

export function importFixedassetExcel(file) {
  return upload('/excelImport/fixedasset', file)
}

export function downloadImportTemplate(type) {
  return get('/excelImport/template', {
    params: { type },
    responseType: 'blob'
  })
}
