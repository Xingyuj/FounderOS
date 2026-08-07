import type { ReactNode } from 'react'
import { AlertTriangle, ArrowUpRight, Check, Clock3, FileText, ShieldCheck } from 'lucide-react'
import type { Artifact, Decision, SoulDefinition, Task, WorkStatus } from '../domain/types'

export function StatusPill({ status }: { status: WorkStatus | 'OPEN' | 'RESOLVED' | 'FINAL' | 'REVIEWED' | 'DRAFT' }) {
  const labels: Record<string, string> = {
    IN_PROGRESS: 'In progress', WAITING: 'Waiting', BLOCKED: 'Blocked', FAILED: 'Failed', COMPLETED: 'Completed',
    PLANNED: 'Planned', OPEN: 'Needs you', RESOLVED: 'Resolved', FINAL: 'Final', REVIEWED: 'Reviewed', DRAFT: 'Draft',
  }
  return <span className={`status status--${status.toLowerCase()}`}>{labels[status] ?? status}</span>
}

export function AgentMark({ soul, size = 'medium' }: { soul?: SoulDefinition; size?: 'small' | 'medium' | 'large' }) {
  if (!soul) return <span className={`agent-mark agent-mark--${size} agent-mark--vacant`}>+</span>
  return <span className={`agent-mark agent-mark--${size} ${soul.portrait ? 'agent-mark--portrait' : ''}`} style={{ '--agent-color': soul.color } as React.CSSProperties}>{soul.portrait ?? soul.monogram}</span>
}

export function CharacterCard({ soul, role, task }: { soul: SoulDefinition; role: string; task?: string }) {
  return <article className="character-card" style={{ '--agent-color': soul.color } as React.CSSProperties}>
    <div className="character-card__portrait"><span>{soul.portrait ?? soul.monogram}</span><i>LV {soul.level ?? 1}</i></div>
    <div className="character-card__body"><span className="character-card__class">{role}</span><h3>{soul.name}</h3><p>{soul.archetype ?? 'Specialist'} · {soul.voice}</p><div className="trait-row">{soul.values.slice(0, 2).map((value) => <span key={value}>{value}</span>)}</div></div>
    <div className="character-card__quest"><span className={task ? 'quest-pulse' : ''} />{task ?? 'Ready for a quest'}</div>
  </article>
}

export function Panel({ children, className = '', labelledBy }: { children: ReactNode; className?: string; labelledBy?: string }) {
  return <section className={`panel ${className}`} aria-labelledby={labelledBy}>{children}</section>
}

export function SectionHeading({ eyebrow, title, action }: { eyebrow?: string; title: string; action?: ReactNode }) {
  return <div className="section-heading"><div>{eyebrow && <p className="eyebrow">{eyebrow}</p>}<h2>{title}</h2></div>{action}</div>
}

export function TaskCard({ task, soul }: { task: Task; soul?: SoulDefinition }) {
  return <article className="task-card">
    <div className="task-card__top"><StatusPill status={task.status} /><span className="task-card__id">{task.id.replace('task-', 'FOS-')}</span></div>
    <h3>{task.title}</h3><p>{task.projectName}</p>
    <div className="task-card__footer"><span className="person"><AgentMark soul={soul} size="small" />{soul?.name ?? 'Vacant'}</span><span>{task.dueLabel}</span></div>
  </article>
}

export function ArtifactCard({ artifact, featured = false }: { artifact: Artifact; featured?: boolean }) {
  return <article className={`artifact-card ${featured ? 'artifact-card--featured' : ''}`}>
    <div className="artifact-card__icon"><FileText size={18} /></div>
    <div><div className="artifact-card__meta"><span>{artifact.type.replace('_', ' ')}</span><span>v{artifact.version}</span><StatusPill status={artifact.status} /></div><h3>{artifact.title}</h3><p>{artifact.excerpt}</p></div>
    <ArrowUpRight size={18} aria-hidden="true" />
  </article>
}

export function DecisionSummary({ decision }: { decision: Decision }) {
  const chosen = decision.options.find((option) => option.id === decision.selectedOptionId)
  return <div className={`decision-summary ${decision.status === 'RESOLVED' ? 'decision-summary--resolved' : ''}`}>
    <span className="decision-summary__icon">{decision.status === 'OPEN' ? <AlertTriangle size={19} /> : <Check size={19} />}</span>
    <div><p className="eyebrow">{decision.status === 'OPEN' ? 'Founder decision' : 'Decision recorded'}</p><h3>{decision.question}</h3>{chosen && <p>Selected: <strong>{chosen.label}</strong></p>}</div>
  </div>
}

export function StatePanel({ kind, title, children }: { kind: 'loading' | 'empty' | 'error' | 'unavailable'; title: string; children?: ReactNode }) {
  const Icon = kind === 'loading' ? Clock3 : kind === 'error' || kind === 'unavailable' ? AlertTriangle : ShieldCheck
  return <div className={`state-panel state-panel--${kind}`} role={kind === 'error' ? 'alert' : 'status'}><Icon size={24} /><h2>{title}</h2>{children && <p>{children}</p>}</div>
}
