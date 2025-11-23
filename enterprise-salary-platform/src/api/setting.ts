import http from './http'

// --- 参数设置相关 ---
export interface ParamItem {
  id: number
  name: string
  type: 'text' | 'select' | 'date'
  typeText: string
}

export const getParamList = (keyword?: string) =>
  http.get<ParamItem[]>('/settings/params', { params: { keyword } })

// --- 分类设置相关 ---
export interface CategoryItem {
  id: number
  name: string
  paramCount: number // 关联参数数量
  isRequired?: boolean
}

export const getCategoryList = (keyword?: string) =>
  http.get<{ list: CategoryItem[]; total: number }>('/settings/categories', {
    params: { keyword }
  })
