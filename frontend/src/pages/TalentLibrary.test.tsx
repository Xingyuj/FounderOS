import { screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { TalentLibrary } from './TalentLibrary'
import { renderApp } from '../test/renderApp'

describe('Talent Library', () => {
  it('creates an unassigned employee in the available talent pool', async () => {
    const user = userEvent.setup()
    renderApp(<TalentLibrary />, '/talent')
    expect(await screen.findByRole('heading', { name: /Your characters, before the job title/i })).toBeVisible()
    expect(screen.getByText('Ember')).toBeVisible()
    await user.click(screen.getByRole('button', { name: /Create employee/i }))
    await user.type(screen.getByLabelText('Name your employee'), 'Juniper')
    await user.click(screen.getByRole('radio', { name: /Sage/i }))
    await user.click(screen.getByRole('button', { name: /Review Soul/i }))
    expect(screen.getByText('Available talent')).toBeVisible()
    await user.click(screen.getByRole('button', { name: /Add to talent pool/i }))
    expect(await screen.findByRole('heading', { name: /Juniper joined your talent pool/i })).toBeVisible()
    expect(screen.getByText(/No Position was created/i)).toBeVisible()
  })
})
