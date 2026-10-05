// Course Catalog, Enrollment, Course Detail & Lesson Reader Logic (Phases 2 & 3)

let allPublishedCourses = [];

async function loadCourseCatalog() {
  try {
    const [catalog, myCourses] = await Promise.all([
      apiFetch('/courses'),
      apiFetch('/courses/my').catch(() => [])
    ]);

    allPublishedCourses = catalog;
    const enrolledIds = new Set(myCourses.map(c => c.id));

    renderCourseList(catalog, enrolledIds, 'catalogGrid');
    renderCourseList(myCourses, enrolledIds, 'myCoursesGrid');
  } catch (err) {
    console.error('Error loading courses:', err);
  }
}

function renderCourseList(courses, enrolledIds, containerId) {
  const container = document.getElementById(containerId);
  if (!container) return;

  if (!courses || courses.length === 0) {
    container.innerHTML = `<p style="color: var(--text-muted);">No courses found.</p>`;
    return;
  }

  container.innerHTML = courses.map(c => {
    const isEnrolled = enrolledIds.has(c.id);
    return `
      <div class="card" style="display:flex; flex-direction:column; justify-content:space-between;">
        <div>
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.65rem;">
            <span class="badge badge-student">${c.topicCount} Topics</span>
            <span style="font-size:0.82rem; color:var(--text-muted);">👥 ${c.enrollmentCount} enrolled</span>
          </div>
          <h3 style="margin-bottom:0.4rem;">${c.title}</h3>
          <p style="font-size:0.88rem; color:var(--text-muted); margin-bottom:0.75rem;">
            Instructor: <strong>${c.instructorName}</strong>
          </p>
          <p style="font-size:0.92rem; color:var(--text-muted); margin-bottom:1.25rem;">
            ${c.description || ''}
          </p>
        </div>
        <div style="display:flex; gap:0.65rem;">
          ${isEnrolled
            ? `<a href="/student/course-detail.html?id=${c.id}" class="btn btn-primary" style="width:100%;">Continue Learning →</a>`
            : `<button class="btn btn-primary" style="flex:1;" onclick="enrollInCourse(${c.id})">Enroll Now</button>
               <a href="/student/course-detail.html?id=${c.id}" class="btn btn-outline">Syllabus</a>`
          }
        </div>
      </div>
    `;
  }).join('');
}

function filterCourses() {
  const query = (document.getElementById('courseSearch')?.value || '').toLowerCase();
  const filtered = allPublishedCourses.filter(c =>
    c.title.toLowerCase().includes(query) ||
    (c.description && c.description.toLowerCase().includes(query))
  );
  renderCourseList(filtered, new Set(), 'catalogGrid');
}

async function enrollInCourse(courseId) {
  try {
    await apiFetch(`/courses/${courseId}/enroll`, { method: 'POST' });
    localStorage.setItem('edunest_activeCourseId', courseId);
    window.location.href = `/student/course-detail.html?id=${courseId}`;
  } catch (err) {
    alert(err.message);
  }
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
      <div class="card" style="margin-bottom:1rem;">
        <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:0.5rem; margin-bottom:0.5rem;">
          <h3 style="font-size:1.1rem;">Topic ${idx + 1}: ${t.title}</h3>
          <div style="display:flex; gap:0.5rem; align-items:center;">
            <a href="/student/practice.html?topicId=${t.id}" class="btn btn-outline btn-sm">⚡ Practice</a>
            <a href="/student/quiz.html?topicId=${t.id}" class="btn btn-primary btn-sm">📝 Topic Quiz</a>
          </div>
        </div>
        <p style="font-size:0.9rem; color:var(--text-muted); margin-bottom:1rem;">${t.description || ''}</p>
        <div style="display:flex; flex-direction:column; gap:0.5rem;">
          ${t.lessons.map(l => `
            <a href="/student/lesson.html?id=${l.id}"
               style="display:flex; justify-content:space-between; align-items:center; padding:0.75rem 1rem; background:#f8fafc; border:1px solid var(--border); border-radius:8px; text-decoration:none; color:var(--text-main);">
              <span>📖 <strong>${l.title}</strong></span>
              <span style="font-size:0.85rem; color:var(--text-muted);">⏱ ${l.estimatedMinutes} mins →</span>
            </a>
          `).join('')}
        </div>
      </div>
    `).join('');
  } catch (err) {
    console.error('Failed to load course detail:', err);
  }
}

async function loadLessonReader() {
  const params = new URLSearchParams(window.location.search);
  const lessonId = params.get('id');
  if (!lessonId) return;

  try {
    const lesson = await apiFetch(`/lessons/${lessonId}`);
    document.getElementById('breadcrumbCourse').href = `/student/course-detail.html?id=${lesson.courseId}`;
    document.getElementById('topicBadge').textContent = lesson.topicTitle;
    document.getElementById('lessonTitle').textContent = lesson.title;
    document.getElementById('lessonTime').textContent = `⏱ Estimated Study Time: ${lesson.estimatedMinutes} mins`;
    document.getElementById('lessonBody').innerHTML = lesson.content;

    const kpList = document.getElementById('keyPointsList');
    if (lesson.keyPoints) {
      const points = lesson.keyPoints.split(';').map(s => s.trim()).filter(Boolean);
      kpList.innerHTML = points.map(p => `<li style="margin-bottom:0.4rem;">${p}</li>`).join('');
    }

    const prevBtn = document.getElementById('prevLessonBtn');
    const nextBtn = document.getElementById('nextLessonBtn');

    if (lesson.previousLessonId) {
      prevBtn.href = `/student/lesson.html?id=${lesson.previousLessonId}`;
      prevBtn.classList.remove('hidden');
    } else {
      prevBtn.classList.add('hidden');
    }

    if (lesson.nextLessonId) {
      nextBtn.href = `/student/lesson.html?id=${lesson.nextLessonId}`;
      nextBtn.classList.remove('hidden');
    } else {
      nextBtn.classList.add('hidden');
    }
  } catch (err) {
    console.error('Failed to load lesson:', err);
  }
}
