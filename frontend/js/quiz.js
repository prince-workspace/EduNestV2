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
  const requestedCount = parseInt(params.get('count') || params.get('limit') || 0, 10);

  const topicParam = String(topicId);
  const isAll = (topicParam.toUpperCase() === 'ALL');
  const isMulti = topicParam.includes(',');

  currentQuestionIndex = 0;
  selectedAnswers = {};
  flaggedQuestions = new Set();

  if (mode === 'FINAL_ASSESSMENT') {
    try {
      currentQuiz = await apiFetch(`/quizzes/final/${courseId}`);
    } catch (err) {
      console.error('Final assessment access error:', err);
      alert(err.message || 'Final Assessment is locked. You must complete all required course lessons first.');
      window.location.href = `/student/course-detail.html?id=${courseId}`;
      return;
    }
  } else if (mode === 'PRACTICE' && (isMulti || isAll)) {
    const topicIdList = isAll ? [1, 2, 3, 4, 5, 6] : topicParam.split(',').map(s => s.trim()).filter(Boolean);
    try {
      const fetchPromises = topicIdList.map(tId => apiFetch(`/quizzes/practice/${tId}`).catch(() => null));
      const results = await Promise.all(fetchPromises);
      let combined = [];
      let baseQuizId = null;

      results.forEach(res => {
        if (res && res.questions) {
          combined.push(...res.questions);
          if (!baseQuizId && res.quizId) baseQuizId = res.quizId;
        }
      });

      currentQuiz = {
        quizId: baseQuizId || 1,
        title: isAll ? 'Comprehensive Practice (All Course Topics)' : `${topicIdList.length} Selected Topics Practice`,
        timeLimitMinutes: null,
        questions: combined.length > 0 ? shuffleArray(combined) : shuffleArray(defaultQuizQuestions)
      };
    } catch (err) {
      console.warn('Fallback multi-topic practice:', err);
      currentQuiz = {
        quizId: 1,
        title: isAll ? 'Comprehensive Practice (All Course Topics)' : `${topicIdList.length} Selected Topics Practice`,
        timeLimitMinutes: null,
        questions: shuffleArray(defaultQuizQuestions)
      };
    }
  } else {
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
        title: (mode === 'PRACTICE') ? 'Concept Practice' : (mode === 'DIAGNOSTIC' ? 'Diagnostic Assessment' : 'Topic Assessment Quiz'),
        timeLimitMinutes: mode === 'DIAGNOSTIC' ? 15 : (mode === 'CHAPTER_QUIZ' ? 10 : null),
        questions: shuffleArray(defaultQuizQuestions)
      };
    } else {
      currentQuiz.questions = shuffleArray(currentQuiz.questions);
    }
  }

  currentQuiz.isPractice = (mode === 'PRACTICE');
  currentQuiz.mode = mode;

  if (mode === 'PRACTICE') {
    currentQuiz.timeLimitMinutes = null;
  }

  // Slice questions if a question count was specified
  if (requestedCount > 0 && currentQuiz.questions && currentQuiz.questions.length > requestedCount) {
    currentQuiz.questions = currentQuiz.questions.slice(0, requestedCount);
  }

  const titleEl = document.getElementById('quizTitle');
  if (titleEl) titleEl.textContent = currentQuiz.title || 'Java Basics Quiz';

  // Timer or Untimed Pill
  const timerPill = document.getElementById('quizTimer');
  const timerTextEl = document.getElementById('quizTimerText');
  if (currentQuiz.timeLimitMinutes && mode !== 'PRACTICE') {
    startCountdown(currentQuiz.timeLimitMinutes * 60);
  } else if (timerTextEl) {
    timerTextEl.innerHTML = `🌱 Practice Mode &bull; Untimed`;
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

function togglePracticeHint() {
  const box = document.getElementById('practiceHintBox');
  if (box) {
    box.classList.toggle('open');
  }
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
    if (currentQuiz.mode === 'FINAL_ASSESSMENT') {
      breadcrumbEl.innerHTML = `FINAL ASSESSMENT &bull; COMPREHENSIVE COURSE EXAM`;
    } else if (isPracticeMode) {
      breadcrumbEl.innerHTML = `PRACTICE &bull; ${q.topicTitle ? q.topicTitle.toUpperCase() : 'PROBLEM SOLVING'}`;
    } else {
      const topicPart = q.topicTitle ? q.topicTitle.toUpperCase() : 'TOPIC ASSESSMENT';
      breadcrumbEl.innerHTML = `ASSESSMENT &bull; ${topicPart}`;
    }
  }

  // Update Prev / Next buttons
  const prevBtn = document.getElementById('prevQuestionBtn');
  if (prevBtn) {
    prevBtn.disabled = currentQuestionIndex === 0;
  }
  const nextBtn = document.getElementById('nextQuestionBtn');
  if (nextBtn) {
    if (currentQuestionIndex === total - 1) {
      nextBtn.innerHTML = isPracticeMode ? `<span>Finish Practice</span> &rarr;` : `<span>Submit Assessment</span> &rarr;`;
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
    ? `Topic: ${q.topicTitle} &bull; Practice problem solving with immediate hints and concept walkthroughs.`
    : `Course Topic Evaluation &bull; Answer carefully to assess your conceptual understanding.`;

  // Practice Hint UI
  const hintHtml = (isPracticeMode && q.hint) ? `
    <div style="margin-bottom: 0.95rem;">
      <button type="button" class="practice-hint-btn" onclick="togglePracticeHint()">
        💡 <span>Need a Hint?</span>
      </button>
      <div id="practiceHintBox" class="practice-hint-card">
        <strong>💡 Hint:</strong> ${q.hint}
      </div>
    </div>
  ` : '';

  cardContainer.innerHTML = `
    <div class="quiz-question-card">
      <div class="quiz-question-tag">QUESTION ${tagNum} · ${isPracticeMode ? 'PRACTICE DRILL' : 'EVALUATION'}</div>
      <h2 class="quiz-question-text">${q.questionText}</h2>

      ${hintHtml}

      <div class="quiz-options-list">
        ${options.map(opt => {
          const isSel = selected === opt.key;
          let extraClass = '';
          if (isPracticeMode && selected) {
            if (opt.key === q.correctOption) {
              extraClass = 'correct';
            } else if (isSel && opt.key !== q.correctOption) {
              extraClass = 'incorrect';
            }
          } else if (isSel) {
            extraClass = 'selected';
          }

          return `
            <div class="quiz-option-item ${extraClass}" onclick="handleOptionSelect(${q.id}, '${opt.key}', ${isPracticeMode})">
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
          <div class="quiz-ref-title">${isPracticeMode ? 'Practice Insight' : 'Study Reference'}</div>
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
    <div class="practice-feedback-card ${isCorrect ? 'correct' : 'incorrect'}">
      <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:0.75rem; flex-wrap:wrap;">
        <div style="display:flex; align-items:flex-start; gap:0.65rem; max-width:75%;">
          <span style="font-size:1.3rem;">${isCorrect ? '✅' : '❌'}</span>
          <div>
            <div style="font-weight:800; font-size:1rem; margin-bottom:0.25rem;">
              ${isCorrect ? 'Correct! Well Done.' : 'Incorrect Choice'}
            </div>
            <div style="font-size:0.92rem; line-height:1.5;">
              ${q.explanation || (isCorrect ? 'Great job! You mastered this concept.' : `The correct option is <strong>${q.correctOption}</strong>.`)}
            </div>
          </div>
        </div>
        <button class="btn btn-primary btn-sm" onclick="navigateQuestion(1)" style="font-weight:700; padding:0.5rem 1.15rem; white-space:nowrap; align-self:center;">
          ${isLast ? 'Complete Practice Session →' : 'Next Practice Problem →'}
        </button>
      </div>
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

    window.location.href = `/student/quiz-result.html?attemptId=${result.attemptId}&mode=${currentQuiz.mode || 'CHAPTER_QUIZ'}`;
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

    const isFinalExam = result.quizType === 'FINAL_ASSESSMENT';
    const isPractice = result.quizType === 'PRACTICE';
    const isTopicQuiz = result.quizType === 'CHAPTER_QUIZ';

    const titleEl = document.getElementById('resultQuizTitle');
    if (titleEl) {
      if (isFinalExam) {
        titleEl.textContent = `🎓 Final Course Assessment — ${result.quizTitle}`;
      } else if (isPractice) {
        titleEl.textContent = `🌱 Practice Session Summary — ${result.quizTitle}`;
      } else {
        titleEl.textContent = `📝 Topic Assessment Results — ${result.quizTitle}`;
      }
    }

    const scoreDisplay = document.getElementById('overallScoreDisplay');
    if (scoreDisplay) {
      let readinessBadge = '';
      if (isFinalExam) {
        if (result.overallPercentage >= 80) {
          readinessBadge = ' · <span class="badge" style="background:#d1fae5; color:#047857; font-weight:800;">🌟 Mastered &bull; Certification Ready</span>';
        } else if (result.overallPercentage >= 60) {
          readinessBadge = ' · <span class="badge" style="background:#eff6ff; color:#2563eb; font-weight:800;">🟢 Passing Grade &bull; Proficient</span>';
        } else {
          readinessBadge = ' · <span class="badge" style="background:#fee2e2; color:#b91c1c; font-weight:800;">🟡 Revision Recommended</span>';
        }
      }
      scoreDisplay.innerHTML = `${result.totalScore} / ${result.totalQuestions} (${Math.round(result.overallPercentage)}%)${readinessBadge}`;
    }

    // Render Dedicated Final Assessment Outcome Card
    const finalCardEl = document.getElementById('finalAssessmentResultCard');
    if (finalCardEl) {
      if (isFinalExam) {
        finalCardEl.style.display = 'block';
        const isPassed = (result.passed !== undefined && result.passed !== null)
          ? result.passed
          : (result.overallPercentage >= 70.0);
        const passingScore = result.passingScore || 70.0;

        if (isPassed) {
          finalCardEl.innerHTML = `
            <div class="card" style="background: linear-gradient(135deg, #ecfdf5 0%, #f0fdf4 100%); border: 1.5px solid #86efac; border-radius: 16px; padding: 1.6rem 1.8rem; box-shadow: 0 4px 16px rgba(16,185,129,0.08);">
              <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div style="max-width: 680px;">
                  <div style="display: flex; align-items: center; gap: 0.6rem; margin-bottom: 0.35rem;">
                    <span class="badge" style="background:#059669; color:#ffffff; font-weight:800; font-size:0.8rem; padding:0.25rem 0.65rem; border-radius:999px;">✓ PASSED</span>
                    <span style="font-size: 0.82rem; font-weight: 700; color: #047857;">Final Assessment Score: ${result.overallPercentage}% (Passing Threshold: ${passingScore}%)</span>
                  </div>
                  <h3 style="font-size: 1.35rem; font-weight: 800; color: #064e3b; margin: 0 0 0.4rem 0;">🎉 Final Assessment Passed!</h3>
                  <p style="color: #047857; font-size: 0.92rem; margin: 0; line-height: 1.55;">
                    ${result.courseCompleted
                      ? `<strong>🏆 Course Completed!</strong> Congratulations! You have achieved 100% Lesson Completion and passed the Final Assessment with a score of <strong>${result.overallPercentage}%</strong>. Your completion and verified certificate are officially recorded.`
                      : (result.completionMessage || 'You scored above the required 70% passing threshold! Complete any remaining lessons to earn your full course completion certificate.')}
                  </p>
                </div>
                <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                  <a href="/student/course-detail.html?id=${result.courseId}" class="btn btn-primary" style="background:#059669; border-color:#059669; padding:0.7rem 1.35rem; font-weight:700;">
                    Return to Course Workspace &rarr;
                  </a>
                  ${result.courseCompleted ? `
                    <a href="/student/profile.html" class="btn btn-outline" style="border-color:#10b981; color:#064e3b; padding:0.7rem 1.25rem; font-weight:700; background:#ffffff;">
                      View Certificate 🎓
                    </a>
                  ` : ''}
                </div>
              </div>
            </div>
          `;
        } else {
          finalCardEl.innerHTML = `
            <div class="card" style="background: linear-gradient(135deg, #fef2f2 0%, #fff1f2 100%); border: 1.5px solid #fca5a5; border-radius: 16px; padding: 1.6rem 1.8rem; box-shadow: 0 4px 16px rgba(239,68,68,0.06);">
              <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div style="max-width: 680px;">
                  <div style="display: flex; align-items: center; gap: 0.6rem; margin-bottom: 0.35rem;">
                    <span class="badge" style="background:#dc2626; color:#ffffff; font-weight:800; font-size:0.8rem; padding:0.25rem 0.65rem; border-radius:999px;">NOT PASSED</span>
                    <span style="font-size: 0.82rem; font-weight: 700; color: #991b1b;">Passing Score Required: ${passingScore}% · Scored: ${result.overallPercentage}%</span>
                  </div>
                  <h3 style="font-size: 1.35rem; font-weight: 800; color: #7f1d1d; margin: 0 0 0.4rem 0;">Assessment Not Passed</h3>
                  <p style="color: #991b1b; font-size: 0.92rem; margin: 0; line-height: 1.55;">
                    A minimum passing score of <strong>${passingScore}%</strong> is required to complete this course.
                    The course remains in progress. Review the topic breakdown below and retake the final assessment when ready.
                  </p>
                </div>
                <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                  <a href="/student/quiz.html?courseId=${result.courseId}&mode=FINAL_ASSESSMENT" class="btn btn-primary" style="background:#dc2626; border-color:#dc2626; padding:0.7rem 1.35rem; font-weight:700;">
                    Retake Final Assessment &rarr;
                  </a>
                </div>
              </div>
            </div>
          `;
        }
      } else {
        finalCardEl.style.display = 'none';
      }
    }

    const backBtn = document.getElementById('backToCourseBtn');
    if (backBtn) {
      backBtn.href = `/student/course-detail.html?id=${result.courseId}`;
    }

    // Render Topic-by-Topic Breakdown
    const topicGrid = document.getElementById('topicBreakdownGrid');
    if (topicGrid && Array.isArray(result.topicBreakdown)) {
      topicGrid.innerHTML = result.topicBreakdown.map(tb => {
        const colorMap = {
          WEAK: '#ef4444',
          NEEDS_WORK: '#f59e0b',
          STRONG: '#10b981',
          MASTERED: '#8b5cf6'
        };
        const barColor = colorMap[tb.statusLabel] || '#4f46e5';

        return `
          <div class="card" style="border: 1px solid #e2e8f0; border-radius: 14px; padding: 1.25rem 1.4rem;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.5rem;">
              <strong>${tb.topicTitle}</strong>
              <span class="badge" style="background:${barColor}20; color:${barColor}; font-weight:700;">${tb.statusLabel}</span>
            </div>
            <div style="background:#e2e8f0; height:8px; border-radius:999px; overflow:hidden; margin:0.6rem 0;">
              <div style="width:${tb.percentage}%; background:${barColor}; height:100%;"></div>
            </div>
            <div style="display:flex; justify-content:space-between; font-size:0.86rem; color:var(--text-muted); align-items:center;">
              <span>${tb.correct} of ${tb.total} correct (${Math.round(tb.percentage)}%)</span>
              ${tb.percentage < 70
                ? `<a href="/student/practice.html?topicId=${tb.topicId}" class="btn btn-outline btn-sm" style="font-size:0.75rem; padding:0.25rem 0.65rem;">Practice Topic →</a>`
                : `<span style="color:#047857; font-weight:700; font-size:0.8rem;">✓ Strong Concept</span>`
              }
            </div>
          </div>
        `;
      }).join('');
    }

    // Render Question-by-Question Review with Explanations
    const reviewContainer = document.getElementById('answerReviewContainer');
    if (reviewContainer && Array.isArray(result.answerDetails)) {
      reviewContainer.innerHTML = result.answerDetails.map((ad, idx) => `
        <div class="card" style="margin-bottom:1rem; border-left:4px solid ${ad.correct ? '#10b981' : '#ef4444'}; border-radius:12px; padding:1.25rem 1.4rem;">
          <div style="display:flex; justify-content:space-between; margin-bottom:0.4rem; flex-wrap:wrap; gap:0.5rem;">
            <span class="card-question" style="font-weight:700; color:#64748b;">Question ${idx + 1} &bull; ${ad.topicTitle}</span>
            <span style="font-weight:800; font-size:0.85rem; color:${ad.correct ? '#047857' : '#b91c1c'};">
              ${ad.correct ? '✓ Correct' : '✗ Incorrect'}
            </span>
          </div>
          <p style="font-weight:700; font-size:1.02rem; color:#0f172a; margin-bottom:0.6rem;">${ad.questionText}</p>
          <p style="font-size:0.9rem; color:#475569; margin: 0 0 0.5rem 0;">
            Your Answer: <strong style="color:${ad.correct ? '#047857' : '#b91c1c'};">${ad.selectedOption || 'Skipped'}</strong> &bull;
            Correct Option: <strong style="color:#047857;">${ad.correctOption}</strong>
          </p>
          ${ad.explanation ? `
            <div style="background:#f8fafc; border:1px solid #e2e8f0; border-radius:8px; padding:0.65rem 0.95rem; font-size:0.88rem; color:#1e293b; line-height:1.5;">
              <strong style="color:#2563eb;">Explanation &amp; Mechanics:</strong> ${ad.explanation}
            </div>
          ` : ''}
        </div>
      `).join('');
    }
  } catch (err) {
    console.error('Failed to load quiz result:', err);
  }
}

