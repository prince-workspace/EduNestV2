// Diagnostic Assessment, Concept Practice & Topic Quiz Engine (Phase 3)

let currentQuiz = null;
let selectedAnswers = {};
let timerInterval = null;

async function initQuizPage(mode) {
  const params = new URLSearchParams(window.location.search);
  const courseId = params.get('courseId') || localStorage.getItem('edunest_activeCourseId') || 1;
  const topicId = params.get('topicId') || 1;

  let endpoint = '';
  if (mode === 'DIAGNOSTIC') {
    endpoint = `/quizzes/diagnostic/${courseId}`;
  } else if (mode === 'PRACTICE') {
    endpoint = `/quizzes/practice/${topicId}`;
  } else if (mode === 'CHAPTER_QUIZ') {
    endpoint = `/quizzes/chapter/${topicId}`;
  }

  try {
    currentQuiz = await apiFetch(endpoint);
    selectedAnswers = {};

    document.getElementById('quizTitle').textContent = currentQuiz.title;
    document.getElementById('questionCountBadge').textContent = `${currentQuiz.questions.length} Questions`;

    if (currentQuiz.timeLimitMinutes && mode !== 'PRACTICE') {
      startCountdown(currentQuiz.timeLimitMinutes * 60);
    }

    renderQuestions(mode === 'PRACTICE');
  } catch (err) {
    console.error('Failed to load quiz:', err);
  }
}

function startCountdown(secondsRemaining) {
  const timerEl = document.getElementById('quizTimer');
  if (!timerEl) return;
  timerEl.classList.remove('hidden');

  const updateDisplay = () => {
    const mins = Math.floor(secondsRemaining / 60);
    const secs = secondsRemaining % 60;
    timerEl.textContent = `⏱ ${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  updateDisplay();
  timerInterval = setInterval(() => {
    secondsRemaining--;
    updateDisplay();
    if (secondsRemaining <= 0) {
      clearInterval(timerInterval);
      submitCurrentQuiz();
    }
  }, 1000);
}

function renderQuestions(isPracticeMode) {
  const container = document.getElementById('questionsContainer');
  if (!container || !currentQuiz) return;

  container.innerHTML = currentQuiz.questions.map((q, idx) => {
    const options = [
      { key: 'A', text: q.optionA },
      { key: 'B', text: q.optionB },
      { key: 'C', text: q.optionC },
      { key: 'D', text: q.optionD }
    ].filter(o => o.text);

    return `
      <div class="card" style="margin-bottom: 1.25rem;" id="qcard-${q.id}">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.6rem;">
          <span class="card-question">Question ${idx + 1}</span>
          <span class="badge badge-student">${q.topicTitle}</span>
        </div>
        <h3 style="font-size: 1.08rem; margin-bottom: 1rem;">${q.questionText}</h3>
        <div style="display:flex; flex-direction:column; gap:0.55rem;">
          ${options.map(opt => `
            <label style="display:flex; align-items:center; gap:0.75rem; padding:0.75rem 1rem; border:1px solid var(--border); border-radius:8px; cursor:pointer;"
                   id="lbl-${q.id}-${opt.key}">
              <input type="radio" name="q-${q.id}" value="${opt.key}"
                     onchange="selectOption(${q.id}, '${opt.key}', ${isPracticeMode})" />
              <span><strong>${opt.key}.</strong> ${opt.text}</span>
            </label>
          `).join('')}
        </div>
        <div id="feedback-${q.id}" class="alert hidden" style="margin-top: 1rem; margin-bottom: 0;"></div>
      </div>
    `;
  }).join('');
}

function selectOption(questionId, optionKey, isPracticeMode) {
  selectedAnswers[questionId] = optionKey;

  if (isPracticeMode && currentQuiz) {
    const q = currentQuiz.questions.find(item => item.id === questionId);
    const feedbackEl = document.getElementById(`feedback-${questionId}`);
    if (q && feedbackEl && q.correctOption) {
      const isCorrect = q.correctOption.toUpperCase() === optionKey.toUpperCase();
      feedbackEl.className = `alert ${isCorrect ? 'alert-success' : 'alert-error'}`;
      feedbackEl.innerHTML = isCorrect
        ? `✅ <strong>Correct!</strong> ${q.explanation || ''}`
        : `❌ <strong>Not quite.</strong> Correct Answer: <strong>${q.correctOption}</strong>. ${q.explanation || ''}`;
      feedbackEl.classList.remove('hidden');
    }
  }
}

async function submitCurrentQuiz() {
  if (!currentQuiz) return;
  if (timerInterval) clearInterval(timerInterval);

  const answersList = Object.entries(selectedAnswers).map(([qId, opt]) => ({
    questionId: parseInt(qId, 10),
    selectedOption: opt
  }));

  try {
    const result = await apiFetch('/quizzes/submit', {
      method: 'POST',
      body: JSON.stringify({
        quizId: currentQuiz.quizId,
        answers: answersList
      })
    });

    window.location.href = `/student/quiz-result.html?attemptId=${result.attemptId}`;
  } catch (err) {
    alert(err.message);
  }
}

async function loadQuizResult() {
  const params = new URLSearchParams(window.location.search);
  const attemptId = params.get('attemptId');
  if (!attemptId) return;

  try {
    const result = await apiFetch(`/quizzes/attempts/${attemptId}`);

    document.getElementById('resultQuizTitle').textContent = result.quizTitle;
    document.getElementById('overallScoreDisplay').textContent =
      `${result.totalScore} / ${result.totalQuestions} (${result.overallPercentage}%)`;
    document.getElementById('backToCourseBtn').href =
      `/student/course-detail.html?id=${result.courseId}`;

    // Render Topic-by-Topic Breakdown
    const topicGrid = document.getElementById('topicBreakdownGrid');
    topicGrid.innerHTML = result.topicBreakdown.map(tb => {
      const colorMap = {
        WEAK: '#ef4444',
        NEEDS_WORK: '#f59e0b',
        STRONG: '#10b981',
        MASTERED: '#8b5cf6'
      };
      const barColor = colorMap[tb.statusLabel] || '#4f46e5';

      return `
        <div class="card">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.5rem;">
            <strong>${tb.topicTitle}</strong>
            <span class="badge" style="background:${barColor}20; color:${barColor};">${tb.statusLabel}</span>
          </div>
          <div style="background:#e2e8f0; height:10px; border-radius:999px; overflow:hidden; margin:0.6rem 0;">
            <div style="width:${tb.percentage}%; background:${barColor}; height:100%;"></div>
          </div>
          <div style="display:flex; justify-content:space-between; font-size:0.86rem; color:var(--text-muted);">
            <span>${tb.correct} of ${tb.total} correct (${tb.percentage}%)</span>
            ${tb.percentage < 70
              ? `<a href="/student/practice.html?topicId=${tb.topicId}" style="color:var(--primary); font-weight:600;">Practice Topic →</a>`
              : `<span>Strong Concept ✓</span>`
            }
          </div>
        </div>
      `;
    }).join('');

    // Render Question-by-Question Review
    const reviewContainer = document.getElementById('answerReviewContainer');
    reviewContainer.innerHTML = result.answerDetails.map((ad, idx) => `
      <div class="card" style="margin-bottom:1rem; border-left:4px solid ${ad.correct ? '#10b981' : '#ef4444'};">
        <div style="display:flex; justify-content:space-between; margin-bottom:0.4rem;">
          <span class="card-question">Q${idx + 1} · ${ad.topicTitle}</span>
          <span style="font-weight:700; color:${ad.correct ? '#047857' : '#b91c1c'};">
            ${ad.correct ? '✓ Correct' : '✗ Incorrect'}
          </span>
        </div>
        <p style="font-weight:600; margin-bottom:0.5rem;">${ad.questionText}</p>
        <p style="font-size:0.9rem; color:var(--text-muted);">
          Your Answer: <strong>${ad.selectedOption || 'Skipped'}</strong> ·
          Correct Option: <strong>${ad.correctOption}</strong>
        </p>
        ${ad.explanation ? `<p style="font-size:0.88rem; color:var(--primary); margin-top:0.4rem;">💡 ${ad.explanation}</p>` : ''}
      </div>
    `).join('');
  } catch (err) {
    console.error('Failed to load quiz result:', err);
  }
}
