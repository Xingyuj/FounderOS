import { Bell, Blocks, Building2, ChevronDown, Command, FileStack, Menu, Search, UserRoundPlus, X } from 'lucide-react'
import { useState } from 'react'
import { NavLink, Outlet } from 'react-router-dom'

const navigation = [
  { to: '/', label: 'Command Center', icon: Command },
  { to: '/content', label: 'Content Studio', icon: FileStack },
  { to: '/organization', label: 'Organization', icon: Building2 },
  { to: '/talent', label: 'Talent Library', icon: UserRoundPlus, featured: true },
]

export function AppShell() {
  const [menuOpen, setMenuOpen] = useState(false)
  return <div className="app-shell">
    <header className="mobile-bar"><button className="icon-button" onClick={() => setMenuOpen(!menuOpen)} aria-label={menuOpen ? 'Close navigation' : 'Open navigation'}>{menuOpen ? <X /> : <Menu />}</button><span className="wordmark">Founder<span>OS</span></span></header>
    <aside className={`sidebar ${menuOpen ? 'sidebar--open' : ''}`}>
      <div className="brand"><div className="brand__mark"><Blocks size={22} /></div><span className="wordmark">Founder<span>OS</span></span></div>
      <button className="company-switcher"><span className="company-switcher__avatar">F</span><span><small>Company</small><strong>FounderOS Studio</strong></span><ChevronDown size={16} /></button>
      <nav aria-label="Primary navigation">{navigation.map(({ to, label, icon: Icon, featured }) => <NavLink key={to} to={to} end={to === '/'} className={({ isActive }) => `${isActive ? 'active' : ''} ${featured ? 'nav-hire' : ''}`} onClick={() => setMenuOpen(false)}><Icon size={18} /><span>{label}</span>{featured && <b>NEW</b>}</NavLink>)}</nav>
      <div className="sidebar__section"><p className="sidebar__label">Workspace</p><button><Search size={18} /><span>Search</span><kbd>⌘ K</kbd></button><button><Bell size={18} /><span>Notifications</span><i>1</i></button></div>
      <div className="sidebar__footer"><div className="founder-avatar">XY</div><span><strong>Founder</strong><small>Owner · Melbourne</small></span><span className="presence" aria-label="Online" /></div>
    </aside>
    <main className="main-content"><Outlet /></main>
  </div>
}
