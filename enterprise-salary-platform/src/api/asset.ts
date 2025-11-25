import http from './http'

// 资产数据接口定义
export interface AssetItem {
  id: number
  code: string // 资产编码
  name: string // 资产名称
  category: string // 资产分类
  spec: string // 规格型号
  brand: string // 品牌
  originalValue: number // 原值
  acquisitionDate: string // 取得日期
  accumulatedDepreciation: number // 累计折旧
  postingDate: string // 记账日期
  voucherNo: string // 记账凭证号
  depreciationMonths: number // 折旧年月
  remarks: string // 备注
  useDept?: string
  userName?: string
  managerDept?: string
  managerName?: string
  quantity?: number
}

export const getAssetList = (params?: any) =>
  http.get<{ list: AssetItem[]; total: number }>('/fiscal-cards', { params })

export const updateAsset = (data: AssetItem) =>
  http.put(`/fiscal-cards/${data.id}`, data)

export const deleteAssets = (ids: number[]) =>
  http.request({
    url: '/fiscal-cards',
    method: 'DELETE',
    data: { ids }
  })

export const importFiscalCards = (data: any[]) =>
  http.post('/fiscal-cards/import', data)
