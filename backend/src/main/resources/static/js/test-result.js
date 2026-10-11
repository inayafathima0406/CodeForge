async function load() {
    const attemptId = new URLSearchParams(window.location.search).get("attemptId");
    const el = document.getElementById("result-content");
    try {
        const attempts = await api.get("/attempts");
        const result = attempts.find(a => a.attemptId == attemptId);
        if (!result) { el.innerHTML = '<p class="error-text">Result not found.</p>'; return; }

        const percent = result.totalScore === 0 ? 0 : Math.round(100 * result.score / result.totalScore);
        el.innerHTML = `
            <div class="card" style="text-align: center;">
                <h2 style="margin-bottom: 4px;">${escapeHtml(result.testTitle)}</h2>
                <p class="text-muted" style="margin-bottom: 20px;">${result.status.replace(/_/g, " ")}</p>
                <div style="font-size: 42px; font-weight: 700; color: var(--color-primary);">${percent}%</div>
                <p class="text-muted" style="margin-bottom: 20px;">${result.score} / ${result.totalScore} points</p>
                <p>${result.questionsSolved} / ${result.totalQuestions} questions solved</p>
                <a href="/pages/arena.html" class="btn btn-primary" style="margin-top: 20px;">Back to Arena</a>
            </div>
        `;
    } catch (err) {
        el.innerHTML = `<p class="error-text">${escapeHtml(err.message)}</p>`;
    }
}
load();