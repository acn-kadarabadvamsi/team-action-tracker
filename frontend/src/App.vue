<script setup>
import { ref, computed, onMounted } from 'vue'


// Form defaults and UI state used by the template and action handlers.
function createEmptyUser() {
  return {
    name: '',
    email: '',
    password: '',
    role: 'TEAM_MEMBER'
  }
}

function createEmptyAction() {
  return {
    title: '',
    description: '',
    assigneeId: '',
    priority: 'MEDIUM',
    dueDate: ''
  }
}

const newAction = ref(createEmptyAction())
const updatingActionId = ref(null)
const statusUpdateError = ref('')
const statusUpdateSuccess = ref('')

const selectedHistoryAction = ref(null)
const actionHistory = ref([])
const historyError = ref('')
const historyLoading = ref(false)

const selectedCommentAction = ref(null)
const actionComments = ref([])
const newCommentText = ref('')
const commentsError = ref('')
const commentsSuccess = ref('')
const commentsLoading = ref(false)
const commentsSaving = ref(false)

const API_BASE = 'http://localhost:8080/api'
const API_URL = `${API_BASE}/action-items`

const currentUser = ref(null)
const csrfToken = ref('')
const csrfHeader = ref('X-CSRF-TOKEN')

const loginEmail = ref('vamsi@example.com')
const loginPassword = ref('')
const loginError = ref('')
const loginLoading = ref(false)
const isLoggedIn = ref(false)

const actions = ref([])
const users = ref([])
const usersLoading = ref(false)
const userListError = ref('')
const loading = ref(false)
const saving = ref(false)
const error = ref('')
const formError = ref('')
const formSuccess = ref('')
const showCreateForm = ref(false)

const showCreateUserForm = ref(false)
const creatingUser = ref(false)
const userError = ref('')
const userSuccess = ref('')

const newUser = ref(createEmptyUser())

const search = ref('')
const statusFilter = ref('ALL')
const priorityFilter = ref('ALL')
const assigneeFilter = ref('ALL')
const dateFrom = ref('')
const dateTo = ref('')
const sortBy = ref('dueDate')
const sortDirection = ref('asc')
const currentPage = ref(1)
const pageSize = ref(10)

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

function isOverdue(action) {
  if (!action.dueDate || action.status === 'COMPLETED') {
    return false
  }

  const now = new Date()
  const today = [
    now.getFullYear(),
    String(now.getMonth() + 1).padStart(2, '0'),
    String(now.getDate()).padStart(2, '0')
  ].join('-')

  return action.dueDate < today
}

const overdue = computed(() =>
  actions.value.filter(isOverdue).length
)

const blocked = computed(() =>
  actions.value.filter(a => a.status === 'BLOCKED').length
)

const filteredActions = computed(() => {
  const query = search.value.trim().toLowerCase()

  const result = actions.value.filter(action => {
    const matchesSearch = !query || [
      action.title,
      action.description,
      action.assignee?.name
    ].some(value =>
      String(value || '').toLowerCase().includes(query)
    )

    const matchesStatus =
      statusFilter.value === 'ALL' ||
      action.status === statusFilter.value

    const matchesPriority =
      priorityFilter.value === 'ALL' ||
      action.priority === priorityFilter.value

    const matchesAssignee =
      assigneeFilter.value === 'ALL' ||
      String(action.assignee?.userId ?? '') === assigneeFilter.value

    const matchesStartDate =
      !dateFrom.value ||
      Boolean(action.dueDate && action.dueDate >= dateFrom.value)

    const matchesEndDate =
      !dateTo.value ||
      Boolean(action.dueDate && action.dueDate <= dateTo.value)

    return matchesSearch &&
      matchesStatus &&
      matchesPriority &&
      matchesAssignee &&
      matchesStartDate &&
      matchesEndDate
  })

  const priorityRank = {
    LOW: 1,
    MEDIUM: 2,
    HIGH: 3,
    CRITICAL: 4
  }

  result.sort((a, b) => {
    let comparison = 0

    if (sortBy.value === 'title') {
      comparison = String(a.title || '').localeCompare(
        String(b.title || '')
      )
    } else if (sortBy.value === 'priority') {
      comparison =
        (priorityRank[a.priority] || 0) -
        (priorityRank[b.priority] || 0)
    } else {
      comparison = String(a.dueDate || '9999-12-31').localeCompare(
        String(b.dueDate || '9999-12-31')
      )
    }

    return sortDirection.value === 'asc'
      ? comparison
      : -comparison
  })

  return result
})

const totalPages = computed(() =>
  Math.max(1, Math.ceil(filteredActions.value.length / pageSize.value))
)

const paginatedActions = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value

  return filteredActions.value.slice(
    start,
    start + pageSize.value
  )
})

const firstVisibleItem = computed(() =>
  filteredActions.value.length === 0
    ? 0
    : (currentPage.value - 1) * pageSize.value + 1
)

const lastVisibleItem = computed(() =>
  Math.min(
    currentPage.value * pageSize.value,
    filteredActions.value.length
  )
)

function resetPage() {
  currentPage.value = 1
}

function changeSort(field) {
  if (sortBy.value === field) {
    sortDirection.value =
      sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = field
    sortDirection.value = 'asc'
  }

  resetPage()
}

async function getCsrfToken() {
  const response = await fetch(`${API_BASE}/auth/csrf`, {
    credentials: 'include'
  })

  if (!response.ok) {
    throw new Error('Could not initialize secure request.')
  }

  const data = await response.json()
  csrfToken.value = data.token
  csrfHeader.value = data.headerName
}

async function login() {
  loginError.value = ''
  loginLoading.value = true

  try {
    await getCsrfToken()

    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader.value]: csrfToken.value
      },
      body: JSON.stringify({
        email: loginEmail.value.trim(),
        password: loginPassword.value
      })
    })

    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        throw new Error('Invalid email or password.')
      }

      throw new Error(`Login failed (HTTP ${response.status}).`)
    }

    currentUser.value = await response.json()
    isLoggedIn.value = true
    loginPassword.value = ''

    await getCsrfToken()
    await loadActions()

    if (currentUser.value?.role === 'MANAGER') {
      await loadUsers()
    }
  } catch (e) {
    currentUser.value = null
    isLoggedIn.value = false
    loginError.value = e.message || 'Login failed. Please try again.'
  } finally {
    loginLoading.value = false
  }
}

async function loadActions() {
  loading.value = true
  error.value = ''

  try {
    const response = await fetch(API_URL, {
      credentials: 'include'
    })

    if (response.status === 401 || response.status === 403) {
      isLoggedIn.value = false
      currentUser.value = null
      actions.value = []
      throw new Error(
        'Session expired or access denied. Please log in again.'
      )
    }

    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`)
    }

    const data = await response.json()
    actions.value = Array.isArray(data) ? data : []
  } catch (e) {
    error.value = e.message ||
      'Could not load action items. Check that the backend is running.'
  } finally {
    loading.value = false
  }
}

async function updateActionStatus(action, newStatus) {
  updatingActionId.value = action.actionItemId
  statusUpdateError.value = ''
  statusUpdateSuccess.value = ''

  try {
    await getCsrfToken()

    const response = await fetch(
      `${API_URL}/${action.actionItemId}/status`,
      {
        method: 'PATCH',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
          [csrfHeader.value]: csrfToken.value
        },
        body: JSON.stringify({
          status: newStatus,
          comment: `Status changed to ${newStatus}`
        })
      }
    )

    if (!response.ok) {
      if (response.status === 401) {
        throw new Error(
          'Your session has expired. Please log in again.'
        )
      }

      if (response.status === 403) {
        throw new Error(
          'You are not authorized to update this action item.'
        )
      }

      const errorData = await response.json().catch(() => null)

      throw new Error(
        errorData?.message ||
        `Status update failed (HTTP ${response.status}).`
      )
    }

    const updatedAction = await response.json()

    actions.value = actions.value.map(item =>
      item.actionItemId === updatedAction.actionItemId
        ? updatedAction
        : item
    )

    statusUpdateSuccess.value =
      `Status updated to ${newStatus.replaceAll('_', ' ')}.`
  } catch (e) {
    statusUpdateError.value =
      e.message || 'Could not update the action item.'
  } finally {
    updatingActionId.value = null
  }
}

async function loadActionHistory(action) {
  selectedHistoryAction.value = action
  actionHistory.value = []
  historyError.value = ''
  historyLoading.value = true

  try {
    const response = await fetch(
      `${API_URL}/${action.actionItemId}/history`,
      { credentials: 'include' }
    )

    if (response.status === 401 || response.status === 403) {
      throw new Error(
        'You are not authorized to view this action history.'
      )
    }

    if (!response.ok) {
      throw new Error(
        `Could not load action history (HTTP ${response.status}).`
      )
    }

    const data = await response.json()

    actionHistory.value = Array.isArray(data)
      ? data
      : Array.isArray(data.value)
        ? data.value
        : []
  } catch (e) {
    historyError.value =
      e.message || 'Could not load action history.'
  } finally {
    historyLoading.value = false
  }
}

function closeActionHistory() {
  selectedHistoryAction.value = null
  actionHistory.value = []
  historyError.value = ''
}

// Load comments for one action item from the secured backend API.
async function loadActionComments(action) {
  selectedCommentAction.value = action
  actionComments.value = []
  newCommentText.value = ''
  commentsError.value = ''
  commentsSuccess.value = ''
  commentsLoading.value = true

  try {
    const response = await fetch(
      `${API_BASE}/comments?actionItemId=${encodeURIComponent(action.actionItemId)}`,
      {
        method: 'GET',
        credentials: 'include'
      }
    )

    if (response.status === 401 || response.status === 403) {
      throw new Error(
        'You are not authorized to view comments.'
      )
    }

    if (!response.ok) {
      throw new Error(
        `Could not load comments (HTTP ${response.status}).`
      )
    }

    const data = await response.json()

    actionComments.value = Array.isArray(data)
      ? data
      : Array.isArray(data.value)
        ? data.value
        : []
  } catch (e) {
    commentsError.value =
      e.message || 'Could not load comments.'
  } finally {
    commentsLoading.value = false
  }
}

// The backend derives the commenter from the authenticated session.
// Never send a user ID from the frontend as proof of identity.
async function addActionComment() {
  const text = newCommentText.value.trim()

  if (!selectedCommentAction.value || !text) {
    commentsError.value = 'Enter a comment before submitting.'
    return
  }

  if (text.length > 5000) {
    commentsError.value =
      'Comment must be 5000 characters or fewer.'
    return
  }

  commentsSaving.value = true
  commentsError.value = ''
  commentsSuccess.value = ''

  try {
    await getCsrfToken()

    const response = await fetch(`${API_BASE}/comments`, {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader.value]: csrfToken.value
      },
      body: JSON.stringify({
        actionItemId: selectedCommentAction.value.actionItemId,
        comment: text
      })
    })

    if (response.status === 401) {
      throw new Error(
        'Your session has expired. Please log in again.'
      )
    }

    if (response.status === 403) {
      throw new Error(
        'You are not authorized to add comments.'
      )
    }

    if (!response.ok) {
      const errorData = await response.json().catch(() => null)

      throw new Error(
        errorData?.message ||
        `Could not add comment (HTTP ${response.status}).`
      )
    }

    const savedResponse = await response.json()
    const savedComment = savedResponse?.value ?? savedResponse

    actionComments.value.push(savedComment)

    actionComments.value.sort(
      (a, b) =>
        new Date(a.createdAt || 0) -
        new Date(b.createdAt || 0)
    )

    newCommentText.value = ''
    commentsSuccess.value = 'Comment added successfully.'
  } catch (e) {
    commentsError.value =
      e.message || 'Could not add comment.'
  } finally {
    commentsSaving.value = false
  }
}

function closeActionComments() {
  selectedCommentAction.value = null
  actionComments.value = []
  newCommentText.value = ''
  commentsError.value = ''
  commentsSuccess.value = ''
}

async function loadUsers() {
  if (currentUser.value?.role !== 'MANAGER') return

  usersLoading.value = true
  userListError.value = ''

  try {
    const response = await fetch(`${API_BASE}/users`, {
      credentials: 'include'
    })

    if (response.status === 401 || response.status === 403) {
      throw new Error(
        'Only a signed-in Manager can load the assignee list.'
      )
    }

    if (!response.ok) {
      throw new Error(`Could not load users (HTTP ${response.status}).`)
    }

    const data = await response.json()
    users.value = Array.isArray(data) ? data : []

    const selectedId = Number(newAction.value.assigneeId)
    const selectedUserExists = users.value.some(
      user => Number(user.userId) === selectedId
    )

    if (!selectedUserExists && users.value.length > 0) {
      newAction.value.assigneeId = Number(users.value[0].userId)
    }
  } catch (e) {
    users.value = []
    userListError.value =
      e.message || 'Could not load users for assignment.'
  } finally {
    usersLoading.value = false
  }
}

async function createAction() {
  if (currentUser.value?.role !== 'MANAGER') return

  await loadUsers()

  formError.value = ''
  formSuccess.value = ''
  error.value = ''
  newAction.value = createEmptyAction()
  showCreateForm.value = true
}

function closeCreateForm() {
  if (saving.value) return
  showCreateForm.value = false
  formError.value = ''
}

function openCreateUserForm() {
  if (currentUser.value?.role !== 'MANAGER') return

  userError.value = ''
  userSuccess.value = ''
  newUser.value = createEmptyUser()
  showCreateUserForm.value = true
}

function closeCreateUserForm() {
  if (creatingUser.value) return
  showCreateUserForm.value = false
  userError.value = ''
}

async function createUser() {
  userError.value = ''
  userSuccess.value = ''

  if (currentUser.value?.role !== 'MANAGER') {
    userError.value = 'Only Managers can create accounts.'
    return
  }

  const name = newUser.value.name.trim()
  const email = newUser.value.email.trim()
  const password = newUser.value.password
  const role = newUser.value.role

  if (!name || !email || !password) {
    userError.value = 'Please fill in all required fields.'
    return
  }

  if (name.length > 100) {
    userError.value = 'Name must not exceed 100 characters.'
    return
  }

  if (email.length > 150) {
    userError.value = 'Email must not exceed 150 characters.'
    return
  }

  if (password.length < 12 || password.length > 72) {
    userError.value = 'Password must contain 12 to 72 characters.'
    return
  }

  if (!['MANAGER', 'TEAM_MEMBER'].includes(role)) {
    userError.value = 'Please select a valid role.'
    return
  }

  creatingUser.value = true

  try {
    await getCsrfToken()

    const response = await fetch(`${API_BASE}/users`, {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader.value]: csrfToken.value
      },
      body: JSON.stringify({
        name,
        email,
        password,
        role
      })
    })

    if (response.status === 401 || response.status === 403) {
      throw new Error(
        'Access denied. Sign in with a Manager account to create users.'
      )
    }

    if (response.status === 409) {
      throw new Error(
        'An account with this email already exists.'
      )
    }

    if (response.status === 400) {
      const message = await response.text()
      throw new Error(
        message || 'Please check the submitted details.'
      )
    }

    if (!response.ok) {
      throw new Error(
        `Could not create account (HTTP ${response.status}).`
      )
    }

    const result = await response.json()

    userSuccess.value =
      `Account created for ${result.name} (${result.role}).`

    newUser.value = createEmptyUser()
    showCreateUserForm.value = false
    await loadUsers()
  } catch (e) {
    userError.value =
      e.message || 'Could not create the account.'
  } finally {
    creatingUser.value = false
  }
}

async function saveAction() {
  if (currentUser.value?.role !== 'MANAGER') {
    formError.value = 'Only Managers can create action items.'
    return
  }

  formError.value = ''
  formSuccess.value = ''

  const title = newAction.value.title.trim()
  const dueDate = newAction.value.dueDate
  const today = new Date()

  const localToday = [
    today.getFullYear(),
    String(today.getMonth() + 1).padStart(2, '0'),
    String(today.getDate()).padStart(2, '0')
  ].join('-')

  if (!title) {
    formError.value = 'Title is required.'
    return
  }

  if (title.length > 200) {
    formError.value = 'Title must not exceed 200 characters.'
    return
  }

  if (!dueDate) {
    formError.value = 'Due date is required.'
    return
  }

  if (dueDate < localToday) {
    formError.value = 'Due date cannot be in the past.'
    return
  }

  saving.value = true

  try {
    await getCsrfToken()

    const payload = {
      title,
      description: newAction.value.description.trim() || null,
      assigneeId: Number(newAction.value.assigneeId),
      createdBy: currentUser.value.userId,
      priority: newAction.value.priority,
      status: 'NOT_STARTED',
      dueDate
    }

    const response = await fetch(API_URL, {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader.value]: csrfToken.value
      },
      body: JSON.stringify(payload)
    })

    if (response.status === 401 || response.status === 403) {
      throw new Error(
        'Session expired or you do not have permission.'
      )
    }

    if (!response.ok) {
      const message = await response.text()

      throw new Error(
        message || `Unable to create task (HTTP ${response.status}).`
      )
    }

    showCreateForm.value = false
    formSuccess.value = 'Action item created successfully.'
    await loadActions()
  } catch (e) {
    formError.value =
      e.message || 'Could not create action item.'
  } finally {
    saving.value = false
  }
}

function formatStatus(status) {
  return (status || 'UNKNOWN').replaceAll('_', ' ')
}

onMounted(() => {
  // Protected action items load only after a successful login.
})
</script>

<template>
  <!-- LOGIN SCREEN -->
  <main v-if="!isLoggedIn" class="dashboard">
    <section
      class="panel"
      style="max-width: 480px; margin: 8vh auto; padding: 32px;"
    >
      <p class="eyebrow">TEAM WORKSPACE</p>
      <h1>Team Action Tracker</h1>
      <p class="subtitle">Sign in to manage your team's action items.</p>

      <form
        @submit.prevent="login"
        style="display: grid; gap: 16px; margin-top: 28px;"
      >
        <label for="login-email">Email address</label>
        <input
          id="login-email"
          v-model="loginEmail"
          type="email"
          autocomplete="username"
          required
          placeholder="you@example.com"
        />

        <label for="login-password">Password</label>
        <input
          id="login-password"
          v-model="loginPassword"
          type="password"
          autocomplete="current-password"
          required
          placeholder="Enter your password"
        />

        <p v-if="loginError" class="form-error" role="alert">
          {{ loginError }}
        </p>

        <button class="primary-btn" type="submit" :disabled="loginLoading">
          {{ loginLoading ? 'Signing in...' : 'Sign in' }}
        </button>
      </form>
    </section>
  </main>

  <!-- DASHBOARD -->
  <main v-else class="dashboard">
    <header class="topbar">
      <div>
        <p class="eyebrow">TEAM WORKSPACE</p>
        <h1>Team Action Tracker</h1>
        <p class="subtitle">Track responsibilities. Keep work moving.</p>
        <p class="subtitle">
          Signed in as <strong>{{ currentUser?.name }}</strong>
          · {{ currentUser?.role }}
        </p>
      </div>

      <div class="topbar-actions">
        <button
          class="refresh-btn"
          type="button"
          @click="loadActions"
          :disabled="loading"
        >
          <span class="refresh-icon" aria-hidden="true">↻</span>
          <span>{{ loading ? 'Refreshing...' : 'Refresh' }}</span>
        </button>

        <button
          v-if="currentUser?.role === 'MANAGER'"
          class="refresh-btn"
          type="button"
          @click="openCreateUserForm"
        >
          Create User
        </button>

        <button
          v-if="currentUser?.role === 'MANAGER'"
          class="primary-btn"
          type="button"
          @click="createAction"
        >
          <span aria-hidden="true">+</span>
          <span>Create Action</span>
        </button>
      </div>
    </header>

    <p v-if="formSuccess" class="success-banner" role="status">
      {{ formSuccess }}
      <button type="button" @click="formSuccess = ''" aria-label="Dismiss">
        ✕
      </button>
    </p>

    <p v-if="userSuccess" class="success-banner" role="status">
      {{ userSuccess }}
      <button type="button" @click="userSuccess = ''" aria-label="Dismiss">
        ✕
      </button>
    </p>

    <p v-if="statusUpdateSuccess" class="success-banner" role="status">
      {{ statusUpdateSuccess }}
      <button
        type="button"
        @click="statusUpdateSuccess = ''"
        aria-label="Dismiss"
      >✕</button>
    </p>

    <p v-if="statusUpdateError" class="message error" role="alert">
      {{ statusUpdateError }}
      <button
        type="button"
        @click="statusUpdateError = ''"
        aria-label="Dismiss"
      >✕</button>
    </p>

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

  <article class="summary-card">
    <p>Blocked</p>
    <h2>{{ blocked }}</h2>
  </article>

  <article class="summary-card">
    <p>Overdue</p>
    <h2>{{ overdue }}</h2>
  </article>
</section>

    <section class="panel">
      <div class="panel-heading">
        <div>
          <h2>Action items</h2>
          <p>Search and filter tasks assigned to your team.</p>
        </div>
        <span class="count">{{ filteredActions.length }} items</span>
      </div>

      <div class="filters">
  <input
    v-model="search"
    type="search"
    placeholder="Search title, description, or assignee..."
    aria-label="Search action items"
    @input="resetPage"
  />

  <select
    v-model="statusFilter"
    aria-label="Filter by status"
    @change="resetPage"
  >
    <option value="ALL">All statuses</option>
    <option value="NOT_STARTED">Not started</option>
    <option value="IN_PROGRESS">In progress</option>
    <option value="BLOCKED">Blocked</option>
    <option value="COMPLETED">Completed</option>
  </select>

  <select
    v-model="priorityFilter"
    aria-label="Filter by priority"
    @change="resetPage"
  >
    <option value="ALL">All priorities</option>
    <option value="CRITICAL">Critical</option>
    <option value="HIGH">High</option>
    <option value="MEDIUM">Medium</option>
    <option value="LOW">Low</option>
  </select>

  <select
    v-if="currentUser?.role === 'MANAGER'"
    v-model="assigneeFilter"
    aria-label="Filter by assignee"
    @change="resetPage"
  >
    <option value="ALL">All assignees</option>
    <option
      v-for="user in users"
      :key="user.userId"
      :value="String(user.userId)"
    >
      {{ user.name }}
    </option>
  </select>

  <label>
    Due from
    <input
      v-model="dateFrom"
      type="date"
      aria-label="Due date from"
      @change="resetPage"
    />
  </label>

  <label>
    Due to
    <input
      v-model="dateTo"
      type="date"
      aria-label="Due date to"
      :min="dateFrom || undefined"
      @change="resetPage"
    />
  </label>

  <select
    v-model="sortBy"
    aria-label="Sort action items by"
    @change="resetPage"
  >
    <option value="dueDate">Sort by due date</option>
    <option value="title">Sort by title</option>
    <option value="priority">Sort by priority</option>
  </select>

  <button
    type="button"
    class="refresh-btn"
    @click="changeSort(sortBy)"
  >
    {{ sortDirection === 'asc' ? 'Ascending ↑' : 'Descending ↓' }}
  </button>

  <button
    type="button"
    class="refresh-btn"
    @click="
      search = '';
      statusFilter = 'ALL';
      priorityFilter = 'ALL';
      assigneeFilter = 'ALL';
      dateFrom = '';
      dateTo = '';
      sortBy = 'dueDate';
      sortDirection = 'asc';
      currentPage = 1
    "
  >
    Clear filters
  </button>
</div>

      <p v-if="loading" class="message">Loading action items...</p>

      <p v-else-if="error" class="message error">
        {{ error }}
        <button class="refresh-btn" type="button" @click="loadActions">
          Try again
        </button>
      </p>

      <p v-else-if="filteredActions.length === 0" class="message">
        No matching action items found.
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
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="action in paginatedActions"
              :key="action.actionItemId"
            >
              <td class="task-cell">
                <strong>{{ action.title }}</strong>
                <small>{{ action.description || 'No description' }}</small>
              </td>

              <td>{{ action.assignee?.name || 'Unassigned' }}</td>

              <td>
                <span
                  class="priority"
                  :class="(action.priority || '').toLowerCase()"
                >
                  {{ action.priority || '—' }}
                </span>
              </td>

              <td>
                <span
                  class="status"
                  :class="(action.status || '').toLowerCase()"
                >
                  {{ formatStatus(action.status) }}
                </span>
              </td>

              <td>{{ action.dueDate || '—' }}</td>

              <td class="action-controls">
                <select
                  :value="action.status"
                  :disabled="updatingActionId === action.actionItemId ||
                    (currentUser?.role !== 'MANAGER' &&
                     Number(action.assignee?.userId) !== Number(currentUser?.userId))"
                  :aria-label="`Update status for ${action.title}`"
                  @change="updateActionStatus(action, $event.target.value)"
                >
                  <option value="NOT_STARTED">Not started</option>
                  <option value="IN_PROGRESS">In progress</option>
                  <option value="BLOCKED">Blocked</option>
                  <option value="COMPLETED">Completed</option>
                </select>

                <small v-if="updatingActionId === action.actionItemId">
                  Saving...
                </small>

                <button
                  type="button"
                  class="refresh-btn"
                  @click="loadActionHistory(action)"
                >
                  History
                </button>

                <button
                  type="button"
                  class="refresh-btn"
                  @click="loadActionComments(action)"
                >
                  Comments
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div
        v-if="!loading && !error && filteredActions.length > 0"
        class="pagination"
      >
        <span>
          Showing {{ firstVisibleItem }}–{{ lastVisibleItem }}
          of {{ filteredActions.length }} items
        </span>

        <label>
          Rows per page
          <select v-model.number="pageSize" @change="currentPage = 1">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>
        </label>

        <button
          type="button"
          class="refresh-btn"
          :disabled="currentPage <= 1"
          @click="currentPage--"
        >
          Previous
        </button>

        <span>Page {{ currentPage }} of {{ totalPages }}</span>

        <button
          type="button"
          class="refresh-btn"
          :disabled="currentPage >= totalPages"
          @click="currentPage++"
        >
          Next
        </button>
      </div>
    </section>

    <footer>Team Action Tracker · Project dashboard</footer>

    <!-- ACTION HISTORY MODAL -->
    <div
      v-if="selectedHistoryAction"
      class="modal-overlay"
      @click.self="closeActionHistory"
    >
      <section
        class="create-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="history-title"
      >
        <div class="modal-heading">
          <div>
            <p class="eyebrow">AUDIT TRAIL</p>
            <h2 id="history-title">Action History</h2>
            <p class="subtitle">{{ selectedHistoryAction.title }}</p>
          </div>

          <button
            class="close-btn"
            type="button"
            @click="closeActionHistory"
            aria-label="Close history"
          >✕</button>
        </div>

        <p v-if="historyLoading" class="message">
          Loading action history...
        </p>

        <p v-else-if="historyError" class="form-error" role="alert">
          {{ historyError }}
        </p>

        <p v-else-if="actionHistory.length === 0" class="message">
          No history entries yet.
        </p>

        <div v-else class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Previous status</th>
                <th>New status</th>
                <th>Updated by</th>
                <th>Comment</th>
              </tr>
            </thead>

            <tbody>
              <tr v-for="entry in actionHistory" :key="entry.historyId">
                <td>
                  {{ entry.updatedAt
                    ? new Date(entry.updatedAt).toLocaleString()
                    : '—' }}
                </td>
                <td>
                  {{ entry.previousStatus
                    ? formatStatus(entry.previousStatus)
                    : '—' }}
                </td>
                <td>{{ formatStatus(entry.newStatus) }}</td>
                <td>
                  {{ entry.updatedBy?.name ||
                     entry.updatedBy?.email ||
                     'Unknown' }}
                </td>
                <td>{{ entry.comment || '—' }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="modal-actions">
          <button
            class="refresh-btn"
            type="button"
            @click="closeActionHistory"
          >
            Close
          </button>
        </div>
      </section>
    </div>

    <!-- ACTION COMMENTS MODAL -->
    <div
      v-if="selectedCommentAction"
      class="modal-overlay"
      @click.self="closeActionComments"
    >
      <section
        class="create-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="comments-title"
      >
        <div class="modal-heading">
          <div>
            <p class="eyebrow">DISCUSSION</p>
            <h2 id="comments-title">Action Comments</h2>
            <p class="subtitle">{{ selectedCommentAction.title }}</p>
          </div>

          <button
            class="close-btn"
            type="button"
            @click="closeActionComments"
            aria-label="Close comments"
          >✕</button>
        </div>

        <p v-if="commentsLoading" class="message">
          Loading comments...
        </p>

        <p v-if="commentsError" class="form-error" role="alert">
          {{ commentsError }}
        </p>

        <p v-if="commentsSuccess" class="form-success" role="status">
          {{ commentsSuccess }}
        </p>

        <div
          v-if="!commentsLoading &&
                actionComments.length === 0 &&
                !commentsError"
          class="message"
        >
          No comments yet. Start the discussion below.
        </div>

        <div v-if="actionComments.length > 0" class="comment-list">
          <article
            v-for="(entry, index) in actionComments"
            :key="entry.commentId || index"
            class="comment-entry"
          >
            <div class="comment-meta">
              <strong>
                {{ entry.user?.name || entry.user?.email || 'User' }}
              </strong>
              <small>
                {{ entry.createdAt
                  ? new Date(entry.createdAt).toLocaleString()
                  : '' }}
              </small>
            </div>

            <p>{{ entry.comment }}</p>
          </article>
        </div>

        <form class="comment-form" @submit.prevent="addActionComment">
          <label for="new-action-comment">Add a comment</label>

          <textarea
            id="new-action-comment"
            v-model="newCommentText"
            rows="3"
            maxlength="5000"
            placeholder="Write an update or ask a question..."
            required
          ></textarea>

          <div class="modal-actions">
            <button
              class="refresh-btn"
              type="button"
              @click="closeActionComments"
            >
              Close
            </button>

            <button
              class="primary-btn"
              type="submit"
              :disabled="commentsSaving || !newCommentText.trim()"
            >
              {{ commentsSaving ? 'Adding...' : 'Add comment' }}
            </button>
          </div>
        </form>
      </section>
    </div>

    <!-- CREATE ACTION MODAL -->
    <div
      v-if="showCreateForm"
      class="modal-overlay"
      @click.self="closeCreateForm"
    >
      <section
        class="create-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="create-title"
      >
        <div class="modal-heading">
          <div>
            <p class="eyebrow">NEW TASK</p>
            <h2 id="create-title">Create Action</h2>
            <p class="subtitle">Fill in the details below.</p>
          </div>

          <button
            class="close-btn"
            type="button"
            @click="closeCreateForm"
            :disabled="saving"
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        <form @submit.prevent="saveAction">
          <label for="action-title">Title *</label>
          <input
            id="action-title"
            v-model="newAction.title"
            maxlength="200"
            required
            placeholder="Enter action title"
          />

          <label for="action-description">Description</label>
          <textarea
            id="action-description"
            v-model="newAction.description"
            rows="3"
            placeholder="Describe the action"
          ></textarea>

          <label for="action-assignee">Assignee *</label>
          <select
            id="action-assignee"
            v-model.number="newAction.assigneeId"
            :disabled="usersLoading || users.length === 0"
            required
          >
            <option v-if="usersLoading" disabled value="">
              Loading users...
            </option>

            <option
              v-for="user in users"
              :key="user.userId"
              :value="Number(user.userId)"
            >
              {{ user.name }} — {{ user.role.replaceAll('_', ' ') }}
            </option>
          </select>

          <small v-if="userListError" class="form-error" role="alert">
            {{ userListError }}
          </small>

          <small v-else-if="!usersLoading && users.length === 0">
            No active users available. Refresh the page or check the backend.
          </small>

          <label for="action-priority">Priority *</label>
          <select
            id="action-priority"
            v-model="newAction.priority"
            required
          >
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="CRITICAL">Critical</option>
          </select>

          <label for="action-due-date">Due date *</label>
          <input
            id="action-due-date"
            v-model="newAction.dueDate"
            type="date"
            :min="new Date().getFullYear() + '-' +
                 String(new Date().getMonth() + 1).padStart(2, '0') + '-' +
                 String(new Date().getDate()).padStart(2, '0')"
            required
          />

          <p v-if="formError" class="form-error" role="alert">
            {{ formError }}
          </p>

          <div class="modal-actions">
            <button
              type="button"
              class="refresh-btn"
              @click="closeCreateForm"
              :disabled="saving"
            >
              Cancel
            </button>

            <button
              class="primary-btn"
              type="submit"
              :disabled="saving"
            >
              {{ saving ? 'Saving...' : 'Save Action' }}
            </button>
          </div>
        </form>
      </section>
    </div>

    <!-- CREATE USER MODAL -->
    <div
      v-if="showCreateUserForm"
      class="modal-overlay"
      @click.self="closeCreateUserForm"
    >
      <section
        class="create-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="create-user-title"
      >
        <div class="modal-heading">
          <div>
            <p class="eyebrow">MANAGER ACCESS</p>
            <h2 id="create-user-title">Create User</h2>
            <p class="subtitle">
              Create an account for a Manager or Team Member.
            </p>
          </div>

          <button
            class="close-btn"
            type="button"
            @click="closeCreateUserForm"
            :disabled="creatingUser"
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        <form @submit.prevent="createUser">
          <label for="new-user-name">Full name *</label>
          <input
            id="new-user-name"
            v-model="newUser.name"
            maxlength="100"
            autocomplete="name"
            required
            placeholder="Enter full name"
          />

          <label for="new-user-email">Email address *</label>
          <input
            id="new-user-email"
            v-model="newUser.email"
            type="email"
            maxlength="150"
            autocomplete="email"
            required
            placeholder="person@example.com"
          />

          <label for="new-user-password">Temporary password *</label>
          <input
            id="new-user-password"
            v-model="newUser.password"
            type="password"
            minlength="12"
            maxlength="72"
            autocomplete="new-password"
            required
            placeholder="At least 12 characters"
          />

          <small>Use a unique password of at least 12 characters.</small>

          <label for="new-user-role">Account role *</label>
          <select
            id="new-user-role"
            v-model="newUser.role"
            required
          >
            <option value="TEAM_MEMBER">Team Member</option>
            <option value="MANAGER">Manager</option>
          </select>

          <p v-if="userError" class="form-error" role="alert">
            {{ userError }}
          </p>

          <div class="modal-actions">
            <button
              type="button"
              class="refresh-btn"
              @click="closeCreateUserForm"
              :disabled="creatingUser"
            >
              Cancel
            </button>

            <button
              class="primary-btn"
              type="submit"
              :disabled="creatingUser"
            >
              {{ creatingUser ? 'Creating...' : 'Create Account' }}
            </button>
          </div>
        </form>
      </section>
    </div>
  </main>
</template>

<style scoped>
.comment-list {
  display: grid;
  gap: 12px;
  max-height: 280px;
  overflow-y: auto;
  margin: 16px 0;
}

.comment-entry {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 12px 14px;
  background: #fafafa;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.comment-meta small {
  color: #6b7280;
  font-size: 12px;
}

.comment-entry p {
  margin: 8px 0 0;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.comment-form {
  display: grid;
  gap: 8px;
  margin-top: 16px;
}

.comment-form label {
  font-weight: 600;
}

.comment-form textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font: inherit;
  resize: vertical;
}

.comment-form .modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
}
</style>