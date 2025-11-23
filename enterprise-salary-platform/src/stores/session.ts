import { defineStore } from 'pinia'
import type { LoginResponse } from '@/api/login'

export interface EnterpriseOption {
  id: number
  code: string
  name: string
}

export interface UserInfo {
  id: number
  name: string
  phone: string
}

type SessionPayload = Pick<
  LoginResponse,
  'token' | 'user' | 'enterprises' | 'currentEnterpriseId'
>

const safeParse = <T>(value: string | null): T | null => {
  if (!value) return null
  try {
    return JSON.parse(value) as T
  } catch (e) {
    console.warn('Failed to parse cached session', e)
    return null
  }
}

export const useSessionStore = defineStore('session', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: safeParse<UserInfo>(localStorage.getItem('user')),
    enterprises: safeParse<EnterpriseOption[]>(localStorage.getItem('enterprises')) || [],
    currentEnterpriseId: (() => {
      const raw = localStorage.getItem('currentEnterpriseId')
      if (!raw) return null
      const parsed = Number(raw)
      return Number.isNaN(parsed) ? null : parsed
    })()
  }),

  getters: {
    currentEnterprise(state): EnterpriseOption | null {
      return (
        state.enterprises.find((item) => item.id === state.currentEnterpriseId) || null
      )
    }
  },

  actions: {
    setSession(payload: SessionPayload) {
      this.token = payload.token
      this.user = payload.user
      this.enterprises = payload.enterprises || []
      this.currentEnterpriseId =
        payload.currentEnterpriseId ?? this.enterprises[0]?.id ?? null

      localStorage.setItem('token', this.token)
      localStorage.setItem('user', JSON.stringify(this.user))
      localStorage.setItem('enterprises', JSON.stringify(this.enterprises))
      if (this.currentEnterpriseId !== null) {
        localStorage.setItem('currentEnterpriseId', String(this.currentEnterpriseId))
      } else {
        localStorage.removeItem('currentEnterpriseId')
      }
    },

    updateUser(user: UserInfo) {
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },

    switchEnterprise(id: number) {
      this.currentEnterpriseId = id
      localStorage.setItem('currentEnterpriseId', String(id))
    },

    logout() {
      this.token = ''
      this.user = null
      this.enterprises = []
      this.currentEnterpriseId = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('enterprises')
      localStorage.removeItem('currentEnterpriseId')
    }
  }
})
