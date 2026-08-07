import type { ContentItem, DashboardData, OrganizationData } from '../domain/types'

const company = {
  id: 'company-founder-os',
  name: 'FounderOS Studio',
  mission: 'Build a company that turns founder intent into trustworthy, finished work.',
  activeMilestone: 'M3A · Experience discovery',
  milestoneProgress: 68,
}

const jobs = [
  { id: 'job-content', name: 'Content Lead', purpose: 'Own the editorial outcome.', responsibilities: ['Set angle', 'Coordinate the pipeline'], authority: ['Assign content work'] },
  { id: 'job-research', name: 'Researcher', purpose: 'Build an evidence base.', responsibilities: ['Find sources', 'Flag contradictions'], authority: ['Request clarification'] },
  { id: 'job-writer', name: 'Writer', purpose: 'Turn approved evidence into prose.', responsibilities: ['Draft', 'Revise'], authority: ['Propose structure'] },
  { id: 'job-editor', name: 'Editor', purpose: 'Improve clarity and argument.', responsibilities: ['Review', 'Request changes'], authority: ['Reject weak drafts'] },
  { id: 'job-checker', name: 'Fact Checker', purpose: 'Independently verify claims.', responsibilities: ['Verify claims', 'Record uncertainty'], authority: ['Block publication'] },
  { id: 'job-distribution', name: 'Distribution Lead', purpose: 'Find the right audience.', responsibilities: ['Plan channels'], authority: ['Draft distribution plans'] },
]

const souls = [
  { id: 'soul-maya', name: 'Maya', monogram: 'MY', color: '#e36b3d', voice: 'Decisive and warm', values: ['clarity', 'momentum'], portrait: '🦊', archetype: 'Pathfinder', level: 7 },
  { id: 'soul-iris', name: 'Iris', monogram: 'IR', color: '#356a62', voice: 'Curious and exact', values: ['evidence', 'nuance'], portrait: '🦉', archetype: 'Seeker', level: 5 },
  { id: 'soul-nova', name: 'Nova', monogram: 'NV', color: '#6656a8', voice: 'Energetic and direct', values: ['usefulness', 'rhythm'], portrait: '🐇', archetype: 'Spark', level: 6 },
  { id: 'soul-ada', name: 'Ada', monogram: 'AD', color: '#b04759', voice: 'Candid and constructive', values: ['coherence', 'craft'], portrait: '🐈', archetype: 'Refiner', level: 6 },
  { id: 'soul-guardian', name: 'Guardian', monogram: 'GU', color: '#33465d', voice: 'Measured and skeptical', values: ['truth', 'traceability'], portrait: '🐺', archetype: 'Sentinel', level: 8 },
  { id: 'soul-ember', name: 'Ember', monogram: 'EM', color: '#c65f3b', voice: 'Bold and resourceful', values: ['bold', 'resourceful'], portrait: '🦁', archetype: 'Spark', level: 3 },
  { id: 'soul-sora', name: 'Sora', monogram: 'SO', color: '#4a7690', voice: 'Curious and empathetic', values: ['curious', 'empathetic'], portrait: '🦝', archetype: 'Sage', level: 2 },
]

const positions = [
  { id: 'position-content', jobId: 'job-content', title: 'Content Lead', status: 'ACTIVE' as const },
  { id: 'position-research', jobId: 'job-research', title: 'Researcher', status: 'ACTIVE' as const, reportsToPositionId: 'position-content' },
  { id: 'position-writer', jobId: 'job-writer', title: 'Writer', status: 'ACTIVE' as const, reportsToPositionId: 'position-content' },
  { id: 'position-editor', jobId: 'job-editor', title: 'Editor', status: 'ACTIVE' as const, reportsToPositionId: 'position-content' },
  { id: 'position-checker', jobId: 'job-checker', title: 'Fact Checker', status: 'ACTIVE' as const, reportsToPositionId: 'position-content' },
  { id: 'position-distribution', jobId: 'job-distribution', title: 'Distribution Lead', status: 'VACANT' as const, reportsToPositionId: 'position-content' },
]

const assignments = souls.slice(0, 5).map((soul, index) => ({
  id: `assignment-${soul.id}`,
  soulId: soul.id,
  positionId: positions[index].id,
  activeFrom: '2026-08-01T00:00:00Z',
}))

export const organizationFixture: OrganizationData = { company, jobs, souls, positions, assignments }

export const contentFixture: ContentItem = {
  id: 'content-quiet-leverage',
  title: 'The quiet leverage of a one-person company',
  brief: 'A practical essay on building durable leverage with a small, governed AI team—without pretending the agents are autonomous employees.',
  audience: 'Independent founders building software businesses',
  stage: 'FACT_CHECK',
  status: 'BLOCKED',
  ownerPositionId: 'position-content',
  stages: [
    { stage: 'BRIEF', label: 'Brief', status: 'COMPLETE' },
    { stage: 'RESEARCH', label: 'Research', status: 'COMPLETE' },
    { stage: 'DRAFT', label: 'Draft', status: 'COMPLETE' },
    { stage: 'EDIT', label: 'Edit', status: 'COMPLETE' },
    { stage: 'FACT_CHECK', label: 'Fact check', status: 'FAILED' },
    { stage: 'FOUNDER_APPROVAL', label: 'Approval', status: 'UPCOMING' },
    { stage: 'FINAL', label: 'Final', status: 'UPCOMING' },
  ],
  evidence: [
    { id: 'evidence-1', claim: 'Solo businesses are adopting AI-assisted workflows fastest.', source: '2026 Independent Work Index · p. 18', confidence: 'HIGH', note: 'Survey methodology and sample are available.' },
    { id: 'evidence-2', claim: 'AI teams reliably cut production time by 60%.', source: 'Vendor benchmark · 2025', confidence: 'CONTRADICTED', note: 'Independent benchmark reports 18–34%; the draft overstates the evidence.' },
    { id: 'evidence-3', claim: 'Human approval improves trust in consequential automation.', source: 'NIST Human-AI Interaction Review', confidence: 'MEDIUM', note: 'Directionally supportive; not specific to founder workflows.' },
  ],
  artifacts: [
    { id: 'artifact-draft-1', type: 'ARTICLE_DRAFT', title: 'Quiet Leverage · first draft', version: 1, status: 'DRAFT', excerpt: 'The smallest companies have always had an unusual advantage: they can change direction before a meeting would have ended...', createdAt: '2026-08-06T01:15:00Z' },
    { id: 'artifact-draft-2', type: 'ARTICLE_DRAFT', title: 'Quiet Leverage · edited draft', version: 2, status: 'REVIEWED', excerpt: 'Leverage is useful only when a founder can see where it came from, interrupt it, and trust the result...', createdAt: '2026-08-06T03:40:00Z' },
    { id: 'artifact-final', type: 'ARTICLE', title: 'Quiet Leverage · revision candidate', version: 3, status: 'FINAL', excerpt: 'A one-person company does not need an imaginary office. It needs a reliable way to turn intent into reviewed, durable work...', createdAt: '2026-08-06T05:05:00Z' },
  ],
  reviews: [
    { reviewer: 'Ada · Editor', verdict: 'CHANGES_REQUESTED', note: 'The opening is strong. Replace the “60% faster” claim and make the governance example concrete.' },
    { reviewer: 'Guardian · Fact Checker', verdict: 'FAILED', note: 'One material performance claim is unsupported. Publication remains blocked until revised.' },
  ],
}

export const dashboardFixture: DashboardData = {
  company,
  tasks: [
    { id: 'task-142', title: 'Repair unsupported performance claim', status: 'BLOCKED', ownerPositionId: 'position-writer', projectName: 'Quiet Leverage', dueLabel: 'Needs founder direction' },
    { id: 'task-143', title: 'Validate replacement benchmark', status: 'IN_PROGRESS', ownerPositionId: 'position-research', projectName: 'Quiet Leverage', dueLabel: 'Today' },
    { id: 'task-137', title: 'Edit second article draft', status: 'COMPLETED', ownerPositionId: 'position-editor', projectName: 'Founder Field Notes', dueLabel: 'Completed 2h ago' },
  ],
  decisions: [{
    id: 'decision-claim',
    question: 'How should the disputed speed claim be handled?',
    context: 'Fact checking found that the draft’s 60% figure is supported only by a vendor benchmark. This blocks founder approval.',
    recommendation: 'Use the independently supported 18–34% range and explain that results vary by workflow maturity.',
    evidence: ['Vendor study: 60%, methodology not disclosed', 'Independent benchmark: 18–34%, n=412', 'Guardian marked the current claim as unpublishable'],
    options: [
      { id: 'replace', label: 'Use the verified range', impact: 'Unblocks revision with a more defensible, qualified claim.' },
      { id: 'remove', label: 'Remove the number', impact: 'Keeps the argument qualitative and avoids benchmark debate.' },
      { id: 'retain', label: 'Retain with attribution', impact: 'Preserves the stronger hook but adds reputational risk.' },
    ],
    status: 'OPEN',
  }],
  activities: [
    { id: 'activity-1', actor: 'Guardian', verb: 'blocked publication', detail: 'The 60% speed claim could not be independently verified.', createdAt: '2026-08-06T05:12:00Z', tone: 'warning' },
    { id: 'activity-2', actor: 'Iris', verb: 'found contradictory evidence', detail: 'Independent results cluster between 18% and 34%.', createdAt: '2026-08-06T04:52:00Z', tone: 'neutral' },
    { id: 'activity-3', actor: 'Ada', verb: 'completed editorial review', detail: 'Two structural edits accepted; one claim returned for revision.', createdAt: '2026-08-06T03:41:00Z', tone: 'positive' },
  ],
  latestArtifact: contentFixture.artifacts[2],
}
