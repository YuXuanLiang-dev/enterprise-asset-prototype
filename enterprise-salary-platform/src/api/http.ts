import axios, { type AxiosRequestConfig } from 'axios'

type HttpInstance = {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T>
  post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>
  put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>
  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T>
  request<T>(config: AxiosRequestConfig): Promise<T>
}

const instance = axios.create({
  // 统一用相对路径，开发态走 Vite 代理到后端 8080，生产同源
  baseURL: '/api',
  timeout: 15000
})

instance.interceptors.request.use((config) => {
  config.headers = config.headers || {}

  const enterpriseId = localStorage.getItem('currentEnterpriseId')
  if (enterpriseId) {
    config.headers['X-Enterprise-Id'] = enterpriseId
  }

  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

instance.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const resp = error && typeof error === 'object' ? (error as any).response : undefined
    const status = resp?.status
    if (status === 401) {
      try {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        localStorage.removeItem('enterprises')
        localStorage.removeItem('currentEnterpriseId')
      } catch (e) {
        console.warn('clear session failed', e)
      }
      window.location.href = '/login'
      return Promise.reject(error)
    }
    const message =
      resp?.data?.message ||
      error?.message ||
      '请求失败，请稍后重试'
    console.error(message)
    return Promise.reject(error)
  }
)

export default instance as HttpInstance
