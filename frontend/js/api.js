// Centralized API Wrapper & Offline Demo Engine for EduNest V2
// Defaults to localhost:8080 in local development, or relative /api in production (with automatic demo fallback on Vercel)

// Check for ?api= query parameter to automatically configure backend in 1 click
(function() {
  if (typeof window !== 'undefined' && window.location.search) {
    try {
      const urlParams = new URLSearchParams(window.location.search);
      const paramApi = urlParams.get('api');
      if (paramApi) {
        const clean = paramApi.trim().replace(/\/+$/, '');
        localStorage.setItem('edunest_api_url', clean);
        const url = new URL(window.location.href);
        url.searchParams.delete('api');
        window.history.replaceState({}, document.title, url.toString());
        console.log('[EduNest] Backend URL automatically configured:', clean);
      }
    } catch (e) {}
  }
})();

const API_BASE = (function() {
  if (typeof window !== 'undefined' && window.EDUNEST_API_BASE) {
    return window.EDUNEST_API_BASE;
  }
  const stored = typeof localStorage !== 'undefined' ? localStorage.getItem('edunest_api_url') : null;
  if (stored) return stored;

  if (typeof window !== 'undefined') {
    const host = window.location.hostname || '';
    const proto = window.location.protocol || '';
    // Localhost, LAN dev, or direct filesystem access
    if (host === 'localhost' || host === '127.0.0.1' || host === '0.0.0.0' || proto === 'file:' || host.startsWith('192.168.') || host.startsWith('10.')) {
      return 'http://localhost:8080/api';
    }
  }
  return '/api';
})();

// Prompt helper to easily configure a custom backend URL
function promptBackendUrl() {
  const current = localStorage.getItem('edunest_api_url') || API_BASE;
  const next = window.prompt(
    'EduNest Backend API URL:\n(Enter your live backend URL, or leave empty to use automatic localhost / Vercel detection)',
    current
  );
  if (next !== null) {
    const trimmed = next.trim();
    if (trimmed) {
      localStorage.setItem('edunest_api_url', trimmed);
      alert('Backend API URL updated to:\n' + trimmed + '\n\nThe page will now reload.');
    } else {
      localStorage.removeItem('edunest_api_url');
      alert('Reset to automatic detection.\n\nThe page will now reload.');
    }
    window.location.reload();
  }
}

// Standalone Mock Fallback Engine (Active when backend is not deployed on static hosts like Vercel)
function handleOfflineFallback(endpoint, options = {}) {
  const method = (options.method || 'GET').toUpperCase();
  let body = {};
  if (options.body) {
    try {
      body = typeof options.body === 'string' ? JSON.parse(options.body) : options.body;
    } catch (e) {
      body = {};
    }
  }

  // 1. Auth Login Fallback
  if (endpoint.startsWith('/auth/login') && method === 'POST') {
    const email = (body.email || 'student@edunest.com').trim().toLowerCase();
    const users = JSON.parse(localStorage.getItem('edunest_registered_users') || '[]');
    const registered = users.find(u => u.email && u.email.toLowerCase() === email);

    let role = 'STUDENT';
    let name = 'Student';
    let goal = 'Master Core Java & Software Design';

    if (registered) {
      role = registered.role || 'STUDENT';
      name = registered.name || 'Student';
      goal = registered.learningGoal || goal;
    } else if (email.includes('admin')) {
      role = 'ADMIN';
      name = 'Administrator';
    } else if (email.includes('instructor') || email.includes('prof')) {
      role = 'INSTRUCTOR';
      name = 'Instructor';
    } else {
      const emailPrefix = email.split('@')[0] || 'student';
      name = emailPrefix.charAt(0).toUpperCase() + emailPrefix.slice(1);
    }

    return {
      token: 'demo-jwt-' + btoa(email) + '-' + Date.now(),
      userId: registered ? registered.id : 1,
      name: name,
      email: email,
      role: role,
      learningGoal: goal
    };
  }

  // 2. Auth Register Fallback
  if (endpoint.startsWith('/auth/register') && method === 'POST') {
    const name = body.name || 'New Learner';
    const email = (body.email || 'student@example.com').trim().toLowerCase();
    const role = body.role || 'STUDENT';
    const learningGoal = body.learningGoal || 'Master Programming Fundamentals';

    const users = JSON.parse(localStorage.getItem('edunest_registered_users') || '[]');
    const newUser = {
      id: Date.now(),
      name,
      email,
      role,
      learningGoal
    };
    users.push(newUser);
    localStorage.setItem('edunest_registered_users', JSON.stringify(users));

    return {
      token: 'demo-jwt-' + btoa(email) + '-' + Date.now(),
      userId: newUser.id,
      name: newUser.name,
      email: newUser.email,
      role: newUser.role,
      learningGoal: newUser.learningGoal
    };
  }

  // 3. Current User Profile
  if (endpoint.startsWith('/auth/me')) {
    const email = localStorage.getItem('edunest_email') || 'student@edunest.com';
    const name = localStorage.getItem('edunest_name') || 'Student';
    const role = localStorage.getItem('edunest_role') || 'STUDENT';
    const goal = localStorage.getItem('edunest_goal') || 'Master Programming Fundamentals';
    return {
      token: localStorage.getItem('edunest_token') || 'demo-token',
      userId: parseInt(localStorage.getItem('edunest_userId') || '1', 10),
      name,
      email,
      role,
      learningGoal: goal
    };
  }

  // 4. Update Learning Goal
  if (endpoint.startsWith('/auth/goal') || endpoint.startsWith('/students/me/goal')) {
    const newGoal = body.learningGoal || body.goal || '';
    if (newGoal) localStorage.setItem('edunest_goal', newGoal);
    return { success: true, learningGoal: newGoal };
  }

  // 5. Course Catalog
  if (endpoint === '/courses' || endpoint === '/courses/') {
    return [
      {
        id: 1,
        title: 'Java Programming - From Fundamentals to OOP',
        description: 'Master variables, memory structures, object-oriented design, inheritance, polymorphism, and algorithms with 6 hands-on coding challenges.',
        estimatedHours: 24,
        isPublished: true,
        instructorName: 'Prof. Vikram Verma'
      },
      {
        id: 2,
        title: 'Python & Data Science Foundations',
        description: 'Learn clean Python syntax, sequence manipulation, dictionary lookups, list comprehensions, and data analysis fundamentals.',
        estimatedHours: 20,
        isPublished: true,
        instructorName: 'Dr. Neha Patel'
      },
      {
        id: 3,
        title: 'Full-Stack Web Development',
        description: 'Build accessible semantic web pages using modern HTML5, responsive CSS Flexbox/Grid architectures, and modern JavaScript.',
        estimatedHours: 32,
        isPublished: true,
        instructorName: 'Sarah Jenkins'
      }
    ];
  }

  // 6. Enrolled Courses
  if (endpoint === '/courses/my' || endpoint === '/courses/my/') {
    const enrolledIds = JSON.parse(localStorage.getItem('edunest_enrolled_courses') || '[1]');
    const all = handleOfflineFallback('/courses');
    return all.filter(c => enrolledIds.includes(c.id));
  }

  // 7. Enroll in Course
  if (endpoint.match(/\/courses\/(\d+)\/enroll/)) {
    const m = endpoint.match(/\/courses\/(\d+)\/enroll/);
    const courseId = parseInt(m[1], 10);
    const enrolled = JSON.parse(localStorage.getItem('edunest_enrolled_courses') || '[1]');
    if (!enrolled.includes(courseId)) enrolled.push(courseId);
    localStorage.setItem('edunest_enrolled_courses', JSON.stringify(enrolled));
    return { success: true, message: 'Enrolled successfully' };
  }

  // 8. Single Course Detail (with Topics & Lessons)
  if (endpoint.match(/\/courses\/(\d+)$/)) {
    const courseId = parseInt(endpoint.match(/\/courses\/(\d+)$/)[1], 10);
    if (courseId === 1) {
      return {
        id: 1,
        title: 'Java Programming - From Fundamentals to OOP',
        description: 'Master variables, memory structures, object-oriented design, inheritance, polymorphism, and algorithms with 6 hands-on coding challenges.',
        instructorName: 'Prof. Vikram Verma',
        topics: [
          {
            id: 1,
            title: 'Java Fundamentals & Syntax',
            orderIndex: 1,
            lessons: [
              { id: 1, title: 'Variables, Data Types & Memory Representation', orderIndex: 1, estimatedMinutes: 15 },
              { id: 2, title: 'Operators, Arithmetic & Type Casting', orderIndex: 2, estimatedMinutes: 20 },
              { id: 3, title: 'Console I/O & Scanner Mechanics', orderIndex: 3, estimatedMinutes: 15 }
            ]
          },
          {
            id: 2,
            title: 'Control Flow, Loops & Logic',
            orderIndex: 2,
            lessons: [
              { id: 4, title: 'Conditional Branching: if, else-if & switch', orderIndex: 1, estimatedMinutes: 20 },
              { id: 5, title: 'Iteration: for, while & do-while Loops', orderIndex: 2, estimatedMinutes: 25 },
              { id: 6, title: 'Break, Continue & Nested Loop Mechanics', orderIndex: 3, estimatedMinutes: 20 }
            ]
          },
          {
            id: 3,
            title: 'Arrays & Memory Management',
            orderIndex: 3,
            lessons: [
              { id: 7, title: '1D Arrays, Heap Allocation & Traversal', orderIndex: 1, estimatedMinutes: 25 },
              { id: 8, title: 'Multidimensional Arrays & Matrix Problems', orderIndex: 2, estimatedMinutes: 30 },
              { id: 9, title: 'Array Copying, References & Boundary Errors', orderIndex: 3, estimatedMinutes: 20 }
            ]
          },
          {
            id: 4,
            title: 'Methods, Stack Execution & Recursion',
            orderIndex: 4,
            lessons: [
              { id: 10, title: 'Method Signatures, Pass-by-Value & Overloading', orderIndex: 1, estimatedMinutes: 20 },
              { id: 11, title: 'Stack Frames, Variable Scope & Lifetime', orderIndex: 2, estimatedMinutes: 25 },
              { id: 12, title: 'Recursion, Base Cases & Call Stack Analysis', orderIndex: 3, estimatedMinutes: 30 }
            ]
          },
          {
            id: 5,
            title: 'Object-Oriented Programming Core',
            orderIndex: 5,
            lessons: [
              { id: 13, title: 'Classes, Objects, Constructors & this Keyword', orderIndex: 1, estimatedMinutes: 30 },
              { id: 14, title: 'Encapsulation, Access Modifiers & Getters/Setters', orderIndex: 2, estimatedMinutes: 25 },
              { id: 15, title: 'Inheritance, super Keyword & Object Hierarchy', orderIndex: 3, estimatedMinutes: 30 }
            ]
          },
          {
            id: 6,
            title: 'Polymorphism & Abstraction',
            orderIndex: 6,
            lessons: [
              { id: 16, title: 'Method Overriding & Runtime Polymorphism', orderIndex: 1, estimatedMinutes: 30 },
              { id: 17, title: 'Abstract Classes vs Interfaces & Default Methods', orderIndex: 2, estimatedMinutes: 35 },
              { id: 18, title: 'Clean Architecture, Exceptions & Best Practices', orderIndex: 3, estimatedMinutes: 30 }
            ]
          }
        ]
      };
    } else {
      return {
        id: courseId,
        title: courseId === 2 ? 'Python & Data Science Foundations' : 'Full-Stack Web Development',
        description: 'Interactive programming curriculum with lessons, concept drills, and online judge challenges.',
        instructorName: 'EduNest Faculty',
        topics: [
          {
            id: courseId * 10 + 1,
            title: 'Foundations & Architecture',
            orderIndex: 1,
            lessons: [
              { id: courseId * 100 + 1, title: 'Syntax, Data Types & Runtime', orderIndex: 1, estimatedMinutes: 20 },
              { id: courseId * 100 + 2, title: 'Control Flow & Logic Patterns', orderIndex: 2, estimatedMinutes: 25 }
            ]
          }
        ]
      };
    }
  }

  // 9. Completed Lessons
  if (endpoint.startsWith('/lessons/completed')) {
    const list = JSON.parse(localStorage.getItem('edunest_completed_lessons') || '[1, 2]');
    return list;
  }

  // 10. Toggle Lesson Complete
  if (endpoint.match(/\/lessons\/(\d+)\/complete/)) {
    const lessonId = parseInt(endpoint.match(/\/lessons\/(\d+)\/complete/)[1], 10);
    const list = JSON.parse(localStorage.getItem('edunest_completed_lessons') || '[1, 2]');
    if (!list.includes(lessonId)) list.push(lessonId);
    localStorage.setItem('edunest_completed_lessons', JSON.stringify(list));
    return { completed: true, lessonId };
  }

  // 11. Single Lesson Content
  if (endpoint.match(/\/lessons\/(\d+)$/)) {
    const lessonId = parseInt(endpoint.match(/\/lessons\/(\d+)$/)[1], 10);
    return {
      id: lessonId,
      title: 'Lesson ' + lessonId + ': Core Concept & Practice',
      content: '### Welcome to Lesson ' + lessonId + '\n\nIn this lesson, you will master essential software engineering concepts through practical explanations, interactive code samples, and self-paced drills.\n\n```java\npublic class Solution {\n    public static void main(String[] args) {\n        System.out.println("EduNest Lesson ' + lessonId + ' active!");\n    }\n}\n```',
      estimatedMinutes: 20,
      topicId: 1
    };
  }

  // 12. Certificates
  if (endpoint.startsWith('/certificates')) {
    const certs = JSON.parse(localStorage.getItem('edunest_certificates') || '[]');
    return certs;
  }

  // 13. Bookmarks & Notes
  if (endpoint.startsWith('/bookmarks') || endpoint.startsWith('/notes')) {
    return [];
  }

  // Default empty object
  return {};
}

async function apiFetch(endpoint, options = {}) {
  const token = localStorage.getItem('edunest_token');
  const headers = {
    'Content-Type': 'application/json',
    'bypass-tunnel-reminder': 'true',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...(options.headers || {})
  };

  let response;
  try {
    response = await fetch(`${API_BASE}${endpoint}`, {
      ...options,
      headers
    });
  } catch (networkErr) {
    // If network connection failed (e.g. backend server offline or CORS restriction on static host)
    console.warn(`[EduNest API] Network unreachable for ${endpoint}. Using standalone fallback.`);
    return handleOfflineFallback(endpoint, options);
  }

  if (response.status === 401) {
    // Don't auto-redirect on login or register endpoint
    if (!endpoint.startsWith('/auth/login') && !endpoint.startsWith('/auth/register')) {
      localStorage.clear();
      window.location.href = '/login.html';
      throw new Error('Session expired. Please log in again.');
    }
  }

  if (response.status === 204) {
    return null;
  }

  // If endpoint returned 404 (e.g. static hosting on Vercel where Spring Boot is not running)
  if (response.status === 404) {
    console.warn(`[EduNest API] Endpoint ${endpoint} returned 404. Using standalone fallback.`);
    return handleOfflineFallback(endpoint, options);
  }

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(data.error || data.message || `Request failed (${response.status})`);
  }

  return data;
}

// Convenient helper supporting both: apiRequest(url, method, body) and apiRequest(url, options)
async function apiRequest(endpoint, methodOrOptions = 'GET', body = null) {
  if (typeof methodOrOptions === 'object' && methodOrOptions !== null) {
    return apiFetch(endpoint, methodOrOptions);
  }
  const method = (typeof methodOrOptions === 'string' ? methodOrOptions : 'GET').toUpperCase();
  const options = { method };
  if (body !== null && body !== undefined) {
    options.body = typeof body === 'string' ? body : JSON.stringify(body);
  }
  return apiFetch(endpoint, options);
}

// Ensure global accessibility across all scripts
window.API_BASE = API_BASE;
window.apiFetch = apiFetch;
window.apiRequest = apiRequest;
window.promptBackendUrl = promptBackendUrl;
window.handleOfflineFallback = handleOfflineFallback;
