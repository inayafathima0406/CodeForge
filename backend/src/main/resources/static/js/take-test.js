function getAttemptId() {
    return new URLSearchParams(window.location.search).get("attemptId");
}

let attempt = null;
let deadline = null;
let currentQuestionId = null;
let timerInterval = null;

function renderTabs() {
    document.getElementById("question-tabs").innerHTML = attempt.questions.map(q => `
        <button class="btn ${q.questionId === currentQuestionId ? "btn-primary" : "btn-outline"}"
                onclick="selectQuestion(${q.questionId})">${escapeHtml(q.title)} (${q.points}pt)</button>
    `).join("");
}

function selectQuestion(questionId) {
    currentQuestionId = questionId;
    renderTabs();
    document.getElementById("question-area").innerHTML = `
        <div class="card">
            <p class="text-muted" style="margin-bottom: 12px;">Open this question in a new tab to read the statement, write your code there, then come back here to track time. (A combined in-page editor view is a nice future improvement.)</p>
            <a href="/pages/problem-detail.html?id=${questionId}" target="_blank" class="btn btn-primary">Open Problem</a>
            <p class="text-muted" style="margin-top: 12px; font-size: 13px;">Submissions made on the problem page while this test is active are NOT automatically linked to this attempt. Use the API-integrated flow in a future version; for now, track your progress and click Finish Test when done.</p>
        </div>
    `;
}

function updateTimer() {
    const msLeft = deadline - Date.now();
    if (msLeft <= 0) {
        clearInterval(timerInterval);
        document.getElementById("timer").textContent = "00:00";
        finishTest("TIME_UP");
        return;
    }
    const totalSeconds = Math.floor(msLeft / 1000);
    const mins = Math.floor(totalSeconds / 60);
    const secs = totalSeconds % 60;
    document.getElementById("timer").textContent = String(mins).padStart(2, "0") + ":" + String(secs).padStart(2, "0");
}

async function finishTest() {
    clearInterval(timerInterval);
    try {
        await api.post("/attempts/" + attempt.attemptId + "/finish", {});
    } catch (err) {
        // Already finished server-side (e.g. time ran out mid-request) is fine; proceed to results either way
    }
    window.location.href = "/pages/test-result.html?attemptId=" + attempt.attemptId;
}

document.getElementById("finish-button").addEventListener("click", () => {
    if (confirm("Finish the test now? You cannot resume after this.")) {
        finishTest();
    }
});

async function load() {
    const id = getAttemptId();
    const errorEl = document.getElementById("error-message");
    try {
        attempt = await api.get("/attempts/" + id);

        if (attempt.status !== "IN_PROGRESS") {
            window.location.href = "/pages/test-result.html?attemptId=" + id;
            return;
        }

        document.getElementById("test-title").textContent = attempt.testTitle;
        deadline = new Date(attempt.startedAt).getTime() + attempt.durationMinutes * 60000;

        currentQuestionId = attempt.questions[0].questionId;
        renderTabs();
        selectQuestion(currentQuestionId);

        updateTimer();
        timerInterval = setInterval(updateTimer, 1000);
    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.style.display = "block";
    }
}

load();