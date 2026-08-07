import { expect, test } from '@playwright/test'

test('founder creates talent first and appoints it from a vacant position', async ({ page }) => {
  await page.goto('/talent')
  await expect(page.getByRole('heading', { name: /Your characters, before the job title/i })).toBeVisible()
  await page.getByRole('button', { name: /Create employee/i }).click()
  await page.getByLabel('Name your employee').fill('Juniper')
  await page.getByRole('radio', { name: /Sage/i }).click()
  await page.getByRole('button', { name: /Review Soul/i }).click()
  await page.getByRole('button', { name: /Add to talent pool/i }).click()
  await expect(page.getByRole('heading', { name: /Juniper joined your talent pool/i })).toBeVisible()
  await page.getByRole('link', { name: /Find a vacant position/i }).click()
  await page.waitForURL('**/organization')
  await page.locator('.org-node--vacant').click()
  await page.getByRole('button', { name: /Appoint from Talent Library/i }).click()
  await page.getByRole('button', { name: /Appoint Juniper/i }).click()
  await expect(page.getByText(/Juniper is now appointed/i)).toBeVisible()
})
