/**
 * EduNest: Practical Coding Challenges & Online Judge System
 */

let currentChallenge = null;
let currentLanguage = 'JAVA';
let codeCache = {};
let latestTestCaseResults = [];

function escapeHtml(text) {
    if (!text) return '';
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

async function loadChallengesHub() {
    const params = new URLSearchParams(window.location.search);
    let courseId = params.get('courseId');

    const listEl = document.getElementById('challengesList');
    const submissionsEl = document.getElementById('submissionsList');

    try {
        if (!courseId) {
            courseId = localStorage.getItem('edunest_activeCourseId');
            if (!courseId) {
                const courses = await apiRequest('/courses');
                if (courses && courses.length > 0) {
                    courseId = courses[0].id;
                }
            }
        }

        const challenges = await apiRequest(`/challenges/course/${courseId}`);
        if (listEl) {
            if (!challenges || challenges.length === 0) {
                listEl.innerHTML = `
                    <div class="card" style="text-align:center; padding:2.5rem; color:#64748b; background:#ffffff; border-radius:14px;">
                        <div style="font-size:2.25rem; margin-bottom:0.5rem;">💻</div>
                        <h4 style="color:#0f172a; margin-bottom:0.35rem; font-size:1.15rem; font-weight:700;">No Coding Challenges Found</h4>
                        <p style="font-size:0.9rem; max-width:440px; margin:0 auto 1.25rem auto;">Explore your courses to start studying lessons and preparing for coding challenges.</p>
                        <a href="/student/courses.html" class="btn btn-primary btn-sm">Explore Course Lessons &rarr;</a>
                    </div>
                `;
            } else {
                listEl.innerHTML = challenges.map(ch => {
                    const diffColors = {
                        'EASY': { bg: '#ecfdf5', text: '#047857', border: '#a7f3d0' },
                        'MEDIUM': { bg: '#fffbeb', text: '#b45309', border: '#fde68a' },
                        'HARD': { bg: '#fef2f2', text: '#b91c1c', border: '#fecdd3' }
                    };
                    const diff = diffColors[ch.difficulty] || diffColors['EASY'];

                    const statusBadge = ch.completed
                        ? `<span class="badge" style="background:#ecfdf5; color:#047857; font-weight:700; border:1px solid #a7f3d0; padding:0.25rem 0.65rem; border-radius:999px;">✓ Solved (100%)</span>`
                        : `<span class="badge" style="background:#f1f5f9; color:#475569; font-weight:700; border:1px solid #e2e8f0; padding:0.25rem 0.65rem; border-radius:999px;">In Progress</span>`;

                    return `
                        <div class="card" style="background:#ffffff; border-radius:14px; border:1px solid #e2e8f0; border-left:5px solid ${ch.completed ? '#10b981' : '#3b82f6'}; padding:1.35rem 1.6rem; box-shadow:0 2px 10px rgba(15,23,42,0.03);">
                            <div style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:0.6rem; margin-bottom:0.4rem;">
                                <div>
                                    <div style="display:flex; align-items:center; gap:0.5rem; margin-bottom:0.3rem; flex-wrap:wrap;">
                                        <span class="badge" style="background:#eff6ff; color:#2563eb; font-weight:700; font-size:0.75rem;">${escapeHtml(ch.topicTitle)}</span>
                                        <span class="badge" style="background:${diff.bg}; color:${diff.text}; border:1px solid ${diff.border}; font-weight:700; font-size:0.75rem;">${ch.difficulty}</span>
                                        <span style="font-size:0.82rem; font-weight:700; color:#64748b;">+${ch.points} XP</span>
                                    </div>
                                    <h3 style="margin:0; font-size:1.2rem; font-weight:800; color:#0f172a;">${escapeHtml(ch.title)}</h3>
                                </div>
                                <div>${statusBadge}</div>
                            </div>
                            <p style="color:#475569; font-size:0.92rem; line-height:1.55; margin:0.65rem 0 1rem 0;">
                                ${escapeHtml(ch.description ? ch.description.split('\n')[0] : '')}
                            </p>
                            <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:0.75rem; border-top:1px solid #f1f5f9; padding-top:0.85rem;">
                                <div style="display:flex; gap:0.6rem; align-items:center; font-size:0.82rem; color:#64748b;">
                                    <span>Supported: Java • Python • JavaScript</span>
                                </div>
                                <a href="/student/challenge-detail.html?id=${ch.id}" class="btn btn-primary btn-sm" style="font-weight:700; padding:0.45rem 1.1rem;">
                                    ${ch.completed ? 'Review Solution &rarr;' : 'Solve Problem &rarr;'}
                                </a>
                            </div>
                        </div>
                    `;
                }).join('');
            }
        }

        const mySubs = await apiRequest('/challenges/my-submissions');
        if (submissionsEl) {
            if (!mySubs || mySubs.length === 0) {
                submissionsEl.innerHTML = `<p style="color:#64748b; font-size:0.9rem;">No challenge submissions yet. Select any challenge above to compile and submit your code!</p>`;
            } else {
                submissionsEl.innerHTML = `
                    <div style="display:flex; flex-direction:column; gap:0.65rem;">
                        ${mySubs.map(sub => `
                            <div style="padding:0.85rem 1rem; background:#f8fafc; border:1px solid #e2e8f0; border-radius:10px; display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:0.6rem;">
                                <div>
                                    <div style="font-weight:700; color:#0f172a; font-size:0.95rem;">${escapeHtml(sub.challengeTitle)}</div>
                                    <div style="font-size:0.84rem; color:#64748b; margin-top:0.15rem;">${escapeHtml(sub.feedback || '')}</div>
                                </div>
                                <div style="display:flex; align-items:center; gap:0.75rem;">
                                    <span class="badge" style="background:${sub.passed ? '#ecfdf5' : '#fef2f2'}; color:${sub.passed ? '#047857' : '#b91c1c'}; border:1px solid ${sub.passed ? '#a7f3d0' : '#fecdd3'}; font-weight:700;">
                                        ${sub.passed ? '✓ PASSED (100%)' : 'NEEDS WORK (' + sub.score + '%)'}
                                    </span>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                `;
            }
        }
    } catch (err) {
        if (listEl) listEl.innerHTML = `<div class="alert alert-error">Error loading challenges: ${err.message}</div>`;
    }
}

async function loadChallengeDetail() {
    const params = new URLSearchParams(window.location.search);
    const challengeId = params.get('id');
    if (!challengeId) {
        window.location.href = '/student/challenges.html';
        return;
    }

    const titleEl = document.getElementById('chTitle');
    const topicBadge = document.getElementById('chTopicBadge');
    const diffBadge = document.getElementById('chDifficultyBadge');
    const pointsBadge = document.getElementById('chPointsBadge');
    const statusPill = document.getElementById('challengeStatusPill');
    const descEl = document.getElementById('chDescription');
    const inputFormatEl = document.getElementById('chInputFormat');
    const outputFormatEl = document.getElementById('chOutputFormat');
    const constraintsEl = document.getElementById('chConstraints');
    const examplesContainer = document.getElementById('chExamplesContainer');
    const editor = document.getElementById('codeEditor');

    try {
        const ch = await apiRequest(`/challenges/${challengeId}`);
        currentChallenge = ch;

        if (titleEl) titleEl.textContent = ch.title;
        if (topicBadge) topicBadge.textContent = ch.topicTitle || 'Curriculum';
        if (diffBadge) diffBadge.textContent = ch.difficulty || 'MEDIUM';
        if (pointsBadge) pointsBadge.textContent = `+${ch.points || 50} XP`;

        if (statusPill) {
            if (ch.completed) {
                statusPill.innerHTML = `<span class="badge" style="background:#ecfdf5; color:#047857; font-weight:700; border:1px solid #a7f3d0; padding:0.35rem 0.8rem; border-radius:999px;">✓ Solved & Verified</span>`;
            } else {
                statusPill.innerHTML = `<span class="badge" style="background:#eff6ff; color:#2563eb; font-weight:700; border:1px solid #bfdbfe; padding:0.35rem 0.8rem; border-radius:999px;">Ready to Solve</span>`;
            }
        }

        if (descEl) descEl.textContent = ch.description || 'Implement the solution according to specifications.';
        if (inputFormatEl) inputFormatEl.textContent = ch.inputFormat || 'Read inputs from standard input (stdin).';
        if (outputFormatEl) outputFormatEl.textContent = ch.outputFormat || 'Print solution to standard output (stdout).';
        if (constraintsEl) constraintsEl.textContent = ch.constraints || 'Standard memory and runtime limits apply.';

        // Render input/output examples
        if (examplesContainer) {
            let sampleCases = [];
            try {
                if (ch.sampleTestCasesJson) {
                    sampleCases = JSON.parse(ch.sampleTestCasesJson);
                }
            } catch (e) {
                sampleCases = [];
            }

            if (sampleCases.length === 0) {
                examplesContainer.innerHTML = `<p style="color:#64748b; font-size:0.85rem;">Refer to description for sample parameters.</p>`;
            } else {
                examplesContainer.innerHTML = sampleCases.map((sc, idx) => `
                    <div class="example-box">
                        <div style="font-weight:700; color:#0f172a; margin-bottom:0.35rem;">Example ${idx + 1}:</div>
                        <div><strong>Input:</strong></div>
                        <pre>${escapeHtml(sc.input)}</pre>
                        <div><strong>Output:</strong></div>
                        <pre>${escapeHtml(sc.expectedOutput)}</pre>
                        ${sc.explanation ? `<div style="color:#64748b; margin-top:0.35rem; font-size:0.84rem;"><strong>Explanation:</strong> ${escapeHtml(sc.explanation)}</div>` : ''}
                    </div>
                `).join('');
            }
        }

        // Initialize starter code in memory cache
        codeCache['JAVA'] = ch.starterCode || '';
        codeCache['PYTHON'] = ch.starterCodePython || '';
        codeCache['JAVASCRIPT'] = ch.starterCodeJs || '';

        // Configure default language
        currentLanguage = 'JAVA';
        const langSelect = document.getElementById('languageSelect');
        if (langSelect) langSelect.value = 'JAVA';

        if (editor) {
            editor.value = codeCache['JAVA'] || '';
            setupEditorIndentation(editor);
        }

    } catch (err) {
        if (titleEl) titleEl.textContent = 'Error Loading Challenge';
        if (descEl) descEl.textContent = err.message;
    }
}

function handleLanguageChange() {
    const langSelect = document.getElementById('languageSelect');
    const editor = document.getElementById('codeEditor');
    if (!langSelect || !editor) return;

    // Save current editor content for current language
    codeCache[currentLanguage] = editor.value;

    // Switch to new language
    currentLanguage = langSelect.value;
    editor.value = codeCache[currentLanguage] || '';
}

function resetStarterTemplate() {
    if (!currentChallenge || !confirm('Reset editor to original starter template for ' + currentLanguage + '?')) return;
    const editor = document.getElementById('codeEditor');
    if (!editor) return;

    if (currentLanguage === 'PYTHON') {
        editor.value = currentChallenge.starterCodePython || '';
    } else if (currentLanguage === 'JAVASCRIPT') {
        editor.value = currentChallenge.starterCodeJs || '';
    } else {
        editor.value = currentChallenge.starterCode || '';
    }
    codeCache[currentLanguage] = editor.value;
}

function setupEditorIndentation(textarea) {
    textarea.addEventListener('keydown', function(e) {
        if (e.key === 'Tab') {
            e.preventDefault();
            const start = this.selectionStart;
            const end = this.selectionEnd;
            this.value = this.value.substring(0, start) + '    ' + this.value.substring(end);
            this.selectionStart = this.selectionEnd = start + 4;
        }
    });
}

async function runCode() {
    if (!currentChallenge) return;
    const editor = document.getElementById('codeEditor');
    const runBtn = document.getElementById('runCodeBtn');
    const consolePanel = document.getElementById('outputConsolePanel');

    if (!editor.value.trim()) {
        alert('Please write some code before running.');
        return;
    }

    try {
        if (runBtn) {
            runBtn.disabled = true;
            runBtn.innerHTML = `<span>Running...</span>`;
        }
        if (consolePanel) consolePanel.style.display = 'block';

        const res = await apiRequest(`/challenges/${currentChallenge.id}/run`, 'POST', {
            solutionText: editor.value,
            language: currentLanguage
        });

        renderExecutionConsole(res, false);
    } catch (err) {
        alert(`Run failed: ${err.message}`);
    } finally {
        if (runBtn) {
            runBtn.disabled = false;
            runBtn.innerHTML = `<svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor"><polygon points="5 3 19 12 5 21 5 3"/></svg><span>Run Code</span>`;
        }
    }
}

async function submitCode() {
    if (!currentChallenge) return;
    const editor = document.getElementById('codeEditor');
    const submitBtn = document.getElementById('submitCodeBtn');
    const consolePanel = document.getElementById('outputConsolePanel');

    if (!editor.value.trim()) {
        alert('Please write some code before submitting.');
        return;
    }

    try {
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = `<span>Evaluating...</span>`;
        }
        if (consolePanel) consolePanel.style.display = 'block';

        const res = await apiRequest(`/challenges/${currentChallenge.id}/submit`, 'POST', {
            solutionText: editor.value,
            language: currentLanguage
        });

        renderExecutionConsole(res, true);

        // Update top challenge status pill
        const statusPill = document.getElementById('challengeStatusPill');
        if (statusPill && res.passed) {
            statusPill.innerHTML = `<span class="badge" style="background:#ecfdf5; color:#047857; font-weight:700; border:1px solid #a7f3d0; padding:0.35rem 0.8rem; border-radius:999px;">✓ Solved & Verified</span>`;
        }
    } catch (err) {
        alert(`Submission failed: ${err.message}`);
    } finally {
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg><span>Submit Solution</span>`;
        }
    }
}

function renderExecutionConsole(res, isSubmission) {
    const consolePanel = document.getElementById('outputConsolePanel');
    const statusBadge = document.getElementById('consoleStatusBadge');
    const timeBadge = document.getElementById('consoleExecutionTime');
    const scoreBadge = document.getElementById('consoleScoreBadge');
    const compSection = document.getElementById('compilationSection');
    const compText = document.getElementById('compilationText');
    const tabsContainer = document.getElementById('testCasesTabsContainer');
    const detailsContainer = document.getElementById('activeTestCaseDetails');
    const feedbackAlert = document.getElementById('consoleFeedbackAlert');

    if (!consolePanel) return;
    consolePanel.style.display = 'block';

    // 1. Overall Header
    if (statusBadge) {
        if (!res.compiled) {
            statusBadge.textContent = 'Compilation Error';
            statusBadge.style.background = '#fef2f2';
            statusBadge.style.color = '#b91c1c';
            statusBadge.style.border = '1px solid #fecdd3';
        } else if (res.passed) {
            statusBadge.textContent = isSubmission ? '✓ Accepted (100%)' : '✓ Sample Tests Passed';
            statusBadge.style.background = '#ecfdf5';
            statusBadge.style.color = '#047857';
            statusBadge.style.border = '1px solid #a7f3d0';
        } else {
            statusBadge.textContent = isSubmission ? '❌ Needs Work (Wrong Answer)' : '❌ Sample Test Failed';
            statusBadge.style.background = '#fffbeb';
            statusBadge.style.color = '#b45309';
            statusBadge.style.border = '1px solid #fde68a';
        }
    }

    if (timeBadge) {
        timeBadge.textContent = `⏱ Execution: ${res.executionTimeMs || 0} ms`;
    }

    if (scoreBadge) {
        scoreBadge.innerHTML = `<span style="color:${res.passed ? '#047857' : '#b45309'};">Score: ${res.score}% (${res.passedTestCases}/${res.totalTestCases} Tests Passed)</span>`;
    }

    // 2. Compilation section
    if (compSection && compText) {
        if (!res.compiled) {
            compSection.style.display = 'block';
            compText.textContent = res.compilationOutput || 'Compilation error.';
        } else {
            compSection.style.display = 'none';
            compText.textContent = '';
        }
    }

    // 3. Test Cases Tabs & Details
    latestTestCaseResults = res.testCaseResults || [];
    if (tabsContainer && detailsContainer) {
        if (latestTestCaseResults.length === 0) {
            tabsContainer.innerHTML = '';
            detailsContainer.innerHTML = `<p style="color:#64748b;">No test cases evaluated.</p>`;
        } else {
            tabsContainer.innerHTML = latestTestCaseResults.map((tc, idx) => `
                <button type="button" class="testcase-tab ${tc.passed ? 'passed' : 'failed'} ${idx === 0 ? 'active' : ''}" onclick="selectTestCaseTab(${idx})">
                    Case ${idx + 1} ${tc.passed ? '✓' : '✗'}
                </button>
            `).join('');

            selectTestCaseTab(0);
        }
    }

    // 4. Feedback Banner
    if (feedbackAlert) {
        if (res.passed) {
            feedbackAlert.style.background = '#ecfdf5';
            feedbackAlert.style.border = '1px solid #a7f3d0';
            feedbackAlert.style.color = '#065f46';
            feedbackAlert.innerHTML = `
                <div style="font-weight:700; margin-bottom:0.2rem;">🎉 Outstanding Work!</div>
                <div>${escapeHtml(res.feedback || 'All test cases compiled and passed successfully.')}</div>
                ${isSubmission ? '<div style="margin-top:0.35rem; font-weight:700; color:#047857;">✓ Marked as Completed! +XP awarded.</div>' : ''}
            `;
        } else {
            feedbackAlert.style.background = '#fff1f2';
            feedbackAlert.style.border = '1px solid #fecdd3';
            feedbackAlert.style.color = '#9f1239';
            feedbackAlert.innerHTML = `
                <div style="font-weight:700; margin-bottom:0.2rem;">${res.compiled ? 'Test Case Mismatch' : 'Compilation Issue'}</div>
                <div>${escapeHtml(res.feedback || 'Review error details above and test edge cases.')}</div>
                ${isSubmission ? '<div style="margin-top:0.35rem; font-size:0.85rem; color:#b91c1c;">⚠️ Note: Only solutions that compile and pass all test cases count as completed.</div>' : ''}
            `;
        }
    }
}

function selectTestCaseTab(index) {
    const tabs = document.querySelectorAll('.testcase-tab');
    tabs.forEach((tab, i) => {
        if (i === index) tab.classList.add('active');
        else tab.classList.remove('active');
    });

    const detailsContainer = document.getElementById('activeTestCaseDetails');
    const tc = latestTestCaseResults[index];
    if (!detailsContainer || !tc) return;

    detailsContainer.innerHTML = `
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.5rem;">
            <span style="font-weight:700; color:#0f172a;">Test Case ${index + 1}</span>
            <span class="badge" style="background:${tc.passed ? '#ecfdf5' : '#fef2f2'}; color:${tc.passed ? '#047857' : '#b91c1c'}; border:1px solid ${tc.passed ? '#a7f3d0' : '#fecdd3'}; font-weight:700;">
                ${tc.passed ? 'PASSED' : 'FAILED'}
            </span>
        </div>
        <div style="margin-bottom:0.5rem;">
            <div style="font-size:0.8rem; font-weight:700; color:#64748b;">INPUT:</div>
            <pre style="background:#ffffff; border:1px solid #e2e8f0; padding:0.5rem 0.75rem; border-radius:6px; margin:0.2rem 0; font-family:monospace; font-size:0.85rem;">${escapeHtml(tc.input)}</pre>
        </div>
        <div style="margin-bottom:0.5rem;">
            <div style="font-size:0.8rem; font-weight:700; color:#64748b;">EXPECTED OUTPUT:</div>
            <pre style="background:#ffffff; border:1px solid #e2e8f0; padding:0.5rem 0.75rem; border-radius:6px; margin:0.2rem 0; font-family:monospace; font-size:0.85rem;">${escapeHtml(tc.expectedOutput)}</pre>
        </div>
        <div>
            <div style="font-size:0.8rem; font-weight:700; color:#64748b;">YOUR OUTPUT:</div>
            <pre style="background:#ffffff; border:1px solid ${tc.passed ? '#a7f3d0' : '#fecdd3'}; padding:0.5rem 0.75rem; border-radius:6px; margin:0.2rem 0; font-family:monospace; font-size:0.85rem; color:${tc.passed ? '#047857' : '#b91c1c'};">${escapeHtml(tc.actualOutput || (tc.errorMessage ? tc.errorMessage : '(Empty Output)'))}</pre>
        </div>
        ${tc.errorMessage && !tc.passed ? `<div style="color:#b91c1c; font-size:0.82rem; margin-top:0.35rem;"><strong>Error:</strong> ${escapeHtml(tc.errorMessage)}</div>` : ''}
    `;
}
