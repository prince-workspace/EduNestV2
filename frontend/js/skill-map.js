/**
 * EduNest Phase 6: Visual Skill Map & Practical Challenges Frontend Logic
 */

function getColorMeta(colorCode) {
    switch (colorCode) {
        case 'GOLD':
            return {
                border: '#f59e0b',
                bg: 'linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%)',
                badgeBg: '#f59e0b',
                badgeText: '#ffffff',
                label: 'Mastered (90%+)'
            };
        case 'GREEN':
            return {
                border: '#10b981',
                bg: 'linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%)',
                badgeBg: '#10b981',
                badgeText: '#ffffff',
                label: 'Strong (70-89%)'
            };
        case 'YELLOW':
            return {
                border: '#f97316',
                bg: 'linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%)',
                badgeBg: '#f97316',
                badgeText: '#ffffff',
                label: 'Needs Work (40-69%)'
            };
        case 'RED':
        default:
            return {
                border: '#ef4444',
                bg: 'linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%)',
                badgeBg: '#ef4444',
                badgeText: '#ffffff',
                label: 'Weak (<40%)'
            };
    }
}

async function loadSkillMap() {
    const params = new URLSearchParams(window.location.search);
    const courseId = params.get('courseId');
    const endpoint = courseId
        ? `/students/me/courses/${courseId}/skill-map`
        : '/students/me/skill-map';

    const container = document.getElementById('skillMapGrid');
    const summaryEl = document.getElementById('skillMapSummary');

    try {
        const mapData = await apiRequest(endpoint);
        if (summaryEl) {
            summaryEl.innerHTML = `
                <div class="stat-card">
                    <div class="stat-value">${mapData.overallMastery}%</div>
                    <div class="stat-label">Course Mastery (${mapData.courseTitle})</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value" style="color:#f59e0b;">${mapData.unlockedChallengesCount} / ${mapData.entries.length}</div>
                    <div class="stat-label">Practical Challenges Unlocked</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value" style="color:#10b981;">${mapData.completedChallengesCount}</div>
                    <div class="stat-label">Challenges Completed</div>
                </div>
            `;
        }

        if (!container) return;
        container.innerHTML = mapData.entries.map(entry => {
            const meta = getColorMeta(entry.colorCode);
            const challengeStatus = entry.challengeCompleted
                ? `<span class="badge badge-success">Challenge Completed</span>`
                : entry.challengeUnlocked
                    ? `<span class="badge" style="background:#f59e0b;color:#fff;">Challenge Unlocked!</span>`
                    : `<span class="badge" style="background:#64748b;color:#fff;">Locked (Need 90% Mastery)</span>`;

            return `
                <div class="card" style="border: 2px solid ${meta.border}; background: ${meta.bg}; position: relative;">
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.75rem;">
                        <span style="font-size:0.8rem; font-weight:700; color:#475569;">TOPIC #${entry.orderIndex}</span>
                        <span class="badge" style="background:${meta.badgeBg}; color:${meta.badgeText};">${meta.label}</span>
                    </div>
                    <h3 style="margin-bottom:0.5rem; color:#0f172a;">${entry.topicTitle}</h3>
                    <div style="margin: 0.75rem 0;">
                        <div style="display:flex; justify-content:space-between; font-size:0.875rem; font-weight:600; margin-bottom:0.35rem;">
                            <span>Mastery Score</span>
                            <span>${entry.masteryScore}%</span>
                        </div>
                        <div style="height:10px; background:rgba(255,255,255,0.8); border-radius:999px; overflow:hidden; border:1px solid rgba(0,0,0,0.08);">
                            <div style="width:${Math.min(100, entry.masteryScore)}%; height:100%; background:${meta.border};"></div>
                        </div>
                    </div>
                    <div style="margin-top:0.85rem; padding-top:0.75rem; border-top:1px solid rgba(0,0,0,0.08); display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:0.5rem;">
                        <div>
                            <div style="font-size:0.78rem; color:#475569; margin-bottom:0.2rem;">Practical Challenge: <strong>${entry.challengeTitle || 'N/A'}</strong></div>
                            ${challengeStatus}
                        </div>
                        <a href="${entry.recommendedActionUrl}" class="btn btn-primary" style="font-size:0.85rem; padding:0.45rem 0.9rem;">
                            ${entry.challengeUnlocked ? 'Solve Challenge' : 'Improve Mastery'}
                        </a>
                    </div>
                </div>
            `;
        }).join('');
    } catch (err) {
        if (container) {
            container.innerHTML = `<div class="alert alert-error">Failed to load Skill Map: ${err.message}</div>`;
        }
    }
}

async function loadChallengesHub() {
    const params = new URLSearchParams(window.location.search);
    let courseId = params.get('courseId');

    const listEl = document.getElementById('challengesList');
    const submissionsEl = document.getElementById('submissionsList');

    try {
        if (!courseId) {
            const courses = await apiRequest('/courses');
            if (courses && courses.length > 0) {
                courseId = courses[0].id;
            }
        }

        const challenges = await apiRequest(`/challenges/course/${courseId}`);
        if (listEl) {
            listEl.innerHTML = challenges.map(ch => {
                const statusBadge = ch.completed
                    ? `<span class="badge badge-success">Completed (${ch.bestScore}%)</span>`
                    : ch.unlocked
                        ? `<span class="badge" style="background:#f59e0b;color:#fff;">Unlocked</span>`
                        : `<span class="badge" style="background:#64748b;color:#fff;">Locked (${ch.currentTopicMastery}% / ${ch.unlockThreshold}%)</span>`;

                return `
                    <div class="card" style="opacity: ${ch.unlocked ? '1' : '0.82'}; border-left: 5px solid ${ch.completed ? '#10b981' : ch.unlocked ? '#f59e0b' : '#94a3b8'};">
                        <div style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:0.5rem;">
                            <div>
                                <span style="font-size:0.8rem; font-weight:600; color:#6366f1;">${ch.topicTitle} • ${ch.difficulty} • ${ch.points} pts</span>
                                <h3 style="margin:0.25rem 0;">${ch.title}</h3>
                            </div>
                            ${statusBadge}
                        </div>
                        <p style="color:#475569; margin:0.65rem 0;">${ch.description}</p>
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-top:0.75rem;">
                            <span style="font-size:0.85rem; color:#64748b;">Current Topic Mastery: <strong>${ch.currentTopicMastery}%</strong> (Requires ${ch.unlockThreshold}%)</span>
                            ${ch.unlocked
                                ? `<a href="/student/challenge-detail.html?id=${ch.id}" class="btn btn-primary">${ch.completed ? 'Review / Resubmit' : 'Start Challenge'}</a>`
                                : `<a href="/student/quiz.html?topicId=${ch.topicId}" class="btn btn-secondary">Boost Topic Mastery</a>`
                            }
                        </div>
                    </div>
                `;
            }).join('');
        }

        const mySubs = await apiRequest('/challenges/my-submissions');
        if (submissionsEl) {
            if (!mySubs || mySubs.length === 0) {
                submissionsEl.innerHTML = `<p style="color:#64748b;">No challenge submissions yet. Master a topic (90%+) and solve your first practical challenge!</p>`;
            } else {
                submissionsEl.innerHTML = mySubs.map(sub => `
                    <div style="padding:0.75rem 0; border-bottom:1px solid #e2e8f0; display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:0.5rem;">
                        <div>
                            <strong>${sub.challengeTitle}</strong>
                            <div style="font-size:0.85rem; color:#475569;">${sub.feedback}</div>
                        </div>
                        <span class="badge ${sub.passed ? 'badge-success' : 'badge-warning'}">
                            ${sub.passed ? 'PASSED' : 'NEEDS WORK'} (${sub.score}%)
                        </span>
                    </div>
                `).join('');
            }
        }
    } catch (err) {
        if (listEl) {
            listEl.innerHTML = `<div class="alert alert-error">${err.message}</div>`;
        }
    }
}

async function loadChallengeDetail() {
    const params = new URLSearchParams(window.location.search);
    const challengeId = params.get('id');
    if (!challengeId) return;

    const titleEl = document.getElementById('challengeTitle');
    const metaEl = document.getElementById('challengeMeta');
    const descEl = document.getElementById('challengeDescription');
    const codeInput = document.getElementById('solutionText');
    const feedbackBox = document.getElementById('submissionResult');

    try {
        const ch = await apiRequest(`/challenges/${challengeId}`);
        if (titleEl) titleEl.textContent = ch.title;
        if (metaEl) metaEl.textContent = `${ch.topicTitle} • ${ch.challengeType} • ${ch.difficulty} (${ch.points} pts)`;
        if (descEl) descEl.textContent = ch.description;
        if (codeInput && !codeInput.value) {
            codeInput.value = ch.starterCode || '';
        }

        const form = document.getElementById('challengeSubmitForm');
        if (form) {
            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                try {
                    const res = await apiRequest(`/challenges/${challengeId}/submit`, 'POST', {
                        solutionText: codeInput.value
                    });
                    if (feedbackBox) {
                        feedbackBox.style.display = 'block';
                        feedbackBox.className = `alert ${res.passed ? 'alert-success' : 'alert-error'}`;
                        feedbackBox.innerHTML = `
                            <strong>${res.passed ? 'Challenge Passed!' : 'Keep Trying!'} (Score: ${res.score}%)</strong><br/>
                            ${res.feedback}
                        `;
                    }
                } catch (submitErr) {
                    if (feedbackBox) {
                        feedbackBox.style.display = 'block';
                        feedbackBox.className = 'alert alert-error';
                        feedbackBox.textContent = submitErr.message;
                    }
                }
            });
        }
    } catch (err) {
        if (descEl) descEl.textContent = `Error loading challenge: ${err.message}`;
    }
}
