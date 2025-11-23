import http from './http'

export interface DashboardResponse {
  assetOverview: {
    total: number
    detail: { value: number; name: string; percent: string }[]
  }
  assetValueOverview: {
    totalValue: number
    detail: { value: number; name: string; percent: string }[]
  }
  assetStats: {
    categories: string[]
    values: number[]
  }
  locationValueStats: {
    locations: string[]
    values: number[]
  }
}

export const getDashboardData = () => http.get<DashboardResponse>('/dashboard')
