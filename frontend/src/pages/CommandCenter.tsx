import { ArrowRight, CheckCircle2, ChevronRight, CircleAlert, Clock3, Compass, Sparkles } from 'lucide-react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import type { Decision } from '../domain/types'
import { organizationFixture } from '../mocks/fixtures'
import { ArtifactCard, CharacterCard, Panel, SectionHeading, StatePanel, TaskCard } from '../components/ui'

function formatTime(value: string) { return new Intl.DateTimeFormat('en-AU', { hour: 'numeric', minute: '2-digit' }).format(new Date(value)) }

function DecisionDialog({ decision, onClose }: { decision: Decision; onClose: () => void }) {
  const queryClient = useQueryClient()
  const [selected, setSelected] = useState(decision.options[0]?.id ?? '')
  const [comment, setComment] = useState('')
  const mutation = useMutation({
    mutationFn: () => api.resolveDecision(decision.id, selected, comment),
    onSuccess: () => { void queryClient.invalidateQueries({ queryKey: ['dashboard'] }); onClose() },
  })
  return <div className="dialog-backdrop" role="presentation" onMouseDown={(event) => event.currentTarget === event.target && onClose()}>
    <div className="decision-dialog" role="dialog" aria-modal="true" aria-labelledby="decision-title">
      <div className="decision-dialog__top"><span className="decision-signal"><CircleAlert size={20} /></span><div><p className="eyebrow">Decision · Content production</p><h2 id="decision-title">{decision.question}</h2></div><button className="dialog-close" onClick={onClose} aria-label="Close decision">×</button></div>
      <p className="decision-context">{decision.context}</p>
      <div className="recommendation"><Sparkles size={18} /><div><strong>Team recommendation</strong><p>{decision.recommendation}</p></div></div>
      <fieldset className="option-list"><legend>Choose a direction</legend>{decision.options.map((option) => <label key={option.id} className={selected === option.id ? 'option option--selected' : 'option'}><input type="radio" name="decision" checked={selected === option.id} onChange={() => setSelected(option.id)} /><span><strong>{option.label}</strong><small>{option.impact}</small></span></label>)}</fieldset>
      <details className="evidence-details"><summary>Evidence considered <span>{decision.evidence.length}</span></summary><ul>{decision.evidence.map((item) => <li key={item}>{item}</li>)}</ul></details>
      <label className="comment-field"><span>Direction for the team <small>Optional</small></span><textarea value={comment} onChange={(event) => setComment(event.target.value)} placeholder="Add context the team should carry into the revision…" /></label>
      {mutation.isError && <p className="form-error">{mutation.error.message}</p>}
      <div className="dialog-actions"><button className="button button--ghost" onClick={onClose}>Not now</button><button className="button button--primary" onClick={() => mutation.mutate()} disabled={!selected || mutation.isPending}>{mutation.isPending ? 'Recording…' : 'Confirm direction'}<ArrowRight size={17} /></button></div>
    </div>
  </div>
}

export function CommandCenter() {
  const { data, isLoading, isError, refetch } = useQuery({ queryKey: ['dashboard'], queryFn: api.dashboard })
  const [activeDecision, setActiveDecision] = useState<Decision | null>(null)
  if (isLoading) return <div className="page page--center"><StatePanel kind="loading" title="Opening your company">Gathering the latest work, decisions, and output.</StatePanel></div>
  if (isError || !data) return <div className="page page--center"><StatePanel kind="error" title="The company view is unavailable"><button className="button button--primary" onClick={() => void refetch()}>Try again</button></StatePanel></div>
  const soulsByPosition = new Map(organizationFixture.assignments.map((assignment) => [assignment.positionId, organizationFixture.souls.find((soul) => soul.id === assignment.soulId)]))
  const openDecision = data.decisions.find((decision) => decision.status === 'OPEN')
  return <div className="page command-page">
    <header className="page-header"><div><p className="eyebrow">Thursday, 6 August</p><h1>Good morning. <em>One thing needs you.</em></h1><p>Your team is moving the content milestone forward. A disputed claim is holding the next release.</p></div><div className="prototype-note"><span />Prototype data · M3A</div></header>
    <section className="mission-strip"><div><Compass size={19} /><span><small>Company mission</small><strong>{data.company.mission}</strong></span></div><div className="milestone"><span><small>{data.company.activeMilestone}</small><strong>{data.company.milestoneProgress}%</strong></span><div className="progress-track"><i style={{ width: `${data.company.milestoneProgress}%` }} /></div></div></section>
    {openDecision ? <button className="decision-banner" onClick={() => setActiveDecision(openDecision)}><span className="decision-banner__signal"><CircleAlert size={22} /></span><span><small>Founder decision · blocks 2 tasks</small><strong>{openDecision.question}</strong><em>Team recommends: {openDecision.recommendation}</em></span><span className="decision-banner__action">Review decision <ChevronRight size={18} /></span></button> : <div className="resolved-banner"><CheckCircle2 /><span><strong>Direction recorded.</strong> The team has resumed the revision.</span></div>}
    <div className="dashboard-grid">
      <Panel className="work-panel"><SectionHeading eyebrow="Now" title="Work in motion" action={<Link to="/content">Open Content Studio <ArrowRight size={15} /></Link>} /><div className="task-list">{data.tasks.map((task) => <TaskCard key={task.id} task={task} soul={soulsByPosition.get(task.ownerPositionId)} />)}</div></Panel>
      <Panel className="team-panel team-panel--characters"><SectionHeading eyebrow="Your party" title="Meet the crew" action={<Link to="/talent">Open talent library <ArrowRight size={15} /></Link>} /><div className="character-roster">{organizationFixture.positions.slice(0, 5).map((position) => { const soul = soulsByPosition.get(position.id); const task = data.tasks.find((item) => item.ownerPositionId === position.id); return soul ? <CharacterCard key={position.id} soul={soul} role={position.title} task={task?.title} /> : null })}</div></Panel>
      <Panel className="output-panel"><SectionHeading eyebrow="Latest output" title="Ready to inspect" /><ArtifactCard artifact={data.latestArtifact} featured /><Link className="text-link" to="/content">See evidence, reviews, and versions <ArrowRight size={15} /></Link></Panel>
      <Panel className="activity-panel"><SectionHeading eyebrow="Since you were here" title="Company activity" /><ol className="activity-list">{data.activities.map((activity) => <li key={activity.id}><span className={`activity-dot activity-dot--${activity.tone}`} /> <div><p><strong>{activity.actor}</strong> {activity.verb}</p><span>{activity.detail}</span></div><time>{formatTime(activity.createdAt)}</time></li>)}</ol><div className="truth-note"><Clock3 size={15} />Only persisted activity appears here.</div></Panel>
    </div>
    {activeDecision && <DecisionDialog decision={activeDecision} onClose={() => setActiveDecision(null)} />}
  </div>
}
