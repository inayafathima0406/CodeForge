function difficultyBadgeClass(difficulty) {
    if (difficulty === "EASY") return "badge-easy";
    if (difficulty === "MEDIUM") return "badge-medium";
    return "badge-hard";
}

function renderSampleCases(samples) {
    const container = document.getElementById("sample-cases");

    if (samples.length === 0) {
        container.innerHTML = '<p class="text-muted">No sample cases available.</p>';
        return;
    }

    container.innerHTML = samples.map((s, i) => `
        <div class="card" style="margin-bottom: 16px;">
            <div class="text-muted" style="font-size: 13px; margin-bottom: 8px;">Example ${i + 1}</div>
            <div style="margin-bottom: 8px;">
                <strong style="font-size: 13px;">Input:</strong>
                <pre style="font-family: var(--font-mono); background: var(--color-surface-2); padding: 10px; border-radius: 6px; margin-top: 4px; overflow-x: auto;">${escapeHtml(s.input)}</pre>
            </div>
            <div style="margin-bottom: ${s.explanation ? '8px' : '0'};">
                <strong style="font-size: 13px;">Expected Output:</strong>
                <pre style="font-family: var(--font-mono); background: var(--color-surface-2); padding: 10px; border-radius: 6px; margin-top: 4px; overflow-x: auto;">${escapeHtml(s.expectedOutput)}</pre>
            </div>
            ${s.explanation ? `<div class="text-muted" style="font-size: 13px;">${escapeHtml(s.explanation)}</div>` : ''}
        </div>
    `).join("");
}

function escapeHtml(text) {
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
}

function getQuestionId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

async function loadProblem() {
    const loadingEl = document.getElementById("loading-text");
    const errorEl = document.getElementById("error-message");
    const contentEl = document.getElementById("problem-content");

    const id = getQuestionId();
    if (!id) {
        loadingEl.style.display = "none";
        errorEl.textContent = "No problem ID provided.";
        errorEl.style.display = "block";
        return;
    }

    try {
        const q = await api.get("/questions/" + id);

        document.getElementById("problem-title").textContent = q.title;
        document.getElementById("problem-topic").textContent = q.topic;

        const badgeEl = document.getElementById("problem-difficulty");
        badgeEl.textContent = q.difficulty;
        badgeEl.classList.add(difficultyBadgeClass(q.difficulty));

        document.getElementById("problem-statement").textContent = q.statement;
        document.getElementById("problem-input").textContent = q.inputDescription;
        document.getElementById("problem-output").textContent = q.outputDescription;
        document.getElementById("problem-constraints").textContent = q.constraints;

        renderSampleCases(q.samples);

        loadingEl.style.display = "none";
        contentEl.style.display = "block";
    } catch (err) {
        loadingEl.style.display = "none";
        errorEl.textContent = err.message;
        errorEl.style.display = "block";
    }
}

loadProblem();
