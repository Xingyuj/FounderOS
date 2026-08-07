import { render, screen } from '@testing-library/react'
import { ArtifactCard, StatePanel, StatusPill } from './ui'
import { contentFixture } from '../mocks/fixtures'

describe('core product components', () => {
  it('renders productive states with visible text', () => {
    render(<><StatusPill status="BLOCKED" /><StatusPill status="COMPLETED" /></>)
    expect(screen.getByText('Blocked')).toBeVisible()
    expect(screen.getByText('Completed')).toBeVisible()
  })

  it('distinguishes artifact version and final state', () => {
    render(<ArtifactCard artifact={contentFixture.artifacts[2]} />)
    expect(screen.getByText('v3')).toBeVisible()
    expect(screen.getByText('Final')).toBeVisible()
  })

  it('provides an accessible error state', () => {
    render(<StatePanel kind="error" title="Unavailable">Try again later.</StatePanel>)
    expect(screen.getByRole('alert')).toHaveTextContent('Unavailable')
  })
})
