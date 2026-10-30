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
    const userName = localStorage.getItem('edunest_name') || 'Student';
    const initials = userName.split(' ').map(p => p[0]).join('').substring(0, 2).toUpperCase() || 'ST';
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

    // Try fetching knowledge profile to get actual mastery progress for the primary enrolled course
    let progressPct = 0;
    if (allEnrolledCourses.length > 0) {
      try {
        const kp = await apiFetch(`/students/me/courses/${allEnrolledCourses[0].id}/knowledge-profile`);
        if (kp && kp.overallCourseMastery != null) {
          progressPct = Math.round(kp.overallCourseMastery);
        }
      } catch (e) {
        progressPct = 0;
      }
    }

    // Check for first-time onboarding mode
    const params = new URLSearchParams(window.location.search);
    const isOnboarding = params.get('onboarding') === 'true';
    if (isOnboarding) {
      const bannerContainer = document.getElementById('onboardingBannerContainer');
      const studentName = localStorage.getItem('edunest_name') || 'Student';
      const firstName = studentName.split(' ')[0] || 'Student';
      if (bannerContainer) {
        bannerContainer.innerHTML = `
          <div class="card" style="background: linear-gradient(135deg, #eff6ff 0%, #f0fdf4 100%); border: 1px solid #bfdbfe; border-radius: 16px; padding: 1.6rem 2rem; margin-bottom: 2rem; box-shadow: 0 4px 16px rgba(37,99,235,0.06);">
            <span style="font-size: 0.78rem; font-weight: 700; color: #2563eb; letter-spacing: 0.06em; text-transform: uppercase;">WELCOME TO EDUNEST 🎉</span>
            <h2 style="font-size: 1.55rem; color: #0f172a; margin: 0.35rem 0 0.4rem 0;">Welcome, ${firstName}! Choose your first course to begin.</h2>
            <p style="color: #475569; font-size: 0.95rem; margin: 0; max-width: 680px; line-height: 1.5;">
              Explore all available courses below. Preview the syllabus, topics, and instructor details, then click <strong>Preview &amp; Enroll</strong> to start learning.
            </p>
          </div>
        `;
      }
    }

    renderEnrolledCourses(allEnrolledCourses, progressPct, isOnboarding);
    renderAvailableCourses();
  } catch (err) {
    console.error('Error loading courses:', err);
    renderEnrolledCourses([], 0, false);
    renderAvailableCourses();
  }
}

function renderEnrolledCourses(courses, progressPct = 0, isOnboarding = false) {
  const sectionEl = document.getElementById('enrolledCoursesSection');
  const container = document.getElementById('myCoursesList');
  const countBadge = document.getElementById('enrolledActiveCount');
  if (!container) return;

  const enrolled = (courses && Array.isArray(courses)) ? courses : [];

  if (enrolled.length === 0) {
    if (isOnboarding && sectionEl) {
      sectionEl.style.display = 'none';
      return;
    }
    if (sectionEl) sectionEl.style.display = 'block';
    if (countBadge) countBadge.textContent = '0 Active';
    container.innerHTML = `
      <div class="card" style="padding: 1.75rem; text-align: center; color: #64748b; font-size: 0.92rem; border-radius: 14px;">
        You haven't enrolled in any courses yet. Browse all available courses below and enroll to get started.
      </div>
    `;
    return;
  }

  if (sectionEl) sectionEl.style.display = 'block';
  if (countBadge) {
    countBadge.textContent = `${enrolled.length} Active`;
  }

  container.innerHTML = enrolled.map(c => {
    const isCompleted = Boolean(c.completed);
    const statusPill = isCompleted
      ? `<span class="badge" style="background:#ecfdf5; color:#047857; font-weight:800; font-size:0.75rem; padding:0.25rem 0.65rem; border:1px solid #a7f3d0; border-radius:999px;">🏆 Completed &bull; Final Score: ${c.finalScore || 100}%</span>`
      : `<span class="diagnostic-active-pill"><span class="status-dot"></span><span>In Progress</span></span>`;

    const displayPct = isCompleted ? 100 : progressPct;

    return `
    <div class="enrolled-course-card" style="${isCompleted ? 'border-color: #86efac; background: linear-gradient(180deg, #ffffff 0%, #f0fdf4 100%);' : ''}">
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
        ${statusPill}
      </div>

      <h2 class="enrolled-course-title">${c.title}</h2>
      <div class="enrolled-course-instructor">Instructor: <strong>${c.instructorName || 'Prof. Vikram Verma'}</strong></div>
      <p class="enrolled-course-desc">${c.description || 'Master Core Java with adaptive diagnostics, topic-level mastery tracking, spaced revision, and practical coding challenges.'}</p>

      <div class="enrolled-card-bottom">
        <div class="enrolled-progress-box">
          <div class="enrolled-progress-labels">
            <span class="label">Lesson Completion</span>
            <span class="pct">${displayPct}% Finished</span>
          </div>
          <div class="enrolled-progress-track">
            <div class="enrolled-progress-fill" style="width: ${displayPct}%; background: ${isCompleted ? '#10b981' : '#2563eb'};"></div>
          </div>
        </div>

        <a href="/student/course-detail.html?id=${c.id}" class="btn-continue-learning" style="${isCompleted ? 'background:#059669;' : ''}">
          ${isCompleted ? 'View Course &amp; Results &rarr;' : 'Continue Learning &rarr;'}
        </a>
      </div>
    </div>
  `;
  }).join('');
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
      localStorage.setItem('edunest_activeCourseId', courseId);
      window.location.href = `/student/course-detail.html?id=${courseId}`;
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

// Module definitions for structured curriculum presentation
const COURSE_MODULE_DEFINITIONS = {
  // Course 1: Java Programming - From Fundamentals to OOP
  1: [
    {
      moduleNumber: 1,
      title: "Java Fundamentals & Syntax",
      description: "Architecture, bytecode, variables, data types, operators, and control structures.",
      topicOrderIndices: [1, 2]
    },
    {
      moduleNumber: 2,
      title: "Data Structures & Memory Layout",
      description: "Linear array collections, multi-dimensional matrices, and runtime memory allocation.",
      topicOrderIndices: [3]
    },
    {
      moduleNumber: 3,
      title: "Object-Oriented Programming (OOP)",
      description: "Encapsulation, constructors, inheritance hierarchies, and dynamic runtime polymorphism.",
      topicOrderIndices: [4, 5]
    },
    {
      moduleNumber: 4,
      title: "Advanced Core Java & Collections",
      description: "Exception handling mechanics, resource safety, and standard Collections Framework.",
      topicOrderIndices: [6]
    }
  ]
};

async function loadCourseDetail() {
  const params = new URLSearchParams(window.location.search);
  const courseId = params.get('id') || localStorage.getItem('edunest_activeCourseId') || 1;

  try {
    const detail = await apiFetch(`/courses/${courseId}`);
    localStorage.setItem('edunest_activeCourseId', detail.id);

    // Sync persistent lesson completion from backend if enrolled
    if (detail.enrolled) {
      try {
        const completedIds = await apiFetch(`/lessons/completed?courseId=${detail.id}`).catch(() => null);
        if (Array.isArray(completedIds)) {
          localStorage.setItem('edunest_completed_lessons', JSON.stringify(completedIds));
        } else {
          const kp = await apiFetch(`/students/me/courses/${detail.id}/knowledge-profile`).catch(() => null);
          if (kp && Array.isArray(kp.completedLessonIds)) {
            localStorage.setItem('edunest_completed_lessons', JSON.stringify(kp.completedLessonIds));
          }
        }
      } catch (kpErr) {
        console.warn('Could not load course progress:', kpErr);
      }
    }

    // Essential Course Information
    const titleEl = document.getElementById('courseTitle');
    if (titleEl) {
      titleEl.textContent = detail.title || 'Java Programming - From Fundamentals to OOP';
    }
    document.title = `${detail.title || 'Course Workspace'} — EduNest`;

    const totalLessons = detail.topics.reduce((acc, t) => acc + (t.lessons ? t.lessons.length : 0), 0);
    document.getElementById('courseMeta').textContent = `Instructor: ${detail.instructorName} · ${detail.topics.length} Topics · ${totalLessons} Lessons`;
    document.getElementById('courseDesc').textContent = detail.description || '';

    // Calculate lesson completion counts
    let completedLessons = 0;
    if (detail.enrolled) {
      detail.topics.forEach(t => {
        if (t.lessons) {
          completedLessons += t.lessons.filter(l => isLessonCompleted(l.id)).length;
        }
      });
    }
    const isUnlocked = detail.finalAssessmentUnlocked || (totalLessons > 0 && completedLessons >= totalLessons);
    const passingScore = detail.finalAssessmentPassingScore || 70.0;
    const lessonCompletionPct = totalLessons > 0 ? Math.round((completedLessons / totalLessons) * 100) : 0;

    // Course Completion Banner
    const bannerEl = document.getElementById('courseCompletionBanner');
    if (bannerEl) {
      if (detail.courseCompleted) {
        bannerEl.style.display = 'block';
        bannerEl.innerHTML = `
          <div style="background: linear-gradient(135deg, #ecfdf5 0%, #f0fdf4 100%); border: 1.5px solid #86efac; border-radius: 14px; padding: 1.3rem 1.6rem; display: flex; align-items: center; justify-content: space-between; gap: 1rem; flex-wrap: wrap; box-shadow: 0 4px 14px rgba(16,185,129,0.08);">
            <div style="display: flex; align-items: center; gap: 1rem;">
              <div style="font-size: 2.2rem; line-height: 1;">🏆</div>
              <div>
                <span style="font-size: 0.74rem; font-weight: 800; color: #047857; text-transform: uppercase; letter-spacing: 0.06em;">Course Completed</span>
                <h3 style="font-size: 1.25rem; font-weight: 800; color: #064e3b; margin: 0.15rem 0 0.35rem 0;">Congratulations on completing ${detail.title}!</h3>
                <div style="display: flex; align-items: center; gap: 0.85rem; flex-wrap: wrap; font-size: 0.88rem; color: #047857;">
                  <span>📚 <strong>Lesson Completion:</strong> ${lessonCompletionPct}% (${completedLessons}/${totalLessons} finished)</span>
                  <span>&bull;</span>
                  <span>🎯 <strong>Final Assessment Score:</strong> ${detail.finalScore}% (Passed &ge; ${passingScore}%)</span>
                </div>
              </div>
            </div>
            <div style="display:flex; align-items:center; gap:0.6rem;">
              <a href="/student/profile.html" class="btn btn-primary" style="background:#059669; border-color:#059669; font-weight:700; padding:0.55rem 1.15rem; text-decoration:none; display:inline-flex; align-items:center; gap:0.4rem; box-shadow:0 2px 10px rgba(5,150,105,0.25);">
                <span>View Certificate</span> 🎓
              </a>
            </div>
          </div>
        `;
      } else {
        bannerEl.style.display = 'none';
      }
    }

    // Simple Overall Course Progress Indicator
    const progressContainer = document.getElementById('courseProgressBreakdown');
    if (progressContainer) {
      if (detail.enrolled) {
        progressContainer.style.display = 'block';
        progressContainer.innerHTML = `
          <div style="background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 1.15rem 1.4rem; box-shadow: 0 1px 3px rgba(0,0,0,0.02);">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.55rem; flex-wrap: wrap; gap: 0.5rem;">
              <span style="font-weight: 700; font-size: 0.95rem; color: #0f172a;">Curriculum Progress</span>
              <span style="font-weight: 700; font-size: 0.88rem; color: ${lessonCompletionPct === 100 ? '#047857' : '#2563eb'};">
                ${lessonCompletionPct === 100 ? '✓ ' : ''}Lesson Completion: ${lessonCompletionPct}% (${completedLessons} of ${totalLessons} Lessons Finished)
              </span>
            </div>
            <div style="background: #e2e8f0; border-radius: 999px; height: 9px; overflow: hidden;">
              <div style="background: ${lessonCompletionPct === 100 ? '#10b981' : '#2563eb'}; width: ${lessonCompletionPct}% ; height: 100%; border-radius: 999px; transition: width 0.3s ease;"></div>
            </div>
          </div>
        `;
      } else {
        progressContainer.style.display = 'none';
      }
    }

    // Enrollment Action Area (Clean & Consistent, no duplicate score dump)
    const actionArea = document.getElementById('enrollmentActionArea');
    if (!detail.enrolled) {
      actionArea.innerHTML = `
        <button class="btn btn-primary" onclick="enrollInCourse(${detail.id})">
          Enroll in Course
        </button>`;
    } else if (detail.courseCompleted) {
      actionArea.innerHTML = `
        <span class="badge" style="background:#ecfdf5; color:#047857; font-weight:700; font-size:0.84rem; padding:0.4rem 0.85rem; border-radius:999px; border:1px solid #a7f3d0; display:inline-flex; align-items:center; gap:0.35rem;">
          ✓ Completed
        </span>`;
    } else {
      actionArea.innerHTML = `
        <span class="badge" style="background:#ecfdf5; color:#047857; font-weight:700; font-size:0.84rem; padding:0.4rem 0.85rem; border-radius:999px; border:1px solid #a7f3d0;">
          ✓ Enrolled (In Progress)
        </span>`;
    }

    // Final Assessment Section Gate & Status
    const finalSection = document.getElementById('finalAssessmentSection');
    if (finalSection) {
      if (!detail.enrolled) {
        finalSection.style.display = 'none';
      } else {
        finalSection.style.display = 'block';
        if (detail.finalAssessmentPassed) {
          finalSection.innerHTML = `
            <div class="card" style="background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%); border: 1.5px solid #86efac; border-radius: 16px; padding: 1.6rem 1.8rem; box-shadow: 0 4px 16px rgba(16,185,129,0.06);">
              <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div style="max-width: 620px;">
                  <span style="font-size: 0.74rem; font-weight: 800; color: #059669; letter-spacing: 0.06em; text-transform: uppercase;">✅ Final Assessment Passed</span>
                  <h3 style="font-size: 1.25rem; font-weight: 800; color: #064e3b; margin: 0.25rem 0 0.4rem 0;">Course Final Assessment</h3>
                  <p style="color: #047857; font-size: 0.88rem; margin: 0; line-height: 1.55;">
                    🎯 <strong>Final Assessment Score:</strong> ${detail.finalScore}% (Passing Score: ${passingScore}% required) &bull;
                    <strong>Status:</strong> Passed with academic distinction.
                  </p>
                </div>
                <div>
                  <a href="/student/quiz.html?courseId=${detail.id}&mode=FINAL_ASSESSMENT" class="btn btn-outline" style="border-color:#86efac; color:#064e3b; font-weight:700; padding:0.65rem 1.2rem; background:#ffffff;">
                    Review / Retake Assessment &rarr;
                  </a>
                </div>
              </div>
            </div>
          `;
        } else if (isUnlocked) {
          finalSection.innerHTML = `
            <div class="card" style="background: linear-gradient(135deg, #eff6ff 0%, #f0fdf4 100%); border: 1.5px solid #93c5fd; border-radius: 16px; padding: 1.6rem 1.8rem; box-shadow: 0 4px 16px rgba(37,99,235,0.08);">
              <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div style="max-width: 620px;">
                  <span style="font-size: 0.74rem; font-weight: 800; color: #2563eb; letter-spacing: 0.06em; text-transform: uppercase;">🎓 Unlocked &bull; Ready to Take</span>
                  <h3 style="font-size: 1.25rem; font-weight: 800; color: #0f172a; margin: 0.25rem 0 0.4rem 0;">Final Course Assessment</h3>
                  <p style="color: #334155; font-size: 0.88rem; margin: 0; line-height: 1.5;">
                    All required lessons are completed! You can now take the comprehensive final assessment to evaluate your knowledge and complete the course.
                    <br><strong>Passing Score: ${passingScore}%</strong> &bull; 18 questions &bull; 25 minutes.
                    ${detail.finalScore != null ? `<br><span style="color:#b91c1c; font-weight:600;">Previous score: ${detail.finalScore}%. Retake to achieve &ge; ${passingScore}%.</span>` : ''}
                  </p>
                </div>
                <div>
                  <a href="/student/quiz.html?courseId=${detail.id}&mode=FINAL_ASSESSMENT" class="btn btn-primary" style="padding: 0.75rem 1.45rem; font-weight: 700; display: inline-flex; align-items: center; gap: 0.4rem; box-shadow: 0 4px 14px rgba(37, 99, 235, 0.25);">
                    <span>Take Final Assessment</span> &rarr;
                  </a>
                </div>
              </div>
            </div>
          `;
        } else {
          finalSection.innerHTML = `
            <div class="card" style="background: #f8fafc; border: 1.5px dashed #cbd5e1; border-radius: 16px; padding: 1.6rem 1.8rem;">
              <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div style="max-width: 620px;">
                  <span style="font-size: 0.74rem; font-weight: 800; color: #64748b; letter-spacing: 0.06em; text-transform: uppercase;">🔒 Final Assessment Locked</span>
                  <h3 style="font-size: 1.25rem; font-weight: 800; color: #475569; margin: 0.25rem 0 0.4rem 0;">Final Course Assessment</h3>
                  <p style="color: #64748b; font-size: 0.88rem; margin: 0; line-height: 1.5;">
                    The final assessment is locked until all required lessons are completed.
                    Complete all <strong>${totalLessons} lessons</strong> to unlock (${completedLessons}/${totalLessons} completed).
                    A minimum passing score of <strong>${passingScore}%</strong> is required to complete the course.
                  </p>
                </div>
                <div>
                  <button class="btn btn-outline" disabled style="opacity: 0.6; cursor: not-allowed; background: #e2e8f0; color: #64748b; border: 1px solid #cbd5e1; padding: 0.75rem 1.25rem; font-weight: 700;">
                    🔒 Complete All Lessons to Unlock
                  </button>
                </div>
              </div>
            </div>
          `;
        }
      }
    }

    // Identify next unfinished lesson in curriculum order for subtle highlighting
    let nextUnfinishedLessonId = null;
    if (detail.enrolled && detail.topics) {
      for (const t of detail.topics) {
        if (t.lessons) {
          for (const l of t.lessons) {
            if (!isLessonCompleted(l.id)) {
              nextUnfinishedLessonId = l.id;
              break;
            }
          }
        }
        if (nextUnfinishedLessonId) break;
      }
    }

    // Group curriculum into Modules, Topics, and Lessons
    const modulesDef = COURSE_MODULE_DEFINITIONS[detail.id] || [];
    let modulesList = [];

    if (modulesDef.length > 0) {
      modulesDef.forEach(mDef => {
        const moduleTopics = detail.topics.filter(t => mDef.topicOrderIndices.includes(t.orderIndex));
        if (moduleTopics.length > 0) {
          modulesList.push({
            moduleNumber: mDef.moduleNumber,
            title: mDef.title,
            description: mDef.description,
            topics: moduleTopics
          });
        }
      });
    } else {
      // Default: single module or pair grouping for dynamic courses
      modulesList.push({
        moduleNumber: 1,
        title: "Course Curriculum",
        description: "Study concepts sequentially through topics and lessons.",
        topics: detail.topics
      });
    }

    const topicsContainer = document.getElementById('topicsAccordion');
    topicsContainer.innerHTML = modulesList.map(mod => {
      // Compute module-level progress
      let modCompleted = 0;
      let modTotal = 0;
      mod.topics.forEach(t => {
        if (t.lessons) {
          modTotal += t.lessons.length;
          modCompleted += t.lessons.filter(l => isLessonCompleted(l.id)).length;
        }
      });
      const modProgressBadge = detail.enrolled && modTotal > 0
        ? `<span style="font-size:0.75rem; font-weight:700; color:#1d4ed8; background:#eff6ff; border:1px solid #bfdbfe; padding:0.22rem 0.65rem; border-radius:999px;">${modCompleted}/${modTotal} Lessons Completed</span>`
        : '';

      const topicsHtml = mod.topics.map(t => {
        const completedCount = t.lessons ? t.lessons.filter(l => isLessonCompleted(l.id)).length : 0;
        const totalCount = t.lessons ? t.lessons.length : 0;
        const topicPct = totalCount > 0 ? Math.round((completedCount / totalCount) * 100) : 0;

        let progressPill = '';
        if (detail.enrolled && totalCount > 0) {
          if (completedCount === totalCount) {
            progressPill = `<span class="badge" style="background:#ecfdf5; color:#047857; font-size:0.72rem; font-weight:700; padding:0.18rem 0.55rem; border:1px solid #a7f3d0; border-radius:999px;">✓ Completed (${completedCount}/${totalCount})</span>`;
          } else if (completedCount > 0) {
            progressPill = `<span class="badge" style="background:#eff6ff; color:#1d4ed8; font-size:0.72rem; font-weight:700; padding:0.18rem 0.55rem; border:1px solid #bfdbfe; border-radius:999px;">In Progress (${completedCount}/${totalCount})</span>`;
          } else {
            progressPill = `<span class="badge" style="background:#f8fafc; color:#64748b; font-size:0.72rem; font-weight:600; padding:0.18rem 0.55rem; border:1px solid #e2e8f0; border-radius:999px;">0/${totalCount} Completed</span>`;
          }
        }

        const lessonsHtml = (t.lessons || []).map(l => {
          const done = isLessonCompleted(l.id);
          const isCurrent = detail.enrolled && l.id === nextUnfinishedLessonId;

          let rowClass = 'lesson-item-row incomplete';
          let statusBadge = `<span class="lesson-indicator-pill pill-todo">○ Incomplete</span>`;
          let rightAction = `<span style="font-size:0.76rem; color:#64748b;">⏱ ${l.estimatedMinutes} mins →</span>`;

          if (done) {
            rowClass = 'lesson-item-row completed';
            statusBadge = `<span class="lesson-indicator-pill pill-done">✓ Completed</span>`;
            rightAction = `<span style="font-size:0.76rem; color:#047857; font-weight:600;">⏱ ${l.estimatedMinutes} mins &bull; Review</span>`;
          } else if (isCurrent) {
            rowClass = 'lesson-item-row current';
            statusBadge = `<span class="lesson-indicator-pill pill-current">★ Current Lesson</span>`;
            rightAction = `<span style="font-size:0.78rem; font-weight:700; color:#2563eb; display:inline-flex; align-items:center; gap:0.25rem;">Study Now &rarr;</span>`;
          }

          return `
            <a href="/student/lesson.html?id=${l.id}&courseId=${detail.id}" class="${rowClass}">
              <div class="lesson-left-content">
                <span>📖 <strong>${l.title}</strong></span>
                ${statusBadge}
              </div>
              <div>
                ${rightAction}
              </div>
            </a>
          `;
        }).join('');

        const progressBarHtml = detail.enrolled && totalCount > 0 ? `
          <div class="topic-progress-track">
            <div class="topic-progress-fill" style="width: ${topicPct}%; background: ${topicPct === 100 ? '#10b981' : '#2563eb'};"></div>
          </div>
        ` : '';

        return `
          <div class="topic-card">
            <div class="topic-header-row">
              <div class="topic-title-group">
                <span class="topic-order-pill">Topic ${t.orderIndex}</span>
                <h4 class="topic-name">${t.title}</h4>
                ${progressPill}
              </div>
              <div class="topic-actions-group">
                <a href="/student/practice.html?topicId=${t.id}" class="btn-action-topic-outline" title="Practice topic questions with hints and step-by-step explanations">
                  ⚡ Practice
                </a>
                <a href="/student/quiz.html?topicId=${t.id}&mode=CHAPTER_QUIZ" class="btn-action-topic-primary" title="Test your topic understanding with an assessment quiz">
                  📝 Take Quiz
                </a>
              </div>
            </div>
            ${progressBarHtml}
            <p class="topic-desc-text">${t.description || ''}</p>
            <div class="lessons-list-stack">
              ${lessonsHtml}
            </div>
          </div>
        `;
      }).join('');

      return `
        <div class="course-module-group" style="margin-bottom: 2rem;">
          <div class="module-header-box">
            <div>
              <span class="module-badge-tag">Module ${mod.moduleNumber}</span>
              <h3 class="module-title-text">${mod.title}</h3>
              ${mod.description ? `<p class="module-desc-text">${mod.description}</p>` : ''}
            </div>
            ${modProgressBadge}
          </div>
          <div class="module-topics-list">
            ${topicsHtml}
          </div>
        </div>
      `;
    }).join('');
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

async function completeCurrentLesson() {
  if (!currentLoadedLesson) return;

  // Persist lesson completion directly to database
  try {
    await apiFetch(`/lessons/${currentLoadedLesson.id}/complete`, { method: 'POST' });
  } catch (err) {
    console.error('Failed to record lesson completion on server:', err);
  }

  markLessonAsCompleted(currentLoadedLesson.id);
  updateLessonCompletionUI();

  // Populate completion modal
  const modal = document.getElementById('lessonCompletionModal');
  const subTitle = document.getElementById('completionModalSubtitle');
  if (subTitle) {
    subTitle.innerHTML = `Great job! You've successfully finished <strong>${currentLoadedLesson.title}</strong>.`;
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

function showLessonError(title, message, courseId = null) {
  const lessonTitle = document.getElementById('lessonTitle');
  if (lessonTitle) lessonTitle.textContent = `⚠️ ${title}`;

  const topicBadge = document.getElementById('topicBadge');
  if (topicBadge) {
    topicBadge.textContent = 'Notice';
    topicBadge.style.display = 'inline-block';
    topicBadge.style.background = '#fef2f2';
    topicBadge.style.color = '#dc2626';
  }

  const lessonStatusBadge = document.getElementById('lessonStatusBadge');
  if (lessonStatusBadge) lessonStatusBadge.style.display = 'none';

  const lessonTime = document.getElementById('lessonTime');
  if (lessonTime) lessonTime.textContent = '';

  const topCompleteBtn = document.getElementById('topCompleteBtn');
  if (topCompleteBtn) topCompleteBtn.style.display = 'none';

  const bookmarkBtn = document.getElementById('bookmarkLessonBtn');
  if (bookmarkBtn) bookmarkBtn.style.display = 'none';

  const kpBox = document.querySelector('.key-points-box');
  if (kpBox) kpBox.style.display = 'none';

  const bottomActions = document.querySelector('.lesson-bottom-actions');
  if (bottomActions) bottomActions.style.display = 'none';

  const notesPanel = document.getElementById('lessonNoteContent')?.closest('.card');
  if (notesPanel) notesPanel.style.display = 'none';

  const lessonBody = document.getElementById('lessonBody');
  if (lessonBody) {
    const courseUrl = courseId ? `/student/course-detail.html?id=${courseId}` : '/student/courses.html';
    lessonBody.innerHTML = `
      <div style="background:#fff1f2; border:1px solid #fecdd3; border-radius:14px; padding:2.5rem 1.75rem; text-align:center; margin:1rem 0;">
        <div style="font-size:2.8rem; margin-bottom:0.75rem;">⚠️</div>
        <h3 style="color:#9f1239; margin-bottom:0.6rem; font-size:1.3rem; font-weight:700;">${title}</h3>
        <p style="color:#4c0519; margin-bottom:1.5rem; font-size:0.95rem; max-width:540px; margin-left:auto; margin-right:auto; line-height:1.55;">
          ${message}
        </p>
        <div style="display:flex; gap:0.75rem; justify-content:center; flex-wrap:wrap;">
          <a href="${courseUrl}" class="btn btn-primary" style="padding:0.6rem 1.25rem;">← Back to Course Workspace</a>
          <a href="/student/dashboard.html" class="btn btn-outline" style="padding:0.6rem 1.25rem;">Student Dashboard</a>
          <button onclick="location.reload()" class="btn btn-secondary" style="padding:0.6rem 1.25rem;">↻ Retry</button>
        </div>
      </div>
    `;
  }
}

async function loadLessonReader() {
  const params = new URLSearchParams(window.location.search);
  let lessonId = params.get('id') || params.get('lessonId');
  let courseId = params.get('courseId');

  // If no direct lessonId, resolve next unfinished lesson from course
  if (!lessonId) {
    try {
      if (!courseId) {
        courseId = localStorage.getItem('edunest_activeCourseId');
      }
      if (!courseId) {
        const myCourses = await apiFetch('/courses/my').catch(() => []);
        if (myCourses && myCourses.length > 0) {
          courseId = myCourses[0].id;
        }
      }

      if (courseId) {
        const [courseDetail, completedIds] = await Promise.all([
          apiFetch(`/courses/${courseId}`),
          apiFetch(`/lessons/completed?courseId=${courseId}`).catch(() => [])
        ]);

        const completedSet = new Set(Array.isArray(completedIds) ? completedIds : []);
        const topics = (courseDetail && courseDetail.topics) ? courseDetail.topics : [];

        for (const t of topics) {
          const lessons = t.lessons || [];
          for (const l of lessons) {
            if (!completedSet.has(l.id)) {
              lessonId = l.id;
              break;
            }
          }
          if (lessonId) break;
        }

        // If all completed, open first lesson
        if (!lessonId && topics.length > 0 && topics[0].lessons && topics[0].lessons.length > 0) {
          lessonId = topics[0].lessons[0].id;
        }

        if (lessonId && window.history && window.history.replaceState) {
          window.history.replaceState(null, '', `/student/lesson.html?id=${lessonId}&courseId=${courseId}`);
        }
      }
    } catch (resolveErr) {
      console.warn('Could not auto-resolve next unfinished lesson:', resolveErr);
    }
  }

  if (!lessonId) {
    showLessonError(
      'No Lesson Specified',
      'No lesson was selected to display. Please return to your course syllabus to choose a lesson to study.',
      courseId
    );
    return;
  }

  try {
    const lesson = await apiFetch(`/lessons/${lessonId}`);
    if (!lesson || !lesson.id) {
      showLessonError(
        'Lesson Not Found',
        `The requested lesson (#${lessonId}) could not be found. It may have been moved or removed.`,
        courseId
      );
      return;
    }

    currentLoadedLesson = lesson;

    // Refresh completed lesson ids from server so state is accurate across reloads & sessions
    try {
      const completedList = await apiFetch(`/lessons/completed?courseId=${lesson.courseId}`);
      if (Array.isArray(completedList)) {
        localStorage.setItem('edunest_completed_lessons', JSON.stringify(completedList));
      }
    } catch (syncErr) {
      console.warn('Could not sync completed lessons:', syncErr);
    }

    const breadcrumb = document.getElementById('breadcrumbCourse');
    if (breadcrumb) breadcrumb.href = `/student/course-detail.html?id=${lesson.courseId}`;

    const topicBadge = document.getElementById('topicBadge');
    if (topicBadge) {
      topicBadge.textContent = lesson.topicTitle;
      topicBadge.style.display = 'inline-block';
      topicBadge.style.background = '#eff6ff';
      topicBadge.style.color = '#2563eb';
    }

    const lessonStatusBadge = document.getElementById('lessonStatusBadge');
    if (lessonStatusBadge) lessonStatusBadge.style.display = 'inline-block';

    const lessonTitle = document.getElementById('lessonTitle');
    if (lessonTitle) lessonTitle.textContent = lesson.title;

    const lessonTime = document.getElementById('lessonTime');
    if (lessonTime) lessonTime.textContent = `⏱ Estimated Study Time: ${lesson.estimatedMinutes} mins`;

    const lessonBody = document.getElementById('lessonBody');
    if (lessonBody) lessonBody.innerHTML = lesson.content;

    const kpBox = document.querySelector('.key-points-box');
    const kpList = document.getElementById('keyPointsList');
    if (lesson.keyPoints && lesson.keyPoints.trim()) {
      const points = lesson.keyPoints.split(';').map(s => s.trim()).filter(Boolean);
      if (points.length > 0) {
        if (kpList) kpList.innerHTML = points.map(p => `<li style="margin-bottom:0.4rem;">${p}</li>`).join('');
        if (kpBox) kpBox.style.display = 'block';
      } else {
        if (kpBox) kpBox.style.display = 'none';
      }
    } else {
      if (kpBox) kpBox.style.display = 'none';
    }

    const prevBtn = document.getElementById('prevLessonBtn');
    const nextBtn = document.getElementById('nextLessonBtn');
    const finishPracticeBtn = document.getElementById('finishTopicPracticeBtn');
    const bottomActions = document.querySelector('.lesson-bottom-actions');
    if (bottomActions) bottomActions.style.display = 'flex';

    if (prevBtn) {
      if (lesson.previousLessonId) {
        prevBtn.href = `/student/lesson.html?id=${lesson.previousLessonId}&courseId=${lesson.courseId}`;
        prevBtn.classList.remove('hidden');
      } else {
        prevBtn.classList.add('hidden');
      }
    }

    if (nextBtn) {
      if (lesson.nextLessonId) {
        nextBtn.href = `/student/lesson.html?id=${lesson.nextLessonId}&courseId=${lesson.courseId}`;
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

    const topCompleteBtn = document.getElementById('topCompleteBtn');
    if (topCompleteBtn) topCompleteBtn.style.display = 'inline-block';
    const bookmarkBtn = document.getElementById('bookmarkLessonBtn');
    if (bookmarkBtn) bookmarkBtn.style.display = 'inline-block';

    updateLessonCompletionUI();

    // Initialize personal notes and bookmark for this lesson
    if (typeof initLessonNotesAndBookmark === 'function') {
      initLessonNotesAndBookmark(lesson.id);
    }
  } catch (err) {
    console.error('Failed to load lesson:', err);
    showLessonError(
      'Unable to Load Lesson',
      `Failed to load lesson content: ${err.message || 'Unknown network error'}. Please check your connection or try again.`,
      courseId
    );
  }
}

