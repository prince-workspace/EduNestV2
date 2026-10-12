// Topic Mastery, Weak-Topic Detection & Personalized Learning Path Frontend Logic (Phase 4)

const MASTERY_COLORS = {
  WEAK: '#ef4444',
  NEEDS_WORK: '#f59e0b',
  STRONG: '#10b981',
  MASTERED: '#8b5cf6'
};

const MASTERY_LABELS = {
  WEAK: 'Needs Urgent Practice',
  NEEDS_WORK: 'Needs Practice',
  STRONG: 'Strong',
  MASTERED: 'Mastered'
};

// Metadata for topic cards matching reference design
const TOPIC_CARD_METAS = {
  1: {
    description: "Primitive types, type casting, and variable scope are all solid. Reviewed recently with 100% quiz accuracy.",
    metaText: "Verified by 2 quizzes",
    metaIcon: "check"
  },
  2: {
    description: "For-each loops and conditional branching mastered. Practice questions completed with high speed.",
    metaText: "Verified by 1 quiz & 1 homework",
    metaIcon: "check"
  },
  3: {
    practiceAdvice: "Practice 2D array indexing and row-major memory allocation. You scored 95% on the quick review previously.",
    metaText: "Last practiced 3 days ago",
    metaIcon: "clock"
  },
  4: {
    missedConcepts: "Constructor overloading and access modifiers ( private/protected ).",
    drillsNeeded: 2,
    metaText: "Recommended study: ~10 mins",
    metaIcon: "clock"
  },
  5: {
    missedConcepts: "super(...) constructor chaining and dynamic runtime method overriding (@Override).",
    drillsNeeded: 3,
    metaText: "Recommended study: ~15 mins",
    metaIcon: "clock"
  },
  6: {
    description: "Robust try-catch-finally blocks and HashMap key lookups verified across all practice drills.",
    metaText: "Verified by 2 quizzes & 1 practice",
    metaIcon: "check"
  }
};

// Knowledge base mapping of concepts for topic diagnostic reports
const TOPIC_CONCEPT_GUIDES = {
  1: {
    missedConcepts: "Type casting (widening vs narrowing), primitive vs reference memory layout, and 'final' constant immutability.",
    whatIsWeak: "Score in practice drills and primitive boundary edge cases.",
    whatNeedsWork: "Review explicit narrowing type casting (int) 7.9 and memory ranges of byte/int primitives before retaking the quiz.",
    drillsNeeded: 2
  },
  2: {
    missedConcepts: "'do-while' bottom-condition evaluations, 'break' vs 'continue' execution flow, and off-by-one loop boundaries.",
    whatIsWeak: "Loop termination conditions and nested iteration logic.",
    whatNeedsWork: "Practice the difference between skip ('continue') and early loop exit ('break') in iteration drills.",
    drillsNeeded: 2
  },
  3: {
    missedConcepts: "ArrayIndexOutOfBoundsException handling, 2D matrix coordinate traversal [row][col], and length property vs length() method.",
    whatIsWeak: "Boundary indexing checks and 0-based array allocation defaults.",
    whatNeedsWork: "Practice 2D array traversal drills and remember .length is a final property, not a method call.",
    drillsNeeded: 2
  },
  4: {
    missedConcepts: "Constructor overloading and access modifiers ( private/protected ).",
    whatIsWeak: "Private encapsulation visibility rules and parameter matching in overloaded constructors.",
    whatNeedsWork: "You need just 2 solved drills to get back on track. Practice field encapsulation with getters/setters.",
    drillsNeeded: 2
  },
  5: {
    missedConcepts: "'super(...)' constructor chaining, dynamic runtime method overriding (@Override), and single inheritance rules.",
    whatIsWeak: "Parent constructor invocation ordering and runtime polymorphism dispatch.",
    whatNeedsWork: "Practice invoking superclass constructors and verifying @Override method signatures in practice mode.",
    drillsNeeded: 3
  },
  6: {
    missedConcepts: "'try-catch-finally' lifecycle execution, checked vs unchecked exceptions, and HashMap key-value lookups.",
    whatIsWeak: "Guaranteed execution of finally blocks and null key handling in Collections.",
    whatNeedsWork: "Complete 2 drills on exception propagation and Map interface key-value operations.",
    drillsNeeded: 2
  }
};

let currentProfileCourseId = 1;
let currentProfileTopics = [];
let currentTopicFilter = 'ALL';

async function loadKnowledgeProfilePage() {
  const params = new URLSearchParams(window.location.search);
  const courseId = params.get('courseId') || localStorage.getItem('edunest_activeCourseId') || 1;
  currentProfileCourseId = courseId;

  try {
    const profile = await apiFetch(`/students/me/courses/${courseId}/knowledge-profile`);
    currentProfileTopics = profile.topics || [];

    const courseTitleEl = document.getElementById('profileCourseTitle');
    if (courseTitleEl) courseTitleEl.textContent = `${profile.courseTitle} — Knowledge Profile`;
    
    const overallPctEl = document.getElementById('overallMasteryPct');
    if (overallPctEl) overallPctEl.textContent = `${profile.overallCourseMastery}%`;
    
    const weakEl = document.getElementById('weakCountVal');
    if (weakEl) weakEl.textContent = profile.weakCount;
    
    const needsWorkEl = document.getElementById('needsWorkCountVal');
    if (needsWorkEl) needsWorkEl.textContent = profile.needsWorkCount;
    
    const strongEl = document.getElementById('strongCountVal');
    if (strongEl) strongEl.textContent = profile.strongCount;
    
    const masteredEl = document.getElementById('masteredCountVal');
    if (masteredEl) masteredEl.textContent = profile.masteredCount;

    // Update filter pills counters
    const allCount = currentProfileTopics.length;
    const weakCount = currentProfileTopics.filter(t => t.masteryScore < 70).length;
    const strongCount = currentProfileTopics.filter(t => t.masteryScore >= 70 && t.masteryScore < 90).length;
    const masteredCount = currentProfileTopics.filter(t => t.masteryScore >= 90).length;

    const fAll = document.getElementById('filterAllCount');
    if (fAll) fAll.textContent = allCount;
    const fWeak = document.getElementById('filterWeakCount');
    if (fWeak) fWeak.textContent = weakCount;
    const fStrong = document.getElementById('filterStrongCount');
    if (fStrong) fStrong.textContent = strongCount;
    const fMastered = document.getElementById('filterMasteredCount');
    if (fMastered) fMastered.textContent = masteredCount;

    renderTopicCards();
  } catch (err) {
    console.error('Error loading knowledge profile:', err);
  }
}

function setTopicFilter(filterType, btnEl) {
  currentTopicFilter = filterType;
  document.querySelectorAll('.topic-filter-pill').forEach(el => el.classList.remove('active'));
  if (btnEl) btnEl.classList.add('active');
  renderTopicCards();
}

function renderTopicCards() {
  const container = document.getElementById('topicMasteryList');
  if (!container) return;

  // Filter topics based on active tab
  let filtered = [...currentProfileTopics];
  if (currentTopicFilter === 'WEAK') {
    filtered = filtered.filter(t => t.masteryScore < 70);
  } else if (currentTopicFilter === 'STRONG') {
    filtered = filtered.filter(t => t.masteryScore >= 70 && t.masteryScore < 90);
  } else if (currentTopicFilter === 'MASTERED') {
    filtered = filtered.filter(t => t.masteryScore >= 90);
  }

  // Sort in ascending order (Topic 1, Topic 2, Topic 3, ...)
  filtered.sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0) || (a.topicId - b.topicId));

  if (filtered.length === 0) {
    container.innerHTML = `
      <div class="card" style="text-align: center; padding: 2.5rem 1rem; color: #64748b;">
        <p style="font-size: 1rem; font-weight: 600;">No topics found in this category.</p>
        <button class="btn btn-outline btn-sm" onclick="setTopicFilter('ALL', document.querySelector('.topic-filter-pill'))" style="margin-top: 0.75rem;">Show All Topics</button>
      </div>
    `;
    return;
  }

  // Render cards matching user reference design
  container.innerHTML = filtered.map(t => {
    const meta = TOPIC_CARD_METAS[t.orderIndex] || {
      description: `Core concepts and problem-solving rules for ${t.topicTitle}.`,
      metaText: "Verified by quiz",
      metaIcon: "check"
    };

    let badgeClass = 'badge-needs-practice';
    let badgeText = 'Needs Practice';
    let scoreColor = '#1e293b';

    if (t.masteryScore < 40) {
      badgeClass = 'badge-urgent';
      badgeText = 'Needs Urgent Practice';
      scoreColor = '#dc2626';
    } else if (t.masteryScore < 70) {
      badgeClass = 'badge-needs-practice';
      badgeText = 'Needs Practice';
      scoreColor = '#1e293b';
    } else if (t.masteryScore < 90) {
      badgeClass = 'badge-strong';
      badgeText = 'Strong';
      scoreColor = '#2563eb';
    } else {
      badgeClass = 'badge-mastered';
      badgeText = 'Mastered';
      scoreColor = '#059669';
    }

    // Callout or description body matching image
    let bodyHtml = '';
    if (t.masteryScore < 40) {
      const missed = meta.missedConcepts || "Constructor overloading and access modifiers ( private/protected ).";
      const drills = meta.drillsNeeded || 2;
      bodyHtml = `
        <div class="topic-callout-box urgent-box">
          <div class="topic-callout-icon urgent-icon">!</div>
          <div class="topic-callout-text">
            <strong>Missed concepts:</strong> ${missed} You need just ${drills} solved drills to get back on track.
          </div>
        </div>
      `;
    } else if (t.masteryScore < 70) {
      const advice = meta.practiceAdvice || "Practice 2D array indexing and row-major memory allocation. You scored 95% on the quick review previously.";
      bodyHtml = `
        <div class="topic-callout-box practice-box">
          <div class="topic-callout-icon practice-icon">?</div>
          <div class="topic-callout-text">
            <strong>Almost there!</strong> ${advice}
          </div>
        </div>
      `;
    } else {
      bodyHtml = `
        <p class="topic-card-description">
          ${meta.description || 'Primitive types, type casting, and variable scope are all solid. Reviewed recently with 100% quiz accuracy.'}
        </p>
      `;
    }

    return `
      <div class="topic-card-v2">
        <div class="topic-card-header">
          <div class="topic-tag-wrap">
            <span class="topic-num-label">TOPIC ${t.orderIndex}</span>
            <span class="topic-status-badge ${badgeClass}">${badgeText}</span>
          </div>
          <div class="topic-pct-wrap">
            <div class="topic-pct-number" style="color: ${scoreColor};">${t.masteryScore}%</div>
            <div class="topic-pct-label">Mastery Level</div>
          </div>
        </div>

        <h3 class="topic-card-title">${t.topicTitle}</h3>

        <div class="topic-progress-track">
          <div class="topic-progress-fill" style="width: ${t.masteryScore}%; background: ${scoreColor};"></div>
        </div>

        ${bodyHtml}

        <div class="topic-card-footer">
          <div class="topic-card-meta">
            ${meta.metaIcon === 'clock' ? `
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
              </svg>
            ` : `
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            `}
            <span>${meta.metaText || 'Verified by quiz'}</span>
          </div>
          <button class="topic-view-breakdown-btn" onclick="openTopicReviewModal(${t.topicId})">
            View breakdown &rsaquo;
          </button>
        </div>
      </div>
    `;
  }).join('');
}

// Open Topic Review Modal & Generate Diagnostic Report Pop-up
function openTopicReviewModal(topicId) {
  const topic = currentProfileTopics.find(t => t.topicId === Number(topicId));
  if (!topic) {
    console.warn('Topic not found for review modal:', topicId);
    return;
  }

  const modalBackdrop = document.getElementById('topicReviewModalBackdrop');
  if (!modalBackdrop) return;

  const color = MASTERY_COLORS[topic.masteryLevel] || '#4f46e5';
  const label = MASTERY_LABELS[topic.masteryLevel] || topic.masteryLevel;
  const fmt = val => (val !== null && val !== undefined) ? `${val}%` : '—';

  // 1. Header Information
  const badgeEl = document.getElementById('modalTopicBadge');
  if (badgeEl) badgeEl.textContent = `TOPIC ${topic.orderIndex || 1} REPORT`;

  const masteryBadgeEl = document.getElementById('modalTopicMasteryBadge');
  if (masteryBadgeEl) {
    masteryBadgeEl.textContent = label;
    masteryBadgeEl.style.background = `${color}20`;
    masteryBadgeEl.style.color = color;
  }

  const titleEl = document.getElementById('modalTopicTitle');
  if (titleEl) titleEl.textContent = topic.topicTitle;

  // 2. Score Bar & Progress
  const scoreTextEl = document.getElementById('modalTopicScoreText');
  if (scoreTextEl) {
    scoreTextEl.textContent = `${topic.masteryScore}%`;
    scoreTextEl.style.color = color;
  }

  const progressBarEl = document.getElementById('modalTopicProgressBar');
  if (progressBarEl) {
    progressBarEl.style.width = `${topic.masteryScore}%`;
    progressBarEl.style.background = color;
  }

  // 3. Formula 3-Metric Grid
  const practiceEl = document.getElementById('modalPracticeScore');
  if (practiceEl) practiceEl.textContent = fmt(topic.practiceScore);

  const quizEl = document.getElementById('modalQuizScore');
  if (quizEl) quizEl.textContent = fmt(topic.quizScore);

  const revisionEl = document.getElementById('modalRevisionScore');
  if (revisionEl) revisionEl.textContent = fmt(topic.revisionScore);

  // 4. Report Callout: What is Weak, What Needs Work, Missed Concepts
  const calloutContainer = document.getElementById('modalReportCallout');
  if (calloutContainer) {
    const guide = TOPIC_CONCEPT_GUIDES[topic.orderIndex] || {
      missedConcepts: `Core patterns and problem-solving rules for ${topic.topicTitle}.`,
      whatIsWeak: `Scores in targeted drills and assessment accuracy.`,
      whatNeedsWork: `Complete 2 solved drills to get back on track.`,
      drillsNeeded: 2
    };

    if (topic.masteryScore < 40) {
      calloutContainer.innerHTML = `
        <div class="review-callout weak">
          <div class="review-callout-icon-wrap">!</div>
          <div class="review-callout-content">
            <div class="review-callout-title">
              Missed concepts: ${guide.missedConcepts} You need just ${guide.drillsNeeded} solved drills to get back on track.
            </div>
            <div class="review-callout-details">
              <div class="review-callout-detail-item">
                <span class="review-callout-detail-label">🔴 What is weak:</span>
                <span>${guide.whatIsWeak}</span>
              </div>
              <div class="review-callout-detail-item">
                <span class="review-callout-detail-label">⚡ What needs work:</span>
                <span>${guide.whatNeedsWork}</span>
              </div>
            </div>
          </div>
        </div>
      `;
    } else if (topic.masteryScore < 70) {
      calloutContainer.innerHTML = `
        <div class="review-callout almost-there">
          <div class="review-callout-icon-wrap">?</div>
          <div class="review-callout-content">
            <div class="review-callout-title">
              Almost there! Complete 1 more practice session or topic drill to solidify your concepts and unlock Mastered level.
            </div>
            <div class="review-callout-details">
              <div class="review-callout-detail-item">
                <span class="review-callout-detail-label">🔵 What is weak:</span>
                <span>${guide.whatIsWeak}</span>
              </div>
              <div class="review-callout-detail-item">
                <span class="review-callout-detail-label">🎯 What needs work:</span>
                <span>${guide.whatNeedsWork}</span>
              </div>
            </div>
          </div>
        </div>
      `;
    } else if (topic.masteryScore < 90) {
      calloutContainer.innerHTML = `
        <div class="review-callout almost-there">
          <div class="review-callout-icon-wrap">?</div>
          <div class="review-callout-content">
            <div class="review-callout-title">
              Almost there! Complete 1 more practice session or topic drill to solidify your concepts and unlock Mastered level.
            </div>
            <div class="review-callout-details">
              <div class="review-callout-detail-item">
                <span class="review-callout-detail-label">🔵 What is strong:</span>
                <span>High accuracy on concept drills and topic fundamentals (${topic.masteryScore}%).</span>
              </div>
              <div class="review-callout-detail-item">
                <span class="review-callout-detail-label">🎯 What needs work:</span>
                <span>Complete spaced revision or a quick quiz drill to lock in long-term retention.</span>
              </div>
            </div>
          </div>
        </div>
      `;
    } else {
      calloutContainer.innerHTML = `
        <div class="review-callout mastered">
          <div class="review-callout-icon-wrap">★</div>
          <div class="review-callout-content">
            <div class="review-callout-title">
              Verified comprehension: Strong mastery across all tested dimensions. Ready for advanced challenges!
            </div>
            <div class="review-callout-details">
              <div class="review-callout-detail-item">
                <span class="review-callout-detail-label">🟢 Comprehension:</span>
                <span>Fully verified (${topic.masteryScore}%). Consistent performance across practice, quiz, and revision.</span>
              </div>
              <div class="review-callout-detail-item">
                <span class="review-callout-detail-label">🏆 Recommendation:</span>
                <span>Maintain your streak with spaced revision or proceed to hands-on coding challenges.</span>
              </div>
            </div>
          </div>
        </div>
      `;
    }
  }

  // 5. Action Hub: 3 Interactive Options (Practice, Quiz, Revision)
  const actionsGrid = document.getElementById('modalActionsGrid');
  if (actionsGrid) {
    actionsGrid.innerHTML = `
      <a href="/student/practice.html?topicId=${topic.topicId}" class="review-action-card action-practice">
        <div class="review-action-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
            <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" fill="currentColor"/>
          </svg>
        </div>
        <div class="review-action-info">
          <div class="review-action-name-row">
            <span class="review-action-name">Practice</span>
            <span class="review-action-pill">25% wt</span>
          </div>
          <div class="review-action-desc">Targeted untimed concept drills</div>
        </div>
      </a>

      <a href="/student/quiz.html?topicId=${topic.topicId}" class="review-action-card action-quiz">
        <div class="review-action-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/>
            <line x1="16" y1="17" x2="8" y2="17"/>
            <polyline points="10 9 9 9 8 9"/>
          </svg>
        </div>
        <div class="review-action-info">
          <div class="review-action-name-row">
            <span class="review-action-name">Quiz</span>
            <span class="review-action-pill" style="background:#dbeafe; color:#1e40af; font-weight:800;">50% wt</span>
          </div>
          <div class="review-action-desc">Core graded topic assessment</div>
        </div>
      </a>

      <a href="/student/revision.html?topicId=${topic.topicId}" class="review-action-card action-revision">
        <div class="review-action-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
            <polyline points="23 4 23 10 17 10"/>
            <polyline points="1 20 1 14 7 14"/>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
          </svg>
        </div>
        <div class="review-action-info">
          <div class="review-action-name-row">
            <span class="review-action-name">Revision</span>
            <span class="review-action-pill">25% wt</span>
          </div>
          <div class="review-action-desc">Spaced repetition memory review</div>
        </div>
      </a>
    `;
  }

  // Optional: Primary Lesson link in footer
  const lessonLinkWrap = document.getElementById('modalLessonLinkWrap');
  if (lessonLinkWrap) {
    if (topic.primaryLessonId) {
      lessonLinkWrap.innerHTML = `
        <a href="/student/lesson.html?id=${topic.primaryLessonId}" class="btn btn-outline btn-sm" style="display:inline-flex; align-items:center; gap:0.4rem;">
          📖 Study Lesson Material →
        </a>
      `;
    } else {
      lessonLinkWrap.innerHTML = '';
    }
  }

  // Open modal
  modalBackdrop.classList.add('open');
  document.body.style.overflow = 'hidden';
}

function closeTopicReviewModal() {
  const modalBackdrop = document.getElementById('topicReviewModalBackdrop');
  if (modalBackdrop) {
    modalBackdrop.classList.remove('open');
  }
  document.body.style.overflow = '';
}

function handleReviewBackdropClick(event) {
  if (event.target.id === 'topicReviewModalBackdrop') {
    closeTopicReviewModal();
  }
}

// Close on Escape key
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    closeTopicReviewModal();
  }
});

function toggleAiReasoning(stepOrder) {
  const drawer = document.getElementById(`aiDrawer_${stepOrder}`);
  const btn = document.getElementById(`aiReasoningBtn_${stepOrder}`);
  if (!drawer) return;
  drawer.classList.toggle('open');
  if (btn) {
    btn.innerHTML = drawer.classList.contains('open')
      ? 'Hide Diagnostic Insights &#9652;'
      : 'View Diagnostic Insights &#9662;';
  }
}

async function loadLearningPathPage() {
  const params = new URLSearchParams(window.location.search);
  const courseId = params.get('courseId') || localStorage.getItem('edunest_activeCourseId') || 1;

  try {
    const steps = await apiFetch(`/students/me/courses/${courseId}/learning-path`);
    const container = document.getElementById('learningPathContainer');

    if (!container) return;

    if (!steps || steps.length === 0) {
      container.innerHTML = `
        <div class="card" style="text-align: center; padding: 3rem 1.5rem;">
          <h3 style="margin-bottom: 0.5rem; color: #0f172a;">No learning steps generated yet</h3>
          <p style="color: #64748b; margin-bottom: 1.25rem;">Enroll in a course and complete the Diagnostic Assessment to generate your personalized path.</p>
          <a href="/student/courses.html" class="btn btn-primary btn-sm">Explore Courses →</a>
        </div>
      `;
      return;
    }

    // Dynamic focus topic from first step
    const firstStep = steps[0];
    const focusTopic = firstStep.topicTitle || 'Arrays & Memory Layout';

    const focusTopicNameEl = document.getElementById('focusTopicName');
    if (focusTopicNameEl) focusTopicNameEl.textContent = focusTopic;

    const milestoneSeqEl = document.getElementById('milestoneSequenceTitle');
    if (milestoneSeqEl) {
      milestoneSeqEl.textContent = `Milestone Sequence (Sprint: ${focusTopic})`;
    }

    const countsSummaryEl = document.getElementById('milestoneCountsSummary');
    if (countsSummaryEl) {
      const activeCount = 1;
      const pendingCount = Math.max(0, steps.length - 2);
      const lockedCount = Math.min(1, steps.length > 2 ? 1 : 0);
      countsSummaryEl.innerHTML = `${activeCount} Active &bull; ${pendingCount} Pending &bull; ${lockedCount} Locked`;
    }

    // Step descriptions & metadata mapping matching reference design
    const getStepDetails = (step, index) => {
      if (index === 0) {
        // Step 1: Active Weak Topic Lesson
        return {
          nodeHtml: `1`,
          nodeClass: 'node-active',
          cardClass: 'card-active',
          badgesHtml: `
            <span class="badge-weak-detected">⚠️ WEAK TOPIC DETECTED (${step.masteryScore}% MASTERY)</span>
            <span class="badge-step-meta">&bull; Lesson 1 of 3</span>
            <span class="badge-priority-active">Active Priority</span>
          `,
          title: `${step.topicTitle}: Lesson 1 - Contiguous Memory & Indexing Math`,
          desc: `Understand row-major and column-major memory mapping, CPU cache line locality, and internal address resolution formula: <code>BaseAddress + (index &times; elementSize)</code>.`,
          estTime: '15 min estimated',
          theoryMeta: 'Interactive theory + 3 diagram breakdowns',
          xpReward: '🏆 +30 Mastery XP',
          hasAiReasoning: true,
          btnText: 'Begin Topic &rarr;',
          btnClass: 'btn-trail-primary'
        };
      } else if (index === 1) {
        // Step 2: Unlocked Sandbox Practice
        return {
          nodeHtml: `2`,
          nodeClass: 'node-unlocked',
          cardClass: 'card-normal',
          badgesHtml: `
            <span class="badge-sandbox">UNLOCKED SANDBOX DRILLS</span>
            <span class="badge-priority-active" style="background:#f1f5f9; color:#475569;">Difficulty: Medium</span>
            <span class="badge-step-meta">&bull; Up Next in Path</span>
          `,
          title: `Guided Memory Sandbox: Interactive Pointer & Index Simulations`,
          desc: `Test your mental model on contiguous buffer offsets through 5 live code-tracing scenarios with compiler feedback and visual stack/heap memory inspectors.`,
          estTime: '15 mins estimated',
          theoryMeta: '⚡ 5 Drills &bull; Instant validation',
          xpReward: '🎁 Rewards: +25 Mastery XP',
          leftSubtext: 'Available immediately or after Lesson 1',
          btnText: `<svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" style="vertical-align: -2px; margin-right: 4px;"><circle cx="12" cy="12" r="10"></circle><polygon points="10 8 16 12 10 16 10 8"></polygon></svg> Open Practice Sandbox`,
          btnClass: 'btn-trail-outline'
        };
      } else if (index === 2) {
        // Step 3: Checkpoint Assessment (Locked/Milestone)
        return {
          nodeHtml: `🔒`,
          nodeClass: 'node-locked',
          cardClass: 'card-locked',
          badgesHtml: `
            <span class="badge-checkpoint">CHECKPOINT ASSESSMENT</span>
            <span class="badge-step-meta">&bull; Step 3 Milestone</span>
            <span class="badge-locked-pill">&#128274; Auto-unlocks after Practice</span>
          `,
          title: `Mastery Checkpoint: Arrays & Contiguous Storage`,
          desc: `Evaluate comprehension under test constraints. Reaching the threshold promotes your skill profile to Strong.`,
          estTime: '12 mins estimated',
          theoryMeta: '📝 8 Questions &bull; 70% required',
          xpReward: '🎖 Milestone Certification',
          leftSubtext: 'Unlocks upon completing Practice Sandbox',
          btnText: 'Take Checkpoint Quiz &rarr;',
          btnClass: 'btn-trail-outline'
        };
      } else {
        // Step 4+: Challenge or next milestone
        return {
          nodeHtml: `${index + 1}`,
          nodeClass: 'node-bonus',
          cardClass: 'card-normal',
          badgesHtml: `
            <span class="badge-checkpoint">BONUS MILESTONE</span>
            <span class="badge-step-meta">&bull; Step ${index + 1}</span>
            <span class="badge-priority-active" style="background:#f5f3ff; color:#7c3aed;">High Impact</span>
          `,
          title: `${step.topicTitle}: Advanced Application & Mastery`,
          desc: `${step.recommendationText}`,
          estTime: '20 mins estimated',
          theoryMeta: '💻 Practical Coding Sandbox',
          xpReward: '💎 +50 Mastery XP',
          leftSubtext: 'Continuous progression',
          btnText: 'Continue Step &rarr;',
          btnClass: 'btn-trail-outline'
        };
      }
    };

    container.innerHTML = steps.map((step, idx) => {
      const details = getStepDetails(step, idx);

      return `
        <div class="timeline-step-row">
          <!-- Step Node Circle on Timeline -->
          <div class="timeline-step-node ${details.nodeClass}">
            ${details.nodeHtml}
          </div>

          <!-- Step Card Matching Image -->
          <div class="trail-step-card ${details.cardClass}">
            <div class="trail-card-badges">
              ${details.badgesHtml}
            </div>

            <h3 class="trail-card-title">${details.title}</h3>
            <p class="trail-card-desc">${details.desc}</p>

            <div class="trail-card-meta-row">
              <span class="trail-meta-item">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"></circle>
                  <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                ${details.estTime}
              </span>
              <span class="trail-meta-item">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                  <polyline points="14 2 14 8 20 8"></polyline>
                </svg>
                ${details.theoryMeta}
              </span>
              <span class="trail-meta-item" style="color: #059669; font-weight: 700;">
                ${details.xpReward}
              </span>
            </div>

            <div class="trail-card-action-row">
              <div>
                ${details.hasAiReasoning ? `
                  <button class="btn-ai-reasoning" id="aiReasoningBtn_${step.stepOrder}" onclick="toggleAiReasoning(${step.stepOrder})">
                    View Diagnostic Insights &#9662;
                  </button>
                ` : `
                  <span style="font-size: 0.84rem; color: #64748b;">${details.leftSubtext || ''}</span>
                `}
              </div>
              <div>
                <a href="${step.targetUrl}" class="btn-trail-action ${details.btnClass}">
                  ${details.btnText}
                </a>
              </div>
            </div>

            ${details.hasAiReasoning ? `
              <div class="ai-reasoning-drawer" id="aiDrawer_${step.stepOrder}">
                <div style="font-weight: 700; color: #0f172a; margin-bottom: 0.35rem; display: flex; align-items: center; gap: 0.4rem;">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2.2"><path d="M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm1 15h-2v-6h2zm0-8h-2V7h2z"/></svg>
                  Diagnostic Concept Insight:
                </div>
                <div>
                  Diagnostic assessment identified prerequisite conceptual gaps in <strong>${step.topicTitle}</strong> (Mastery: ${step.masteryScore}%). This milestone is recommended first so you master foundational memory models before opening downstream Object-Oriented modules.
                </div>
              </div>
            ` : ''}
          </div>
        </div>
      `;
    }).join('');
  } catch (err) {
    console.error('Error loading learning path:', err);
  }
}
