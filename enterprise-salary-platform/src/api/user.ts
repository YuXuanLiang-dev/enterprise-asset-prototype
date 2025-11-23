import http from './http'

export interface ProfilePayload {
  userId: number
  name: string
  phone: string
}

export const fetchProfile = (userId: number) =>
  http.get<{ id: number; name: string; phone: string }>(`/auth/profile`, {
    params: { userId }
  })

export const updateProfile = (payload: ProfilePayload) =>
  http.put(`/auth/profile`, payload)

export const changePassword = (payload: {
  userId: number
  oldPassword: string
  newPassword: string
}) => http.post(`/auth/change-password`, payload)

