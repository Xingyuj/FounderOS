import { screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { ContentStudio } from './ContentStudio'
import { renderApp } from '../test/renderApp'

describe('Content Studio', () => {
  it('exposes evidence, versions, and the audit boundary', async () => {
    const user = userEvent.setup()
    renderApp(<ContentStudio />, '/content')
    expect(await screen.findByRole('heading', { level: 1, name: /quiet leverage/i })).toBeVisible()
    expect(screen.getByText('CONTRADICTED')).toBeVisible()
    expect(screen.getByText('v3')).toBeVisible()
    await user.click(screen.getByRole('button', { name: 'Audit' }))
    expect(screen.getByText(/None — M3A fixture data/i)).toBeVisible()
  })
})
