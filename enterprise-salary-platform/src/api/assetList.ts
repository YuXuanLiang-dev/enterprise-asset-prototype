import http from './http'

// 资产状态类型
export type AssetStatus =
  | 'idle' // 闲置
  | 'in_use' // 在用
  | 'borrowed' // 借用
  | 'scrapped' // 报废
  | 'disposed' // 处置
  | 'repair'

export interface AssetListItem {
  id: number
  photo?: string // 图片URL
  code: string
  status: AssetStatus // 状态
  statusText: string // 状态中文
  name: string
  category: string
  spec: string // 规格型号
  brand: string
  location: string
  price: number
  purchaseDate: string
}

// 获取列表
export const getAssetListMain = (params?: any) =>
  http.get<{ list: AssetListItem[]; total: number }>('/assets', { params })

// 批量状态操作（领用/报修/报废/归还/变更）
export const batchOperateAssets = (action: string, ids: number[]) =>
  http.post(`/assets/actions/${action}`, { ids })
