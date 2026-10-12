// Diagnostic Assessment, Concept Practice & Topic Quiz Engine (Phase 3)

let currentQuiz = null;
let currentQuestionIndex = 0;
let selectedAnswers = {};
let flaggedQuestions = new Set();
let timerInterval = null;

// Fallback questions matching reference design if API returns empty
const defaultQuizQuestions = [
  {
    id: 1,
    topicTitle: 'Java Programming Fundamentals',
    questionText: 'Which data type is used to store whole numbers in Java?',
    optionA: 'int',
    optionB: 'float',
    optionC: 'boolean',
    optionD: 'char',
    correctOption: 'A',
    explanation: 'The `int` keyword declares a 32-bit signed two\'s complement integer in Java.'
  },
  {
    id: 2,
    topicTitle: 'Java Programming Fundamentals',
    questionText: 'What is the default value of a boolean variable in Java?',
    optionA: 'true',
    optionB: 'false',
    optionC: 'null',
    optionD: '0',
    correctOption: 'B',
    explanation: 'The default boolean value for primitive fields in Java is `false`.'
  },
  {
    id: 3,
    topicTitle: 'Object-Oriented Design',
    questionText: 'Which keyword is used to create a class in Java?',
    optionA: 'class',
    optionB: 'new',
    optionC: 'struct',
    optionD: 'define',
    correctOption: 'A',
    explanation: 'The `class` keyword is used to declare a class definition in Java.'
  },
  {
    id: 4,
    topicTitle: 'Object-Oriented Design',
    questionText: 'Which keyword is used to instantiate a new object in Java?',
    optionA: 'create',
    optionB: 'alloc',
    optionC: 'new',
    optionD: 'instance',
    correctOption: 'C',
    explanation: 'The `new` keyword allocates memory dynamically on the heap for a new object instance.'
  },
  {
    id: 5,
    topicTitle: 'Object-Oriented Design',
    questionText: 'Which access modifier makes a member accessible only within its own class?',
    optionA: 'public',
    optionB: 'protected',
    optionC: 'private',
    optionD: 'default',
    correctOption: 'C',
    explanation: '`private` members cannot be accessed directly from any code outside their declaring class.'
  },
  {
    id: 6,
    topicTitle: 'Java Control Flow',
    questionText: 'Which statement is used to exit early from a loop in Java?',
    optionA: 'stop',
    optionB: 'break',
    optionC: 'exit',
    optionD: 'return',
    correctOption: 'B',
    explanation: 'The `break` statement immediately terminates the loop body.'
  },
  {
    id: 7,
    topicTitle: 'Methods & Return Types',
    questionText: 'What keyword indicates that a method does not return any value in Java?',
    optionA: 'static',
    optionB: 'null',
    optionC: 'void',
    optionD: 'empty',
    correctOption: 'C',
    explanation: '`void` signifies that the method executes without returning a data value.'
  },
  {
    id: 8,
    topicTitle: 'Arrays & Memory',
    questionText: 'In Java, array indexing starts from which index?',
    optionA: '0',
    optionB: '1',
    optionC: '-1',
    optionD: 'Any number configured',
    correctOption: 'A',
    explanation: 'Java arrays are zero-indexed, starting from index 0.'
  },
  {
    id: 9,
    topicTitle: 'Object-Oriented Design',
    questionText: 'Which keyword enables a subclass to inherit from a superclass in Java?',
    optionA: 'implements',
    optionB: 'inherits',
    optionC: 'extends',
    optionD: 'super',
    correctOption: 'C',
    explanation: 'The `extends` keyword establishes class inheritance in Java.'
  },
  {
    id: 10,
    topicTitle: 'Exception Handling',
    questionText: 'Which block in Java always executes regardless of whether an exception occurred?',
    optionA: 'catch',
    optionB: 'finally',
    optionC: 'try',
    optionD: 'throw',
    correctOption: 'B',
    explanation: 'The `finally` block always executes to ensure resource cleanup.'
  }
];

function shuffleArray(arr) {
  const copy = [...arr];
  for (let i = copy.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy;
}

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

  currentQuestionIndex = 0;
  selectedAnswers = {};
  flaggedQuestions = new Set();

  try {
    currentQuiz = await apiFetch(endpoint);
  } catch (err) {
    console.warn('API quiz fetch fallback:', err);
    currentQuiz = {
      quizId: 1,
      title: mode === 'DIAGNOSTIC' ? 'Java Programming - Diagnostic Assessment' : 'Java Basics Quiz',
      timeLimitMinutes: mode === 'DIAGNOSTIC' ? 15 : null,
      questions: defaultQuizQuestions
    };
  }

  if (!currentQuiz || !currentQuiz.questions || currentQuiz.questions.length === 0) {
    currentQuiz = {
      quizId: 1,
      title: mode === 'DIAGNOSTIC' ? 'Java Programming - Diagnostic Assessment' : 'Java Basics Quiz',
      timeLimitMinutes: mode === 'DIAGNOSTIC' ? 15 : null,
      questions: shuffleArray(defaultQuizQuestions)
    };
  } else {
    // Randomize questions order so student doesn't see identical repeated order
    currentQuiz.questions = shuffleArray(currentQuiz.questions);
  }

  currentQuiz.isPractice = (mode === 'PRACTICE');

  const titleEl = document.getElementById('quizTitle');
  if (titleEl) titleEl.textContent = currentQuiz.title || 'Java Basics Quiz';

  // Timer or Untimed Pill
  const timerPill = document.getElementById('quizTimer');
  const timerTextEl = document.getElementById('quizTimerText');
  if (currentQuiz.timeLimitMinutes && mode !== 'PRACTICE') {
    startCountdown(currentQuiz.timeLimitMinutes * 60);
  } else if (timerTextEl) {
    timerTextEl.innerHTML = `🌱 Untimed &bull; Take your time`;
  }

  renderCurrentQuestion(currentQuiz.isPractice);
}

function startCountdown(secondsRemaining) {
  const timerTextEl = document.getElementById('quizTimerText');
  const timerPill = document.getElementById('quizTimer');
  if (!timerTextEl) return;
  if (timerPill) timerPill.classList.add('timed');

  const updateDisplay = () => {
    const mins = Math.floor(secondsRemaining / 60);
    const secs = secondsRemaining % 60;
    timerTextEl.textContent = `⏱ ${mins}:${secs < 10 ? '0' : ''}${secs}`;
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

function renderCurrentQuestion(isPracticeMode) {
  if (!currentQuiz || !currentQuiz.questions || currentQuiz.questions.length === 0) return;
  const q = currentQuiz.questions[currentQuestionIndex];
  const total = currentQuiz.questions.length;
  const qNum = currentQuestionIndex + 1;
  const pct = Math.round((qNum / total) * 100);

  // Update progress text & track
  const progressText = document.getElementById('questionProgressText');
  if (progressText) progressText.textContent = `Question ${qNum} of ${total}`;
  const progressPct = document.getElementById('questionProgressPct');
  if (progressPct) progressPct.textContent = `${pct}% Complete`;
  const progressBar = document.getElementById('questionProgressBar');
  if (progressBar) progressBar.style.width = `${pct}%`;

  // Update breadcrumb
  const breadcrumbEl = document.getElementById('quizBreadcrumbText');
  if (breadcrumbEl) {
    const topicPart = q.topicTitle ? q.topicTitle.toUpperCase() : 'JAVA PROGRAMMING FUNDAMENTALS';
    breadcrumbEl.innerHTML = `ASSESSMENT &bull; ${topicPart}`;
  }

  // Update Prev / Next buttons
  const prevBtn = document.getElementById('prevQuestionBtn');
  if (prevBtn) {
    prevBtn.disabled = currentQuestionIndex === 0;
  }
  const nextBtn = document.getElementById('nextQuestionBtn');
  if (nextBtn) {
    if (currentQuestionIndex === total - 1) {
      nextBtn.innerHTML = `<span>Submit Assessment</span> &rarr;`;
    } else {
      nextBtn.innerHTML = `<span>Next Question</span> &rarr;`;
    }
  }

  // Update Flag button
  const flagBtn = document.getElementById('flagQuestionBtn');
  const flagText = document.getElementById('flagQuestionText');
  if (flagBtn && flagText) {
    const isFlagged = flaggedQuestions.has(q.id);
    flagBtn.classList.toggle('flagged', isFlagged);
    flagText.textContent = isFlagged ? 'Flagged for review' : 'Flag for review';
  }

  // Build Options A, B, C, D
  const options = [
    { key: 'A', text: q.optionA },
    { key: 'B', text: q.optionB },
    { key: 'C', text: q.optionC },
    { key: 'D', text: q.optionD }
  ].filter(o => o.text);

  const selected = selectedAnswers[q.id];
  const cardContainer = document.getElementById('questionCardContainer');
  if (!cardContainer) return;

  const tagNum = String(qNum).padStart(2, '0');
  const moduleRef = q.topicTitle
    ? `Module: ${q.topicTitle} &bull; Section "Core Syntax and Declarations". You may revisit lecture notes at any point without losing your active attempt.`
    : `Module 2: Object-Oriented Principles &bull; Section 2.1 "Class Declarations and Type Definitions". You may revisit lecture notes at any point without losing your active attempt.`;

  cardContainer.innerHTML = `
    <div class="quiz-question-card">
      <div class="quiz-question-tag">QUESTION ${tagNum}</div>
      <h2 class="quiz-question-text">${q.questionText}</h2>

      <div class="quiz-options-list">
        ${options.map(opt => {
          const isSel = selected === opt.key;
          return `
            <div class="quiz-option-item ${isSel ? 'selected' : ''}" onclick="handleOptionSelect(${q.id}, '${opt.key}', ${isPracticeMode})">
              <div class="quiz-option-left">
                <div class="quiz-option-key">${opt.key}</div>
                <div class="quiz-option-text">${opt.text}</div>
              </div>
              <div class="quiz-option-circle">
                ${isSel ? `
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#ffffff" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                ` : ''}
              </div>
            </div>
          `;
        }).join('')}
      </div>

      ${isPracticeMode && selected ? renderPracticeFeedback(q, selected) : ''}

      <div class="quiz-study-reference">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
          <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
          <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
        </svg>
        <div class="quiz-ref-content">
          <div class="quiz-ref-title">Study Reference</div>
          <div>${moduleRef}</div>
        </div>
      </div>
    </div>
  `;
}

function handleOptionSelect(qId, optKey, isPractice) {
  selectedAnswers[qId] = optKey;
  renderCurrentQuestion(isPractice);
}

function renderPracticeFeedback(q, selectedKey) {
  if (!q.correctOption) return '';
  const isCorrect = q.correctOption.toUpperCase() === selectedKey.toUpperCase();
  const isLast = currentQuiz && currentQuiz.questions && (currentQuestionIndex === currentQuiz.questions.length - 1);
  return `
    <div style="margin-top: 1.25rem; padding: 1rem 1.25rem; border-radius: 12px; background: ${isCorrect ? '#ecfdf5' : '#fef2f2'}; border: 1px solid ${isCorrect ? '#a7f3d0' : '#fecaca'}; color: ${isCorrect ? '#065f46' : '#991b1b'}; font-size: 0.88rem; display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 0.75rem;">
      <div style="display: flex; align-items: flex-start; gap: 0.65rem; max-width: 75%;">
        <span style="font-size: 1.2rem;">${isCorrect ? '✅' : '❌'}</span>
        <div>
          <div style="font-weight: 700; margin-bottom: 0.2rem;">${isCorrect ? 'Correct!' : 'Incorrect'}</div>
          <div>${q.explanation || (isCorrect ? 'Great job! You identified the correct concept.' : `The correct option is <strong>${q.correctOption}</strong>.`)}</div>
        </div>
      </div>
      <button class="btn btn-primary btn-sm" onclick="navigateQuestion(1)" style="font-weight: 700; padding: 0.5rem 1rem;">
        ${isLast ? 'Complete Practice &rarr;' : 'Next Question &rarr;'}
      </button>
    </div>
  `;
}

function navigateQuestion(delta) {
  if (!currentQuiz || !currentQuiz.questions) return;
  const newIndex = currentQuestionIndex + delta;
  if (newIndex >= currentQuiz.questions.length) {
    submitCurrentQuiz();
    return;
  }
  if (newIndex >= 0 && newIndex < currentQuiz.questions.length) {
    currentQuestionIndex = newIndex;
    renderCurrentQuestion(currentQuiz.isPractice);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}

function toggleFlagCurrentQuestion() {
  if (!currentQuiz || !currentQuiz.questions) return;
  const q = currentQuiz.questions[currentQuestionIndex];
  if (!q) return;
  if (flaggedQuestions.has(q.id)) {
    flaggedQuestions.delete(q.id);
  } else {
    flaggedQuestions.add(q.id);
  }
  renderCurrentQuestion(currentQuiz.isPractice);
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
    alert(err.message || 'Quiz submitted');
    window.location.href = `/student/courses.html`;
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
