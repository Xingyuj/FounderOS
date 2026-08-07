import { expect, test } from '@playwright/test'

test('founder navigates the company and resolves the blocking decision', async ({ page }) => {
  await page.goto('/')
  await expect(page.getByRole('heading', { name: /Good morning/i })).toBeVisible()
  await page.getByRole('button', { name: /How should the disputed speed claim/i }).click()
  await page.getByLabel(/Use the verified range/i).click()
  await page.getByLabel(/Direction for the team/i).fill('Use the independent range and retain the caveat.')
  await page.getByRole('button', { name: /Confirm direction/i }).click()
  await expect(page.getByText('Direction recorded.')).toBeVisible()
  await page.getByRole('link', { name: 'Content Studio', exact: true }).click()
  await expect(page.getByText('CONTRADICTED')).toBeVisible()
  await page.getByRole('link', { name: 'Organization' }).click()
  await expect(page.getByRole('heading', { name: /team behind the work/i })).toBeVisible()
  await expect(page.getByText('Vacant position')).toBeVisible()
})
