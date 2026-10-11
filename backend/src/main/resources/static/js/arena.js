function statusColor(status) {
    if (status === "SUBMITTED") return "var(--color-success)";
    if (status === "TIME_UP") return "var(--color-warning)";
    return "var(--color-text-muted)";
}

async function startTest(testId) {
    try {
        const attempt = await api.post("/tests/" + testId + "/start", {});
        window.location.href = "/pages/take-test.html?attemptId=" + attempt.attemptId;
    } catch (err) {
        alert(err.message);
    }
}

async function load() {
    const loadingEl = document.getElementById("loading-text");
    const errorEl = document.getElementById("error-message");

    try {
        const [tests, attempts] = await Promise.all([api.get("/tests"), api.get("/attempts")]);
        loadingEl.style.display = "none";

        const testsEl = document.getElementById("tests-list");
        testsEl.innerHTML = tests.length === 0
            ? '<p class="text-muted">No tests available right now.</p>'
            : tests.map(t => `
                <div class="card" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
                    <div>
                        <div style="font-weight: 600;">${escapeHtml(t.title)}</div>
                        <div class="text-muted" style="font-size: 13px;">${t.questionCount} questions &middot; ${t.durationMinutes} minutes</div>
                    </div>
                    <button class="btn btn-primary" onclick="startTest(${t.id})">Start Test</button>
                </div>
            `).join("");

        const attemptsEl = document.getElementById("attempts-list");
        attemptsEl.innerHTML = attempts.length === 0
            ? '<p class="text-muted">No attempts yet.</p>'
            : attempts.map(a => `
                <div class="card" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                    <div>
                        <div style="font-weight: 600;">${escapeHtml(a.testTitle)}</div>
                        <div class="text-muted" style="font-size: 13px;">${new Date(a.startedAt).toLocaleString()}</div>
                    </div>
                    <div style="text-align: right;">
                        <div style="color: ${statusColor(a.status)}; font-weight: 600; font-size: 14px;">${a.status.replace(/_/g, " ")}</div>
                        <div class="text-muted" style="font-size: 13px;">${a.score}/${a.totalScore} points &middot; ${a.questionsSolved}/${a.totalQuestions} solved</div>
                    </div>
                </div>
            `).join("");
    } catch (err) {
        loadingEl.style.display = "none";
        errorEl.textContent = err.message;
        errorEl.style.display = "block";
    }
}

load();