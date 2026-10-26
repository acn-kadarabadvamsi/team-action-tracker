import { test, expect } from '@playwright/test'

test('login screen displays required fields', async ({ page }) => {
  await page.goto('/')

  await expect(
    page.getByRole('heading', { name: 'Team Action Tracker' }).first()
  ).toBeVisible()

  await expect(page.getByLabel('Email address')).toBeVisible()
  await expect(page.getByLabel('Password')).toBeVisible()

  await expect(
    page.getByRole('button', { name: 'Sign in' })
  ).toBeVisible()
})

test('invalid login stays on the login screen', async ({ page }) => {
  await page.goto('/')

  await page.getByLabel('Email address').fill('invalid@example.com')
  await page.getByLabel('Password').fill('WrongPassword123!')
  await page.getByRole('button', { name: 'Sign in' }).click()

  await expect(page.getByRole('alert')).toBeVisible()
  await expect(
    page.getByRole('heading', { name: 'Team Action Tracker' }).first()
  ).toBeVisible()
})
