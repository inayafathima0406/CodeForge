function progressBar(label, solved, total, percent, color) {
    return `<div style="margin-bottom: 12px;">
        <div style="display: flex; justify-content: space-between; font-size: 14px; margin-bottom: 4px;">
            <span>${escapeHtml(label)}</span>
            <span class="text-muted">${solved}/${total}</span>
        </div>
        <div style="background: var(--color-surface-2); border-radius: 4px; height: 8px; overflow: hidden;">
            <div style="background: ${color}; width: ${percent}%; height: 100%;"></div>
        </div>
    </div>`;
}

function statusColor(status) {
    if (status === "ACCEPTED") return "var(--color-success)";
    if (status === "COMPILATION_ERROR" || status === "TIME_LIMIT_EXCEEDED") return "var(--color-warning)";
    return "var(--color-danger)";
}

async function loadDashboard() {
    const loadingEl = document.getElementById("loading-text");
    const errorEl = document.getElementById("error-message");
    const contentEl = document.getElementById("dashboard-content");

    try {
        const d = await api.get("/dashboard");
        loadingEl.style.display = "none";
        contentEl.style.display = "block";

        document.getElementById("stat-attempted").textContent = d.totalAttempted;
        document.getElementById("stat-solved").textContent = d.totalSolved;
        document.getElementById("stat-accuracy").textContent = d.accuracyPercent + "%";
        document.getElementById("stat-streak").textContent = d.currentStreak;

        document.getElementById("topic-progress").innerHTML = d.topicProgress.length
            ? d.topicProgress.map(t => progressBar(t.topic, t.solved, t.total, t.percent, "var(--color-primary)")).join("")
            : '<p class="text-muted">No topics yet.</p>';

        const diffColors = { EASY: "var(--color-success)", MEDIUM: "var(--color-warning)", HARD: "var(--color-danger)" };
        document.getElementById("difficulty-progress").innerHTML = Object.entries(d.difficultyProgress)
            .map(([key, v]) => progressBar(key, v.solved, v.total, v.percent, diffColors[key] || "var(--color-primary)"))
            .join("");

        const recentEl = document.getElementById("recent-submissions");
        if (d.recentSubmissions.length === 0) {
            recentEl.innerHTML = '<p class="text-muted">No submissions yet. Go solve something!</p>';
        } else {
            recentEl.innerHTML = d.recentSubmissions.map(s => `
                <a href="/pages/problem-detail.html?id=${s.questionId}" class="card"
                   style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; color: inherit;">
                    <div>
                        <div style="font-weight: 600;">${escapeHtml(s.questionTitle)}</div>
                        <div class="text-muted" style="font-size: 13px;">${escapeHtml(s.topic)} &middot; ${new Date(s.submittedAt).toLocaleString()}</div>
                    </div>
                    <div style="color: ${statusColor(s.status)}; font-weight: 600; font-size: 14px;">${s.status.replace(/_/g, " ")}</div>
                </a>
            `).join("");
        }
    } catch (err) {
        loadingEl.style.display = "none";
        errorEl.textContent = err.message;
        errorEl.style.display = "block";
    }
}

loadDashboard();