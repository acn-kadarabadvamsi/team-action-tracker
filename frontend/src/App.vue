
<script setup>
import { ref, computed, onMounted } from 'vue'

const actions = ref([])
const loading = ref(false)
const error = ref('')

const total = computed(() => actions.value.length)
const completed = computed(() =>
  actions.value.filter(a => a.status === 'COMPLETED').length
)
const inProgress = computed(() =>
  actions.value.filter(a => a.status === 'IN_PROGRESS').length
)
const pending = computed(() =>
  actions.value.filter(a => a.status === 'NOT_STARTED').length
)

async function loadActions() {
  loading.value = true
  error.value = ''

  try {
    const response = await fetch('http://localhost:8080/api/action-items')

    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`)
    }

    const data = await response.json()
    actions.value = Array.isArray(data) ? data : []
  } catch (e) {
    error.value = 'Could not load action items. Check that the backend is running.'
  } finally {
    loading.value = false
  }
}

onMounted(loadActions)
</script>

<template>
  <main class="dashboard">
    <header class="topbar">
      <div>
        <p class="eyebrow">TEAM WORKSPACE</p>
        <h1>Team Action Tracker</h1>
        <p class="subtitle">Track responsibilities. Keep work moving.</p>
      </div>
      <button class="refresh-btn" @click="loadActions">↻ Refresh</button>
    </header>

    <section class="summary">
      <article class="summary-card">
        <p>Total actions</p>
        <h2>{{ total }}</h2>
      </article>
      <article class="summary-card">
        <p>Not started</p>
        <h2>{{ pending }}</h2>
      </article>
      <article class="summary-card">
        <p>In progress</p>
        <h2>{{ inProgress }}</h2>
      </article>
      <article class="summary-card">
        <p>Completed</p>
        <h2>{{ completed }}</h2>
      </article>
    </section>

    <section class="panel">
      <div class="panel-heading">
        <div>
          <h2>Action items</h2>
          <p>Current tasks assigned to your team</p>
        </div>
        <span class="count">{{ total }} items</span>
      </div>

      <p v-if="loading" class="message">Loading action items...</p>
      <p v-else-if="error" class="message error">{{ error }}</p>
      <p v-else-if="actions.length === 0" class="message">
        No action items found.
      </p>

      <div v-else class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Action item</th>
              <th>Assignee</th>
              <th>Priority</th>
              <th>Status</th>
              <th>Due date</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="action in actions" :key="action.actionItemId">
              <td>
                <strong>{{ action.title }}</strong>
                <small>{{ action.description }}</small>
              </td>
              <td>{{ action.assignee?.name || 'Unassigned' }}</td>
              <td>
                <span class="priority" :class="action.priority?.toLowerCase()">
                  {{ action.priority }}
                </span>
              </td>
              <td>
                <span class="status">
                  {{ (action.status || '').replaceAll('_', ' ') }}
                </span>
              </td>
              <td>{{ action.dueDate || '—' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <footer>Team Action Tracker · Project dashboard</footer>
  </main>
</template>
