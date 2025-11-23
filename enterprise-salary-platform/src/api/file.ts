import http from './http'

export interface FileParseResult {
  type: string
  headers: string[]
  preview: Record<string, any>[]
  count: number
  message: string
}

export const uploadAndParse = (type: string, file: File) => {
  const form = new FormData()
  form.append('file', file)
  return http.post<FileParseResult>(`/files/parse/${type}`, form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
