const STARTER_CODE = `import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Read input from sc and print your answer with System.out.println
    }
}
`;

const editor = document.getElementById("code-editor");
const runButton = document.getElementById("run-button");
const runStatus = document.getElementById("run-status");
const runResults = document.getElementById("run-results");

editor.value = STARTER_CODE;

// Tab inserts four spaces instead of moving focus out of the editor
editor.addEventListener("keydown", (e) => {
    if (e.key === "Tab") {
        e.preventDefault();
        const start = editor.selectionStart;
        editor.setRange = editor.value =
            editor.value.substring(0, start) + "    " + editor.value.substring(editor.selectionEnd);
        editor.selectionStart = editor.selectionEnd = start + 4;
    }
});

function statusStyle(status) {
    if (status === "ACCEPTED") return { color: "var(--color-success)", label: "Passed" };
    if (status === "WRONG_ANSWER") return { color: "var(--color-danger)", label: "Wrong Answer" };
    if (status === "COMPILATION_ERROR") return { color: "var(--color-warning)", label: "Compilation Error" };
    if (status === "RUNTIME_ERROR") return { color: "var(--color-danger)", label: "Runtime Error" };
    if (status === "TIME_LIMIT_EXCEEDED") return { color: "var(--color-warning)", label: "Time Limit Exceeded" };
    return { color: "var(--color-text-muted)", label: status };
}

function renderRun(result) {
    const overall = statusStyle(result.overallStatus);
    let html = `<div class="card" style="margin-bottom: 16px;">
        <div style="font-weight: 600; color: ${overall.color}; margin-bottom: 12px;">
            ${overall.label} (${result.passed}/${result.total} sample cases passed)
        </div>`;

    result.cases.forEach(c => {
        const s = statusStyle(c.status);
        html += `<div style="border-top: 1px solid var(--color-border); padding-top: 12px; margin-top: 12px;">
            <div style="color: ${s.color}; font-weight: 600; margin-bottom: 6px;">Case ${c.caseNumber}: ${s.label}</div>`;
        if (c.errorMessage) {
            html += `<pre style="font-family: var(--font-mono); font-size: 13px; white-space: pre-wrap; color: var(--color-warning);">${escapeHtml(c.errorMessage)}</pre>`;
        } else if (c.status !== "ACCEPTED") {
            html += `<div class="text-muted" style="font-size: 13px;">Expected:</div>
                <pre style="font-family: var(--font-mono); background: var(--color-surface-2); padding: 8px; border-radius: 6px;">${escapeHtml(c.expectedOutput)}</pre>
                <div class="text-muted" style="font-size: 13px;">Your output:</div>
                <pre style="font-family: var(--font-mono); background: var(--color-surface-2); padding: 8px; border-radius: 6px;">${escapeHtml(c.actualOutput || "")}</pre>`;
        }
        html += `</div>`;
    });

    html += `</div>`;
    runResults.innerHTML = html;
}

runButton.addEventListener("click", async () => {
    const id = getQuestionId();
    runButton.disabled = true;
    runStatus.textContent = "Running...";
    runResults.innerHTML = "";

    try {
        const result = await api.post("/questions/" + id + "/run", {
            sourceCode: editor.value,
            language: "java"
        });
        renderRun(result);
        runStatus.textContent = "";
    } catch (err) {
        runStatus.textContent = "";
        runResults.innerHTML = `<p class="error-text">${escapeHtml(err.message)}</p>`;
    } finally {
        runButton.disabled = false;
    }
});