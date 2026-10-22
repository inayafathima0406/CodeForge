let currentTab = "xp";

function switchTab(tab) {
    currentTab = tab;
    document.getElementById("tab-xp").className = tab === "xp" ? "btn btn-primary" : "btn btn-outline";
    document.getElementById("tab-solved").className = tab === "solved" ? "btn btn-primary" : "btn btn-outline";
    loadLeaderboard();
}

async function loadLeaderboard() {
    const listEl = document.getElementById("leaderboard-list");
    try {
        const entries = await api.get("/leaderboard?type=" + currentTab);
        listEl.innerHTML = entries.length === 0
            ? '<p class="text-muted">No ranked students yet.</p>'
            : entries.map(e => `
                <div class="card" style="display: flex; align-items: center; gap: 16px; margin-bottom: 10px;">
                    <div style="font-size: 20px; font-weight: 700; width: 36px; color: ${e.rank <= 3 ? 'var(--color-warning)' : 'var(--color-text-muted)'};">#${e.rank}</div>
                    <div style="flex: 1;">
                        <div style="font-weight: 600;">${escapeHtml(e.fullName)}</div>
                        <div class="text-muted" style="font-size: 13px;">@${escapeHtml(e.username)}</div>
                    </div>
                    <div style="text-align: right;">
                        <div style="font-weight: 600;">${currentTab === "xp" ? e.xp + " XP" : e.problemsSolved + " solved"}</div>
                        <div class="text-muted" style="font-size: 12px;">${currentTab === "xp" ? e.problemsSolved + " solved" : e.xp + " XP"}</div>
                    </div>
                </div>
            `).join("");
    } catch (err) {
        listEl.innerHTML = `<p class="error-text">${escapeHtml(err.message)}</p>`;
    }
}

async function loadBadges() {
    const el = document.getElementById("badges-list");
    try {
        const badges = await api.get("/users/me/badges");
        el.innerHTML = badges.map(b => `
            <div class="card" style="text-align: center; opacity: ${b.earned ? "1" : "0.35"};">
                <div style="font-size: 32px; margin-bottom: 8px;">${b.icon}</div>
                <div style="font-weight: 600; font-size: 14px;">${escapeHtml(b.name)}</div>
                <div class="text-muted" style="font-size: 12px; margin-top: 4px;">${escapeHtml(b.description)}</div>
                ${b.earned ? `<div style="color: var(--color-success); font-size: 11px; margin-top: 6px;">Earned ${new Date(b.awardedAt).toLocaleDateString()}</div>` : ""}
            </div>
        `).join("");
    } catch (err) {
        el.innerHTML = `<p class="error-text">${escapeHtml(err.message)}</p>`;
    }
}

document.getElementById("loading-text").style.display = "none";
loadLeaderboard();
loadBadges();