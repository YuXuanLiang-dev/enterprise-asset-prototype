import http from './http'

// 部门树节点接口
export interface DeptNode {
  id: number
  label: string
  children?: DeptNode[]
}

// 人员信息接口
export interface PersonnelItem {
  id: number
  name: string
  deptName: string
  deptId?: number
  isLeader?: boolean // 是否负责人
}

// 获取部门树
export const getDeptTree = () => http.get<DeptNode[]>('/departments/tree')

// 获取人员列表
export const getPersonnelList = (params?: any) =>
  http.get<{ list: PersonnelItem[]; total: number }>('/departments/personnel', {
    params
  })
