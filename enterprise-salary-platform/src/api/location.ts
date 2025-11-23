import http from './http'

export interface LocationItem {
  id: number
  name: string
  parentId?: number
  parentName?: string
  level: number // 层级
  countText?: string // 例如 "(5 个二级地点...)"
  children?: LocationItem[]
}

// 获取地点列表 (树形结构)
export const getLocationList = (keyword?: string) =>
  http.get<LocationItem[]>('/locations', { params: { keyword } })
