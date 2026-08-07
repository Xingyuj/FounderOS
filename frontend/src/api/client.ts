import type { AppointTalentRequest, Assignment, ContentItem, DashboardData, Decision, OrganizationData, RecruitTalentRequest, SoulDefinition } from '../domain/types'

const baseUrl = import.meta.env.VITE_API_BASE_URL ?? ''

export class ApiError extends Error {
  constructor(public status: number, message: string) { super(message) }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${baseUrl}${path}`, {
    ...init,
    headers: { 'Content-Type': 'application/json', ...init?.headers },
  })
  if (!response.ok) {
    const body = await response.json().catch(() => ({ detail: 'The request could not be completed.' })) as { detail?: string }
    throw new ApiError(response.status, body.detail ?? 'The request could not be completed.')
  }
  return response.json() as Promise<T>
}

export const api = {
  dashboard: () => request<DashboardData>('/api/dashboard'),
  contentItems: () => request<ContentItem[]>('/api/content-items'),
  organization: () => request<OrganizationData>('/api/organizations/current'),
  recruitTalent: (candidate: RecruitTalentRequest) => request<SoulDefinition>('/api/talent', {
    method: 'POST', body: JSON.stringify(candidate),
  }),
  appointTalent: (positionId: string, command: AppointTalentRequest) => request<Assignment>(`/api/positions/${positionId}/appoint`, {
    method: 'POST', body: JSON.stringify(command),
  }),
  resolveDecision: (id: string, selectedOptionId: string, founderComment: string) =>
    request<Decision>(`/api/decisions/${id}/resolve`, {
      method: 'POST', body: JSON.stringify({ selectedOptionId, founderComment }),
    }),
}
