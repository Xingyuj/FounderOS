import { ArrowLeft, ArrowRight, Check, Dices, Library, Plus, ShieldCheck, Sparkles, Star, UserRoundPlus } from 'lucide-react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import type { SoulDefinition } from '../domain/types'
import { CharacterCard, StatePanel } from '../components/ui'

const portraits = ['🦊', '🦉', '🐇', '🐈', '🐺', '🦝', '🐼', '🦁']
const archetypes = [
  { name: 'Pathfinder', description: 'Moves first and turns ambiguity into a direction.', stats: ['+2 Initiative', '+1 Clarity'] },
  { name: 'Sage', description: 'Seeks evidence, spots weak assumptions, stays curious.', stats: ['+2 Insight', '+1 Precision'] },
  { name: 'Spark', description: 'Creates momentum and makes bold ideas tangible.', stats: ['+2 Creativity', '+1 Speed'] },
  { name: 'Sentinel', description: 'Protects quality and will stop unsafe shortcuts.', stats: ['+2 Judgment', '+1 Resolve'] },
]
const traits = ['Curious', 'Candid', 'Playful', 'Methodical', 'Bold', 'Empathetic', 'Skeptical', 'Resourceful']

export function TalentLibrary() {
  const queryClient = useQueryClient()
  const { data, isLoading, isError } = useQuery({ queryKey: ['organization'], queryFn: api.organization })
  const [creating, setCreating] = useState(false)
  const [showAppointed, setShowAppointed] = useState(false)
  const [step, setStep] = useState(0)
  const [name, setName] = useState('')
  const [portrait, setPortrait] = useState(portraits[0])
  const [archetype, setArchetype] = useState(archetypes[0].name)
  const [selectedTraits, setSelectedTraits] = useState<string[]>(['Curious', 'Candid'])
  const [result, setResult] = useState<SoulDefinition | null>(null)
  const voice = `${selectedTraits[0] ?? 'Clear'} and ${selectedTraits[1]?.toLowerCase() ?? 'resourceful'}`
  const mutation = useMutation({ mutationFn: () => api.recruitTalent({ name, portrait, archetype, voice, values: selectedTraits }), onSuccess: (value) => { setResult(value); void queryClient.invalidateQueries({ queryKey: ['organization'] }) } })

  if (isLoading) return <div className="page page--center"><StatePanel kind="loading" title="Opening the Talent Library" /></div>
  if (isError || !data) return <div className="page page--center"><StatePanel kind="error" title="The Talent Library is unavailable" /></div>

  const appointedIds = new Set(data.assignments.map((assignment) => assignment.soulId))
  const available = data.souls.filter((soul) => !appointedIds.has(soul.id))
  const appointed = data.souls.filter((soul) => appointedIds.has(soul.id))

  function toggleTrait(trait: string) { setSelectedTraits((current) => current.includes(trait) ? current.filter((item) => item !== trait) : current.length < 3 ? [...current, trait] : current) }
  function resetForm() { setStep(0); setName(''); setPortrait(portraits[0]); setArchetype(archetypes[0].name); setSelectedTraits(['Curious', 'Candid']); setResult(null) }
  function closeCreator() { resetForm(); setCreating(false) }

  if (result) return <div className="page hire-page hire-success"><div className="confetti confetti--one" /><div className="confetti confetti--two" /><div className="hire-success__crest"><Sparkles /></div><p className="eyebrow">Talent recruited</p><h1>{result.name} joined your talent pool.</h1><div className="contract-card"><CharacterPreview soul={result} subtitle="Available talent · No position" /><div className="bench-seal"><Library /> On bench</div></div><p className="hire-success__copy">No Position was created and no authority was granted. {result.name} remains available until you appoint them to a vacant Position in Organization Studio.</p><div className="hire-success__actions"><button className="button button--outline" onClick={closeCreator}>Back to talent pool</button><Link className="button button--primary" to="/organization">Find a vacant position <ArrowRight size={16} /></Link></div></div>

  if (creating) return <div className="page hire-page">
    <header className="hire-header"><div><button className="back-link" onClick={closeCreator}><ArrowLeft size={15} /> Talent Library</button><p className="eyebrow">Recruit new talent</p><h1>Create the <em>character first.</em></h1><p>This creates an employee Soul only. You can decide where they belong later.</p></div><div className="guild-level"><span><Star size={15} /> Talent pool</span><div><i style={{ width: '38%' }} /></div><small>{available.length} available · {appointed.length} appointed</small></div></header>
    <div className="hire-progress hire-progress--two" aria-label={`Creation step ${step + 1} of 2`}><span className="active"><i>1</i>Create identity</span><b /><span className={step >= 1 ? 'active' : ''}><i>2</i>Review Soul</span></div>
    <div className="hire-workspace"><section className="hire-stage">
      {step === 0 ? <IdentityStep name={name} setName={setName} portrait={portrait} setPortrait={setPortrait} archetype={archetype} setArchetype={setArchetype} selectedTraits={selectedTraits} toggleTrait={toggleTrait} /> : <SoulReview name={name} portrait={portrait} archetype={archetype} traits={selectedTraits} voice={voice} />}
      {mutation.isError && <p className="form-error">{mutation.error.message}</p>}
      <div className="hire-actions">{step > 0 ? <button className="button button--ghost" onClick={() => setStep(0)}><ArrowLeft size={16} /> Back</button> : <span />}{step === 0 ? <button className="button button--primary hire-next" disabled={!name.trim() || selectedTraits.length < 2} onClick={() => setStep(1)}>Review Soul <ArrowRight size={16} /></button> : <button className="button button--primary hire-next" disabled={mutation.isPending} onClick={() => mutation.mutate()}>{mutation.isPending ? 'Recruiting…' : 'Add to talent pool'} <UserRoundPlus size={16} /></button>}</div>
    </section><aside className="candidate-preview"><p className="eyebrow">Live talent card</p><CharacterPreview soul={{ id: 'preview', name: name || 'Unknown hero', monogram: name.slice(0, 2).toUpperCase(), color: '#d86a3f', voice, values: selectedTraits, portrait, archetype, level: 1 }} subtitle="Available talent · No position" /><div className="preview-rule"><ShieldCheck size={17} /><p><strong>No authority yet</strong><span>Creating a Soul does not create a Position or Assignment.</span></p></div></aside></div>
  </div>

  const visible = showAppointed ? appointed : available
  return <div className="page talent-page"><header className="talent-header"><div><p className="eyebrow">Talent Library</p><h1>Your characters, <em>before the job title.</em></h1><p>Build a bench of distinctive employees. Appoint them only when the right Position exists.</p></div><button className="button button--primary talent-create" onClick={() => setCreating(true)}><Plus size={17} /> Create employee</button></header><div className="talent-tabs" role="tablist" aria-label="Talent status"><button role="tab" aria-selected={!showAppointed} onClick={() => setShowAppointed(false)}>Available <span>{available.length}</span></button><button role="tab" aria-selected={showAppointed} onClick={() => setShowAppointed(true)}>Appointed <span>{appointed.length}</span></button></div><section className="talent-shelf" aria-label={showAppointed ? 'Appointed employees' : 'Available talent'}>{visible.map((soul) => { const assignment = data.assignments.find((entry) => entry.soulId === soul.id); const position = data.positions.find((entry) => entry.id === assignment?.positionId); return <CharacterCard key={soul.id} soul={soul} role={position?.title ?? 'Available talent'} task={position ? 'On active assignment' : undefined} /> })}<button className="empty-talent-card" onClick={() => setCreating(true)}><Plus /><strong>Create a new character</strong><span>They will join the available talent pool.</span></button></section><div className="talent-explainer"><div><span>1</span><p><strong>Create employee</strong><small>Build a Soul and add it to the talent pool.</small></p></div><i /><div><span>2</span><p><strong>Create vacant Position</strong><small>Define responsibility and authority separately.</small></p></div><i /><div><span>3</span><p><strong>Appoint</strong><small>Choose available talent from the Position.</small></p></div></div></div>
}

function IdentityStep({ name, setName, portrait, setPortrait, archetype, setArchetype, selectedTraits, toggleTrait }: { name: string; setName: (value: string) => void; portrait: string; setPortrait: (value: string) => void; archetype: string; setArchetype: (value: string) => void; selectedTraits: string[]; toggleTrait: (value: string) => void }) {
  return <><div className="hire-stage__heading"><span className="stage-number">01</span><div><p className="eyebrow">Create an employee</p><h2>Who are they?</h2><p>Define identity and instincts without deciding their organizational seat.</p></div></div><label className="hire-field"><span>Name your employee</span><div><input value={name} maxLength={24} onChange={(event) => setName(event.target.value)} placeholder="e.g. Juniper" /><button type="button" aria-label="Suggest a name" onClick={() => setName(['Juniper', 'Orion', 'Ember', 'Atlas'][Math.floor(Math.random() * 4)])}><Dices size={17} /></button></div></label><fieldset className="portrait-picker"><legend>Choose a portrait</legend><div>{portraits.map((item) => <button type="button" aria-label={`Portrait ${item}`} aria-pressed={portrait === item} key={item} onClick={() => setPortrait(item)}>{item}</button>)}</div></fieldset><fieldset className="archetype-picker"><legend>Choose an archetype</legend><div>{archetypes.map((item) => <label className={archetype === item.name ? 'archetype-option archetype-option--selected' : 'archetype-option'} key={item.name}><input type="radio" name="archetype" checked={archetype === item.name} onChange={() => setArchetype(item.name)} /><span><strong>{item.name}</strong><small>{item.description}</small><em>{item.stats.join(' · ')}</em></span></label>)}</div></fieldset><fieldset className="trait-picker"><legend>Core traits <small>Choose 2–3</small></legend><div>{traits.map((trait) => <button type="button" aria-pressed={selectedTraits.includes(trait)} key={trait} onClick={() => toggleTrait(trait)}>{selectedTraits.includes(trait) && <Check size={12} />}{trait}</button>)}</div></fieldset></>
}

function SoulReview({ name, portrait, archetype, traits, voice }: { name: string; portrait: string; archetype: string; traits: string[]; voice: string }) {
  return <><div className="hire-stage__heading"><span className="stage-number">02</span><div><p className="eyebrow">Review Soul</p><h2>Add {name} to the bench.</h2><p>This saves the character without creating a Position or Assignment.</p></div></div><div className="contract-summary contract-summary--talent"><div><span className="summary-portrait">{portrait}</span><span><small>Identity</small><strong>{name} · {archetype}</strong></span></div><div><Sparkles /><span><small>Core traits</small><strong>{traits.join(' · ')}</strong></span></div><div><Library /><span><small>Starting status</small><strong>Available talent</strong></span></div></div><div className="authority-scroll"><h3>Talent record</h3><ul><li><Check />Voice: {voice}</li><li><Check />Level 1 with no active assignment</li><li><Check />Eligible for future compatible Positions</li></ul><p>No Job authority, tool grants, reporting relationship, or task ownership is created at this step.</p></div></>
}

function CharacterPreview({ soul, subtitle }: { soul: SoulDefinition; subtitle: string }) {
  return <article className="hero-card"><div className="hero-card__shine" /><div className="hero-card__top"><span>FOUNDEROS · TALENT</span><strong>LV {soul.level ?? 1}</strong></div><div className="hero-card__portrait"><i>{soul.portrait}</i><span className="hero-card__spark hero-card__spark--one">✦</span><span className="hero-card__spark hero-card__spark--two">·</span></div><div className="hero-card__identity"><span>{soul.archetype}</span><h2>{soul.name}</h2><p>{subtitle}</p></div><div className="hero-card__traits">{soul.values.map((trait) => <span key={trait}>{trait}</span>)}</div><div className="hero-card__footer"><span>UNASSIGNED SOUL</span><span>BENCH</span></div></article>
}
