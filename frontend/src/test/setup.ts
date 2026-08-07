import '@testing-library/jest-dom/vitest'
import { configure } from '@testing-library/dom'
import { afterAll, afterEach, beforeAll } from 'vitest'
import { server } from '../mocks/server'
import { resetFixtures } from '../mocks/handlers'

configure({ asyncUtilTimeout: 5_000 })

beforeAll(() => server.listen({ onUnhandledRequest: 'error' }))
afterEach(() => { server.resetHandlers(); resetFixtures() })
afterAll(() => server.close())
