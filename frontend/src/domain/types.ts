export type Id = string
export type IsoDateTime = string

export type WorkStatus = 'PLANNED' | 'IN_PROGRESS' | 'WAITING' | 'BLOCKED' | 'FAILED' | 'COMPLETED'
export type PositionStatus = 'ACTIVE' | 'VACANT'
export type ContentStage = 'BRIEF' | 'RESEARCH' | 'DRAFT' | 'EDIT' | 'FACT_CHECK' | 'FOUNDER_APPROVAL' | 'FINAL'

export interface Company {
  id: Id
  name: string
  mission: string
  activeMilestone: string
  milestoneProgress: number
}

export interface JobDefinition {
  id: Id
  name: string
  purpose: string
  responsibilities: string[]
  authority: string[]
}

export interface SoulDefinition {
  id: Id
  name: string
  monogram: string
  color: string
  voice: string
  values: string[]
  portrait?: string
  archetype?: string
  level?: number
}

export interface RecruitTalentRequest {
  name: string
  portrait: string
  archetype: string
  voice: string
  values: string[]
}

export interface AppointTalentRequest { soulId: Id }

export interface Position {
  id: Id
  jobId: Id
  title: string
  status: PositionStatus
  reportsToPositionId?: Id
}

export interface Assignment {
  id: Id
  positionId: Id
  soulId: Id
  activeFrom: IsoDateTime
}

export interface Task {
  id: Id
  title: string
  status: WorkStatus
  ownerPositionId: Id
  projectName: string
  dueLabel?: string
}

export interface DecisionOption { id: string; label: string; impact: string }

export interface Decision {
  id: Id
  question: string
  context: string
  recommendation: string
  evidence: string[]
  options: DecisionOption[]
  status: 'OPEN' | 'RESOLVED'
  selectedOptionId?: string
  founderComment?: string
}

export interface Evidence {
  id: Id
  claim: string
  source: string
  confidence: 'HIGH' | 'MEDIUM' | 'CONTRADICTED'
  note: string
}

export interface Artifact {
  id: Id
  type: string
  title: string
  version: number
  status: 'DRAFT' | 'REVIEWED' | 'FINAL'
  excerpt: string
  createdAt: IsoDateTime
}

export interface Activity {
  id: Id
  actor: string
  verb: string
  detail: string
  createdAt: IsoDateTime
  tone: 'neutral' | 'positive' | 'warning'
}

export interface ContentItem {
  id: Id
  title: string
  brief: string
  audience: string
  stage: ContentStage
  status: WorkStatus
  ownerPositionId: Id
  stages: Array<{ stage: ContentStage; label: string; status: 'COMPLETE' | 'CURRENT' | 'UPCOMING' | 'FAILED' }>
  evidence: Evidence[]
  artifacts: Artifact[]
  reviews: Array<{ reviewer: string; verdict: 'APPROVED' | 'CHANGES_REQUESTED' | 'FAILED'; note: string }>
}

export interface DashboardData {
  company: Company
  tasks: Task[]
  decisions: Decision[]
  activities: Activity[]
  latestArtifact: Artifact
}

export interface OrganizationData {
  company: Company
  jobs: JobDefinition[]
  souls: SoulDefinition[]
  positions: Position[]
  assignments: Assignment[]
}
