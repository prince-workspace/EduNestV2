// Spaced Revision System & Daily Learning Plan Frontend Logic (Phase 5)

async function loadRevisionPage() {
  try {
    const revisions = await apiFetch('/students/me/revision-schedule');
    const pending = revisions.filter(r => !r.completed);
    const completed = revisions.filter(r => r.completed);

    document.getElementById('pendingRevCount').textContent = pending.length;
    document.getElementById('completedRevCount').textContent = completed.length;

    const pendingContainer = document.getElementById('pendingRevisionsList');
    if (pending.length === 0) {
      pendingContainer.innerHTML = `
        <div class="card">
          <p style="color:var(--text-muted);">🎉 All caught up! No pending revision tasks right now.</p>
        </div>`;
    } else {
      pendingContainer.innerHTML = pending.map(r => `
        <div class="card" style="margin-bottom:1rem; border-left:5px solid ${r.dueOrOverdue ? '#ef4444' : '#f59e0b'}; display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:1rem;">
          <div>
            <div style="display:flex; gap:0.6rem; align-items:center; margin-bottom:0.35rem;">
              <span class="badge" style="background:${r.dueOrOverdue ? '#fee2e2' : '#fef3c7'}; color:${r.dueOrOverdue ? '#b91c1c' : '#b45309'};">
                ${r.dueOrOverdue ? 'DUE TODAY / OVERDUE' : 'UPCOMING'} · Stage ${r.intervalStage} (${stageLabel(r.intervalStage)})
              </span>
              <span style="font-size:0.85rem; color:var(--text-muted);">Due: <strong>${r.dueDate}</strong></span>
            </div>
            <h3 style="font-size:1.1rem;">${r.topicTitle}</h3>
            <p style="font-size:0.88rem; color:var(--text-muted); margin-top:0.2rem;">
              Current Topic Mastery: <strong>${r.currentMasteryScore}%</strong> · Revision Weight: <strong>20%</strong>
            </p>
          </div>
          <div style="display:flex; gap:0.5rem; align-items:center;">
            <a href="/student/practice.html?topicId=${r.topicId}" class="btn btn-outline btn-sm">Quick Practice</a>
            <button class="btn btn-primary btn-sm" onclick="markRevisionComplete(${r.id})">
              ✓ Complete Revision Review
            </button>
          </div>
        </div>
      `).join('');
    }

    const completedContainer = document.getElementById('completedRevisionsList');
    if (completed.length === 0) {
      completedContainer.innerHTML = `<p style="color:var(--text-muted);">No completed revisions yet.</p>`;
    } else {
      completedContainer.innerHTML = completed.map(r => `
        <div class="card" style="margin-bottom:0.75rem; opacity:0.88; display:flex; justify-content:space-between; align-items:center;">
          <div>
            <strong>${r.topicTitle}</strong>
            <span style="font-size:0.85rem; color:var(--text-muted); margin-left:0.5rem;">
              Stage ${r.intervalStage} completed · Mastery: ${r.currentMasteryScore}%
            </span>
          </div>
          <span class="badge" style="background:#d1fae5; color:#047857;">✓ Completed</span>
        </div>
      `).join('');
    }
  } catch (err) {
    console.error('Failed to load revisions:', err);
  }
}

function stageLabel(stage) {
  if (stage === 1) return '1-Day Interval';
  if (stage === 2) return '3-Day Interval';
  return '7-Day Interval';
}

async function markRevisionComplete(revisionId) {
  try {
    const updatedMastery = await apiFetch(`/students/me/revision-schedule/${revisionId}/complete`, {
      method: 'POST',
      body: JSON.stringify({ score: 95.0 })
    });
    alert(`Revision completed! Updated ${updatedMastery.topicTitle} mastery to ${updatedMastery.masteryScore}% (${updatedMastery.masteryLevel}).`);
    await loadRevisionPage();
  } catch (err) {
    alert(err.message);
  }
}

async function loadDailyPlanPage() {
  const courseId = localStorage.getItem('edunest_activeCourseId') || 1;

  try {
    const [plan, sessions] = await Promise.all([
      apiFetch(`/students/me/daily-plan?courseId=${courseId}`),
      apiFetch('/students/me/sessions').catch(() => [])
    ]);

    const planContainer = document.getElementById('dailyPlanContainer');
    const totalMinutes = plan.reduce((acc, item) => acc + item.estimatedMinutes, 0);
    document.getElementById('totalDailyMinutes').textContent = `⏱ ~${totalMinutes} mins total`;

    if (plan.length === 0) {
      planContainer.innerHTML = `<div class="card"><p>Your daily plan is empty. Enroll in a course to get started!</p></div>`;
    } else {
      planContainer.innerHTML = plan.map(item => `
        <div class="card" style="margin-bottom:1rem; display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:1rem;">
          <div>
            <div style="display:flex; gap:0.6rem; align-items:center; margin-bottom:0.35rem;">
              <span class="badge badge-student">Task ${item.order} · ${item.priorityBadge}</span>
              <span style="font-size:0.85rem; color:var(--text-muted);">⏱ ${item.estimatedMinutes} mins</span>
            </div>
            <h3 style="font-size:1.08rem;">${item.title}</h3>
            <p style="font-size:0.9rem; color:var(--text-muted); margin-top:0.2rem;">${item.subtitle}</p>
          </div>
          <a href="${item.targetUrl}" class="btn btn-primary btn-sm">Start Task →</a>
        </div>
      `).join('');
    }

    const sessionsContainer = document.getElementById('sessionsHistoryList');
    if (sessions.length === 0) {
      sessionsContainer.innerHTML = `<p style="color:var(--text-muted);">No study sessions logged yet.</p>`;
    } else {
      sessionsContainer.innerHTML = sessions.map(s => `
        <div class="card" style="margin-bottom:0.75rem; display:flex; justify-content:space-between; align-items:center;">
          <div>
            <strong>${s.courseTitle}</strong>
            <span class="badge badge-student" style="margin-left:0.5rem;">${s.activityType}</span>
          </div>
          <span style="font-weight:700; color:var(--primary);">⏱ ${s.durationMinutes} mins</span>
        </div>
      `).join('');
    }
  } catch (err) {
    console.error('Failed to load daily plan:', err);
  }
}

async function submitStudySession(e) {
  e.preventDefault();
  const courseId = parseInt(localStorage.getItem('edunest_activeCourseId') || '1', 10);
  const durationMinutes = parseInt(document.getElementById('sessionMinutes').value, 10);
  const activityType = document.getElementById('sessionActivity').value;

  try {
    await apiFetch('/students/me/sessions', {
      method: 'POST',
      body: JSON.stringify({ courseId, durationMinutes, activityType })
    });
    await loadDailyPlanPage();
  } catch (err) {
    alert(err.message);
  }
}
