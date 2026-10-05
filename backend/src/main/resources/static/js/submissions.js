function statusColor(status) {
    if (status === "ACCEPTED") return "var(--color-success)";
    if (status === "COMPILATION_ERROR" || status === "TIME_LIMIT_EXCEEDED") return "var(--color-warning)";
    return "var(--color-danger)";
}

async function loadSubmissions() {
    const loadingEl = document.getElementById("loading-text");
    const errorEl = document.getElementById("error-message");
    const listEl = document.getElementById("submissions-list");

    try {
        const subs = await api.get("/submissions");
        loadingEl.style.display = "none";

        if (subs.length === 0) {
            listEl.innerHTML = '<p class="text-muted">No submissions yet. Solve a problem to see it here.</p>';
            return;
        }

        listEl.innerHTML = subs.map(s => `
            <a href="/pages/problem-detail.html?id=${s.questionId}" class="card"
               style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; color: inherit;">
                <div>
                    <div style="font-weight: 600;">${s.questionTitle}</div>
                    <div class="text-muted" style="font-size: 13px;">${s.topic} &middot; ${new Date(s.submittedAt).toLocaleString()}</div>
                </div>
                <div style="text-align: right;">
                    <div style="color: ${statusColor(s.status)}; font-weight: 600; font-size: 14px;">${s.status.replace(/_/g, " ")}</div>
                    <div class="text-muted" style="font-size: 13px;">${s.passed}/${s.total} passed</div>
                </div>
            </a>
        `).join("");
    } catch (err) {
        loadingEl.style.display = "none";
        errorEl.textContent = err.message;
        errorEl.style.display = "block";
    }
}

loadSubmissions();