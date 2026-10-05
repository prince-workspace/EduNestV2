// Topic Mastery, Weak-Topic Detection & Personalized Learning Path Frontend Logic (Phase 4)

const MASTERY_COLORS = {
  WEAK: '#ef4444',
  NEEDS_WORK: '#f59e0b',
  STRONG: '#10b981',
  MASTERED: '#8b5cf6'
};

async function loadKnowledgeProfilePage() {
  const params = new URLSearchParams(window.location.search);
  const courseId = params.get('courseId') || localStorage.getItem('edunest_activeCourseId') || 1;

  try {
    const profile = await apiFetch(`/students/me/courses/${courseId}/knowledge-profile`);

    document.getElementById('profileCourseTitle').textContent = `${profile.courseTitle} — Knowledge Profile`;
    document.getElementById('overallMasteryPct').textContent = `${profile.overallCourseMastery}%`;
    document.getElementById('weakCountVal').textContent = profile.weakCount;
    document.getElementById('needsWorkCountVal').textContent = profile.needsWorkCount;
    document.getElementById('strongCountVal').textContent = profile.strongCount;
    document.getElementById('masteredCountVal').textContent = profile.masteredCount;

    const container = document.getElementById('topicMasteryList');
    container.innerHTML = profile.topics.map(t => {
      const color = MASTERY_COLORS[t.masteryLevel] || '#4f46e5';
      const fmt = val => (val !== null && val !== undefined) ? `${val}%` : '—';

      return `
        <div class="card" style="margin-bottom: 1rem;">
          <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:0.5rem; margin-bottom:0.65rem;">
            <div>
              <span class="card-question">Topic ${t.orderIndex}</span>
              <h3 style="font-size:1.12rem;">${t.topicTitle}</h3>
            </div>
            <div style="display:flex; align-items:center; gap:0.75rem;">
              <span style="font-size:1.25rem; font-weight:800; color:${color};">${t.masteryScore}%</span>
              <span class="badge" style="background:${color}20; color:${color};">${t.masteryLevel}</span>
            </div>
          </div>

          <div style="background:#e2e8f0; height:12px; border-radius:999px; overflow:hidden; margin-bottom:0.85rem;">
            <div style="width:${t.masteryScore}%; background:${color}; height:100%; transition:width 0.4s;"></div>
          </div>

          <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:0.75rem; font-size:0.86rem; color:var(--text-muted);">
            <div style="display:flex; gap:1.25rem; flex-wrap:wrap;">
              <span>Diagnostic (20%): <strong>${fmt(t.diagnosticScore)}</strong></span>
              <span>Practice (20%): <strong>${fmt(t.practiceScore)}</strong></span>
              <span>Quiz (40%): <strong>${fmt(t.quizScore)}</strong></span>
              <span>Revision (20%): <strong>${fmt(t.revisionScore)}</strong></span>
            </div>
            <div style="display:flex; gap:0.5rem;">
              ${t.primaryLessonId ? `<a href="/student/lesson.html?id=${t.primaryLessonId}" class="btn btn-outline btn-sm">Lesson</a>` : ''}
              <a href="/student/practice.html?topicId=${t.topicId}" class="btn btn-outline btn-sm">Practice</a>
              <a href="/student/quiz.html?topicId=${t.topicId}" class="btn btn-primary btn-sm">Quiz</a>
            </div>
          </div>
        </div>
      `;
    }).join('');
  } catch (err) {
    console.error('Error loading knowledge profile:', err);
  }
}

async function loadLearningPathPage() {
  const params = new URLSearchParams(window.location.search);
  const courseId = params.get('courseId') || localStorage.getItem('edunest_activeCourseId') || 1;

  try {
    const steps = await apiFetch(`/students/me/courses/${courseId}/learning-path`);
    const container = document.getElementById('learningPathContainer');

    if (!steps || steps.length === 0) {
      container.innerHTML = `<div class="card"><p>No learning steps generated yet. Enroll in a course and take the Diagnostic Assessment!</p></div>`;
      return;
    }

    const priorityColors = {
      HIGH: '#ef4444',
      MEDIUM: '#f59e0b',
      LOW: '#10b981',
      BONUS: '#8b5cf6'
    };

    const actionLabels = {
      READ_LESSON: '📖 Study Lesson →',
      PRACTICE: '⚡ Start Practice →',
      TAKE_QUIZ: '📝 Take Topic Quiz →',
      REVISION: '🔄 Quick Review →',
      CHALLENGE: '🏆 View Challenge →'
    };

    container.innerHTML = steps.map(step => {
      const pColor = priorityColors[step.priority] || '#4f46e5';
      return `
        <div class="card" style="margin-bottom: 1rem; border-left: 5px solid ${pColor}; display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:1rem;">
          <div style="max-width: 680px;">
            <div style="display:flex; align-items:center; gap:0.6rem; margin-bottom:0.35rem;">
              <span class="badge" style="background:${pColor}20; color:${pColor};">Step ${step.stepOrder} · ${step.priority} PRIORITY</span>
              <span style="font-size:0.85rem; color:var(--text-muted); font-weight:600;">
                Topic: ${step.topicTitle} (${step.masteryScore}% · ${step.masteryLevel})
              </span>
            </div>
            <h3 style="font-size:1.05rem;">${step.recommendationText}</h3>
          </div>
          <a href="${step.targetUrl}" class="btn btn-primary btn-sm">
            ${actionLabels[step.actionType] || 'Continue →'}
          </a>
        </div>
      `;
    }).join('');
  } catch (err) {
    console.error('Error loading learning path:', err);
  }
}
