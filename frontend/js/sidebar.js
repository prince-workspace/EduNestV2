// Shared Sidebar Component for EduNest V2
// Injects the unified 4-item student navigation across all student pages

(function () {
  function getActiveNav() {
    if (document.body && document.body.dataset && document.body.dataset.activeNav) {
      return document.body.dataset.activeNav.toLowerCase();
    }
    const path = window.location.pathname.toLowerCase();
    if (path.includes('courses') || path.includes('course-detail') || path.includes('lesson')) {
      return 'courses';
    }
    if (path.includes('quiz') || path.includes('practice')) {
      return 'practices';
    }
    if (path.includes('challenges') || path.includes('challenge-detail')) {
      return 'challenges';
    }
    if (path.includes('bookmarks')) {
      return 'saved';
    }
    if (path.includes('profile')) {
      return 'profile';
    }
    if (path.includes('dashboard')) {
      return 'dashboard';
    }
    return '';
  }

  function getUserInfo() {
    const fullName = localStorage.getItem('edunest_name') || 'Student';
    const initials = fullName
      .split(' ')
      .filter(Boolean)
      .map(p => p[0])
      .join('')
      .substring(0, 2)
      .toUpperCase() || 'ST';
    return { fullName, initials };
  }

  function buildSidebarHtml(activeNav) {
    const isAct = (key) => (activeNav === key ? 'active' : '');
    const { fullName } = getUserInfo();

    return `
      <a href="/student/dashboard.html" class="sidebar-brand">
        <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
          <path d="M16 4L2 11L16 18L30 11L16 4Z" fill="#1e3a8a"/>
          <path d="M7 14.5V22.5C7 22.5 11 26 16 26C21 26 25 22.5 25 22.5V14.5L16 19L7 14.5Z" fill="#2563eb"/>
          <path d="M28 12.5V21" stroke="#f59e0b" stroke-width="2.2" stroke-linecap="round"/>
        </svg>
        <div class="sidebar-brand-text">Edu<span>Nest</span></div>
      </a>

      <nav class="sidebar-nav">
        <a href="/student/dashboard.html" class="sidebar-link ${isAct('dashboard')}">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9.5L12 3l9 6.5V20a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1V9.5z"/></svg>
          <span>Dashboard</span>
        </a>
        <a href="/student/courses.html" class="sidebar-link ${isAct('courses')}">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
          <span>My Courses</span>
        </a>
        <a href="/student/practices.html" class="sidebar-link ${isAct('practices')}">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
          <span>Practices</span>
        </a>
        <a href="/student/challenges.html" class="sidebar-link ${isAct('challenges')}">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>
          <span>Challenges</span>
        </a>
        <a href="/student/bookmarks.html" class="sidebar-link ${isAct('saved')}">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
          <span>Saved</span>
        </a>
      </nav>

      <div class="sidebar-footer">
        <a href="/student/profile.html" class="sidebar-link ${isAct('profile')}" title="${fullName}">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          <span>Profile</span>
        </a>
        <button onclick="logout()" class="sidebar-link" type="button">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
          <span>Logout</span>
        </button>
      </div>
    `;
  }

  function syncTopUserChip() {
    const { fullName, initials } = getUserInfo();
    const nameEls = document.querySelectorAll('#topUserName');
    nameEls.forEach(el => { el.textContent = fullName; });

    const initialEls = document.querySelectorAll('#topUserInitials');
    initialEls.forEach(el => { el.textContent = initials; });
  }

  function renderSidebar() {
    const activeNav = getActiveNav();
    let mount = document.getElementById('sidebarMount') || document.querySelector('aside.app-sidebar');

    if (!mount) {
      const appShell = document.querySelector('.app-shell');
      if (appShell) {
        mount = document.createElement('aside');
        mount.className = 'app-sidebar';
        mount.id = 'sidebarMount';
        appShell.prepend(mount);
      }
    }

    if (mount) {
      mount.innerHTML = buildSidebarHtml(activeNav);
    }

    syncTopUserChip();
  }

  // Run immediately and on DOMContentLoaded
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', renderSidebar);
  } else {
    renderSidebar();
  }

  window.renderEduNestSidebar = renderSidebar;
})();
