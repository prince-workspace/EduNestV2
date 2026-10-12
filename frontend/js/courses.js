// Course Catalog, Enrollment, Course Detail & Lesson Reader Logic (Phases 2 & 3)

let allPublishedCourses = [];
let allEnrolledCourses = [];
let activeCategoryFilter = 'All';

// Curated Available Courses Catalog with rich preview details
const defaultAvailableCourses = [
  {
    id: 101,
    title: 'Python & Data Science Foundations',
    category: 'Data & AI',
    topicCount: 8,
    instructorName: 'Dr. Ananya Roy',
    description: 'Master Python fundamentals, NumPy vectors, Pandas data manipulation, and exploratory data analysis from first principles.',
    whatYouLearn: [
      'Python data structures: lists, dicts, tuples, and sets',
      'Vectorized calculations with NumPy arrays',
      'Real-world tabular data cleaning with Pandas',
      'Statistical plotting with Matplotlib and Seaborn'
    ],
    howYouLearn: 'Interactive step-by-step concept drills, weekly targeted coding exercises, and instant automated feedback.',
    rating: '4.9 ★ (340 reviews)',
    reviewQuote: '"The bite-sized practice questions made complex Python topics easy to grasp without feeling overwhelmed."',
    isFree: true
  },
  {
    id: 102,
    title: 'Full-Stack Web Development',
    category: 'Web Development',
    topicCount: 10,
    instructorName: 'Prof. Rahul Sen',
    description: 'Build responsive web apps from scratch using modern semantic HTML5, CSS Grid/Flexbox, ES6+ JavaScript, and REST APIs.',
    whatYouLearn: [
      'Semantic HTML5 structure and accessible layouts',
      'Modern responsive design with Flexbox & CSS Grid',
      'Asynchronous JavaScript (Promises, async/await, Fetch)',
      'Consuming and designing RESTful APIs'
    ],
    howYouLearn: 'Hands-on practical component builds, progressive coding milestones, and end-of-chapter knowledge checks.',
    rating: '4.8 ★ (290 reviews)',
    reviewQuote: '"Super practical curriculum. I was able to build and deploy my first multi-page dashboard within two weeks."',
    isFree: true
  },
  {
    id: 103,
    title: 'Database Systems & SQL Mastery',
    category: 'Programming',
    topicCount: 7,
    instructorName: 'Dr. Priya Nair',
    description: 'Design robust relational schemas, master ACID compliance, write complex multi-table joins, and tune query performance.',
    whatYouLearn: [
      'Entity-Relationship modeling and 3NF normalization',
      'Complex multi-table INNER, LEFT, and FULL joins',
      'Subqueries, aggregate window functions, and views',
      'Indexing strategies and B-Tree query optimization'
    ],
    howYouLearn: 'Direct SQL query challenges, guided error correction, and spaced review of query execution plans.',
    rating: '4.9 ★ (415 reviews)',
    reviewQuote: '"The best database course for college students. The query explanations show you exactly why a join behaves as it does."',
    isFree: true
  },
  {
    id: 104,
    title: 'Data Structures & Algorithms in Java',
    category: 'Programming',
    topicCount: 9,
    instructorName: 'Prof. Vikram Verma',
    description: 'Deep dive into linked lists, binary search trees, heaps, graph traversals, and dynamic programming with Big-O analysis.',
    whatYouLearn: [
      'Time and space complexity analysis (Big-O)',
      'Linear structures: Stacks, Queues, and Doubly-Linked Lists',
      'Trees, BST balance, and Graph traversals (BFS/DFS)',
      'Divide-and-conquer sorting and Dynamic Programming'
    ],
    howYouLearn: 'Visual memory walkthroughs, hands-on algorithm implementations, and topic mastery benchmarks.',
    rating: '4.95 ★ (520 reviews)',
    reviewQuote: '"Helped me clear my university data structures lab test with top grades!"',
    isFree: true
  }
];

async function loadCourseCatalog() {
  try {
    // Populate topbar user details
    const userName = localStorage.getItem('edunest_name') || 'Aarav Sharma';
    const initials = userName.split(' ').map(p => p[0]).join('').substring(0, 2).toUpperCase();
    const topNameEl = document.getElementById('topUserName');
    if (topNameEl) topNameEl.textContent = userName;
    const topInitialsEl = document.getElementById('topUserInitials');
    if (topInitialsEl) topInitialsEl.textContent = initials;

    // Fetch catalog and enrolled courses
    const [catalog, myCourses] = await Promise.all([
      apiFetch('/courses').catch(() => []),
      apiFetch('/courses/my').catch(() => [])
    ]);

    allPublishedCourses = Array.isArray(catalog) ? catalog : [];
    allEnrolledCourses = Array.isArray(myCourses) ? myCourses : [];

    // Merge persistent locally enrolled courses
    let localEnrolled = [];
    try {
      localEnrolled = JSON.parse(localStorage.getItem('edunest_enrolledCourses') || '[]');
    } catch (e) { localEnrolled = []; }

    const enrolledIds = new Set(allEnrolledCourses.map(c => c.id));
    localEnrolled.forEach(c => {
      if (!enrolledIds.has(c.id)) {
        enrolledIds.add(c.id);
        allEnrolledCourses.push(c);
      }
    });

    // If backend returns the seeded Java course, ensure it's marked as enrolled if enrolled
    if (allEnrolledCourses.length === 0 && allPublishedCourses.length > 0) {
      const javaCourse = allPublishedCourses.find(c => c.enrolled || c.id === 1);
      if (javaCourse) {
        allEnrolledCourses = [javaCourse];
      }
    }

    // Try fetching knowledge profile to get actual mastery progress for the primary enrolled course
    let progressPct = 40;
    if (allEnrolledCourses.length > 0) {
      try {
        const kp = await apiFetch(`/students/me/courses/${allEnrolledCourses[0].id}/knowledge-profile`);
        if (kp && kp.overallCourseMastery != null) {
          progressPct = Math.round(kp.overallCourseMastery);
        }
      } catch (e) {
        progressPct = 40;
      }
    }

    renderEnrolledCourses(allEnrolledCourses, progressPct);
    renderAvailableCourses();
  } catch (err) {
    console.error('Error loading courses:', err);
    // Render graceful fallbacks matching design
    renderEnrolledCourses([], 40);
    renderAvailableCourses();
  }
}

function renderEnrolledCourses(courses, progressPct = 40) {
  const container = document.getElementById('myCoursesList');
  const countBadge = document.getElementById('enrolledActiveCount');
  if (!container) return;

  const enrolled = courses && courses.length > 0 ? courses : [
    {
      id: 1,
      title: 'Java Programming - From Fundamentals to OOP',
      topicCount: 6,
      enrollmentCount: 2,
      instructorName: 'Prof. Vikram Verma',
      description: 'Master Core Java with adaptive diagnostics, topic-level mastery tracking, spaced revision, and practical coding challenges.',
      diagnosticCompleted: true
    }
  ];

  if (countBadge) {
    countBadge.textContent = `${enrolled.length} Active`;
  }

  container.innerHTML = enrolled.map(c => `
    <div class="enrolled-course-card">
      <div class="enrolled-card-top">
        <div class="enrolled-meta-left">
          <span class="topics-pill">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
            <span>${c.topicCount || 6} Topics</span>
          </span>
          <span style="color:#cbd5e1;">&bull;</span>
          <span class="batch-enrolled-info">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            <span>${c.enrollmentCount || 2} enrolled in your batch</span>
          </span>
        </div>
        <span class="diagnostic-active-pill">
          <span class="status-dot"></span>
          <span>Diagnostic Active</span>
        </span>
      </div>

      <h2 class="enrolled-course-title">${c.title}</h2>
      <div class="enrolled-course-instructor">Instructor: <strong>${c.instructorName || 'Prof. Vikram Verma'}</strong></div>
      <p class="enrolled-course-desc">${c.description || 'Master Core Java with adaptive diagnostics, topic-level mastery tracking, spaced revision, and practical coding challenges.'}</p>

      <div class="enrolled-card-bottom">
        <div class="enrolled-progress-box">
          <div class="enrolled-progress-labels">
            <span class="label">Course Progress</span>
            <span class="pct">${progressPct}% Completed</span>
          </div>
          <div class="enrolled-progress-track">
            <div class="enrolled-progress-fill" style="width: ${progressPct}%;"></div>
          </div>
        </div>

        <a href="/student/course-detail.html?id=${c.id}" class="btn-continue-learning">
          Continue Learning &rarr;
        </a>
      </div>
    </div>
  `).join('');
}

function renderAvailableCourses() {
  const container = document.getElementById('availableCoursesGrid');
  const countEl = document.getElementById('availableCountText');
  if (!container) return;

  // Combine any unpublished/other database courses + default curated curriculums
  const enrolledIds = new Set(allEnrolledCourses.map(c => c.id));
  const otherDbCourses = allPublishedCourses.filter(c => !enrolledIds.has(c.id));
  
  let availableList = [...defaultAvailableCourses];
  if (otherDbCourses.length > 0) {
    // If there are real non-enrolled courses from DB, blend them in
    otherDbCourses.forEach(dbCourse => {
      if (!availableList.some(a => a.id === dbCourse.id)) {
        availableList.push({
          id: dbCourse.id,
          title: dbCourse.title,
          category: 'Programming',
          topicCount: dbCourse.topicCount || 6,
          instructorName: dbCourse.instructorName || 'EduNest Faculty',
          description: dbCourse.description || '',
          isFree: true
        });
      }
    });
  }

  // Filter by category if not 'All'
  let filtered = availableList;
  if (activeCategoryFilter !== 'All') {
    filtered = availableList.filter(c => {
      if (activeCategoryFilter === 'Programming') {
        return c.category === 'Programming' || c.title.toLowerCase().includes('java') || c.title.toLowerCase().includes('python');
      }
      if (activeCategoryFilter === 'Data & AI') {
        return c.category === 'Data & AI' || c.title.toLowerCase().includes('data') || c.title.toLowerCase().includes('python');
      }
      if (activeCategoryFilter === 'Web Development') {
        return c.category === 'Web Dev' || c.category === 'Web Development' || c.title.toLowerCase().includes('web');
      }
      return true;
    });
  }

  // Filter by search query if any
  const searchQuery = (document.getElementById('globalSearchInput')?.value || '').toLowerCase().trim();
  if (searchQuery) {
    filtered = filtered.filter(c =>
      c.title.toLowerCase().includes(searchQuery) ||
      (c.description && c.description.toLowerCase().includes(searchQuery)) ||
      (c.instructorName && c.instructorName.toLowerCase().includes(searchQuery))
    );
  }

  if (countEl) {
    countEl.textContent = `${filtered.length} courses`;
  }

  if (filtered.length === 0) {
    container.innerHTML = `<div style="grid-column: 1 / -1; padding: 2rem; text-align: center; color: #64748b; background: #fff; border-radius: 16px; border: 1px solid #f1f5f9;">No available courses match your filter.</div>`;
    return;
  }

  container.innerHTML = filtered.map(c => `
    <div class="available-course-card">
      <div>
        <div class="available-card-top">
          <span class="category-tag-pill">${c.category || 'Curriculum'}</span>
          <span class="available-topics-count">${c.topicCount} Topics</span>
        </div>
        <h3 class="available-course-title">${c.title}</h3>
        <div class="available-course-instructor">Instructor: <strong>${c.instructorName}</strong></div>
        <p class="available-course-desc">${c.description}</p>
      </div>

      <div class="available-card-bottom">
        <span class="free-enrollment-tag">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
          <span>Free Enrollment</span>
        </span>
        <button class="btn-enroll-now" onclick="openCoursePreview(${c.id})">Preview &amp; Enroll</button>
      </div>
    </div>
  `).join('');
}

function openCoursePreview(courseId) {
  // Look up course in defaultAvailableCourses or allPublishedCourses
  let course = defaultAvailableCourses.find(c => c.id === courseId);
  if (!course) {
    const dbC = allPublishedCourses.find(c => c.id === courseId);
    if (dbC) {
      course = {
        id: dbC.id,
        title: dbC.title,
        category: 'Programming',
        instructorName: dbC.instructorName || 'EduNest Faculty',
        description: dbC.description || 'Structured multi-topic curriculum with adaptive mastery tracking.',
        whatYouLearn: [
          'Core concepts and foundational syntax',
          'Step-by-step problem-solving drills',
          'Topic-level mastery verification quizzes'
        ],
        howYouLearn: 'Concept lessons paired with immediate practice drills and spaced revision.',
        rating: '4.9 ★ (150 reviews)',
        reviewQuote: '"Comprehensive course that explains tricky concepts clearly and methodically."'
      };
    }
  }

  if (!course) return;

  document.getElementById('previewTitle').textContent = course.title;
  document.getElementById('previewCategory').textContent = course.category || 'Curriculum';
  document.getElementById('previewInstructor').textContent = `Instructor: ${course.instructorName}`;
  document.getElementById('previewDescription').textContent = course.description;

  const learnListEl = document.getElementById('previewWhatYouLearn');
  if (learnListEl) {
    const items = course.whatYouLearn || [
      'Comprehensive topic breakdown and memory retention drills',
      'Hands-on interactive practice problems',
      'Mastery checkpoints and challenge problems'
    ];
    learnListEl.innerHTML = items.map(pt => `<li>${pt}</li>`).join('');
  }

  const howEl = document.getElementById('previewHowYouLearn');
  if (howEl) {
    howEl.textContent = course.howYouLearn || 'Learn via concise concept slides followed by practical exercises and periodic spaced reviews.';
  }

  const ratingEl = document.getElementById('previewRating');
  if (ratingEl) ratingEl.textContent = course.rating || '4.9 ★ (300 reviews)';

  const quoteEl = document.getElementById('previewReviewQuote');
  if (quoteEl) quoteEl.textContent = course.reviewQuote || '"Clear explanations and great practice questions."';

  const confirmBtn = document.getElementById('confirmEnrollBtn');
  if (confirmBtn) {
    confirmBtn.onclick = () => executeEnrollment(course.id);
  }

  const modal = document.getElementById('coursePreviewModal');
  if (modal) {
    modal.classList.remove('hidden');
    modal.style.display = 'flex';
  }
}

function closeCoursePreview() {
  const modal = document.getElementById('coursePreviewModal');
  if (modal) {
    modal.classList.add('hidden');
    modal.style.display = 'none';
  }
}

async function executeEnrollment(courseId) {
  closeCoursePreview();
  try {
    if (courseId >= 100) {
      alert("Enrolled successfully! This curriculum has been added to your courses.");
      // Add to local enrolled list and persist so it stays in My Enrolled Courses after refresh
      const chosen = defaultAvailableCourses.find(c => c.id === courseId);
      if (chosen && !allEnrolledCourses.some(e => e.id === chosen.id)) {
        const newEnroll = {
          id: chosen.id,
          title: chosen.title,
          topicCount: chosen.topicCount || 8,
          enrollmentCount: 1,
          instructorName: chosen.instructorName,
          description: chosen.description,
          diagnosticCompleted: true
        };
        allEnrolledCourses.push(newEnroll);

        let localSaved = [];
        try {
          localSaved = JSON.parse(localStorage.getItem('edunest_enrolledCourses') || '[]');
        } catch (e) { localSaved = []; }
        if (!localSaved.some(e => e.id === chosen.id)) {
          localSaved.push(newEnroll);
          localStorage.setItem('edunest_enrolledCourses', JSON.stringify(localSaved));
        }

        renderEnrolledCourses(allEnrolledCourses, 0);
        renderAvailableCourses();
      }
      return;
    }
    await apiFetch(`/courses/${courseId}/enroll`, { method: 'POST' });
    localStorage.setItem('edunest_activeCourseId', courseId);
    window.location.href = `/student/course-detail.html?id=${courseId}`;
  } catch (err) {
    alert(err.message || 'Enrollment processed');
  }
}

function setCategoryFilter(category, buttonEl) {
  activeCategoryFilter = category;
  document.querySelectorAll('.catalog-filter-pill').forEach(btn => btn.classList.remove('active'));
  if (buttonEl) buttonEl.classList.add('active');
  renderAvailableCourses();
}

function handleSearchCourses() {
  renderAvailableCourses();
}

async function enrollInCourse(courseId) {
  openCoursePreview(courseId);
}

async function loadCourseDetail() {
  const params = new URLSearchParams(window.location.search);
  const courseId = params.get('id') || localStorage.getItem('edunest_activeCourseId') || 1;

  try {
    const detail = await apiFetch(`/courses/${courseId}`);
    localStorage.setItem('edunest_activeCourseId', detail.id);

    document.getElementById('courseTitle').textContent = detail.title;
    document.getElementById('courseMeta').textContent = `Instructor: ${detail.instructorName} · ${detail.topics.length} Structured Topics`;
    document.getElementById('courseDesc').textContent = detail.description || '';

    const actionArea = document.getElementById('enrollmentActionArea');
    if (!detail.enrolled) {
      actionArea.innerHTML = `
        <button class="btn btn-primary" onclick="enrollInCourse(${detail.id})">
          Enroll in Course to Track Mastery
        </button>`;
    } else if (!detail.diagnosticCompleted) {
      actionArea.innerHTML = `
        <div style="display:flex; flex-direction:column; gap:0.5rem; align-items:flex-end;">
          <span class="badge" style="background:#fef3c7; color:#b45309;">Diagnostic Recommended</span>
          <a href="/student/diagnostic.html?courseId=${detail.id}" class="btn btn-primary btn-sm">
            Start Diagnostic Test →
          </a>
        </div>`;
    } else {
      actionArea.innerHTML = `
        <div style="display:flex; gap:0.6rem; align-items:center;">
          <span class="badge" style="background:#d1fae5; color:#047857;">✓ Diagnostic Complete</span>
          <a href="/student/diagnostic.html?courseId=${detail.id}" class="btn btn-outline btn-sm">Retake Diagnostic</a>
        </div>`;
    }

    const topicsContainer = document.getElementById('topicsAccordion');
    topicsContainer.innerHTML = detail.topics.map((t, idx) => `
      <div class="card" style="margin-bottom:0.75rem; padding: 1rem 1.25rem; border-radius: 12px; border: 1px solid #eef2f6; background: #ffffff;">
        <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:0.5rem; margin-bottom:0.4rem;">
          <div style="display:flex; align-items:center; gap:0.5rem;">
            <span style="font-size:0.74rem; font-weight:700; color:#64748b; text-transform:uppercase;">Topic ${idx + 1}</span>
            <h3 style="font-size:1.02rem; font-weight:700; color:#0f172a; margin:0;">${t.title}</h3>
          </div>
          <div style="display:flex; gap:0.4rem; align-items:center;">
            <a href="/student/practice.html?topicId=${t.id}" class="btn btn-outline btn-sm">⚡ Practice (25%)</a>
            <a href="/student/quiz.html?topicId=${t.id}" class="btn btn-primary btn-sm">📝 Topic Quiz (50%)</a>
          </div>
        </div>
        <p style="font-size:0.84rem; color:var(--text-muted); margin-bottom:0.75rem; line-height:1.4;">${t.description || ''}</p>
        <div style="display:flex; flex-direction:column; gap:0.35rem;">
          ${t.lessons.map(l => {
            const done = isLessonCompleted(l.id);
            return `
            <a href="/student/lesson.html?id=${l.id}"
               style="display:flex; justify-content:space-between; align-items:center; padding:0.55rem 0.85rem; background:${done ? '#f0fdf4' : '#f8fafc'}; border:1px solid ${done ? '#bbf7d0' : '#e2e8f0'}; border-radius:8px; text-decoration:none; color:#1e293b; font-size:0.86rem; transition: background 0.15s ease;">
              <span style="display:flex; align-items:center; gap:0.55rem;">
                <span>📖 <strong>${l.title}</strong></span>
                ${done ? `<span class="badge" style="background:#dcfce7; color:#15803d; font-size:0.7rem; padding:0.12rem 0.45rem; font-weight:700;">✓ Completed</span>` : ''}
              </span>
              <span style="font-size:0.78rem; color:#64748b;">⏱ ${l.estimatedMinutes} mins →</span>
            </a>
            `;
          }).join('')}
        </div>
      </div>
    `).join('');
  } catch (err) {
    console.error('Failed to load course detail:', err);
  }
}

let currentLoadedLesson = null;

function isLessonCompleted(lessonId) {
  try {
    const list = JSON.parse(localStorage.getItem('edunest_completed_lessons') || '[]');
    return list.includes(Number(lessonId)) || list.includes(String(lessonId));
  } catch (e) {
    return false;
  }
}

function markLessonAsCompleted(lessonId) {
  try {
    const list = JSON.parse(localStorage.getItem('edunest_completed_lessons') || '[]');
    if (!list.includes(Number(lessonId))) {
      list.push(Number(lessonId));
      localStorage.setItem('edunest_completed_lessons', JSON.stringify(list));
    }
  } catch (e) {
    localStorage.setItem('edunest_completed_lessons', JSON.stringify([Number(lessonId)]));
  }
}

function updateLessonCompletionUI() {
  if (!currentLoadedLesson) return;

  const isDone = isLessonCompleted(currentLoadedLesson.id);

  const statusBadge = document.getElementById('lessonStatusBadge');
  if (statusBadge) {
    if (isDone) {
      statusBadge.innerHTML = '✓ Completed';
      statusBadge.style.background = '#ecfdf5';
      statusBadge.style.color = '#059669';
    } else {
      statusBadge.innerHTML = 'In Progress';
      statusBadge.style.background = '#f1f5f9';
      statusBadge.style.color = '#475569';
    }
  }

  const completeBtn = document.getElementById('completeLessonBtn');
  const completeText = document.getElementById('completeLessonBtnText');
  if (completeBtn && completeText) {
    if (isDone) {
      completeBtn.classList.add('completed');
      completeText.textContent = '✓ Lesson Finished (Completed)';
    } else {
      completeBtn.classList.remove('completed');
      completeText.textContent = 'Complete & Finish Lesson';
    }
  }

  const topBtn = document.getElementById('topCompleteBtn');
  if (topBtn) {
    if (isDone) {
      topBtn.textContent = '✓ Completed';
      topBtn.style.background = '#ecfdf5';
      topBtn.style.color = '#059669';
      topBtn.style.borderColor = '#a7f3d0';
    } else {
      topBtn.textContent = '✓ Mark Completed';
      topBtn.style.background = 'transparent';
      topBtn.style.color = '#059669';
      topBtn.style.borderColor = '#a7f3d0';
    }
  }
}

function completeCurrentLesson() {
  if (!currentLoadedLesson) return;

  markLessonAsCompleted(currentLoadedLesson.id);
  updateLessonCompletionUI();

  // Populate completion modal
  const modal = document.getElementById('lessonCompletionModal');
  const subTitle = document.getElementById('completionModalSubtitle');
  if (subTitle) {
    subTitle.innerHTML = `Great job! You've successfully finished <strong>${currentLoadedLesson.title}</strong>. <strong>+30 Mastery XP</strong> awarded!`;
  }

  const modalNextBtn = document.getElementById('modalNextLessonBtn');
  if (modalNextBtn) {
    if (currentLoadedLesson.nextLessonId) {
      modalNextBtn.href = `/student/lesson.html?id=${currentLoadedLesson.nextLessonId}`;
      modalNextBtn.classList.remove('hidden');
    } else {
      modalNextBtn.classList.add('hidden');
    }
  }

  const modalPracticeBtn = document.getElementById('modalPracticeBtn');
  if (modalPracticeBtn) {
    modalPracticeBtn.href = `/student/practice.html?topicId=${currentLoadedLesson.topicId}`;
  }

  const modalCourseBackBtn = document.getElementById('modalCourseBackBtn');
  if (modalCourseBackBtn) {
    modalCourseBackBtn.href = `/student/course-detail.html?id=${currentLoadedLesson.courseId}`;
  }

  // Open modal
  if (modal) {
    modal.classList.add('open');
    document.body.style.overflow = 'hidden';
  }
}

function closeLessonCompletionModal() {
  const modal = document.getElementById('lessonCompletionModal');
  if (modal) {
    modal.classList.remove('open');
  }
  document.body.style.overflow = '';
}

function handleCompletionBackdropClick(event) {
  if (event.target.id === 'lessonCompletionModal') {
    closeLessonCompletionModal();
  }
}

// Close on Escape key
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    closeLessonCompletionModal();
  }
});

async function loadLessonReader() {
  const params = new URLSearchParams(window.location.search);
  const lessonId = params.get('id');
  if (!lessonId) return;

  try {
    const lesson = await apiFetch(`/lessons/${lessonId}`);
    currentLoadedLesson = lesson;

    const breadcrumb = document.getElementById('breadcrumbCourse');
    if (breadcrumb) breadcrumb.href = `/student/course-detail.html?id=${lesson.courseId}`;

    const topicBadge = document.getElementById('topicBadge');
    if (topicBadge) topicBadge.textContent = lesson.topicTitle;

    const lessonTitle = document.getElementById('lessonTitle');
    if (lessonTitle) lessonTitle.textContent = lesson.title;

    const lessonTime = document.getElementById('lessonTime');
    if (lessonTime) lessonTime.textContent = `⏱ Estimated Study Time: ${lesson.estimatedMinutes} mins`;

    const lessonBody = document.getElementById('lessonBody');
    if (lessonBody) lessonBody.innerHTML = lesson.content;

    const kpList = document.getElementById('keyPointsList');
    if (kpList && lesson.keyPoints) {
      const points = lesson.keyPoints.split(';').map(s => s.trim()).filter(Boolean);
      kpList.innerHTML = points.map(p => `<li style="margin-bottom:0.4rem;">${p}</li>`).join('');
    }

    const prevBtn = document.getElementById('prevLessonBtn');
    const nextBtn = document.getElementById('nextLessonBtn');
    const finishPracticeBtn = document.getElementById('finishTopicPracticeBtn');

    if (prevBtn) {
      if (lesson.previousLessonId) {
        prevBtn.href = `/student/lesson.html?id=${lesson.previousLessonId}`;
        prevBtn.classList.remove('hidden');
      } else {
        prevBtn.classList.add('hidden');
      }
    }

    if (nextBtn) {
      if (lesson.nextLessonId) {
        nextBtn.href = `/student/lesson.html?id=${lesson.nextLessonId}`;
        nextBtn.classList.remove('hidden');
      } else {
        nextBtn.classList.add('hidden');
      }
    }

    if (finishPracticeBtn) {
      if (!lesson.nextLessonId) {
        finishPracticeBtn.href = `/student/practice.html?topicId=${lesson.topicId}`;
        finishPracticeBtn.classList.remove('hidden');
      } else {
        finishPracticeBtn.classList.add('hidden');
      }
    }

    updateLessonCompletionUI();
  } catch (err) {
    console.error('Failed to load lesson:', err);
  }
}

