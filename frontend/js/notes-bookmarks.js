/**
 * EduNest: Student Saved Bookmarks & Personal Study Notes Logic
 */

function escapeHtml(text) {
    if (!text) return '';
    return text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

async function initLessonNotesAndBookmark(lessonId) {
    if (!lessonId) return;

    const noteTextarea = document.getElementById('lessonNoteContent');
    const noteStatus = document.getElementById('noteSaveStatus');
    const bookmarkBtn = document.getElementById('bookmarkLessonBtn');

    // 1. Check if this lesson is already bookmarked
    let isBookmarked = false;
    try {
        const bmStatus = await apiRequest(`/students/me/bookmarks/lesson/${lessonId}`);
        if (bmStatus && bmStatus.bookmarked) {
            isBookmarked = true;
        }
    } catch (e) {
        console.warn('Could not fetch initial bookmark status:', e);
    }

    const updateBookmarkBtnUI = (saved) => {
        if (!bookmarkBtn) return;
        if (saved) {
            bookmarkBtn.innerHTML = '★ Saved';
            bookmarkBtn.classList.remove('btn-secondary');
            bookmarkBtn.classList.add('btn-primary');
            bookmarkBtn.style.background = '#2563eb';
            bookmarkBtn.style.color = '#ffffff';
            bookmarkBtn.style.borderColor = '#1d4ed8';
            bookmarkBtn.title = 'Click to unbookmark this lesson';
        } else {
            bookmarkBtn.innerHTML = '☆ Bookmark';
            bookmarkBtn.classList.remove('btn-primary');
            bookmarkBtn.classList.add('btn-secondary');
            bookmarkBtn.style.background = '';
            bookmarkBtn.style.color = '';
            bookmarkBtn.style.borderColor = '';
            bookmarkBtn.title = 'Click to bookmark this lesson';
        }
    };

    updateBookmarkBtnUI(isBookmarked);

    // Toggle bookmark on click
    if (bookmarkBtn) {
        const newBtn = bookmarkBtn.cloneNode(true);
        bookmarkBtn.parentNode.replaceChild(newBtn, bookmarkBtn);
        newBtn.addEventListener('click', async () => {
            try {
                newBtn.disabled = true;
                const res = await apiRequest('/students/me/bookmarks/toggle', 'POST', {
                    lessonId: Number(lessonId),
                    noteLabel: 'Saved from Lesson Workspace'
                });
                isBookmarked = Boolean(res && res.bookmarked);
                updateBookmarkBtnUI(isBookmarked);
            } catch (err) {
                alert(`Bookmark action failed: ${err.message}`);
            } finally {
                newBtn.disabled = false;
            }
        });
    }

    // 2. Load existing personal note for this lesson
    try {
        const existingNote = await apiRequest(`/students/me/notes/lesson/${lessonId}`);
        if (existingNote && existingNote.content && noteTextarea) {
            noteTextarea.value = existingNote.content;
            if (noteStatus) {
                noteStatus.style.color = '#64748b';
                noteStatus.textContent = 'Note loaded from your saved archive.';
            }
        }
    } catch (e) {
        // No existing note yet
    }

    // Save Note button handler
    const saveNoteBtn = document.getElementById('saveNoteBtn');
    if (saveNoteBtn && noteTextarea) {
        const newSaveBtn = saveNoteBtn.cloneNode(true);
        saveNoteBtn.parentNode.replaceChild(newSaveBtn, saveNoteBtn);
        newSaveBtn.addEventListener('click', async () => {
            const content = noteTextarea.value.trim();
            if (!content) {
                if (noteStatus) {
                    noteStatus.style.color = '#ef4444';
                    noteStatus.textContent = 'Please enter note text before saving.';
                }
                return;
            }
            try {
                newSaveBtn.disabled = true;
                if (noteStatus) {
                    noteStatus.style.color = '#3b82f6';
                    noteStatus.textContent = 'Saving note...';
                }
                await apiRequest('/students/me/notes', 'POST', {
                    lessonId: Number(lessonId),
                    content
                });
                if (noteStatus) {
                    noteStatus.style.color = '#10b981';
                    noteStatus.textContent = '✓ Saved to your study notes archive!';
                }
            } catch (err) {
                if (noteStatus) {
                    noteStatus.style.color = '#ef4444';
                    noteStatus.textContent = `Error: ${err.message}`;
                }
            } finally {
                newSaveBtn.disabled = false;
            }
        });
    }
}

async function loadBookmarksAndNotesHub() {
    const bookmarksEl = document.getElementById('bookmarksContainer');
    const notesEl = document.getElementById('notesContainer');

    try {
        const [bookmarks, notes] = await Promise.all([
            apiRequest('/students/me/bookmarks').catch(() => []),
            apiRequest('/students/me/notes').catch(() => [])
        ]);

        if (bookmarksEl) {
            if (!bookmarks || bookmarks.length === 0) {
                bookmarksEl.innerHTML = `
                    <div class="card" style="text-align:center; padding:2.5rem 1.5rem; background:#ffffff; border:1px dashed #cbd5e1; border-radius:14px;">
                        <div style="font-size:2.25rem; margin-bottom:0.5rem;">🔖</div>
                        <h4 style="color:#0f172a; margin-bottom:0.25rem; font-size:1.1rem; font-weight:700;">No Bookmarked Lessons Yet</h4>
                        <p style="color:#64748b; font-size:0.9rem; max-width:440px; margin:0 auto 1.25rem auto;">
                            Click the <strong>Bookmark</strong> button while reading any lesson to save it here for fast revision.
                        </p>
                        <a href="/student/courses.html" class="btn btn-primary btn-sm">Browse Course Lessons &rarr;</a>
                    </div>
                `;
            } else {
                bookmarksEl.innerHTML = bookmarks.map(b => {
                    const savedDate = b.createdAt ? new Date(b.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' }) : '';
                    return `
                        <div class="card" style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:1rem; border-left:4px solid var(--primary); padding:1.2rem 1.4rem; border-radius:12px; background:#ffffff; box-shadow:0 2px 10px rgba(15,23,42,0.03);">
                            <div style="flex:1; min-width:260px;">
                                <div style="display:flex; align-items:center; gap:0.5rem; margin-bottom:0.35rem; flex-wrap:wrap;">
                                    <span class="badge" style="background:#eff6ff; color:#2563eb; font-size:0.75rem; font-weight:700;">${b.courseTitle || 'Course'}</span>
                                    <span style="font-size:0.82rem; color:#64748b; font-weight:600;">• ${b.topicTitle}</span>
                                    ${savedDate ? `<span style="font-size:0.78rem; color:#94a3b8;">• Saved on ${savedDate}</span>` : ''}
                                </div>
                                <h3 style="margin:0 0 0.25rem 0; font-size:1.15rem; color:#0f172a; font-weight:700;">${escapeHtml(b.lessonTitle)}</h3>
                                <span style="font-size:0.85rem; color:#64748b;">${escapeHtml(b.noteLabel || 'Bookmarked for study')}</span>
                            </div>
                            <div style="display:flex; gap:0.6rem; align-items:center;">
                                <a href="/student/lesson.html?id=${b.lessonId}&courseId=${b.courseId || 1}" class="btn btn-primary btn-sm" style="display:inline-flex; align-items:center; gap:0.35rem;">
                                    <span>📖 Open Lesson</span>
                                </a>
                                <button onclick="removeBookmark(${b.id})" class="btn btn-outline btn-sm" style="color:#ef4444; border-color:#fecdd3;" title="Remove this bookmark">
                                    🗑 Remove
                                </button>
                            </div>
                        </div>
                    `;
                }).join('');
            }
        }

        if (notesEl) {
            if (!notes || notes.length === 0) {
                notesEl.innerHTML = `
                    <div class="card" style="text-align:center; padding:2.5rem 1.5rem; background:#ffffff; border:1px dashed #cbd5e1; border-radius:14px;">
                        <div style="font-size:2.25rem; margin-bottom:0.5rem;">📝</div>
                        <h4 style="color:#0f172a; margin-bottom:0.25rem; font-size:1.1rem; font-weight:700;">No Personal Notes Yet</h4>
                        <p style="color:#64748b; font-size:0.9rem; max-width:440px; margin:0 auto 1.25rem auto;">
                            Write notes and syntax reminders at the bottom of any lesson page and click <strong>Save Note</strong>.
                        </p>
                        <a href="/student/courses.html" class="btn btn-outline btn-sm">Explore Lessons &rarr;</a>
                    </div>
                `;
            } else {
                notesEl.innerHTML = notes.map(n => {
                    const updatedDate = n.updatedAt ? new Date(n.updatedAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' }) : '';
                    return `
                        <div class="card" style="border-left:4px solid #10b981; padding:1.2rem 1.4rem; border-radius:12px; background:#ffffff; box-shadow:0 2px 10px rgba(15,23,42,0.03); margin-bottom:0.85rem;">
                            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.75rem; flex-wrap:wrap; gap:0.5rem;">
                                <div>
                                    <div style="display:flex; align-items:center; gap:0.5rem; margin-bottom:0.25rem; flex-wrap:wrap;">
                                        <span class="badge" style="background:#ecfdf5; color:#059669; font-size:0.75rem; font-weight:700;">${n.courseTitle || 'Course'}</span>
                                        <span style="font-size:0.82rem; color:#64748b; font-weight:600;">• ${n.topicTitle}</span>
                                        ${updatedDate ? `<span style="font-size:0.78rem; color:#94a3b8;">• Last updated ${updatedDate}</span>` : ''}
                                    </div>
                                    <h3 style="margin:0; font-size:1.15rem; color:#0f172a; font-weight:700;">${escapeHtml(n.lessonTitle)}</h3>
                                </div>
                                <div style="display:flex; gap:0.5rem;">
                                    <a href="/student/lesson.html?id=${n.lessonId}" class="btn btn-outline btn-sm">Edit in Lesson</a>
                                    <button onclick="removeNote(${n.id})" class="btn btn-outline btn-sm" style="color:#ef4444; border-color:#fecdd3;">Delete</button>
                                </div>
                            </div>
                            <pre style="white-space:pre-wrap; font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace; background:#f8fafc; padding:1rem 1.2rem; border-radius:10px; border:1px solid #e2e8f0; color:#1e293b; font-size:0.92rem; line-height:1.6; margin:0;">${escapeHtml(n.content)}</pre>
                        </div>
                    `;
                }).join('');
            }
        }
    } catch (err) {
        if (bookmarksEl) bookmarksEl.innerHTML = `<div class="alert alert-error">Failed to load saved items: ${err.message}</div>`;
    }
}

async function removeBookmark(id) {
    if (!confirm('Remove this lesson from your saved bookmarks?')) return;
    try {
        await apiRequest(`/students/me/bookmarks/${id}`, 'DELETE');
        loadBookmarksAndNotesHub();
    } catch (err) {
        alert(`Failed to remove bookmark: ${err.message}`);
    }
}

async function removeNote(id) {
    if (!confirm('Delete this study note? This cannot be undone.')) return;
    try {
        await apiRequest(`/students/me/notes/${id}`, 'DELETE');
        loadBookmarksAndNotesHub();
    } catch (err) {
        alert(`Failed to delete note: ${err.message}`);
    }
}
