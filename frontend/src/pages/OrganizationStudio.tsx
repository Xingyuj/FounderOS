import { Background, Controls, Handle, MiniMap, Position as FlowPosition, ReactFlow, type Edge, type Node, type NodeProps } from '@xyflow/react'
import '@xyflow/react/dist/style.css'
import { BriefcaseBusiness, CircleHelp, GitBranch, Move, ShieldCheck, UserRoundPlus } from 'lucide-react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useMemo, useState } from 'react'
import { api } from '../api/client'
import type { OrganizationData } from '../domain/types'
import { AgentMark, Panel, SectionHeading, StatePanel } from '../components/ui'

type PositionNodeData = { title: string; jobPurpose: string; soulName?: string; soulMonogram?: string; soulColor?: string; soulPortrait?: string; vacant: boolean }

function PositionNode({ data, selected }: NodeProps<Node<PositionNodeData>>) {
  const soul = data.soulName ? { id: '', name: data.soulName, monogram: data.soulMonogram ?? '', color: data.soulColor ?? '#555', voice: '', values: [], portrait: data.soulPortrait } : undefined
  return <div className={`org-node ${selected ? 'org-node--selected' : ''} ${data.vacant ? 'org-node--vacant' : ''}`}>
    <Handle type="target" position={FlowPosition.Top} />
    <div className="org-node__head"><AgentMark soul={soul} /><span><strong>{data.title}</strong><small>{data.soulName ?? 'Vacant position'}</small></span></div>
    <p>{data.jobPurpose}</p><span className="org-node__status">{data.vacant ? <><UserRoundPlus size={13} /> Appointment needed</> : <><span /> Active</>}</span>
    <Handle type="source" position={FlowPosition.Bottom} />
  </div>
}

const nodeTypes = { position: PositionNode }
const placement: Record<string, { x: number; y: number }> = {
  'position-content': { x: 360, y: 20 }, 'position-research': { x: 0, y: 245 }, 'position-writer': { x: 245, y: 245 },
  'position-editor': { x: 490, y: 245 }, 'position-checker': { x: 735, y: 245 }, 'position-distribution': { x: 245, y: 465 },
}

function makeGraph(data: OrganizationData): { nodes: Node<PositionNodeData>[]; edges: Edge[] } {
  const nodes = data.positions.map((position) => {
    const job = data.jobs.find((entry) => entry.id === position.jobId)!
    const assignment = data.assignments.find((entry) => entry.positionId === position.id)
    const soul = data.souls.find((entry) => entry.id === assignment?.soulId)
    return { id: position.id, type: 'position', position: placement[position.id] ?? { x: 0, y: 0 }, data: { title: position.title, jobPurpose: job.purpose, soulName: soul?.name, soulMonogram: soul?.monogram, soulColor: soul?.color, soulPortrait: soul?.portrait, vacant: !assignment } }
  })
  const edges = data.positions.filter((position) => position.reportsToPositionId).map((position) => ({ id: `edge-${position.id}`, source: position.reportsToPositionId!, target: position.id, type: 'smoothstep', className: 'reporting-edge' }))
  return { nodes, edges }
}

export function OrganizationStudio() {
  const queryClient = useQueryClient()
  const { data, isLoading, isError } = useQuery({ queryKey: ['organization'], queryFn: api.organization })
  const [selectedId, setSelectedId] = useState<string | null>(null)
  const [appointing, setAppointing] = useState(false)
  const [appointmentNotice, setAppointmentNotice] = useState('')
  const appointment = useMutation({ mutationFn: ({ positionId, soulId }: { positionId: string; soulId: string }) => api.appointTalent(positionId, { soulId }), onSuccess: (_, variables) => { const soul = data?.souls.find((entry) => entry.id === variables.soulId); setAppointmentNotice(`${soul?.name ?? 'Employee'} is now appointed to this Position.`); setAppointing(false); void queryClient.invalidateQueries({ queryKey: ['organization'] }) } })
  const graph = useMemo(() => data ? makeGraph(data) : { nodes: [], edges: [] }, [data])
  if (isLoading) return <div className="page page--center"><StatePanel kind="loading" title="Arranging the team" /></div>
  if (isError || !data) return <div className="page page--center"><StatePanel kind="error" title="Organization is unavailable" /></div>
  const selected = data.positions.find((position) => position.id === selectedId)
  const selectedJob = data.jobs.find((job) => job.id === selected?.jobId)
  const selectedAssignment = data.assignments.find((assignment) => assignment.positionId === selected?.id)
  const selectedSoul = data.souls.find((soul) => soul.id === selectedAssignment?.soulId)
  const appointedSoulIds = new Set(data.assignments.map((assignment) => assignment.soulId))
  const availableTalent = data.souls.filter((soul) => !appointedSoulIds.has(soul.id))
  return <div className="page organization-page">
    <header className="organization-header"><div><p className="eyebrow">Organization Studio</p><h1>The team behind the work.</h1><p>Jobs define responsibility. Souls bring identity. Positions connect both to your company.</p></div><div className="organization-actions"><button className="button button--outline"><BriefcaseBusiness size={16} /> Job Library</button><button className="button button--primary"><UserRoundPlus size={16} /> Add position</button></div></header>
    <div className="org-legend"><span><GitBranch size={15} /> Solid line · Reports to</span><span><Move size={15} /> Dragging changes layout only</span><span><ShieldCheck size={15} /> Authority stays in the Job</span></div>
    <div className="organization-workspace"><Panel className="canvas-panel"><ReactFlow nodes={graph.nodes} edges={graph.edges} nodeTypes={nodeTypes} fitView fitViewOptions={{ padding: 0.18 }} minZoom={0.55} maxZoom={1.25} onNodeClick={(_, node) => { setSelectedId(node.id); setAppointing(false); setAppointmentNotice('') }} aria-label="FounderOS organization chart"><Background gap={24} size={1} color="#d6d0c4" /><Controls showInteractive={false} /><MiniMap nodeColor={(node) => node.data.vacant ? '#d6a046' : '#486b61'} maskColor="rgba(245,241,232,.68)" /></ReactFlow></Panel>
      <aside className="org-inspector"><Panel>{selected ? <><p className="eyebrow">Selected position</p><div className="inspector-person"><AgentMark soul={selectedSoul} size="large" /><div><h2>{selected.title}</h2><p>{selectedSoul?.name ?? 'Vacant'}</p></div></div><div className="definition-block"><span>Job definition</span><strong>{selectedJob?.name}</strong><p>{selectedJob?.purpose}</p></div>{appointmentNotice && <div className="appointment-notice"><ShieldCheck size={16} />{appointmentNotice}</div>}{selectedSoul ? <div className="definition-block"><span>Soul appointment</span><strong>{selectedSoul.name}</strong><p>{selectedSoul.voice} · Values {selectedSoul.values.join(' and ')}.</p></div> : appointing ? <div className="appointment-picker"><div className="appointment-picker__head"><span><strong>Available talent</strong><small>Choose one employee for this Position.</small></span><button onClick={() => setAppointing(false)}>×</button></div>{availableTalent.length ? availableTalent.map((soul) => <div className="appointment-candidate" key={soul.id}><AgentMark soul={soul} size="large" /><span><strong>{soul.name}</strong><small>{soul.archetype} · LV {soul.level}</small><em>{soul.values.join(' · ')}</em></span><button className="button button--primary" disabled={appointment.isPending} onClick={() => appointment.mutate({ positionId: selected.id, soulId: soul.id })} aria-label={`Appoint ${soul.name}`}>Appoint</button></div>) : <div className="appointment-empty"><p>No available talent.</p><a href="/talent">Create an employee first</a></div>}{appointment.isError && <p className="form-error">{appointment.error.message}</p>}</div> : <button className="button button--primary button--full" onClick={() => setAppointing(true)}><UserRoundPlus size={16} /> Appoint from Talent Library</button>}</> : <div className="inspector-empty"><CircleHelp size={26} /><h2>Select a position</h2><p>Inspect its Job, Soul appointment, and place in the company.</p></div>}</Panel><Panel className="vocabulary-panel"><SectionHeading eyebrow="Company vocabulary" title="Four things, four jobs" /><dl><div><dt>Job</dt><dd>Reusable responsibility and authority.</dd></div><div><dt>Soul</dt><dd>Portable identity, values, and voice.</dd></div><div><dt>Position</dt><dd>A seat in this company.</dd></div><div><dt>Assignment</dt><dd>A Soul appointed to a Position.</dd></div></dl></Panel></aside>
    </div>
  </div>
}
