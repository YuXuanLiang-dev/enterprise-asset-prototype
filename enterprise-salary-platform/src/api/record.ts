// src/api/record.ts
import http from './http'

export interface RecordItem {
  id: number
  type: string // 操作类型 (如: 编辑记录, 入库, 变更)
  assetCode: string // 资产编码
  actionTime: string // 操作时间
  operator: string // 操作人
}

// 获取操作记录列表
export const getRecordList = (params?: any) =>
  http.get<{ list: RecordItem[]; total: number }>('/records', { params })
