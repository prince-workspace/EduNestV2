/**
 * EduNest Phase 7: Student Lesson Notes & Bookmarks Logic
 */

async function initLessonNotesAndBookmark(lessonId) {
    if (!lessonId) return;

    const noteTextarea = document.getElementById('lessonNoteContent');
    const noteStatus = document.getElementById('noteSaveStatus');
    const bookmarkBtn = document.getElementById('bookmarkLessonBtn');

    // Load existing note for this lesson
    try {
        const existingNote = await apiRequest(`/students/me/notes/lesson/${lessonId}`);
        if (existingNote && existingNote.content && noteTextarea) {
            noteTextarea.value = existingNote.content;
        }
    } catch (e) {
        // No existing note yet
    }

    // Save Note button handler
    const saveNoteBtn = document.getElementById('saveNoteBtn');
    if (saveNoteBtn && noteTextarea) {
        saveNoteBtn.addEventListener('click', async () => {
            const content = noteTextarea.value.trim();
            if (!content) {
                if (noteStatus) noteStatus.textContent = 'Please enter some note text before saving.';
                return;
            }
            try {
                await apiRequest('/students/me/notes', 'POST', {
                    lessonId: Number(lessonId),
                    content
                });
                if (noteStatus) {
                    noteStatus.style.color = '#10b981';
                    noteStatus.textContent = 'Saved to your study notes!';
                }
            } catch (err) {
                if (noteStatus) {
                    noteStatus.style.color = '#ef4444';
                    noteStatus.textContent = `Error: ${err.message}`;
                }
            }
        });
    }

    // Bookmark Lesson button handler
    if (bookmarkBtn) {
        bookmarkBtn.addEventListener('click', async () => {
            try {
                await apiRequest('/students/me/bookmarks', 'POST', {
                    lessonId: Number(lessonId),
                    noteLabel: 'Bookmarked from Lesson Viewer'
                });
                bookmarkBtn.textContent = '★ Bookmarked';
                bookmarkBtn.classList.remove('btn-secondary');
                bookmarkBtn.classList.add('btn-primary');
            } catch (err) {
                alert(`Failed to bookmark lesson: ${err.message}`);
            }
        });
    }
}

async function loadBookmarksAndNotesHub() {
    const bookmarksEl = document.getElementById('bookmarksContainer');
    const notesEl = document.getElementById('notesContainer');

    try {
        const bookmarks = await apiRequest('/students/me/bookmarks');
        if (bookmarksEl) {
            if (!bookmarks || bookmarks.length === 0) {
                bookmarksEl.innerHTML = `<p style="color:#64748b;">No bookmarked lessons yet. Click "Bookmark Lesson" while studying any lesson to save it here.</p>`;
            } else {
                bookmarksEl.innerHTML = bookmarks.map(b => `
                    <div class="card" style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:1rem; border-left:4px solid #6366f1;">
                        <div>
                            <div style="font-size:0.8rem; font-weight:600; color:#6366f1;">${b.courseTitle} • ${b.topicTitle}</div>
                            <h3 style="margin:0.25rem 0;">${b.lessonTitle}</h3>
                            <span style="font-size:0.85rem; color:#64748b;">${b.noteLabel}</span>
                        </div>
                        <div style="display:flex; gap:0.5rem;">
                            <a href="/student/lesson.html?id=${b.lessonId}" class="btn btn-primary btn-sm">Open Lesson</a>
                            <button onclick="removeBookmark(${b.id})" class="btn btn-secondary btn-sm">Remove</button>
                        </div>
                    </div>
                `).join('');
            }
        }

        const notes = await apiRequest('/students/me/notes');
        if (notesEl) {
            if (!notes || notes.length === 0) {
                notesEl.innerHTML = `<p style="color:#64748b;">You haven't written any lesson notes yet.</p>`;
            } else {
                notesEl.innerHTML = notes.map(n => `
                    <div class="card" style="border-left:4px solid #10b981;">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.5rem;">
                            <div>
                                <span style="font-size:0.8rem; font-weight:600; color:#10b981;">${n.topicTitle}</span>
                                <h3 style="margin:0.15rem 0;">${n.lessonTitle}</h3>
                            </div>
                            <div style="display:flex; gap:0.5rem;">
                                <a href="/student/lesson.html?id=${n.lessonId}" class="btn btn-secondary btn-sm">Edit in Lesson</a>
                                <button onclick="removeNote(${n.id})" class="btn btn-secondary btn-sm">Delete</button>
                            </div>
                        </div>
                        <pre style="white-space:pre-wrap; font-family:inherit; background:#f8fafc; padding:0.85rem; border-radius:8px; border:1px solid #e2e8f0; color:#1e293b;">${n.content}</pre>
                    </div>
                `).join('');
            }
        }
    } catch (err) {
        if (bookmarksEl) bookmarksEl.innerHTML = `<div class="alert alert-error">${err.message}</div>`;
    }
}

async function removeBookmark(id) {
    await apiRequest(`/students/me/bookmarks/${id}`, 'DELETE');
    loadBookmarksAndNotesHub();
}

async function removeNote(id) {
    await apiRequest(`/students/me/notes/${id}`, 'DELETE');
    loadBookmarksAndNotesHub();
}
