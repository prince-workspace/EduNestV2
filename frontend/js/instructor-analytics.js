/**
 * EduNest Phase 7: Instructor Course Analytics & Admin Overview Logic
 */

let topicChartInstance = null;

async function loadInstructorAnalytics() {
    const params = new URLSearchParams(window.location.search);
    const courseId = params.get('courseId');
    const endpoint = courseId
        ? `/instructor/courses/${courseId}/analytics`
        : '/instructor/analytics';

    const summaryEl = document.getElementById('analyticsSummary');
    const weakestEl = document.getElementById('weakestTopicsList');
    const tableBody = document.getElementById('topicBreakdownBody');
    const titleEl = document.getElementById('analyticsCourseTitle');

    try {
        const data = await apiRequest(endpoint);
        if (titleEl) {
            titleEl.textContent = `Course Analytics: ${data.courseTitle}`;
        }

        if (summaryEl) {
            summaryEl.innerHTML = `
                <div class="stat-card">
                    <div class="stat-value">${data.enrolledStudentsCount}</div>
                    <div class="stat-label">Enrolled Students</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${data.activeStudentsCount}</div>
                    <div class="stat-label">Assessed Active Students</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value" style="color:#4f46e5;">${data.overallAverageMastery}%</div>
                    <div class="stat-label">Class Average Mastery</div>
                </div>
            `;
        }

        if (weakestEl) {
            if (!data.weakestTopics || data.weakestTopics.length === 0) {
                weakestEl.innerHTML = `<p style="color:#64748b;">No student mastery assessments recorded yet.</p>`;
            } else {
                weakestEl.innerHTML = data.weakestTopics.map(t => `
                    <div style="padding:0.85rem; border-left:4px solid #ef4444; background:#fef2f2; border-radius:6px; display:flex; justify-content:space-between; align-items:center;">
                        <div>
                            <strong style="color:#991b1b;">Topic #${t.orderIndex}: ${t.topicTitle}</strong>
                            <div style="font-size:0.85rem; color:#7f1d1d;">${t.weakStudentCount} of ${t.studentsAssessed} assessed students need support</div>
                        </div>
                        <span class="badge" style="background:#ef4444; color:#fff;">Avg: ${t.averageMasteryScore}%</span>
                    </div>
                `).join('');
            }
        }

        if (tableBody && data.topicBreakdown) {
            tableBody.innerHTML = data.topicBreakdown.map(t => `
                <tr style="border-bottom:1px solid #e2e8f0;">
                    <td style="padding:0.75rem;">#${t.orderIndex}</td>
                    <td style="padding:0.75rem; font-weight:600;">${t.topicTitle}</td>
                    <td style="padding:0.75rem;">${t.averageMasteryScore}%</td>
                    <td style="padding:0.75rem;">${t.studentsAssessed}</td>
                    <td style="padding:0.75rem; color:#ef4444; font-weight:600;">${t.weakStudentCount}</td>
                    <td style="padding:0.75rem; color:#10b981; font-weight:600;">${t.masteredStudentCount}</td>
                </tr>
            `).join('');
        }

        // Render Chart.js Bar Chart if Chart is available
        const canvas = document.getElementById('topicMasteryChart');
        if (canvas && typeof Chart !== 'undefined' && data.topicBreakdown) {
            if (topicChartInstance) {
                topicChartInstance.destroy();
            }
            const labels = data.topicBreakdown.map(t => `T${t.orderIndex}: ${t.topicTitle}`);
            const scores = data.topicBreakdown.map(t => t.averageMasteryScore);
            const colors = scores.map(s => s >= 90 ? '#f59e0b' : s >= 70 ? '#10b981' : s >= 40 ? '#f97316' : '#ef4444');

            topicChartInstance = new Chart(canvas, {
                type: 'bar',
                data: {
                    labels,
                    datasets: [{
                        label: 'Average Topic Mastery (%)',
                        data: scores,
                        backgroundColor: colors,
                        borderRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: { beginAtZero: true, max: 100 }
                    }
                }
            });
        }
    } catch (err) {
        if (summaryEl) {
            summaryEl.innerHTML = `<div class="alert alert-error">${err.message}</div>`;
        }
    }
}
