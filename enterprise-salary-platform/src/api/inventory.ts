import http from './http'

// 盘点统计数据
export interface InventoryStats {
  pending: number
  verified: number
  surplus: number
  loss: number
  pendingTag: number
  tagged: number
  noTag: number
}

// 盘点列表项
export interface InventoryItem {
  id: number
  status: 'pending' | 'verified' | 'surplus' | 'loss'
  statusText: string
  photo?: string
  codeFiscal: string
  nameFiscal: string
  category: string
  spec: string
  brand: string
  location: string
  acquisitionDate: string
  originalValue: number
  netValue: number
  accumulatedDepreciation: number
}

// 获取盘点数据
export const getInventoryData = (params?: any) =>
  http.get<{ stats: InventoryStats; list: InventoryItem[]; total: number }>('/inventory', { params })
