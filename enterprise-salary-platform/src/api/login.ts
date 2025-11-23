// src/api/login.ts
import http from './http'

export interface LoginParams {
  phone: string
  password: string
}

export interface LoginResponse {
  token: string
  user: {
    id: number
    name: string
    phone: string
  }
  enterprises: { id: number; code: string; name: string }[]
  currentEnterpriseId: number
}

export function login(data: LoginParams) {
  return http.post<LoginResponse>('/auth/login', data)
}
