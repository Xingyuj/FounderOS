import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { CommandCenter } from './CommandCenter'
import { renderApp } from '../test/renderApp'

describe('Command Center founder journey', () => {
  it('shows company state and resolves an open decision', async () => {
    const user = userEvent.setup()
    renderApp(<CommandCenter />)
    expect(await screen.findByRole('heading', { name: /Good morning/i })).toBeVisible()
    await user.click(screen.getByRole('button', { name: /How should the disputed speed claim/i }))
    expect(screen.getByRole('dialog')).toBeVisible()
    await user.click(screen.getByLabelText(/Remove the number/i))
    await user.type(screen.getByLabelText(/Direction for the team/i), 'Keep the argument grounded.')
    await user.click(screen.getByRole('button', { name: /Confirm direction/i }))
    await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument())
    expect(await screen.findByText('Direction recorded.')).toBeVisible()
  })
})
