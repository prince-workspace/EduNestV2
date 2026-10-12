// Authentication, Role Guard & Modern SaaS Sidebar Shell for EduNest V2

function saveAuthSession(authData) {
  if (authData.token) {
    localStorage.setItem('edunest_token', authData.token);
  }
  localStorage.setItem('edunest_userId', authData.userId);
  localStorage.setItem('edunest_name', authData.name);
  localStorage.setItem('edunest_email', authData.email);
  localStorage.setItem('edunest_role', authData.role);
  localStorage.setItem('edunest_goal', authData.learningGoal || '');
}

function redirectByRole(role) {
  if (role === 'STUDENT') {
    window.location.href = '/student/dashboard.html';
  } else if (role === 'INSTRUCTOR') {
    window.location.href = '/instructor/dashboard.html';
  } else if (role === 'ADMIN') {
    window.location.href = '/admin/dashboard.html';
  } else {
    window.location.href = '/index.html';
  }
}

function requireAuth(expectedRole = null) {
  const token = localStorage.getItem('edunest_token');
  const role = localStorage.getItem('edunest_role');
  if (!token) {
    window.location.href = '/login.html';
    return false;
  }
  if (expectedRole && role !== expectedRole) {
    redirectByRole(role);
    return false;
  }
  return true;
}

function logout() {
  localStorage.clear();
  window.location.href = '/login.html';
}

function showError(elementId, message) {
  const el = document.getElementById(elementId);
  if (el) {
    el.textContent = message;
    el.classList.remove('hidden');
  }
}

function fillDemo(email, password) {
  const emailInput = document.getElementById('email');
  const passInput = document.getElementById('password');
  if (emailInput && passInput) {
    emailInput.value = email;
    passInput.value = password;
  }
}

// Build sidebar links based on role and current path
function buildSidebarLinksHtml(role, currentPath) {
  const isActive = (p) => currentPath.includes(p) ? 'active' : '';

  if (role === 'INSTRUCTOR') {
    return `
      <a href="/instructor/dashboard.html" class="sidebar-link ${isActive('/instructor/dashboard.html')}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9.5L12 3l9 6.5V20a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1V9.5z"/></svg>
        <span>Dashboard</span>
      </a>
      <a href="/instructor/create-course.html" class="sidebar-link ${isActive('/instructor/create-course.html')}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
        <span>Course Builder</span>
      </a>
      <a href="/instructor/analytics.html" class="sidebar-link ${isActive('/instructor/analytics.html')}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
        <span>Class Analytics</span>
      </a>
    `;
  }

  if (role === 'ADMIN') {
    return `
      <a href="/admin/dashboard.html" class="sidebar-link ${isActive('/admin/dashboard.html')}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9.5L12 3l9 6.5V20a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1V9.5z"/></svg>
        <span>Admin Overview</span>
      </a>
    `;
  }

  // Default: STUDENT sidebar links
  return `
    <a href="/student/dashboard.html" class="sidebar-link ${isActive('/student/dashboard.html')}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9.5L12 3l9 6.5V20a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1V9.5z"/></svg>
      <span>Dashboard</span>
    </a>
    <a href="/student/courses.html" class="sidebar-link ${isActive('/student/courses.html') || isActive('/student/course-detail.html') || isActive('/student/lesson.html') ? 'active' : ''}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
      <span>My Courses</span>
    </a>
    <a href="/student/daily-plan.html" class="sidebar-link ${isActive('/student/daily-plan.html')}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
      <span>Daily Plan</span>
    </a>
    <a href="/student/knowledge-profile.html" class="sidebar-link ${isActive('/student/knowledge-profile.html')}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
      <span>Knowledge Profile</span>
    </a>
    <a href="/student/learning-path.html" class="sidebar-link ${isActive('/student/learning-path.html')}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6"/><line x1="8" y1="2" x2="8" y2="18"/><line x1="16" y1="6" x2="16" y2="22"/></svg>
      <span>Learning Path</span>
    </a>
    <a href="/student/skill-map.html" class="sidebar-link ${isActive('/student/skill-map.html')}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/></svg>
      <span>Skill Map</span>
    </a>
    <a href="/student/challenges.html" class="sidebar-link ${isActive('/student/challenges.html') || isActive('/student/challenge-detail.html') ? 'active' : ''}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>
      <span>Challenges</span>
    </a>
    <a href="/student/revision.html" class="sidebar-link ${isActive('/student/revision.html')}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
      <span>Revision</span>
    </a>
    <a href="/student/bookmarks.html" class="sidebar-link ${isActive('/student/bookmarks.html')}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
      <span>Bookmarks</span>
    </a>
    <a href="/student/profile.html" class="sidebar-link ${isActive('/student/profile.html')}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
      <span>Profile & Goals</span>
    </a>
  `;
}

// Automatically transform legacy top-navbar pages into the modern Sidebar + Topbar App Shell
function initModernAppShell() {
  const path = window.location.pathname;
  const isPortalPage = path.startsWith('/student/') || path.startsWith('/instructor/') || path.startsWith('/admin/');
  if (!isPortalPage) return;

  // Skip if page already uses .app-shell natively
  if (document.querySelector('.app-shell')) {
    wireGlobalSearchInput();
    return;
  }

  const oldNav = document.querySelector('nav.navbar');
  if (!oldNav) return;

  const role = localStorage.getItem('edunest_role') || 'STUDENT';
  const userName = localStorage.getItem('edunest_name') || 'Aarav Sharma';
  const initials = userName.split(' ').map(p => p[0]).join('').substring(0, 2).toUpperCase();
  const homeUrl = role === 'INSTRUCTOR' ? '/instructor/dashboard.html' : role === 'ADMIN' ? '/admin/dashboard.html' : '/student/dashboard.html';
  const profileUrl = role === 'STUDENT' ? '/student/profile.html' : homeUrl;

  const contentNodes = [];
  let sibling = oldNav.nextSibling;
  while (sibling) {
    const next = sibling.nextSibling;
    if (sibling.nodeType === 1 && sibling.tagName !== 'SCRIPT') {
      contentNodes.push(sibling);
    }
    sibling = next;
  }

  oldNav.remove();

  const shell = document.createElement('div');
  shell.className = 'app-shell';
  shell.innerHTML = `
    <aside class="app-sidebar">
      <a href="${homeUrl}" class="sidebar-brand">
        <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
          <path d="M16 4L2 11L16 18L30 11L16 4Z" fill="#1e3a8a"/>
          <path d="M7 14.5V22.5C7 22.5 11 26 16 26C21 26 25 22.5 25 22.5V14.5L16 19L7 14.5Z" fill="#2563eb"/>
          <path d="M28 12.5V21" stroke="#f59e0b" stroke-width="2.2" stroke-linecap="round"/>
        </svg>
        <div class="sidebar-brand-text">Edu<span>Nest</span></div>
      </a>
      <nav class="sidebar-nav">
        ${buildSidebarLinksHtml(role, path)}
      </nav>
      <div class="sidebar-footer">
        <a href="${profileUrl}" class="sidebar-link">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
          <span>Profile & Settings</span>
        </a>
        <button onclick="logout()" class="sidebar-link">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
          <span>Logout</span>
        </button>
      </div>
    </aside>
    <div class="app-main">
      <header class="app-topbar">
        <div class="topbar-search">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input type="text" id="globalSearchInput" placeholder="Search for courses, topics or resources..." />
        </div>
        <div class="topbar-user">
          <a href="${profileUrl}" class="user-avatar-chip" style="text-decoration:none; color:inherit;">
            <div class="user-avatar-circle">${initials}</div>
            <span>${userName}</span>
          </a>
        </div>
      </header>
      <div class="app-content" id="appShellContent"></div>
    </div>
  `;

  document.body.prepend(shell);
  const contentContainer = document.getElementById('appShellContent');
  contentNodes.forEach(node => {
    if (node.classList.contains('container')) {
      node.style.maxWidth = '100%';
      node.style.padding = '0';
      node.style.marginTop = '0';
    }
    contentContainer.appendChild(node);
  });

  wireGlobalSearchInput();
}

function wireGlobalSearchInput() {
  const input = document.getElementById('globalSearchInput');
  if (!input) return;
  input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      const q = input.value.trim().toLowerCase();
      if (!q) return;
      if (q.includes('skill') || q.includes('map')) {
        window.location.href = '/student/skill-map.html';
      } else if (q.includes('challenge') || q.includes('code')) {
        window.location.href = '/student/challenges.html';
      } else if (q.includes('daily') || q.includes('plan')) {
        window.location.href = '/student/daily-plan.html';
      } else if (q.includes('profile') || q.includes('goal') || q.includes('setting')) {
        window.location.href = '/student/profile.html';
      } else if (q.includes('revise') || q.includes('revision')) {
        window.location.href = '/student/revision.html';
      } else if (q.includes('weak') || q.includes('mastery')) {
        window.location.href = '/student/knowledge-profile.html';
      } else {
        window.location.href = '/student/courses.html';
      }
    }
  });
}

document.addEventListener('DOMContentLoaded', () => {
  initModernAppShell();

  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const errEl = document.getElementById('errorMsg');
      errEl?.classList.add('hidden');

      const email = document.getElementById('email').value.trim();
      const password = document.getElementById('password').value;

      try {
        const data = await apiFetch('/auth/login', {
          method: 'POST',
          body: JSON.stringify({ email, password })
        });
        saveAuthSession(data);
        redirectByRole(data.role);
      } catch (err) {
        showError('errorMsg', err.message);
      }
    });
  }

  const registerForm = document.getElementById('registerForm');
  if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const errEl = document.getElementById('errorMsg');
      errEl?.classList.add('hidden');

      const name = document.getElementById('name').value.trim();
      const email = document.getElementById('email').value.trim();
      const password = document.getElementById('password').value;
      const role = document.getElementById('role').value;
      const learningGoal = document.getElementById('learningGoal')?.value.trim() || '';

      try {
        const data = await apiFetch('/auth/register', {
          method: 'POST',
          body: JSON.stringify({ name, email, password, role, learningGoal })
        });
        saveAuthSession(data);
        redirectByRole(data.role);
      } catch (err) {
        showError('errorMsg', err.message);
      }
    });
  }
});
