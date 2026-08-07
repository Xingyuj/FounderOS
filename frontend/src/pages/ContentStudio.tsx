import { AlertTriangle, ArrowRight, BookOpen, Check, ChevronRight, ExternalLink, FileCheck2, Layers3, MessageSquareText, ShieldAlert } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import { api } from '../api/client'
import { organizationFixture } from '../mocks/fixtures'
import { AgentMark, ArtifactCard, Panel, SectionHeading, StatePanel, StatusPill } from '../components/ui'

const stageTone: Record<string, string> = { COMPLETE: 'complete', CURRENT: 'current', UPCOMING: 'upcoming', FAILED: 'failed' }

export function ContentStudio() {
  const { data, isLoading, isError } = useQuery({ queryKey: ['content-items'], queryFn: api.contentItems })
  const [mode, setMode] = useState<'production' | 'audit'>('production')
  if (isLoading) return <div className="page page--center"><StatePanel kind="loading" title="Opening Content Studio" /></div>
  if (isError || !data?.length) return <div className="page page--center"><StatePanel kind={isError ? 'error' : 'empty'} title={isError ? 'Content Studio is unavailable' : 'No content in production'} /></div>
  const item = data[0]
  const assignment = organizationFixture.assignments.find((entry) => entry.positionId === item.ownerPositionId)
  const soul = organizationFixture.souls.find((entry) => entry.id === assignment?.soulId)
  return <div className="page content-page">
    <header className="content-header"><div><p className="eyebrow">Content Studio · FOS-C12</p><div className="title-line"><h1>{item.title}</h1><StatusPill status={item.status} /></div><p>{item.brief}</p><div className="content-meta"><span className="person"><AgentMark soul={soul} size="small" />{soul?.name} · Accountable</span><span>Audience · {item.audience}</span></div></div><div className="view-toggle" aria-label="View detail"><button className={mode === 'production' ? 'active' : ''} onClick={() => setMode('production')}>Production</button><button className={mode === 'audit' ? 'active' : ''} onClick={() => setMode('audit')}>Audit</button></div></header>
    <Panel className="pipeline-panel"><div className="pipeline">{item.stages.map((stage, index) => <div className={`pipeline-stage pipeline-stage--${stageTone[stage.status]}`} key={stage.stage}><span>{stage.status === 'COMPLETE' ? <Check size={14} /> : stage.status === 'FAILED' ? <AlertTriangle size={14} /> : index + 1}</span><strong>{stage.label}</strong>{index < item.stages.length - 1 && <i />}</div>)}</div><div className="pipeline-alert"><ShieldAlert size={20} /><span><strong>Fact check failed</strong><small>One material claim needs a defensible replacement before founder approval.</small></span><button>Open blocker <ChevronRight size={16} /></button></div></Panel>
    {mode === 'production' ? <div className="content-grid">
      <div className="content-main">
        <Panel><SectionHeading eyebrow="Evidence base" title="What the draft relies on" action={<span className="count-chip">{item.evidence.length} claims</span>} /><div className="evidence-list">{item.evidence.map((evidence) => <article className={`evidence-card evidence-card--${evidence.confidence.toLowerCase()}`} key={evidence.id}><div className="evidence-card__top"><span>{evidence.confidence === 'CONTRADICTED' ? <AlertTriangle size={17} /> : <BookOpen size={17} />}{evidence.confidence}</span><a href="#source" onClick={(event) => event.preventDefault()}>{evidence.source}<ExternalLink size={13} /></a></div><h3>{evidence.claim}</h3><p>{evidence.note}</p></article>)}</div></Panel>
        <Panel><SectionHeading eyebrow="Review room" title="Independent review" /><div className="review-list">{item.reviews.map((review) => <article className="review" key={review.reviewer}><span className={review.verdict === 'FAILED' ? 'review__icon review__icon--failed' : 'review__icon'}>{review.verdict === 'FAILED' ? <ShieldAlert size={18} /> : <MessageSquareText size={18} />}</span><div><div><strong>{review.reviewer}</strong><span>{review.verdict.replace('_', ' ')}</span></div><p>{review.note}</p></div></article>)}</div></Panel>
      </div>
      <aside className="versions"><Panel><SectionHeading eyebrow="Artifacts" title="Versions" /><div className="version-stack">{[...item.artifacts].reverse().map((artifact, index) => <ArtifactCard key={artifact.id} artifact={artifact} featured={index === 0} />)}</div></Panel></aside>
    </div> : <AuditView />}
  </div>
}

function AuditView() {
  return <div className="audit-grid"><Panel><SectionHeading eyebrow="Execution context" title="Traceable, not theatrical" /><dl className="audit-facts"><div><dt>Workflow</dt><dd>content-production-v0 · mock</dd></div><div><dt>Current checkpoint</dt><dd>fact_check_failed</dd></div><div><dt>Attempt</dt><dd>1 of 2</dd></div><div><dt>Elapsed</dt><dd>4h 18m simulated scenario time</dd></div><div><dt>Model calls</dt><dd>None — M3A fixture data</dd></div><div><dt>External actions</dt><dd>None</dd></div></dl></Panel><Panel><SectionHeading eyebrow="Attribution" title="Artifact lineage" /><div className="lineage"><span><Layers3 />Brief approved</span><i /><span><BookOpen />3 evidence records</span><i /><span><FileCheck2 />3 artifact versions</span><i /><span className="lineage--blocked"><ShieldAlert />Fact check blocked</span></div><p className="audit-explainer">This view exposes the source state behind the production summary. It never invents agent activity or hides failed checks.</p><button className="button button--outline">Export audit record <ArrowRight size={16} /></button></Panel></div>
}
