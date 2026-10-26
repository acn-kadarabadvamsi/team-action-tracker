import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import App from './App.vue'

describe('Team Action Tracker', () => {
  it('renders the login screen', () => {
    const wrapper = mount(App)

    expect(wrapper.text()).toContain('Team Action Tracker')
    expect(wrapper.text()).toContain('Sign in')
    expect(wrapper.find('input[type="email"]').exists()).toBe(true)
    expect(wrapper.find('input[type="password"]').exists()).toBe(true)
  })

  it('requires email and password', () => {
    const wrapper = mount(App)

    expect(wrapper.find('input[type="email"]').attributes('required')).toBeDefined()
    expect(wrapper.find('input[type="password"]').attributes('required')).toBeDefined()
  })
})