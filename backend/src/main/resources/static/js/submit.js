const submitButton = document.getElementById("submit-button");

function renderSubmit(result) {
    const overall = statusStyle(result.status);
    let html = `<div class="card" style="margin-bottom: 16px;">
        <div style="font-weight: 600; color: ${overall.color}; margin-bottom: 8px;">
            ${overall.label} (${result.passed}/${result.total} test cases passed)
        </div>
        <div class="text-muted" style="font-size: 13px; margin-bottom: 12px;">
            Submission #${result.id}${result.execTimeMs != null ? " &middot; " + result.execTimeMs + " ms" : ""}${result.memoryKb != null ? " &middot; " + result.memoryKb + " KB" : ""}
        </div>`;

    result.cases.forEach(c => {
        const s = statusStyle(c.status);
        html += `<div style="border-top: 1px solid var(--color-border); padding-top: 10px; margin-top: 10px;">
            <div style="color: ${s.color}; font-size: 14px;">
                Case ${c.caseNumber}${c.sample ? "" : " (hidden)"}: ${s.label}
            </div>`;
        if (c.sample && c.errorMessage) {
            html += `<pre style="font-family: var(--font-mono); font-size: 13px; white-space: pre-wrap; color: var(--color-warning);">${escapeHtml(c.errorMessage)}</pre>`;
        } else if (c.sample && c.status !== "ACCEPTED") {
            html += `<div class="text-muted" style="font-size: 13px;">Expected: <code>${escapeHtml(c.expectedOutput || "")}</code> &nbsp; Got: <code>${escapeHtml(c.actualOutput || "")}</code></div>`;
        }
        html += `</div>`;
    });

    html += `</div>`;
    runResults.innerHTML = html;
}

submitButton.addEventListener("click", async () => {
    const id = getQuestionId();
    submitButton.disabled = true;
    runButton.disabled = true;
    runStatus.textContent = "Submitting...";
    runResults.innerHTML = "";

    try {
        const result = await api.post("/questions/" + id + "/submit", {
            sourceCode: editor.value,
            language: "java"
        });
        renderSubmit(result);
        runStatus.textContent = "";
    } catch (err) {
        runStatus.textContent = "";
        runResults.innerHTML = `<p class="error-text">${escapeHtml(err.message)}</p>`;
    } finally {
        submitButton.disabled = false;
        runButton.disabled = false;
    }
});