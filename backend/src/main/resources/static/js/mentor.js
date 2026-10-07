let lastSubmissionId = null;
let lastSubmissionStatus = null;

const originalRenderSubmit = renderSubmit;
renderSubmit = function (result) {
    originalRenderSubmit(result);
    lastSubmissionId = result.id;
    lastSubmissionStatus = result.status;

    if (result.status !== "ACCEPTED") {
        const mentorDiv = document.createElement("div");
        mentorDiv.id = "mentor-section";
        mentorDiv.style.marginTop = "16px";
        mentorDiv.innerHTML = `
            <button id="mentor-button" class="btn btn-outline">Get AI Hint</button>
            <div id="mentor-output" style="margin-top: 12px;"></div>
        `;
        runResults.appendChild(mentorDiv);
        document.getElementById("mentor-button").addEventListener("click", () => requestMentor(false));
    }
};

async function requestMentor(wantsSolution) {
    const outputEl = document.getElementById("mentor-output");
    const button = document.getElementById("mentor-button");
    button.disabled = true;
    outputEl.innerHTML = '<p class="text-muted">Thinking...</p>';

    try {
        const path = "/submissions/" + lastSubmissionId + "/mentor" + (wantsSolution ? "/solution" : "");
        const feedback = await api.post(path, {});
        renderMentor(feedback);
    } catch (err) {
        outputEl.innerHTML = `<p class="error-text">${escapeHtml(err.message)}</p>`;
    } finally {
        button.disabled = false;
    }
}

function renderMentor(feedback) {
    const outputEl = document.getElementById("mentor-output");

    let html = `<div class="card">
        <div style="font-weight: 600; margin-bottom: 8px; color: var(--color-primary);">${escapeHtml(feedback.errorType || "Feedback")}</div>
        <p style="margin-bottom: 12px;">${escapeHtml(feedback.explanation || "")}</p>
        <div class="text-muted" style="font-size: 13px; margin-bottom: 4px;">Hint</div>
        <p style="margin-bottom: 12px;">${escapeHtml(feedback.hint || "")}</p>`;

    if (feedback.conceptsToRevise) {
        html += `<div style="margin-bottom: 12px;">`;
        feedback.conceptsToRevise.split(",").forEach(c => {
            html += `<span class="badge" style="background: rgba(79,140,255,0.15); color: var(--color-primary); margin-right: 6px;">${escapeHtml(c.trim())}</span>`;
        });
        html += `</div>`;
    }

    if (feedback.complexityFeedback) {
        html += `<div class="text-muted" style="font-size: 13px;">${escapeHtml(feedback.complexityFeedback)}</div>`;
    }

    if (feedback.suggestedSolution) {
        html += `<div class="text-muted" style="font-size: 13px; margin: 12px 0 4px;">Suggested Solution</div>
            <pre style="font-family: var(--font-mono); background: var(--color-surface-2); padding: 10px; border-radius: 6px; overflow-x: auto; font-size: 13px;">${escapeHtml(feedback.suggestedSolution)}</pre>`;
    } else {
        html += `<button id="reveal-solution-button" class="btn btn-outline" style="margin-top: 12px;">Show Full Solution</button>`;
    }

    html += `</div>`;
    outputEl.innerHTML = html;

    const revealButton = document.getElementById("reveal-solution-button");
    if (revealButton) {
        revealButton.addEventListener("click", () => requestMentor(true));
    }
}