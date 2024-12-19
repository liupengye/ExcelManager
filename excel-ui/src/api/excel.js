import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 响应拦截器
api.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error)
    ElMessage.error(error.response?.data || '请求失败')
    return Promise.reject(error)
  }
)

export const uploadExcel = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/excel/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const getExcelList = () => {
  return api.get('/excel/list')
}

export const getTableData = (tableId, page = 1, size = 10) => {
  return api.get(`/excel/data/${tableId}`, {
    params: { page, size }
  }).then(response => response.data)
}

export const updateCell = (tableId, data) => {
  return api.put(`/excel/data/${tableId}`, data)
}

export const deleteExcel = (id) => {
  return api.delete(`/excel/${id}`)
} 