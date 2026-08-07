import { Navigate, Route, Routes } from 'react-router-dom'
import { AppShell } from './components/AppShell'
import { CommandCenter } from './pages/CommandCenter'
import { ContentStudio } from './pages/ContentStudio'
import { OrganizationStudio } from './pages/OrganizationStudio'
import { TalentLibrary } from './pages/TalentLibrary'

export function App() {
  return <Routes><Route element={<AppShell />}><Route index element={<CommandCenter />} /><Route path="content" element={<ContentStudio />} /><Route path="organization" element={<OrganizationStudio />} /><Route path="talent" element={<TalentLibrary />} /><Route path="hire" element={<Navigate to="/talent" replace />} /><Route path="*" element={<Navigate to="/" replace />} /></Route></Routes>
}
