import { delay, http, HttpResponse } from 'msw'
import type { AppointTalentRequest, RecruitTalentRequest } from '../domain/types'
import { contentFixture, dashboardFixture, organizationFixture } from './fixtures'

let dashboard = structuredClone(dashboardFixture)
let organization = structuredClone(organizationFixture)

export const handlers = [
  http.get('/api/dashboard', async () => { await delay(180); return HttpResponse.json(dashboard) }),
  http.get('/api/content-items', async () => { await delay(160); return HttpResponse.json([contentFixture]) }),
  http.get('/api/organizations/current', async () => { await delay(140); return HttpResponse.json(organization) }),
  http.post('/api/talent', async ({ request }) => {
    const candidate = await request.json() as RecruitTalentRequest
    if (!candidate.name.trim() || candidate.values.length < 2) {
      return HttpResponse.json({ detail: 'Complete the name and two core traits.' }, { status: 400 })
    }
    const key = `${candidate.name.toLowerCase().replace(/[^a-z0-9]+/g, '-')}-${Date.now()}`
    const soul = { id: `soul-${key}`, name: candidate.name.trim(), monogram: candidate.name.trim().slice(0, 2).toUpperCase(), color: '#d86a3f', voice: candidate.voice, values: candidate.values, portrait: candidate.portrait, archetype: candidate.archetype, level: 1 }
    organization.souls.push(soul)
    await delay(450)
    return HttpResponse.json(soul, { status: 201 })
  }),
  http.post('/api/positions/:positionId/appoint', async ({ params, request }) => {
    const command = await request.json() as AppointTalentRequest
    const position = organization.positions.find((entry) => entry.id === params.positionId)
    const soul = organization.souls.find((entry) => entry.id === command.soulId)
    if (!position || !soul) return HttpResponse.json({ detail: 'Position or talent not found.' }, { status: 404 })
    if (organization.assignments.some((entry) => entry.positionId === position.id)) return HttpResponse.json({ detail: 'This position is already filled.' }, { status: 409 })
    if (organization.assignments.some((entry) => entry.soulId === soul.id)) return HttpResponse.json({ detail: 'This employee is already appointed.' }, { status: 409 })
    const assignment = { id: `assignment-${position.id}-${soul.id}`, soulId: soul.id, positionId: position.id, activeFrom: new Date().toISOString() }
    organization.assignments.push(assignment)
    position.status = 'ACTIVE'
    await delay(350)
    return HttpResponse.json(assignment, { status: 201 })
  }),
  http.post('/api/decisions/:id/resolve', async ({ params, request }) => {
    const body = await request.json() as { selectedOptionId?: string; founderComment?: string }
    const decision = dashboard.decisions.find((item) => item.id === params.id)
    if (!decision) return HttpResponse.json({ detail: 'Decision not found.' }, { status: 404 })
    if (decision.status === 'RESOLVED') return HttpResponse.json({ detail: 'Decision was already resolved.' }, { status: 409 })
    if (!decision.options.some((option) => option.id === body.selectedOptionId)) {
      return HttpResponse.json({ detail: 'Choose one of the stored options.' }, { status: 400 })
    }
    decision.status = 'RESOLVED'
    decision.selectedOptionId = body.selectedOptionId
    decision.founderComment = body.founderComment
    dashboard.tasks[0] = { ...dashboard.tasks[0], status: 'IN_PROGRESS', dueLabel: 'Revision underway' }
    dashboard.activities.unshift({ id: 'activity-resolved', actor: 'Founder', verb: 'resolved a production decision', detail: decision.options.find((option) => option.id === body.selectedOptionId)?.label ?? '', createdAt: new Date().toISOString(), tone: 'positive' })
    await delay(240)
    return HttpResponse.json(decision)
  }),
]

export function resetFixtures() { dashboard = structuredClone(dashboardFixture); organization = structuredClone(organizationFixture) }
